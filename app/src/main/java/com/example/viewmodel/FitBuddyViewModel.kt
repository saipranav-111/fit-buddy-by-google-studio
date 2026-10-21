package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiCoachService
import com.example.ai.ChatMessage
import com.example.ai.GeneratedWorkoutRoutine
import com.example.ai.MessageSender
import com.example.data.FitBuddyDatabase
import com.example.data.UserPreferencesRepository
import com.example.model.DailyNutritionSummary
import com.example.model.DietType
import com.example.model.FitnessGoal
import com.example.model.FoodItem
import com.example.model.MealLog
import com.example.model.MealType
import com.example.model.MifflinStJeor
import com.example.model.SavedWorkoutPlan
import com.example.model.UserProfile
import com.example.model.WeightLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import com.example.data.FitBuddyFirestoreRepository
import com.example.data.FirestoreMealLog
import com.example.data.FirestoreUserProfile
import com.example.data.FirestoreWeightLog
import com.example.data.FirestoreWorkoutPlan
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class FitBuddyViewModel(application: Application) : AndroidViewModel(application) {

    private val database = FitBuddyDatabase.getDatabase(application, viewModelScope)
    private val dao = database.fitBuddyDao()
    private val prefs = UserPreferencesRepository(application)
    private val aiService = AiCoachService()
    private val firestoreRepo = FitBuddyFirestoreRepository(application)

    val userProfile: StateFlow<UserProfile> = prefs.userProfile

    private val _selectedDate = MutableStateFlow(prefs.getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _waterGlasses = MutableStateFlow(prefs.getWaterGlassesForDate(_selectedDate.value))
    val waterGlasses: StateFlow<Int> = _waterGlasses.asStateFlow()

    private val _streak = MutableStateFlow(prefs.getStreak())
    val streak: StateFlow<Int> = _streak.asStateFlow()

    // Logs for selected date
    @OptIn(ExperimentalCoroutinesApi::class)
    val mealLogsForSelectedDate: StateFlow<List<MealLog>> = _selectedDate
        .flatMapLatest { date -> dao.getMealLogsByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Summary calculation
    val dailySummary: StateFlow<DailyNutritionSummary> = combine(
        mealLogsForSelectedDate,
        userProfile,
        _waterGlasses,
        _selectedDate
    ) { logs, profile, water, date ->
        var cal = 0
        var pro = 0f
        var carbs = 0f
        var fat = 0f

        for (log in logs) {
            cal += log.calories
            pro += log.protein
            carbs += log.carbs
            fat += log.fat
        }

        DailyNutritionSummary(
            date = date,
            totalCalories = cal,
            totalProtein = pro,
            totalCarbs = carbs,
            totalFat = fat,
            targetCalories = profile.targetCalories,
            targetProtein = profile.targetProteinG,
            targetCarbs = profile.targetCarbsG,
            targetFat = profile.targetFatG,
            waterGlasses = water,
            waterGoal = profile.waterGoalGlasses
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyNutritionSummary(date = prefs.getTodayDateString()))

    // Food catalog search and filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedDiet = MutableStateFlow<DietType?>(null)
    val selectedDiet: StateFlow<DietType?> = _selectedDiet.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredFoods: StateFlow<List<FoodItem>> = combine(
        _searchQuery,
        _selectedCategory,
        _selectedDiet
    ) { query, category, diet ->
        Triple(query, category, diet)
    }.flatMapLatest { (query, category, diet) ->
        if (query.isBlank()) {
            dao.getAllFoods()
        } else {
            dao.searchFoods(query.trim())
        }
    }.combine(_selectedCategory) { foods, category ->
        if (category == "All") foods else foods.filter { it.category.equals(category, ignoreCase = true) }
    }.combine(_selectedDiet) { foods, diet ->
        if (diet == null) foods else foods.filter { it.dietType == diet }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recent items
    val recentMealLogs: StateFlow<List<MealLog>> = dao.getRecentMealLogs(8)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weight logs
    val weightLogs: StateFlow<List<WeightLog>> = dao.getAllWeightLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved workouts
    val savedWorkouts: StateFlow<List<SavedWorkoutPlan>> = dao.getAllSavedWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.COACH,
                text = "Hey there! I'm your Fit Buddy AI Coach. 🏋️‍♂️\nI can help you hit your macros, suggest healthy meal ideas, or answer fitness questions tailored directly to today's logged nutrition."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiTyping = MutableStateFlow(false)
    val isAiTyping: StateFlow<Boolean> = _isAiTyping.asStateFlow()

    // Workout generator
    private val _generatedRoutine = MutableStateFlow<GeneratedWorkoutRoutine?>(null)
    val generatedRoutine: StateFlow<GeneratedWorkoutRoutine?> = _generatedRoutine.asStateFlow()

    private val _isGeneratingWorkout = MutableStateFlow(false)
    val isGeneratingWorkout: StateFlow<Boolean> = _isGeneratingWorkout.asStateFlow()

    // --- In-Gym Exercise Personalizer ---
    private val _selectedGymBodyType = MutableStateFlow(userProfile.value.bodyType)
    val selectedGymBodyType: StateFlow<com.example.model.BodyType> = _selectedGymBodyType.asStateFlow()

    private val _selectedGymGoal = MutableStateFlow(userProfile.value.gymGoal)
    val selectedGymGoal: StateFlow<com.example.model.GymTrainingGoal> = _selectedGymGoal.asStateFlow()

    private val _selectedMuscleGroup = MutableStateFlow<com.example.model.MuscleGroup?>(null)
    val selectedMuscleGroup: StateFlow<com.example.model.MuscleGroup?> = _selectedMuscleGroup.asStateFlow()

    val personalizedGymExercises: StateFlow<List<com.example.model.GymExercise>> = combine(
        _selectedGymGoal,
        _selectedGymBodyType,
        _selectedMuscleGroup
    ) { goal, bodyType, muscle ->
        com.example.data.GymExerciseCatalog.getFilteredGymExercises(goal, bodyType, muscle)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.GymExerciseCatalog.allGymExercises)

    private val _activeGymSession = MutableStateFlow<List<com.example.model.ActiveGymExerciseItem>>(emptyList())
    val activeGymSession: StateFlow<List<com.example.model.ActiveGymExerciseItem>> = _activeGymSession.asStateFlow()

    init {
        // Seed initial weight log if none exists and sync with Firebase
        viewModelScope.launch(Dispatchers.IO) {
            val count = dao.getAllWeightLogs()
            _streak.value = prefs.getStreak()

            val authUser = Firebase.auth.currentUser
            if (authUser != null) {
                val cloudResult = firestoreRepo.getUserProfile(authUser.uid)
                val cloudProfile = cloudResult.getOrNull()
                if (cloudProfile != null && !prefs.userProfile.value.isProfileComplete) {
                    val restored = UserProfile(
                        name = cloudProfile.name,
                        age = cloudProfile.age,
                        gender = try { com.example.model.Gender.valueOf(cloudProfile.gender) } catch (e: Exception) { com.example.model.Gender.MALE },
                        heightCm = cloudProfile.heightCm.toFloat(),
                        weightKg = cloudProfile.weightKg.toFloat(),
                        targetWeightKg = cloudProfile.weightKg.toFloat(),
                        activityLevel = try { com.example.model.ActivityLevel.valueOf(cloudProfile.activityLevel) } catch (e: Exception) { com.example.model.ActivityLevel.MODERATELY_ACTIVE },
                        goal = try { FitnessGoal.valueOf(cloudProfile.goal) } catch (e: Exception) { FitnessGoal.MAINTAIN },
                        targetCalories = cloudProfile.targetCalories,
                        targetProteinG = cloudProfile.targetProteinG.toInt(),
                        targetCarbsG = cloudProfile.targetCarbsG.toInt(),
                        targetFatG = cloudProfile.targetFatG.toInt(),
                        isProfileComplete = true
                    )
                    prefs.saveProfile(restored)
                }
            }
        }
    }

    fun completeOnboarding(
        name: String,
        age: Int,
        gender: com.example.model.Gender,
        heightCm: Float,
        weightKg: Float,
        targetWeightKg: Float,
        activityLevel: com.example.model.ActivityLevel,
        goal: FitnessGoal,
        bodyType: com.example.model.BodyType = com.example.model.BodyType.MESOMORPH,
        gymGoal: com.example.model.GymTrainingGoal = com.example.model.GymTrainingGoal.BULK
    ) {
        val targets = MifflinStJeor.calculateTargets(
            gender = gender,
            weightKg = weightKg,
            heightCm = heightCm,
            age = age,
            activityLevel = activityLevel,
            goal = goal
        )

        val profile = UserProfile(
            name = name.ifBlank { "Fitness Buddy" },
            age = age,
            gender = gender,
            heightCm = heightCm,
            weightKg = weightKg,
            targetWeightKg = targetWeightKg,
            activityLevel = activityLevel,
            goal = goal,
            bodyType = bodyType,
            gymGoal = gymGoal,
            targetCalories = targets.calories,
            targetProteinG = targets.protein,
            targetCarbsG = targets.carbs,
            targetFatG = targets.fat,
            waterGoalGlasses = 8,
            isProfileComplete = true
        )
        prefs.saveProfile(profile)
        _selectedGymBodyType.value = bodyType
        _selectedGymGoal.value = gymGoal

        // Sync to Firebase Cloud
        val authUser = Firebase.auth.currentUser
        if (authUser != null) {
            viewModelScope.launch(Dispatchers.IO) {
                firestoreRepo.saveUserProfile(
                    FirestoreUserProfile(
                        userId = authUser.uid,
                        name = profile.name,
                        age = profile.age,
                        gender = profile.gender.name,
                        heightCm = profile.heightCm.toDouble(),
                        weightKg = profile.weightKg.toDouble(),
                        activityLevel = profile.activityLevel.name,
                        goal = profile.goal.name,
                        targetCalories = profile.targetCalories,
                        targetProteinG = profile.targetProteinG.toDouble(),
                        targetCarbsG = profile.targetCarbsG.toDouble(),
                        targetFatG = profile.targetFatG.toDouble()
                    )
                )
            }
        }

        // Also add first weight log
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertWeightLog(
                WeightLog(
                    weightKg = weightKg,
                    date = prefs.getTodayDateString(),
                    note = "Starting baseline"
                )
            )
        }
    }

    fun updateProfile(profile: UserProfile) {
        prefs.saveProfile(profile)
        val authUser = Firebase.auth.currentUser
        if (authUser != null) {
            viewModelScope.launch(Dispatchers.IO) {
                firestoreRepo.saveUserProfile(
                    FirestoreUserProfile(
                        userId = authUser.uid,
                        name = profile.name,
                        age = profile.age,
                        gender = profile.gender.name,
                        heightCm = profile.heightCm.toDouble(),
                        weightKg = profile.weightKg.toDouble(),
                        activityLevel = profile.activityLevel.name,
                        goal = profile.goal.name,
                        targetCalories = profile.targetCalories,
                        targetProteinG = profile.targetProteinG.toDouble(),
                        targetCarbsG = profile.targetCarbsG.toDouble(),
                        targetFatG = profile.targetFatG.toDouble()
                    )
                )
            }
        }
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        _waterGlasses.value = prefs.getWaterGlassesForDate(date)
    }

    fun logMeal(
        food: FoodItem,
        amountGrams: Float,
        mealType: MealType
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val date = _selectedDate.value
            val log = MealLog(
                foodItemId = food.id,
                foodName = food.name,
                mealType = mealType,
                amountGrams = amountGrams,
                calories = food.calculateCalories(amountGrams),
                protein = food.calculateProtein(amountGrams),
                carbs = food.calculateCarbs(amountGrams),
                fat = food.calculateFat(amountGrams),
                date = date,
                timestamp = System.currentTimeMillis()
            )
            dao.insertMealLog(log)
            val updatedStreak = prefs.recordLogForStreak(date)
            _streak.value = updatedStreak

            val authUser = Firebase.auth.currentUser
            if (authUser != null) {
                firestoreRepo.addMealLog(
                    FirestoreMealLog(
                        userId = authUser.uid,
                        foodName = food.name,
                        portionGrams = amountGrams.toDouble(),
                        calories = log.calories.toDouble(),
                        proteinG = log.protein.toDouble(),
                        carbsG = log.carbs.toDouble(),
                        fatG = log.fat.toDouble(),
                        mealType = mealType.displayName,
                        date = date
                    )
                )
            }
        }
    }

    fun deleteMealLog(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteMealLog(id)
        }
    }

    fun incrementWater() {
        val date = _selectedDate.value
        val newCount = _waterGlasses.value + 1
        _waterGlasses.value = newCount
        prefs.setWaterGlassesForDate(date, newCount)
    }

    fun decrementWater() {
        val date = _selectedDate.value
        val newCount = (_waterGlasses.value - 1).coerceAtLeast(0)
        _waterGlasses.value = newCount
        prefs.setWaterGlassesForDate(date, newCount)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setDiet(diet: DietType?) {
        _selectedDiet.value = diet
    }

    fun logWeight(weightKg: Float, note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertWeightLog(
                WeightLog(
                    weightKg = weightKg,
                    date = prefs.getTodayDateString(),
                    note = note
                )
            )
            // Also update current weight in profile and recalculate
            val current = userProfile.value
            val updated = current.copy(weightKg = weightKg)
            val targets = MifflinStJeor.calculateTargets(
                gender = updated.gender,
                weightKg = weightKg,
                heightCm = updated.heightCm,
                age = updated.age,
                activityLevel = updated.activityLevel,
                goal = updated.goal
            )
            prefs.saveProfile(
                updated.copy(
                    targetCalories = targets.calories,
                    targetProteinG = targets.protein,
                    targetCarbsG = targets.carbs,
                    targetFatG = targets.fat
                )
            )

            val authUser = Firebase.auth.currentUser
            if (authUser != null) {
                firestoreRepo.addWeightLog(
                    FirestoreWeightLog(
                        userId = authUser.uid,
                        weightKg = weightKg.toDouble(),
                        date = prefs.getTodayDateString(),
                        note = note
                    )
                )
            }
        }
    }

    fun deleteWeight(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteWeightLog(id)
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(sender = MessageSender.USER, text = text.trim())
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isAiTyping.value = true
            val reply = aiService.askCoach(
                userMessage = text,
                userProfile = userProfile.value,
                summary = dailySummary.value,
                todayLogs = mealLogsForSelectedDate.value
            )
            _isAiTyping.value = false
            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = MessageSender.COACH,
                text = reply
            )
        }
    }

    fun generateWorkoutPlan(
        goal: String,
        level: String,
        equipment: String,
        daysPerWeek: Int
    ) {
        viewModelScope.launch {
            _isGeneratingWorkout.value = true
            val routine = aiService.generateWorkoutRoutine(
                goal = goal,
                level = level,
                equipment = equipment,
                daysPerWeek = daysPerWeek,
                profile = userProfile.value
            )
            _generatedRoutine.value = routine
            _isGeneratingWorkout.value = false
        }
    }

    fun saveCurrentWorkoutPlan() {
        val routine = _generatedRoutine.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            // Convert to lightweight string json
            val summaryText = buildString {
                appendLine(routine.title)
                appendLine(routine.summary)
                for (day in routine.days) {
                    appendLine("\n${day.dayName}: ${day.focus}")
                    for (ex in day.exercises) {
                        appendLine(" • ${ex.name} - ${ex.sets} sets x ${ex.reps} (${ex.restSeconds}s rest) [${ex.coachingTip}]")
                    }
                }
            }
            dao.insertWorkoutPlan(
                SavedWorkoutPlan(
                    title = routine.title,
                    goal = routine.days.firstOrNull()?.focus ?: "Fitness",
                    level = "Custom",
                    equipment = "All",
                    daysPerWeek = routine.days.size,
                    planDetailsJson = summaryText
                )
            )

            val authUser = Firebase.auth.currentUser
            if (authUser != null) {
                firestoreRepo.saveWorkoutPlan(
                    FirestoreWorkoutPlan(
                        userId = authUser.uid,
                        goal = routine.days.firstOrNull()?.focus ?: "Fitness",
                        level = "Custom",
                        daysPerWeek = routine.days.size,
                        splitName = routine.title,
                        planSummary = routine.summary,
                        daysJson = summaryText
                    )
                )
            }
        }
    }

    fun deleteSavedWorkout(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteWorkoutPlan(id)
        }
    }

    // --- In-Gym Session Actions ---
    fun setGymBodyType(bodyType: com.example.model.BodyType) {
        _selectedGymBodyType.value = bodyType
        val updated = userProfile.value.copy(bodyType = bodyType)
        prefs.saveProfile(updated)
    }

    fun setGymGoal(goal: com.example.model.GymTrainingGoal) {
        _selectedGymGoal.value = goal
        val updated = userProfile.value.copy(gymGoal = goal)
        prefs.saveProfile(updated)
        // Refresh active gym session target sets & reps based on new goal
        _activeGymSession.value = _activeGymSession.value.map { item ->
            item.copy(
                targetSets = goal.recommendedSets,
                targetReps = goal.repRange,
                completedSetChecklist = List(goal.recommendedSets) { false },
                completedSets = 0
            )
        }
    }

    fun setGymMuscleGroup(group: com.example.model.MuscleGroup?) {
        _selectedMuscleGroup.value = group
    }

    fun toggleExerciseInGymSession(exercise: com.example.model.GymExercise) {
        val current = _activeGymSession.value
        val exists = current.any { it.exercise.id == exercise.id }
        if (exists) {
            _activeGymSession.value = current.filterNot { it.exercise.id == exercise.id }
        } else {
            val goal = _selectedGymGoal.value
            _activeGymSession.value = current + com.example.model.ActiveGymExerciseItem(
                exercise = exercise,
                targetSets = goal.recommendedSets,
                targetReps = goal.repRange,
                completedSets = 0,
                completedSetChecklist = List(goal.recommendedSets) { false }
            )
        }
    }

    fun toggleGymSetCompleted(exerciseId: String, setIndex: Int) {
        _activeGymSession.value = _activeGymSession.value.map { item ->
            if (item.exercise.id == exerciseId) {
                val updatedChecklist = item.completedSetChecklist.toMutableList()
                if (setIndex in updatedChecklist.indices) {
                    updatedChecklist[setIndex] = !updatedChecklist[setIndex]
                }
                item.copy(
                    completedSets = updatedChecklist.count { it },
                    completedSetChecklist = updatedChecklist
                )
            } else {
                item
            }
        }
    }

    fun clearActiveGymSession() {
        _activeGymSession.value = emptyList()
    }

    fun saveActiveGymSession() {
        val session = _activeGymSession.value
        if (session.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val goal = _selectedGymGoal.value
            val bodyType = _selectedGymBodyType.value
            val summaryText = buildString {
                appendLine("Gym Routine: ${goal.title}")
                appendLine("Body Type Target: ${bodyType.title}")
                appendLine("Execution Style: ${goal.executionFocus}")
                appendLine("Rest: ${goal.restInterval}")
                appendLine("\nCompleted In-Gym Exercises:")
                for (item in session) {
                    appendLine(" • ${item.exercise.name} - ${item.completedSets}/${item.targetSets} sets x ${item.targetReps} (${item.exercise.equipmentNeeded})")
                    appendLine("   Form Cue: ${item.exercise.executionCue}")
                }
            }
            dao.insertWorkoutPlan(
                SavedWorkoutPlan(
                    title = "Gym: ${goal.title}",
                    goal = goal.title,
                    level = bodyType.title,
                    equipment = "In-Gym",
                    daysPerWeek = 1,
                    planDetailsJson = summaryText
                )
            )
        }
    }
}
