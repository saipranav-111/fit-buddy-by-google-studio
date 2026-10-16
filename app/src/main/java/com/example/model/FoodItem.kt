package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DietType(val displayName: String, val badgeEmoji: String) {
    VEGAN("Vegan", "🌱"),
    VEGETARIAN("Vegetarian", "🥬"),
    NON_VEG("Non-Veg", "🥩")
}

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val caloriesPer100g: Int,
    val proteinPer100g: Float,
    val carbsPer100g: Float,
    val fatPer100g: Float,
    val defaultServingG: Int = 100,
    val servingUnitName: String = "serving",
    val dietType: DietType = DietType.VEGETARIAN,
    val isPopular: Boolean = false
) {
    fun calculateCalories(grams: Float): Int =
        ((caloriesPer100g * grams) / 100f).toInt()

    fun calculateProtein(grams: Float): Float =
        ((proteinPer100g * grams) / 100f)

    fun calculateCarbs(grams: Float): Float =
        ((carbsPer100g * grams) / 100f)

    fun calculateFat(grams: Float): Float =
        ((fatPer100g * grams) / 100f)
}
