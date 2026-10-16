package com.example.model

enum class Gender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other")
}

enum class ActivityLevel(
    val title: String,
    val description: String,
    val factor: Float
) {
    SEDENTARY("Sedentary", "Little or no exercise, desk job", 1.2f),
    LIGHTLY_ACTIVE("Lightly Active", "Exercise 1-3 days/week", 1.375f),
    MODERATELY_ACTIVE("Moderately Active", "Exercise 3-5 days/week", 1.55f),
    VERY_ACTIVE("Very Active", "Heavy exercise 6-7 days/week", 1.725f),
    SUPER_ACTIVE("Super Active", "Intense training or physical labor", 1.9f)
}

enum class FitnessGoal(
    val title: String,
    val description: String,
    val calorieDelta: Int
) {
    LOSE_WEIGHT("Fat Loss", "Calorie deficit for sustainable weight loss", -500),
    MAINTAIN("Maintain Weight", "Keep current weight and body composition", 0),
    GAIN_MUSCLE("Muscle Gain / Bulk", "Surplus to support hypertrophy & recovery", 350)
}

enum class BodyType(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val gymAdvice: String
) {
    ECTOMORPH(
        "Skinny / Ectomorph",
        "Naturally lean frame, fast metabolism, hard to gain weight",
        "⚡",
        "Focus on heavy compound lifts (6-8 reps), longer rest periods (2-3 min), and conserve energy by minimizing long cardio sessions."
    ),
    SKINNY_FAT(
        "Skinny-Fat / Soft",
        "Normal BMI with low muscle definition & softer midsection",
        "⚖️",
        "Prioritize progressive overload with moderate-heavy weights to stimulate muscle protein synthesis while keeping calories near maintenance."
    ),
    MESOMORPH(
        "Athletic / Mesomorph",
        "Naturally muscular, responds fast to strength & volume",
        "🏋️",
        "Thrives on high-intensity progressive overload, pyramid sets, and balanced volume cycles across upper and lower body."
    ),
    ENDOMORPH(
        "Solid / Endomorph",
        "Wider bone structure, gains weight and mass easily",
        "🛡️",
        "Benefit from high work density, supersets, shorter rest periods (45-60s), and combining heavy strength with metabolic finishers."
    )
}

enum class GymTrainingGoal(
    val title: String,
    val description: String,
    val emoji: String,
    val recommendedSets: Int,
    val repRange: String,
    val restInterval: String,
    val executionFocus: String
) {
    BULK(
        "Bulk / Muscle Mass",
        "Heavy compound movements and mechanical tension for maximum hypertrophy and size",
        "💪",
        4,
        "6 - 8 reps",
        "90 - 120 sec",
        "Heavy progressive overload, controlled 3s negatives, pause at peak stretch"
    ),
    FIT(
        "Fit / Lean & Toned",
        "Functional hypertrophy, core stability, athletic posture and balanced definition",
        "⚡",
        3,
        "10 - 12 reps",
        "60 - 75 sec",
        "Full range of motion, mind-muscle contraction, unilateral balance"
    ),
    WEIGHT_LOSS(
        "Weight Loss / Shred",
        "High work capacity, supersets and metabolic burn to accelerate calorie deficit",
        "🔥",
        4,
        "12 - 18 reps",
        "30 - 45 sec",
        "Continuous tension, short rest intervals, supersets & circuit finishers"
    )
}

data class UserProfile(
    val name: String = "Fitness Buddy",
    val age: Int = 26,
    val gender: Gender = Gender.MALE,
    val heightCm: Float = 175f,
    val weightKg: Float = 72f,
    val targetWeightKg: Float = 70f,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE,
    val goal: FitnessGoal = FitnessGoal.LOSE_WEIGHT,
    val bodyType: BodyType = BodyType.MESOMORPH,
    val gymGoal: GymTrainingGoal = GymTrainingGoal.BULK,
    val targetCalories: Int = 2100,
    val targetProteinG: Int = 150,
    val targetCarbsG: Int = 210,
    val targetFatG: Int = 60,
    val waterGoalGlasses: Int = 8,
    val isProfileComplete: Boolean = false,
    val reminderBreakfast: Boolean = true,
    val reminderLunch: Boolean = true,
    val reminderDinner: Boolean = true,
    val reminderHydration: Boolean = true
)
