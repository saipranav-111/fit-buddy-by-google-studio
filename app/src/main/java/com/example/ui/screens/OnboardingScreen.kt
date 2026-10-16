package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActivityLevel
import com.example.model.BodyType
import com.example.model.FitnessGoal
import com.example.model.Gender
import com.example.model.GymTrainingGoal
import com.example.model.MifflinStJeor
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatColor
import com.example.ui.theme.ProteinColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onComplete: (
        name: String,
        age: Int,
        gender: Gender,
        heightCm: Float,
        weightKg: Float,
        targetWeightKg: Float,
        activityLevel: ActivityLevel,
        goal: FitnessGoal,
        bodyType: BodyType,
        gymGoal: GymTrainingGoal
    ) -> Unit
) {
    val totalSteps = 5
    var step by remember { mutableIntStateOf(1) }

    var name by remember { mutableStateOf("") }
    var age by remember { mutableIntStateOf(26) }
    var gender by remember { mutableStateOf(Gender.MALE) }

    var heightCm by remember { mutableFloatStateOf(175f) }
    var weightKg by remember { mutableFloatStateOf(74f) }
    var targetWeightKg by remember { mutableFloatStateOf(70f) }

    var activityLevel by remember { mutableStateOf(ActivityLevel.MODERATELY_ACTIVE) }
    var bodyType by remember { mutableStateOf(BodyType.MESOMORPH) }
    var gymGoal by remember { mutableStateOf(GymTrainingGoal.BULK) }
    var goal by remember { mutableStateOf(FitnessGoal.LOSE_WEIGHT) }

    // Live targets calculated on-the-fly
    val targets = MifflinStJeor.calculateTargets(
        gender = gender,
        weightKg = weightKg,
        heightCm = heightCm,
        age = age,
        activityLevel = activityLevel,
        goal = goal
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (step > 1) {
                        IconButton(onClick = { step-- }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    Text(
                        text = "Step $step of $totalSteps",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.size(48.dp))
                }

                LinearProgressIndicator(
                    progress = { step / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EmeraldPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "OnboardingStep"
            ) { targetStep ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    when (targetStep) {
                        1 -> StepPersonalInfo(
                            name = name,
                            onNameChange = { name = it },
                            age = age,
                            onAgeChange = { age = it },
                            gender = gender,
                            onGenderChange = { gender = it }
                        )
                        2 -> StepMeasurements(
                            heightCm = heightCm,
                            onHeightChange = { heightCm = it },
                            weightKg = weightKg,
                            onWeightChange = { weightKg = it },
                            targetWeightKg = targetWeightKg,
                            onTargetWeightChange = { targetWeightKg = it }
                        )
                        3 -> StepActivityLevel(
                            selected = activityLevel,
                            onSelect = { activityLevel = it }
                        )
                        4 -> StepBodyTypeAndGymGoal(
                            selectedBodyType = bodyType,
                            onBodyTypeSelect = { bodyType = it },
                            selectedGymGoal = gymGoal,
                            onGymGoalSelect = {
                                gymGoal = it
                                // Align general nutrition goal if convenient
                                goal = when (it) {
                                    GymTrainingGoal.BULK -> FitnessGoal.GAIN_MUSCLE
                                    GymTrainingGoal.FIT -> FitnessGoal.MAINTAIN
                                    GymTrainingGoal.WEIGHT_LOSS -> FitnessGoal.LOSE_WEIGHT
                                }
                            }
                        )
                        5 -> StepGoalAndTargets(
                            selectedGoal = goal,
                            onGoalSelect = { goal = it },
                            targets = targets,
                            weightKg = weightKg,
                            heightCm = heightCm,
                            selectedBodyType = bodyType,
                            selectedGymGoal = gymGoal
                        )
                    }
                }
            }

            // Bottom Navigation Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Button(
                    onClick = {
                        if (step < totalSteps) {
                            step++
                        } else {
                            onComplete(
                                name.ifBlank { "Fitness Buddy" },
                                age,
                                gender,
                                heightCm,
                                weightKg,
                                targetWeightKg,
                                activityLevel,
                                goal,
                                bodyType,
                                gymGoal
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("onboarding_continue_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text(
                        text = if (step < totalSteps) "Continue" else "Get Started With Fit Buddy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (step < totalSteps) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun StepPersonalInfo(
    name: String,
    onNameChange: (String) -> Unit,
    age: Int,
    onAgeChange: (Int) -> Unit,
    gender: Gender,
    onGenderChange: (Gender) -> Unit
) {
    Column {
        Text(
            text = "Welcome to Fit Buddy! 👋",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Let's calibrate your metabolic profile with the Mifflin-St Jeor formula for pinpoint nutritional accuracy.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("What's your name?") },
            placeholder = { Text("e.g. Alex") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("name_input"),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = if (age > 0) age.toString() else "",
            onValueChange = { onAgeChange(it.toIntOrNull() ?: 0) },
            label = { Text("Age (years)") },
            placeholder = { Text("e.g. 26") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("age_input"),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Biological Sex",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Used for hormonal baseline BMR calculations.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Gender.entries.forEach { g ->
                FilterChip(
                    selected = gender == g,
                    onClick = { onGenderChange(g) },
                    label = { Text(g.displayName) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = EmeraldPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun StepMeasurements(
    heightCm: Float,
    onHeightChange: (Float) -> Unit,
    weightKg: Float,
    onWeightChange: (Float) -> Unit,
    targetWeightKg: Float,
    onTargetWeightChange: (Float) -> Unit
) {
    Column {
        Text(
            text = "Your Body Stats 📏",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Enter your current metrics and your target milestone.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = if (heightCm > 0) heightCm.toInt().toString() else "",
            onValueChange = { onHeightChange(it.toFloatOrNull() ?: 0f) },
            label = { Text("Height (cm)") },
            placeholder = { Text("e.g. 175") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("height_input"),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = if (weightKg > 0) String.format("%.1f", weightKg) else "",
            onValueChange = { onWeightChange(it.toFloatOrNull() ?: 0f) },
            label = { Text("Current Weight (kg)") },
            placeholder = { Text("e.g. 74.0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weight_input"),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = if (targetWeightKg > 0) String.format("%.1f", targetWeightKg) else "",
            onValueChange = { onTargetWeightChange(it.toFloatOrNull() ?: 0f) },
            label = { Text("Goal Weight (kg)") },
            placeholder = { Text("e.g. 70.0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("target_weight_input"),
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@Composable
private fun StepActivityLevel(
    selected: ActivityLevel,
    onSelect: (ActivityLevel) -> Unit
) {
    Column {
        Text(
            text = "Activity Level 🏃",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "This determines your Total Daily Energy Expenditure (TDEE).",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 18.dp)
        )

        ActivityLevel.entries.forEach { level ->
            val isSelected = selected == level
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelect(level) }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = level.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = level.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "x${level.factor}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun StepBodyTypeAndGymGoal(
    selectedBodyType: BodyType,
    onBodyTypeSelect: (BodyType) -> Unit,
    selectedGymGoal: GymTrainingGoal,
    onGymGoalSelect: (GymTrainingGoal) -> Unit
) {
    Column {
        Text(
            text = "Body Type & Gym Goal 🏋️",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tell us about your present build and your in-gym objective so we calibrate your lifts and rep ranges.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 14.dp)
        )

        Text(
            text = "Present Type of Body:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        BodyType.entries.forEach { bType ->
            val isSelected = selectedBodyType == bType
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onBodyTypeSelect(bType) }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = bType.emoji, fontSize = 22.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bType.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = bType.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "In-Gym Objective:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        GymTrainingGoal.entries.forEach { gGoal ->
            val isSelected = selectedGymGoal == gGoal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onGymGoalSelect(gGoal) }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = gGoal.emoji, fontSize = 18.sp)
                            Text(
                                text = gGoal.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${gGoal.recommendedSets} sets x ${gGoal.repRange}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                    Text(
                        text = gGoal.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StepGoalAndTargets(
    selectedGoal: FitnessGoal,
    onGoalSelect: (FitnessGoal) -> Unit,
    targets: com.example.model.MacroTargets,
    weightKg: Float,
    heightCm: Float,
    selectedBodyType: BodyType,
    selectedGymGoal: GymTrainingGoal
) {
    val bmi = MifflinStJeor.calculateBmi(weightKg, heightCm)
    val bmiCat = MifflinStJeor.getBmiCategory(bmi)

    Column {
        Text(
            text = "Personalized Blueprint 🎯",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Calibrated for ${selectedBodyType.title} to ${selectedGymGoal.title}.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 14.dp)
        )

        // Goal Cards
        FitnessGoal.entries.forEach { g ->
            val isSelected = selectedGoal == g
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onGoalSelect(g) }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = g.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = g.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (g.calorieDelta >= 0) "+${g.calorieDelta} kcal" else "${g.calorieDelta} kcal",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calculated Blueprint Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Metabolic Blueprint",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "BMI $bmi ($bmiCat)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Basal Metabolic Rate (BMR):", style = MaterialTheme.typography.bodySmall)
                    Text(text = "${targets.bmr} kcal", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Maintenance (TDEE):", style = MaterialTheme.typography.bodySmall)
                    Text(text = "${targets.tdee} kcal", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Target", style = MaterialTheme.typography.labelSmall)
                            Text(text = "${targets.calories} kcal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Protein", style = MaterialTheme.typography.labelSmall)
                            Text(text = "${targets.protein}g", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ProteinColor)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Carbs", style = MaterialTheme.typography.labelSmall)
                            Text(text = "${targets.carbs}g", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CarbsColor)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Fat", style = MaterialTheme.typography.labelSmall)
                            Text(text = "${targets.fat}g", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FatColor)
                        }
                    }
                }
            }
        }
    }
}
