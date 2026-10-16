package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ActivityLevel
import com.example.model.FitnessGoal
import com.example.model.Gender
import com.example.model.MifflinStJeor
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UserPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fit_buddy_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun loadProfile(): UserProfile {
        val isComplete = prefs.getBoolean("is_profile_complete", false)
        val name = prefs.getString("user_name", "Fitness Buddy") ?: "Fitness Buddy"
        val age = prefs.getInt("user_age", 26)
        val genderStr = prefs.getString("user_gender", Gender.MALE.name) ?: Gender.MALE.name
        val gender = try { Gender.valueOf(genderStr) } catch (e: Exception) { Gender.MALE }
        val height = prefs.getFloat("user_height", 175f)
        val weight = prefs.getFloat("user_weight", 72f)
        val targetWeight = prefs.getFloat("user_target_weight", 70f)
        val activityStr = prefs.getString("user_activity", ActivityLevel.MODERATELY_ACTIVE.name)
            ?: ActivityLevel.MODERATELY_ACTIVE.name
        val activity = try { ActivityLevel.valueOf(activityStr) } catch (e: Exception) { ActivityLevel.MODERATELY_ACTIVE }
        val goalStr = prefs.getString("user_goal", FitnessGoal.LOSE_WEIGHT.name)
            ?: FitnessGoal.LOSE_WEIGHT.name
        val goal = try { FitnessGoal.valueOf(goalStr) } catch (e: Exception) { FitnessGoal.LOSE_WEIGHT }
        val bodyTypeStr = prefs.getString("user_body_type", com.example.model.BodyType.MESOMORPH.name)
            ?: com.example.model.BodyType.MESOMORPH.name
        val bodyType = try { com.example.model.BodyType.valueOf(bodyTypeStr) } catch (e: Exception) { com.example.model.BodyType.MESOMORPH }
        val gymGoalStr = prefs.getString("user_gym_goal", com.example.model.GymTrainingGoal.BULK.name)
            ?: com.example.model.GymTrainingGoal.BULK.name
        val gymGoal = try { com.example.model.GymTrainingGoal.valueOf(gymGoalStr) } catch (e: Exception) { com.example.model.GymTrainingGoal.BULK }

        val targets = MifflinStJeor.calculateTargets(
            gender = gender,
            weightKg = weight,
            heightCm = height,
            age = age,
            activityLevel = activity,
            goal = goal
        )

        val cal = prefs.getInt("target_calories", targets.calories)
        val pro = prefs.getInt("target_protein", targets.protein)
        val carbs = prefs.getInt("target_carbs", targets.carbs)
        val fat = prefs.getInt("target_fat", targets.fat)

        return UserProfile(
            name = name,
            age = age,
            gender = gender,
            heightCm = height,
            weightKg = weight,
            targetWeightKg = targetWeight,
            activityLevel = activity,
            goal = goal,
            bodyType = bodyType,
            gymGoal = gymGoal,
            targetCalories = cal,
            targetProteinG = pro,
            targetCarbsG = carbs,
            targetFatG = fat,
            waterGoalGlasses = prefs.getInt("water_goal", 8),
            isProfileComplete = isComplete,
            reminderBreakfast = prefs.getBoolean("remind_breakfast", true),
            reminderLunch = prefs.getBoolean("remind_lunch", true),
            reminderDinner = prefs.getBoolean("remind_dinner", true),
            reminderHydration = prefs.getBoolean("remind_hydration", true)
        )
    }

    fun saveProfile(profile: UserProfile) {
        prefs.edit()
            .putBoolean("is_profile_complete", profile.isProfileComplete)
            .putString("user_name", profile.name)
            .putInt("user_age", profile.age)
            .putString("user_gender", profile.gender.name)
            .putFloat("user_height", profile.heightCm)
            .putFloat("user_weight", profile.weightKg)
            .putFloat("user_target_weight", profile.targetWeightKg)
            .putString("user_activity", profile.activityLevel.name)
            .putString("user_goal", profile.goal.name)
            .putString("user_body_type", profile.bodyType.name)
            .putString("user_gym_goal", profile.gymGoal.name)
            .putInt("target_calories", profile.targetCalories)
            .putInt("target_protein", profile.targetProteinG)
            .putInt("target_carbs", profile.targetCarbsG)
            .putInt("target_fat", profile.targetFatG)
            .putInt("water_goal", profile.waterGoalGlasses)
            .putBoolean("remind_breakfast", profile.reminderBreakfast)
            .putBoolean("remind_lunch", profile.reminderLunch)
            .putBoolean("remind_dinner", profile.reminderDinner)
            .putBoolean("remind_hydration", profile.reminderHydration)
            .apply()
        _userProfile.value = profile
    }

    // --- Water Tracking ---
    fun getWaterGlassesForDate(date: String): Int {
        return prefs.getInt("water_$date", 0)
    }

    fun setWaterGlassesForDate(date: String, count: Int) {
        prefs.edit().putInt("water_$date", count.coerceAtLeast(0)).apply()
    }

    // --- Streak Tracking ---
    fun getStreak(): Int {
        return prefs.getInt("current_streak", 1)
    }

    fun recordLogForStreak(todayDate: String): Int {
        val lastLoggedDate = prefs.getString("last_logged_date", "") ?: ""
        var streak = prefs.getInt("current_streak", 1)

        if (lastLoggedDate.isEmpty()) {
            streak = 1
            prefs.edit()
                .putString("last_logged_date", todayDate)
                .putInt("current_streak", streak)
                .apply()
            return streak
        }

        if (lastLoggedDate == todayDate) {
            return streak
        }

        // Check if last logged date was yesterday
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val lastDate = sdf.parse(lastLoggedDate)
            val calYesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }
            val yesterdayStr = sdf.format(calYesterday.time)

            streak = if (lastLoggedDate == yesterdayStr) {
                streak + 1
            } else {
                1
            }

            prefs.edit()
                .putString("last_logged_date", todayDate)
                .putInt("current_streak", streak)
                .apply()
        } catch (e: Exception) {
            streak = 1
        }
        return streak
    }
}
