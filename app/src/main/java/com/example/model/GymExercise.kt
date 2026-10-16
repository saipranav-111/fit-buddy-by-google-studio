package com.example.model

enum class MuscleGroup(val displayName: String, val emoji: String) {
    CHEST("Chest", "🛡️"),
    BACK("Back & Lats", "🦅"),
    LEGS("Legs & Glutes", "🦵"),
    SHOULDERS("Shoulders & Delts", "🏹"),
    ARMS("Arms (Biceps & Triceps)", "💪"),
    CORE("Core & Abs", "🧱"),
    FULL_BODY("Full Body Metabolic", "⚡")
}

data class GymExercise(
    val id: String,
    val name: String,
    val muscleGroup: MuscleGroup,
    val equipmentNeeded: String, // "Barbell", "Dumbbells", "Cables", "Machine", "Bodyweight"
    val suitableGoals: List<GymTrainingGoal>,
    val defaultSets: Int,
    val defaultReps: String,
    val restSeconds: Int,
    val executionCue: String,
    val whyGoodForGoal: String
)

data class ActiveGymExerciseItem(
    val exercise: GymExercise,
    val targetSets: Int,
    val targetReps: String,
    val completedSets: Int = 0,
    val completedSetChecklist: List<Boolean> = List(targetSets) { false }
)
