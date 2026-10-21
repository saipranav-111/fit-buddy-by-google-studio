package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.model.MealType
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FoodSearchScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProgressProfileScreen
import com.example.ui.screens.appSignOut
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FitBuddyTheme
import com.example.viewmodel.FitBuddyViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

enum class FitBuddyDestination(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
    FOOD_LOG("Food Log", Icons.Default.Restaurant, "nav_food_log"),
    ANALYTICS("Analytics", Icons.Default.BarChart, "nav_analytics"),
    AI_COACH("AI Coach", Icons.Default.AutoAwesome, "nav_ai_coach"),
    PROFILE("Profile", Icons.Default.Person, "nav_profile")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitBuddyTheme {
                FitBuddyApp()
            }
        }
    }
}

@Composable
fun FitBuddyApp() {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        FitBuddyMainContent(
            currentUserId = currentUser!!.uid,
            onSignOut = {
                appSignOut(context, { currentUser = null }, scope)
            }
        )
    }
}

@Composable
fun FitBuddyMainContent(
    currentUserId: String,
    viewModel: FitBuddyViewModel = viewModel(),
    onSignOut: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val dailySummary by viewModel.dailySummary.collectAsState()
    val todayMealLogs by viewModel.mealLogsForSelectedDate.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentStreak by viewModel.streak.collectAsState()

    val foods by viewModel.filteredFoods.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedDiet by viewModel.selectedDiet.collectAsState()
    val recentLogs by viewModel.recentMealLogs.collectAsState()

    val weightLogs by viewModel.weightLogs.collectAsState()
    val savedWorkouts by viewModel.savedWorkouts.collectAsState()

    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAiTyping by viewModel.isAiTyping.collectAsState()
    val generatedRoutine by viewModel.generatedRoutine.collectAsState()
    val isGeneratingWorkout by viewModel.isGeneratingWorkout.collectAsState()

    // In-Gym Personalizer states
    val selectedGymBodyType by viewModel.selectedGymBodyType.collectAsState()
    val selectedGymGoal by viewModel.selectedGymGoal.collectAsState()
    val selectedMuscleGroup by viewModel.selectedMuscleGroup.collectAsState()
    val personalizedGymExercises by viewModel.personalizedGymExercises.collectAsState()
    val activeGymSession by viewModel.activeGymSession.collectAsState()

    var currentDestination by remember { mutableStateOf(FitBuddyDestination.DASHBOARD) }
    var foodLogPreselectedMealType by remember { mutableStateOf(MealType.currentForHour()) }

    // If profile is not complete, show Onboarding
    if (!userProfile.isProfileComplete) {
        OnboardingScreen(
            onComplete = { name, age, gender, heightCm, weightKg, targetWeightKg, activityLevel, goal, bodyType, gymGoal ->
                viewModel.completeOnboarding(
                    name = name,
                    age = age,
                    gender = gender,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    targetWeightKg = targetWeightKg,
                    activityLevel = activityLevel,
                    goal = goal,
                    bodyType = bodyType,
                    gymGoal = gymGoal
                )
            }
        )
    } else {
        // Back navigation handler: return to Dashboard if on another tab
        BackHandler(enabled = currentDestination != FitBuddyDestination.DASHBOARD) {
            currentDestination = FitBuddyDestination.DASHBOARD
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.navigationBarsPadding(),
                    tonalElevation = 8.dp
                ) {
                    FitBuddyDestination.entries.forEach { destination ->
                        val isSelected = currentDestination == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = EmeraldPrimary.copy(alpha = 0.2f),
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = EmeraldPrimary
                            ),
                            modifier = Modifier.testTag(destination.tag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentDestination) {
                    FitBuddyDestination.DASHBOARD -> DashboardScreen(
                        userProfile = userProfile,
                        summary = dailySummary,
                        mealLogs = todayMealLogs,
                        selectedDate = selectedDate,
                        streak = currentStreak,
                        onDateChange = { viewModel.setSelectedDate(it) },
                        onIncrementWater = { viewModel.incrementWater() },
                        onDecrementWater = { viewModel.decrementWater() },
                        onAddMealClick = { mealType ->
                            foodLogPreselectedMealType = mealType
                            currentDestination = FitBuddyDestination.FOOD_LOG
                        },
                        onDeleteMealLog = { viewModel.deleteMealLog(it) }
                    )

                    FitBuddyDestination.FOOD_LOG -> FoodSearchScreen(
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        selectedCategory = selectedCategory,
                        onCategorySelect = { viewModel.setCategory(it) },
                        selectedDiet = selectedDiet,
                        onDietSelect = { viewModel.setDiet(it) },
                        foods = foods,
                        recentLogs = recentLogs,
                        initialMealType = foodLogPreselectedMealType,
                        onLogMeal = { food, grams, mealType ->
                            viewModel.logMeal(food, grams, mealType)
                            currentDestination = FitBuddyDestination.DASHBOARD
                        }
                    )

                    FitBuddyDestination.ANALYTICS -> AnalyticsScreen(
                        userProfile = userProfile,
                        todaySummary = dailySummary,
                        recentMealLogs = recentLogs
                    )

                    FitBuddyDestination.AI_COACH -> AiCoachScreen(
                        userProfile = userProfile,
                        summary = dailySummary,
                        chatMessages = chatMessages,
                        isAiTyping = isAiTyping,
                        onSendMessage = { viewModel.sendChatMessage(it) },
                        generatedRoutine = generatedRoutine,
                        isGeneratingWorkout = isGeneratingWorkout,
                        onGenerateWorkout = { goal, level, equipment, days ->
                            viewModel.generateWorkoutPlan(goal, level, equipment, days)
                        },
                        onSaveWorkout = { viewModel.saveCurrentWorkoutPlan() },
                        savedWorkouts = savedWorkouts,
                        onDeleteWorkout = { viewModel.deleteSavedWorkout(it) },
                        selectedGymBodyType = selectedGymBodyType,
                        selectedGymGoal = selectedGymGoal,
                        selectedMuscleGroup = selectedMuscleGroup,
                        gymExercises = personalizedGymExercises,
                        activeGymSession = activeGymSession,
                        onSetGymBodyType = { viewModel.setGymBodyType(it) },
                        onSetGymGoal = { viewModel.setGymGoal(it) },
                        onSetMuscleGroup = { viewModel.setGymMuscleGroup(it) },
                        onToggleGymExercise = { viewModel.toggleExerciseInGymSession(it) },
                        onToggleGymSetCompleted = { id, idx -> viewModel.toggleGymSetCompleted(id, idx) },
                        onSaveGymSession = { viewModel.saveActiveGymSession() }
                    )

                    FitBuddyDestination.PROFILE -> ProgressProfileScreen(
                        userProfile = userProfile,
                        weightLogs = weightLogs,
                        streakDays = currentStreak,
                        onLogWeight = { w, note -> viewModel.logWeight(w, note) },
                        onDeleteWeight = { viewModel.deleteWeight(it) },
                        onUpdateProfile = { viewModel.updateProfile(it) },
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}
