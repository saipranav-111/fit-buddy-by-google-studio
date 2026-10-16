package com.example.data

import com.example.model.BodyType
import com.example.model.GymExercise
import com.example.model.GymTrainingGoal
import com.example.model.MuscleGroup

object GymExerciseCatalog {

    val allGymExercises: List<GymExercise> = listOf(
        // === CHEST ===
        GymExercise(
            id = "bb_bench_press",
            name = "Barbell Flat Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            equipmentNeeded = "Barbell & Bench",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT),
            defaultSets = 4,
            defaultReps = "6-8",
            restSeconds = 120,
            executionCue = "Tuck elbows at 45°, squeeze shoulder blades into bench, touch mid-sternum with pause.",
            whyGoodForGoal = "King of upper body mass building; delivers massive mechanical tension for pectoral thickness."
        ),
        GymExercise(
            id = "db_incline_press",
            name = "Incline Dumbbell Press",
            muscleGroup = MuscleGroup.CHEST,
            equipmentNeeded = "Dumbbells & 30° Bench",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 4,
            defaultReps = "8-10",
            restSeconds = 90,
            executionCue = "Set bench to 30°, press dumbbells in a slight inward arc, feel clavicular chest stretch.",
            whyGoodForGoal = "Builds the upper 'shelf' of the chest and prevents muscular imbalances."
        ),
        GymExercise(
            id = "cable_crossover",
            name = "Cable Chest Flyes",
            muscleGroup = MuscleGroup.CHEST,
            equipmentNeeded = "Cable Crossover Tower",
            suitableGoals = listOf(GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 3,
            defaultReps = "12-15",
            restSeconds = 45,
            executionCue = "Slight bend in elbows, bring hands together like hugging a tree, squeeze inner chest.",
            whyGoodForGoal = "Continuous tension across the entire range of motion; high metabolic fatigue without joint stress."
        ),
        GymExercise(
            id = "chest_dips",
            name = "Weighted / Bodyweight Chest Dips",
            muscleGroup = MuscleGroup.CHEST,
            equipmentNeeded = "Dip Station",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT),
            defaultSets = 3,
            defaultReps = "8-12",
            restSeconds = 90,
            executionCue = "Lean torso forward 30°, flare elbows slightly, lower until shoulders are below elbows.",
            whyGoodForGoal = "Exceptional lower-chest and tricep hypertrophy builder."
        ),

        // === BACK ===
        GymExercise(
            id = "bb_bent_row",
            name = "Barbell Bent-Over Row",
            muscleGroup = MuscleGroup.BACK,
            equipmentNeeded = "Barbell",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT),
            defaultSets = 4,
            defaultReps = "6-8",
            restSeconds = 100,
            executionCue = "Hinge hips back at 45°, pull bar to lower belly button, drive elbows straight back.",
            whyGoodForGoal = "Constructs thick mid-back rhomboids, traps, and posterior lat foundation."
        ),
        GymExercise(
            id = "lat_pulldown",
            name = "Wide-Grip Lat Pulldown",
            muscleGroup = MuscleGroup.BACK,
            equipmentNeeded = "Cable Pulldown Machine",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 4,
            defaultReps = "10-12",
            restSeconds = 75,
            executionCue = "Lean back slightly (10°), pull bar down to collarbone, drive elbows down to hips.",
            whyGoodForGoal = "Crucial for developing the wide V-taper look and lat width."
        ),
        GymExercise(
            id = "cable_seated_row",
            name = "Seated Cable Row (Close Grip)",
            muscleGroup = MuscleGroup.BACK,
            equipmentNeeded = "Cable Row Station",
            suitableGoals = listOf(GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 3,
            defaultReps = "12-15",
            restSeconds = 60,
            executionCue = "Keep spine neutral, pull V-bar to navel, pause 1 full second at full squeeze.",
            whyGoodForGoal = "Excellent postural corrector and scapular retractor for lean athletic look."
        ),
        GymExercise(
            id = "cable_face_pull",
            name = "Cable Face Pulls",
            muscleGroup = MuscleGroup.BACK,
            equipmentNeeded = "Cable Tower & Rope",
            suitableGoals = listOf(GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS, GymTrainingGoal.BULK),
            defaultSets = 3,
            defaultReps = "15-20",
            restSeconds = 45,
            executionCue = "Set cable at eye level, pull rope handles past ears with knuckles back.",
            whyGoodForGoal = "Protects rotator cuff, improves rear delt capping, and unlocks 3D shoulder aesthetics."
        ),

        // === LEGS ===
        GymExercise(
            id = "bb_back_squat",
            name = "Barbell Back Squat",
            muscleGroup = MuscleGroup.LEGS,
            equipmentNeeded = "Squat Rack & Barbell",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT),
            defaultSets = 4,
            defaultReps = "6-8",
            restSeconds = 120,
            executionCue = "Feet shoulder-width, break at hips and knees simultaneously, descend below parallel, drive up.",
            whyGoodForGoal = "Ultimate lower-body anabolic primer; stimulates full quad, glute, and core recruitment."
        ),
        GymExercise(
            id = "romanian_deadlift",
            name = "Barbell Romanian Deadlift (RDL)",
            muscleGroup = MuscleGroup.LEGS,
            equipmentNeeded = "Barbell or Heavy DBs",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 4,
            defaultReps = "8-10",
            restSeconds = 90,
            executionCue = "Soft knee bend, push hips as far back as possible until deep hamstring stretch, thrust forward.",
            whyGoodForGoal = "Isolates hamstrings and glutes without lower-back fatigue; great for body recomposition."
        ),
        GymExercise(
            id = "bulgarian_split_squat",
            name = "Bulgarian Split Squat",
            muscleGroup = MuscleGroup.LEGS,
            equipmentNeeded = "Dumbbells & Bench",
            suitableGoals = listOf(GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 3,
            defaultReps = "10-12 each",
            restSeconds = 60,
            executionCue = "Rear foot on bench, descend vertically, keep 80% of weight on front heel.",
            whyGoodForGoal = "Eliminates side-to-side quad imbalances and spikes cardiovascular heart rate intensely."
        ),
        GymExercise(
            id = "db_walking_lunges",
            name = "Dumbbell Walking Lunges",
            muscleGroup = MuscleGroup.LEGS,
            equipmentNeeded = "Pair of Dumbbells",
            suitableGoals = listOf(GymTrainingGoal.WEIGHT_LOSS, GymTrainingGoal.FIT),
            defaultSets = 3,
            defaultReps = "16 steps",
            restSeconds = 45,
            executionCue = "Step forward with long stride, tap back knee lightly, push through front heel.",
            whyGoodForGoal = "High-calorie furnace movement that burns fat and tones legs simultaneously."
        ),
        GymExercise(
            id = "leg_press",
            name = "45° Leg Press",
            muscleGroup = MuscleGroup.LEGS,
            equipmentNeeded = "Leg Press Machine",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT),
            defaultSets = 4,
            defaultReps = "10-12",
            restSeconds = 90,
            executionCue = "Feet high and wide for glutes, lower and closer for quads. Do not lock knees at top.",
            whyGoodForGoal = "Allows heavy loading of quad fibers without spinal axial compression."
        ),

        // === SHOULDERS ===
        GymExercise(
            id = "oh_press",
            name = "Standing Overhead Military Press",
            muscleGroup = MuscleGroup.SHOULDERS,
            equipmentNeeded = "Barbell",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT),
            defaultSets = 4,
            defaultReps = "6-8",
            restSeconds = 100,
            executionCue = "Brace glutes and abs, press straight up moving head forward once bar clears forehead.",
            whyGoodForGoal = "Foundational boulder shoulder builder and vertical pushing power generator."
        ),
        GymExercise(
            id = "db_lateral_raise",
            name = "Dumbbell Lateral Raises",
            muscleGroup = MuscleGroup.SHOULDERS,
            equipmentNeeded = "Dumbbells",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 4,
            defaultReps = "12-15",
            restSeconds = 45,
            executionCue = "Lead with elbows, tilt pinkies slightly upward at parallel, controlled 2-second drop.",
            whyGoodForGoal = "Directly isolates side lateral deltoids for wide, tapered silhouette."
        ),
        GymExercise(
            id = "cable_rear_delt",
            name = "Cable Rear Delt Flyes",
            muscleGroup = MuscleGroup.SHOULDERS,
            equipmentNeeded = "Dual Cable Tower",
            suitableGoals = listOf(GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 3,
            defaultReps = "15",
            restSeconds = 45,
            executionCue = "Cross cables without handles, pull arms outwards in horizontal plane, squeeze rear delts.",
            whyGoodForGoal = "Balances shoulder joints and gives complete 3D rounded aesthetics."
        ),

        // === ARMS ===
        GymExercise(
            id = "bb_bicep_curl",
            name = "Standing Barbell / EZ-Bar Curl",
            muscleGroup = MuscleGroup.ARMS,
            equipmentNeeded = "Barbell or EZ-Bar",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT),
            defaultSets = 3,
            defaultReps = "8-10",
            restSeconds = 75,
            executionCue = "Elbows pinned to sides, curl bar up without swinging hips, full contraction at top.",
            whyGoodForGoal = "Maximum progressive overload for biceps peak and overall arm volume."
        ),
        GymExercise(
            id = "db_incline_curl",
            name = "Incline Dumbbell Bicep Curl",
            muscleGroup = MuscleGroup.ARMS,
            equipmentNeeded = "Dumbbells & 45° Bench",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT),
            defaultSets = 3,
            defaultReps = "10-12",
            restSeconds = 60,
            executionCue = "Lie back on 45° incline, let arms hang for deep stretch on long head, curl and supinate.",
            whyGoodForGoal = "Isolates the long head of biceps in a stretched position for taller biceps peak."
        ),
        GymExercise(
            id = "cable_tricep_pushdown",
            name = "Triceps Rope Pushdowns",
            muscleGroup = MuscleGroup.ARMS,
            equipmentNeeded = "Cable Tower & Rope",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 3,
            defaultReps = "12-15",
            restSeconds = 45,
            executionCue = "Keep upper arms glued to sides, push down and spread rope handles apart at bottom.",
            whyGoodForGoal = "Isolates the lateral head for horseshoe triceps definition."
        ),
        GymExercise(
            id = "skull_crushers",
            name = "Lying EZ-Bar Skull Crushers",
            muscleGroup = MuscleGroup.ARMS,
            equipmentNeeded = "EZ-Bar & Flat Bench",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT),
            defaultSets = 3,
            defaultReps = "8-10",
            restSeconds = 75,
            executionCue = "Angle upper arms slightly backwards, lower bar towards forehead/behind head, extend elbows.",
            whyGoodForGoal = "Massive stretch on the long head of the tricep which accounts for 60% of upper arm size."
        ),

        // === CORE & METABOLIC ===
        GymExercise(
            id = "hanging_leg_raise",
            name = "Hanging Knee / Leg Raises",
            muscleGroup = MuscleGroup.CORE,
            equipmentNeeded = "Pull-up Bar",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 3,
            defaultReps = "12-15",
            restSeconds = 45,
            executionCue = "Do not swing; curl pelvis up towards chest, pause 1 second at top, lower slowly.",
            whyGoodForGoal = "Targets lower abdominals and hip flexors for washboard core definition."
        ),
        GymExercise(
            id = "cable_woodchopper",
            name = "Cable Rotational Woodchoppers",
            muscleGroup = MuscleGroup.CORE,
            equipmentNeeded = "Cable Tower",
            suitableGoals = listOf(GymTrainingGoal.FIT, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 3,
            defaultReps = "15 each side",
            restSeconds = 45,
            executionCue = "Pivot rear foot, rotate torso with straight arms from high to low diagonal.",
            whyGoodForGoal = "Strengthens oblique slings, builds rotational torque, and carves waistline."
        ),
        GymExercise(
            id = "db_thrusters",
            name = "Dumbbell Thrusters (Squat to Press)",
            muscleGroup = MuscleGroup.FULL_BODY,
            equipmentNeeded = "Pair of Dumbbells",
            suitableGoals = listOf(GymTrainingGoal.WEIGHT_LOSS, GymTrainingGoal.FIT),
            defaultSets = 4,
            defaultReps = "12-15",
            restSeconds = 45,
            executionCue = "Hold dumbbells at shoulders, squat to parallel, explosively press overhead as you stand.",
            whyGoodForGoal = "Full-body metabolic inferno that taxes legs, shoulders, core, and cardiovascular endurance."
        ),
        GymExercise(
            id = "deadlift_conventional",
            name = "Barbell Conventional Deadlift",
            muscleGroup = MuscleGroup.FULL_BODY,
            equipmentNeeded = "Barbell & Plates",
            suitableGoals = listOf(GymTrainingGoal.BULK, GymTrainingGoal.WEIGHT_LOSS),
            defaultSets = 4,
            defaultReps = "5-6",
            restSeconds = 120,
            executionCue = "Feet hip-width, engage lats, push floor away through heels, lock out hips without over-arching.",
            whyGoodForGoal = "Recruits more total muscle fibers than almost any exercise; burns maximal calories."
        ),
        GymExercise(
            id = "renegade_row",
            name = "Dumbbell Push-Up into Renegade Row",
            muscleGroup = MuscleGroup.FULL_BODY,
            equipmentNeeded = "Hex Dumbbells",
            suitableGoals = listOf(GymTrainingGoal.WEIGHT_LOSS, GymTrainingGoal.FIT),
            defaultSets = 3,
            defaultReps = "10 each",
            restSeconds = 45,
            executionCue = "Plank on dumbbells, perform push-up, row one dumbbell up while resisting torso twist.",
            whyGoodForGoal = "Anti-rotation core stability combined with upper chest and back density."
        )
    )

    fun getFilteredGymExercises(
        goal: GymTrainingGoal,
        bodyType: BodyType,
        muscleGroup: MuscleGroup? = null
    ): List<GymExercise> {
        val list = allGymExercises.filter { ex ->
            val matchesGoal = ex.suitableGoals.contains(goal)
            val matchesMuscle = muscleGroup == null || ex.muscleGroup == muscleGroup
            matchesGoal && matchesMuscle
        }

        // If filtered is empty or small, fallback to matching muscle group
        if (list.size < 3 && muscleGroup != null) {
            return allGymExercises.filter { it.muscleGroup == muscleGroup }
        }
        return list
    }
}
