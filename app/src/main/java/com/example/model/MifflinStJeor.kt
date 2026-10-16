package com.example.model

import kotlin.math.roundToInt

object MifflinStJeor {

    /**
     * Calculates Basal Metabolic Rate (BMR) using the Mifflin-St Jeor Equation:
     * Men: BMR = (10 × weight in kg) + (6.25 × height in cm) - (5 × age in years) + 5
     * Women: BMR = (10 × weight in kg) + (6.25 × height in cm) - (5 × age in years) - 161
     */
    fun calculateBmr(
        gender: Gender,
        weightKg: Float,
        heightCm: Float,
        age: Int
    ): Float {
        val base = (10f * weightKg) + (6.25f * heightCm) - (5f * age)
        return when (gender) {
            Gender.MALE -> base + 5f
            Gender.FEMALE -> base - 161f
            Gender.OTHER -> base - 78f // Neutral midpoint
        }
    }

    /**
     * Total Daily Energy Expenditure (TDEE) = BMR × Activity Factor
     */
    fun calculateTdee(bmr: Float, activityLevel: ActivityLevel): Float {
        return bmr * activityLevel.factor
    }

    /**
     * Complete Macro Breakdown based on Fitness Goal:
     * - Protein: 2.0g per kg of bodyweight (4 kcal/g)
     * - Fat: ~25-28% of total calories (9 kcal/g)
     * - Carbs: Remaining calories (4 kcal/g)
     */
    fun calculateTargets(
        gender: Gender,
        weightKg: Float,
        heightCm: Float,
        age: Int,
        activityLevel: ActivityLevel,
        goal: FitnessGoal
    ): MacroTargets {
        val bmr = calculateBmr(gender, weightKg, heightCm, age)
        val tdee = calculateTdee(bmr, activityLevel)
        val targetCalories = (tdee + goal.calorieDelta).coerceAtLeast(1200f).roundToInt()

        // Protein: Higher for muscle gain or deficit retention (2.0 - 2.2 g/kg)
        val proteinGrams = (weightKg * 2.0f).roundToInt().coerceIn(60, 260)
        val proteinCalories = proteinGrams * 4

        // Fat: ~25% of calories
        val fatCalories = (targetCalories * 0.25f).roundToInt()
        val fatGrams = (fatCalories / 9f).roundToInt().coerceIn(35, 120)

        // Carbs: Remainder of calorie budget
        val remainingCalories = (targetCalories - proteinCalories - (fatGrams * 9)).coerceAtLeast(200)
        val carbsGrams = (remainingCalories / 4f).roundToInt().coerceAtLeast(50)

        return MacroTargets(
            bmr = bmr.roundToInt(),
            tdee = tdee.roundToInt(),
            calories = targetCalories,
            protein = proteinGrams,
            carbs = carbsGrams,
            fat = fatGrams
        )
    }

    fun calculateBmi(weightKg: Float, heightCm: Float): Float {
        if (heightCm <= 0) return 0f
        val heightM = heightCm / 100f
        return (weightKg / (heightM * heightM) * 10f).roundToInt() / 10f
    }

    fun getBmiCategory(bmi: Float): String {
        return when {
            bmi < 18.5f -> "Underweight"
            bmi < 25f -> "Normal weight"
            bmi < 30f -> "Overweight"
            else -> "Obese"
        }
    }
}

data class MacroTargets(
    val bmr: Int,
    val tdee: Int,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int
)
