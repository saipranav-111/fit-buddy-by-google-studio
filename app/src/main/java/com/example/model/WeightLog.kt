package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_logs")
data class WeightLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weightKg: Float,
    val date: String, // Format: YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "saved_workouts")
data class SavedWorkoutPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val goal: String,
    val level: String,
    val equipment: String,
    val daysPerWeek: Int,
    val planDetailsJson: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class DailyNutritionSummary(
    val date: String,
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
    val targetCalories: Int = 2000,
    val targetProtein: Int = 150,
    val targetCarbs: Int = 200,
    val targetFat: Int = 60,
    val waterGlasses: Int = 0,
    val waterGoal: Int = 8
) {
    val remainingCalories: Int get() = targetCalories - totalCalories
    val calorieProgress: Float get() = if (targetCalories > 0) (totalCalories.toFloat() / targetCalories).coerceIn(0f, 1.5f) else 0f
    val proteinProgress: Float get() = if (targetProtein > 0) (totalProtein / targetProtein).coerceIn(0f, 1.5f) else 0f
    val carbsProgress: Float get() = if (targetCarbs > 0) (totalCarbs / targetCarbs).coerceIn(0f, 1.5f) else 0f
    val fatProgress: Float get() = if (targetFat > 0) (totalFat / targetFat).coerceIn(0f, 1.5f) else 0f
}
