package com.example

import com.example.model.ActivityLevel
import com.example.model.FitnessGoal
import com.example.model.Gender
import com.example.model.MifflinStJeor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testMifflinStJeorBmrCalculation() {
        // Male: 10 * 75kg + 6.25 * 180cm - 5 * 25 + 5 = 750 + 1125 - 125 + 5 = 1755
        val bmrMale = MifflinStJeor.calculateBmr(
            gender = Gender.MALE,
            weightKg = 75f,
            heightCm = 180f,
            age = 25
        )
        assertEquals(1755f, bmrMale, 0.5f)

        // Female: 10 * 60kg + 6.25 * 165cm - 5 * 30 - 161 = 600 + 1031.25 - 150 - 161 = 1320.25
        val bmrFemale = MifflinStJeor.calculateBmr(
            gender = Gender.FEMALE,
            weightKg = 60f,
            heightCm = 165f,
            age = 30
        )
        assertEquals(1320.25f, bmrFemale, 0.5f)
    }

    @Test
    fun testMacroTargetsGeneration() {
        val targets = MifflinStJeor.calculateTargets(
            gender = Gender.MALE,
            weightKg = 75f,
            heightCm = 180f,
            age = 25,
            activityLevel = ActivityLevel.MODERATELY_ACTIVE,
            goal = FitnessGoal.LOSE_WEIGHT
        )

        assertTrue(targets.calories > 1500)
        assertEquals(150, targets.protein) // 75kg * 2.0 = 150g
        assertTrue(targets.fat > 40)
        assertTrue(targets.carbs > 100)
    }

    @Test
    fun testBmiCalculation() {
        val bmi = MifflinStJeor.calculateBmi(70f, 175f)
        assertEquals(22.9f, bmi, 0.1f)
        assertEquals("Normal weight", MifflinStJeor.getBmiCategory(bmi))
    }

    @Test
    fun testGymExercisesFiltering() {
        val bulkChest = com.example.data.GymExerciseCatalog.getFilteredGymExercises(
            goal = com.example.model.GymTrainingGoal.BULK,
            bodyType = com.example.model.BodyType.MESOMORPH,
            muscleGroup = com.example.model.MuscleGroup.CHEST
        )
        assertTrue(bulkChest.isNotEmpty())
        assertTrue(bulkChest.any { it.name.contains("Bench Press") })

        val shredLegs = com.example.data.GymExerciseCatalog.getFilteredGymExercises(
            goal = com.example.model.GymTrainingGoal.WEIGHT_LOSS,
            bodyType = com.example.model.BodyType.ENDOMORPH,
            muscleGroup = com.example.model.MuscleGroup.LEGS
        )
        assertTrue(shredLegs.isNotEmpty())
    }
}
