package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

enum class MealType(val displayName: String, val emoji: String) {
    BREAKFAST("Breakfast", "🍳"),
    LUNCH("Lunch", "🥗"),
    DINNER("Dinner", "🍲"),
    SNACKS("Snacks", "🍎");

    companion object {
        fun currentForHour(calendar: Calendar = Calendar.getInstance()): MealType {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 4..10 -> BREAKFAST
                in 11..15 -> LUNCH
                in 16..18 -> SNACKS
                in 19..22 -> DINNER
                else -> SNACKS
            }
        }
    }
}

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val foodItemId: Long = 0,
    val foodName: String,
    val mealType: MealType,
    val amountGrams: Float,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val date: String, // Format: YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)
