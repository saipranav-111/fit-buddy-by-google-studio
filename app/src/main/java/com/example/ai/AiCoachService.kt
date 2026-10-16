package com.example.ai

import com.example.BuildConfig
import com.example.model.DailyNutritionSummary
import com.example.model.FitnessGoal
import com.example.model.MealLog
import com.example.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER,
    COACH
}

data class GeneratedWorkoutRoutine(
    val title: String,
    val summary: String,
    val days: List<WorkoutDay>
)

data class WorkoutDay(
    val dayName: String,
    val focus: String,
    val exercises: List<ExerciseItem>
)

data class ExerciseItem(
    val name: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val coachingTip: String
)

class AiCoachService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun askCoach(
        userMessage: String,
        userProfile: UserProfile,
        summary: DailyNutritionSummary,
        todayLogs: List<MealLog>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val contextPrompt = buildContextPrompt(userMessage, userProfile, summary, todayLogs)

        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalCoachResponse(userMessage, userProfile, summary)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", contextPrompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val err = response.body?.string() ?: ""
                    return@withContext generateLocalCoachResponse(userMessage, userProfile, summary)
                }

                val responseStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseStr)
                val candidates = responseJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")

                if (!text.isNullOrBlank()) {
                    text.trim()
                } else {
                    generateLocalCoachResponse(userMessage, userProfile, summary)
                }
            }
        } catch (e: Exception) {
            generateLocalCoachResponse(userMessage, userProfile, summary)
        }
    }

    private fun buildContextPrompt(
        userMessage: String,
        profile: UserProfile,
        summary: DailyNutritionSummary,
        logs: List<MealLog>
    ): String {
        val mealsSummary = if (logs.isEmpty()) {
            "No meals logged yet today."
        } else {
            logs.joinToString("; ") { "${it.foodName} (${it.calories} kcal, ${it.protein}g P)" }
        }

        return """
            You are Fit Buddy AI, an encouraging, certified fitness and in-gym nutrition coach.
            User Profile:
            - Name: ${profile.name}
            - Nutrition Goal: ${profile.goal.title}
            - Present Body Type: ${profile.bodyType.title} (${profile.bodyType.subtitle})
            - In-Gym Goal: ${profile.gymGoal.title} (${profile.gymGoal.description})
            - Training Style: ${profile.gymGoal.repRange}, ${profile.gymGoal.restInterval} (${profile.gymGoal.executionFocus})
            - Weight: ${profile.weightKg} kg, Target: ${profile.targetWeightKg} kg
            - Daily Target: ${profile.targetCalories} kcal | ${profile.targetProteinG}g P | ${profile.targetCarbsG}g C | ${profile.targetFatG}g F
            - Today's Consumed: ${summary.totalCalories} kcal (${summary.remainingCalories} remaining) | ${summary.totalProtein}g P | ${summary.totalCarbs}g C | ${summary.totalFat}g F
            - Water: ${summary.waterGlasses} / ${summary.waterGoal} glasses
            - Today's Logged Foods: $mealsSummary

            User Question: "$userMessage"

            Provide concise, highly actionable, friendly, and scientifically sound advice tailored directly to their body type, in-gym training objective (bulk, fit, or weight loss), daily targets, and current nutrition. Keep response within 2-3 concise paragraphs or bullet points.
        """.trimIndent()
    }

    suspend fun generateWorkoutRoutine(
        goal: String,
        level: String,
        equipment: String,
        daysPerWeek: Int,
        profile: UserProfile
    ): GeneratedWorkoutRoutine = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val prompt = """
            Create a structured $daysPerWeek-day in-gym workout plan for a user with:
            - Present Body Type: ${profile.bodyType.title} (${profile.bodyType.subtitle})
            - In-Gym Goal: ${profile.gymGoal.title} (${profile.gymGoal.description})
            - Training Routine Target: $goal
            - Experience Level: $level
            - Available Equipment: $equipment
            - Body Weight: ${profile.weightKg}kg
            - Recommended Sets/Reps: ${profile.gymGoal.recommendedSets} sets x ${profile.gymGoal.repRange} (${profile.gymGoal.restInterval})

            Provide exercises with sets, reps, rest intervals, and coaching cues specifically tailored to their body type and whether they are bulking, getting fit, or losing weight in the gym.
        """.trimIndent()

        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getStructuredRoutineTemplate(goal, level, equipment, daysPerWeek, profile)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val systemInstruction = "You are a professional strength and conditioning coach. Provide realistic, balanced exercises."

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext getStructuredRoutineTemplate(goal, level, equipment, daysPerWeek, profile)
                }
                val responseStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseStr)
                val candidates = responseJson.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)?.optJSONObject("content")
                    ?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")

                if (text.isNullOrBlank()) {
                    getStructuredRoutineTemplate(goal, level, equipment, daysPerWeek, profile)
                } else {
                    // Parse text or fallback cleanly
                    getStructuredRoutineTemplate(goal, level, equipment, daysPerWeek, profile, text.take(150))
                }
            }
        } catch (e: Exception) {
            getStructuredRoutineTemplate(goal, level, equipment, daysPerWeek, profile)
        }
    }

    private fun generateLocalCoachResponse(
        query: String,
        profile: UserProfile,
        summary: DailyNutritionSummary
    ): String {
        val lower = query.lowercase()
        return when {
            "protein" in lower || "dinner" in lower -> {
                val remainingP = (profile.targetProteinG - summary.totalProtein).coerceAtLeast(0f)
                val remainingCal = summary.remainingCalories
                "To hit your target today, you have about ${remainingP.toInt()}g of protein and $remainingCal kcal left! 🎯\n\n" +
                "Top recommendations for high-protein, calorie-conscious dinner:\n" +
                "• Grilled Chicken Breast (150g) with Steamed Broccoli: ~280 kcal, 46g Protein\n" +
                "• Pan-seared Tofu or Paneer with Quinoa and spinach: ~320 kcal, 24g Protein\n" +
                "• 1 Scoop Whey/Plant Protein with 150g Greek Yogurt: ~210 kcal, 38g Protein."
            }
            "snack" in lower || "hungry" in lower -> {
                "Here are great quick snacks that won't blow your macro budget:\n" +
                "• Apple slices with 1 tbsp Natural Peanut Butter (~160 kcal, 4g Protein)\n" +
                "• Low-fat Greek Yogurt bowl with fresh blueberries (~120 kcal, 17g Protein)\n" +
                "• 2 Hard Boiled Eggs with a pinch of sea salt and paprika (~140 kcal, 13g Protein)\n" +
                "• Roasted chickpeas or edamame cup (~140 kcal, 9g Protein)."
            }
            "weight" in lower || "deficit" in lower || "cut" in lower -> {
                "For your goal of ${profile.goal.title}, consistency is key! Your target is ${profile.targetCalories} kcal/day, which creates a healthy ~500 kcal deficit. Make sure to prioritize protein (${profile.targetProteinG}g) to spare lean muscle mass while losing body fat."
            }
            "water" in lower || "hydration" in lower -> {
                "Hydration plays a massive role in metabolic rate and appetite regulation! You're currently at ${summary.waterGlasses} of ${summary.waterGoal} glasses today. Try drinking a tall glass before your next meal."
            }
            else -> {
                "Great question! With your goal of ${profile.goal.title}, you're currently at ${summary.totalCalories} / ${profile.targetCalories} kcal today with ${summary.totalProtein.toInt()}g / ${profile.targetProteinG}g protein.\n\n" +
                "Focus on whole food sources, hitting your 2.0g/kg protein threshold, and getting 7-8 hours of restful sleep for optimal muscle recovery and energy levels."
            }
        }
    }

    private fun getStructuredRoutineTemplate(
        goal: String,
        level: String,
        equipment: String,
        daysPerWeek: Int,
        profile: UserProfile? = null,
        customSummary: String? = null
    ): GeneratedWorkoutRoutine {
        val gymGoal = profile?.gymGoal ?: com.example.model.GymTrainingGoal.BULK
        val bodyType = profile?.bodyType ?: com.example.model.BodyType.MESOMORPH

        val targetSets = gymGoal.recommendedSets
        val targetReps = gymGoal.repRange
        val restTime = when (gymGoal) {
            com.example.model.GymTrainingGoal.BULK -> 120
            com.example.model.GymTrainingGoal.FIT -> 75
            com.example.model.GymTrainingGoal.WEIGHT_LOSS -> 45
        }

        val days = when (daysPerWeek) {
            3 -> listOf(
                WorkoutDay(
                    dayName = "Day 1 - Push Focus (${gymGoal.title})",
                    focus = "Chest, Shoulders & Triceps",
                    exercises = listOf(
                        ExerciseItem("Barbell Flat Bench Press", targetSets, targetReps, restTime, "Tuck elbows at 45°, squeeze shoulder blades into bench, touch mid-chest with control."),
                        ExerciseItem("Standing Overhead Barbell Press", targetSets, targetReps, restTime, "Brace core tight, do not hyperextend lower back, press vertically."),
                        ExerciseItem("Incline Dumbbell Chest Flyes", targetSets - 1, "10-12", restTime - 15, "Feel a deep stretch in the upper clavicular pectorals."),
                        ExerciseItem("Triceps Rope Pushdowns", targetSets - 1, "12-15", 45, "Pin elbows at sides, flare rope outward at the bottom contraction.")
                    )
                ),
                WorkoutDay(
                    dayName = "Day 2 - Pull Focus (${gymGoal.title})",
                    focus = "Back, Rear Delts & Biceps",
                    exercises = listOf(
                        ExerciseItem("Wide-Grip Lat Pulldowns", targetSets, targetReps, restTime, "Drive elbows straight down to your ribcage for V-taper lats."),
                        ExerciseItem("Barbell Bent-Over Row", targetSets, targetReps, restTime, "Hinge at hips 45°, pull bar into navel, squeeze rhomboids."),
                        ExerciseItem("Cable Face Pulls", targetSets - 1, "15-20", 45, "Pull rope towards bridge of nose with high elbows for 3D rear delts."),
                        ExerciseItem("Incline Dumbbell Bicep Curls", targetSets - 1, targetReps, 60, "Full stretch at bottom, strict curl without swinging.")
                    )
                ),
                WorkoutDay(
                    dayName = "Day 3 - Legs & Core (${gymGoal.title})",
                    focus = "Quads, Hamstrings & Abs",
                    exercises = listOf(
                        ExerciseItem("Barbell Back Squats", targetSets, targetReps, restTime, "Feet shoulder-width, break at hips and knees, drive through midfoot."),
                        ExerciseItem("Barbell Romanian Deadlift (RDL)", targetSets, targetReps, restTime, "Hinge hips back, maintain neutral spine, feel deep hamstring load."),
                        ExerciseItem("Walking Dumbbell Lunges", targetSets - 1, "12 each leg", 45, "Upright chest, push through lead heel, continuous tension."),
                        ExerciseItem("Hanging Knee / Leg Raises", targetSets - 1, "15", 45, "Curl pelvis upward, control eccentric descent.")
                    )
                )
            )
            4 -> listOf(
                WorkoutDay(
                    dayName = "Day 1 - Upper Body A",
                    focus = "Chest, Back & Arms",
                    exercises = listOf(
                        ExerciseItem("Dumbbell Incline Bench Press", 4, "8-10", 90, "Controlled 3-second negative"),
                        ExerciseItem("Chest-Supported Rows", 4, "10-12", 75, "Upper back thickness"),
                        ExerciseItem("Lateral Raises", 4, "15", 45, "Slight forward lean"),
                        ExerciseItem("Hammer Curls", 3, "12", 60, "Target brachialis and forearm")
                    )
                ),
                WorkoutDay(
                    dayName = "Day 2 - Lower Body A",
                    focus = "Quads & Calves",
                    exercises = listOf(
                        ExerciseItem("Back or Front Squats", 4, "8-10", 120, "Deep parallel depth"),
                        ExerciseItem("Leg Press", 3, "12-15", 90, "Continuous tension on quads"),
                        ExerciseItem("Leg Extensions", 3, "15", 60, "Hold 1s peak squeeze"),
                        ExerciseItem("Standing Calf Raises", 4, "15-20", 45, "Full ankle range of motion")
                    )
                ),
                WorkoutDay(
                    dayName = "Day 3 - Upper Body B",
                    focus = "Shoulders & Back",
                    exercises = listOf(
                        ExerciseItem("Standing Overhead Press", 4, "8-10", 90, "Vertical pressing power"),
                        ExerciseItem("Neutral Grip Pull-ups / Pulldowns", 4, "10-12", 75, "V-taper lat width"),
                        ExerciseItem("Cable Crossovers", 3, "12-15", 60, "Inner chest burn"),
                        ExerciseItem("Overhead Tricep Extensions", 3, "12", 60, "Long head tricep stretch")
                    )
                ),
                WorkoutDay(
                    dayName = "Day 4 - Lower Body B",
                    focus = "Hamstrings, Glutes & Abs",
                    exercises = listOf(
                        ExerciseItem("Barbell Romanian Deadlifts", 4, "8-10", 120, "Posterior chain builder"),
                        ExerciseItem("Bulgarian Split Squats", 3, "10 each leg", 90, "Balance and unilateral quad/glute strength"),
                        ExerciseItem("Seated Hamstring Curls", 3, "12-15", 60, "Slow eccentric"),
                        ExerciseItem("Cable Woodchoppers", 3, "15 each side", 45, "Rotational core power")
                    )
                )
            )
            else -> listOf(
                WorkoutDay(
                    dayName = "Day 1 - Chest & Triceps",
                    focus = "Push Primary",
                    exercises = listOf(
                        ExerciseItem("Barbell Bench Press", 4, "6-8", 120, "Explosive concentric press"),
                        ExerciseItem("Incline Dumbbell Press", 3, "8-10", 90, "Upper clavicular head"),
                        ExerciseItem("Dips (Weighted or Assisted)", 3, "10-12", 75, "Slight forward torso lean"),
                        ExerciseItem("Skullcrushers", 3, "12", 60, "Strict elbow positioning")
                    )
                ),
                WorkoutDay(
                    dayName = "Day 2 - Back & Biceps",
                    focus = "Pull Primary",
                    exercises = listOf(
                        ExerciseItem("Deadlifts or Barbell Rows", 4, "6-8", 120, "Total back density"),
                        ExerciseItem("Weighted Pull-ups", 3, "8-10", 90, "Full chin over bar"),
                        ExerciseItem("Close Grip Cable Row", 3, "10-12", 60, "Mid-back rhomboids"),
                        ExerciseItem("Barbell Bicep Curls", 3, "10-12", 60, "Strict form, no swinging")
                    )
                ),
                WorkoutDay(
                    dayName = "Day 3 - Legs & Calves",
                    focus = "Lower Primary",
                    exercises = listOf(
                        ExerciseItem("Barbell Squats", 4, "6-8", 120, "Drive hips forward"),
                        ExerciseItem("Lying Hamstring Curls", 3, "10-12", 60, "Keep hips pressed to pad"),
                        ExerciseItem("Leg Press", 3, "12-15", 90, "Quad endurance"),
                        ExerciseItem("Calf Raises", 4, "15", 45, "Pause at stretch")
                    )
                ),
                WorkoutDay(
                    dayName = "Day 4 - Shoulders & Abs",
                    focus = "Delts & Core",
                    exercises = listOf(
                        ExerciseItem("Seated Dumbbell Shoulder Press", 4, "8-10", 90, "Full range of motion"),
                        ExerciseItem("Dumbbell Lateral Raises", 4, "12-15", 45, "Side delt cap"),
                        ExerciseItem("Rear Delt Flyes", 3, "15", 45, "Posture and shoulder health"),
                        ExerciseItem("Ab Wheel Rollouts", 3, "12", 60, "Anti-extension core strength")
                    )
                ),
                WorkoutDay(
                    dayName = "Day 5 - Full Body Conditioning / Arms",
                    focus = "Accessories & Conditioning",
                    exercises = listOf(
                        ExerciseItem("Kettlebell Swings", 4, "20", 60, "Hip hinge explosion"),
                        ExerciseItem("Cable Bicep / Tricep Superset", 3, "12 each", 60, "Arm pump and endurance"),
                        ExerciseItem("Farmer's Carry", 3, "40 meters", 60, "Grip and trap endurance"),
                        ExerciseItem("Plank to Push-up", 3, "12", 45, "Core stability")
                    )
                )
            )
        }

        return GeneratedWorkoutRoutine(
            title = "$daysPerWeek-Day $goal Plan ($level)",
            summary = customSummary ?: "Customized $goal routine tailored for $equipment. Follow the rest intervals and progressive overload principles to maximize strength and body recomposition.",
            days = days
        )
    }
}
