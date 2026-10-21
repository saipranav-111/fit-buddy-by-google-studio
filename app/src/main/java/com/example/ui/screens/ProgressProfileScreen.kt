package com.example.ui.screens

import androidx.compose.foundation.background
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.model.FitnessGoal
import com.example.model.Gender
import com.example.model.MifflinStJeor
import com.example.model.UserProfile
import com.example.model.WeightLog
import com.example.ui.theme.EmeraldPrimary

data class AchievementBadge(
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean
)

@Composable
fun ProgressProfileScreen(
    userProfile: UserProfile,
    weightLogs: List<WeightLog>,
    streakDays: Int,
    onLogWeight: (weightKg: Float, note: String) -> Unit,
    onDeleteWeight: (Long) -> Unit,
    onUpdateProfile: (UserProfile) -> Unit,
    onSignOut: () -> Unit = {}
) {
    var showWeightDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    val currentWeight = weightLogs.lastOrNull()?.weightKg ?: userProfile.weightKg
    val targetWeight = userProfile.targetWeightKg
    val deltaWeight = (currentWeight - targetWeight)

    val bmi = MifflinStJeor.calculateBmi(currentWeight, userProfile.heightCm)
    val bmiCategory = MifflinStJeor.getBmiCategory(bmi)

    val badges = listOf(
        AchievementBadge("First Step", "Logged first meal in Fit Buddy", "🌱", isUnlocked = true),
        AchievementBadge("Streak Starter", "Achieved a 3-day logging streak", "🔥", isUnlocked = streakDays >= 3),
        AchievementBadge("7-Day Master", "Logged consistently for a full week", "⚡", isUnlocked = streakDays >= 7),
        AchievementBadge("Protein Champ", "Hit daily protein target", "🥩", isUnlocked = true),
        AchievementBadge("Hydration Hero", "Drank 8 full glasses of water", "💧", isUnlocked = true),
        AchievementBadge("Goal Crusher", "Made progress toward target weight", "🎯", isUnlocked = true)
    )

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🏃‍♂️", fontSize = 26.sp)
                            }
                            Column {
                                Text(
                                    text = userProfile.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${userProfile.goal.title} • ${userProfile.activityLevel.title}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Button(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(text = "Edit", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Daily Target Summary Row
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Target Calories", style = MaterialTheme.typography.labelSmall)
                                Text(text = "${userProfile.targetCalories} kcal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Protein", style = MaterialTheme.typography.labelSmall)
                                Text(text = "${userProfile.targetProteinG}g", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Carbs", style = MaterialTheme.typography.labelSmall)
                                Text(text = "${userProfile.targetCarbsG}g", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Fat", style = MaterialTheme.typography.labelSmall)
                                Text(text = "${userProfile.targetFatG}g", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Weight & BMI Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Scale, contentDescription = null, tint = EmeraldPrimary)
                            Text(text = "Weight Tracker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showWeightDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Log Weight", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Current Weight", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = String.format("%.1f kg", currentWeight), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Goal Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = String.format("%.1f kg", targetWeight), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (deltaWeight > 0) "${String.format("%.1f", deltaWeight)} kg to lose" else if (deltaWeight < 0) "${String.format("%.1f", -deltaWeight)} kg to gain" else "Goal reached!",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "BMI: $bmi ($bmiCategory)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Weight Logs History List
                    if (weightLogs.isNotEmpty()) {
                        Text(text = "Recent Logs", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        weightLogs.takeLast(5).reversed().forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "${log.weightKg} kg", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text(text = "${log.date} ${if (log.note.isNotBlank()) "• ${log.note}" else ""}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { onDeleteWeight(log.id) }, modifier = Modifier.size(32.dp)) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Achievement Badges Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFF59E0B))
                        Text(text = "Achievements & Streaks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        badges.chunked(2).forEach { rowBadges ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowBadges.forEach { badge ->
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (badge.isUnlocked) EmeraldPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(text = badge.icon, fontSize = 24.sp)
                                            Column {
                                                Text(text = badge.title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                                Text(text = if (badge.isUnlocked) "Unlocked ✨" else "Locked", style = MaterialTheme.typography.labelSmall, color = if (badge.isUnlocked) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Reminders Preferences Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = EmeraldPrimary)
                        Text(text = "Meal & Hydration Reminders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ReminderToggleRow(
                        title = "Breakfast Reminder (8:30 AM)",
                        checked = userProfile.reminderBreakfast,
                        onCheckedChange = { onUpdateProfile(userProfile.copy(reminderBreakfast = it)) }
                    )
                    ReminderToggleRow(
                        title = "Lunch Reminder (1:00 PM)",
                        checked = userProfile.reminderLunch,
                        onCheckedChange = { onUpdateProfile(userProfile.copy(reminderLunch = it)) }
                    )
                    ReminderToggleRow(
                        title = "Dinner Reminder (7:30 PM)",
                        checked = userProfile.reminderDinner,
                        onCheckedChange = { onUpdateProfile(userProfile.copy(reminderDinner = it)) }
                    )
                    ReminderToggleRow(
                        title = "Hydration Interval Pings",
                        checked = userProfile.reminderHydration,
                        onCheckedChange = { onUpdateProfile(userProfile.copy(reminderHydration = it)) }
                    )
                }
            }

            // Export & Data Management Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Data & Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    val currentUser = Firebase.auth.currentUser
                    if (currentUser != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Google Account (Firebase)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = currentUser.email ?: currentUser.uid,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EmeraldPrimary.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Cloud Synced",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Button(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(text = "View Data Export Summary", color = MaterialTheme.colorScheme.onSurface)
                    }

                    if (currentUser != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onSignOut,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sign_out_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text(text = "Sign Out of Google", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Dialog for Logging Weight
        if (showWeightDialog) {
            var newWeight by remember { mutableStateOf(currentWeight.toString()) }
            var note by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showWeightDialog = false },
                title = { Text("Log Body Weight") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newWeight,
                            onValueChange = { newWeight = it },
                            label = { Text("Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Note (optional)") },
                            placeholder = { Text("e.g. Morning fasted") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val w = newWeight.toFloatOrNull()
                            if (w != null && w > 0) {
                                onLogWeight(w, note)
                                showWeightDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWeightDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog for Editing Profile & Recalculating Mifflin-St Jeor
        if (showEditProfileDialog) {
            EditProfileDialog(
                profile = userProfile,
                onDismiss = { showEditProfileDialog = false },
                onSave = { updatedProfile ->
                    onUpdateProfile(updatedProfile)
                    showEditProfileDialog = false
                }
            )
        }

        // Dialog for Data Export
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("Fit Buddy Data Summary") },
                text = {
                    Column {
                        Text(text = "User: ${userProfile.name}")
                        Text(text = "Goal: ${userProfile.goal.title}")
                        Text(text = "Target Calories: ${userProfile.targetCalories} kcal")
                        Text(text = "Macros: ${userProfile.targetProteinG}g P / ${userProfile.targetCarbsG}g C / ${userProfile.targetFatG}g F")
                        Text(text = "Weight Logs Count: ${weightLogs.size}")
                        Text(text = "Streak: $streakDays days")
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "All data is securely stored in local Room SQLite database with full privacy.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                confirmButton = {
                    Button(onClick = { showExportDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
private fun ReminderToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
        )
    }
}

@Composable
private fun EditProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var age by remember { mutableIntStateOf(profile.age) }
    var heightCm by remember { mutableFloatStateOf(profile.heightCm) }
    var weightKg by remember { mutableFloatStateOf(profile.weightKg) }
    var targetWeightKg by remember { mutableFloatStateOf(profile.targetWeightKg) }
    var activityLevel by remember { mutableStateOf(profile.activityLevel) }
    var goal by remember { mutableStateOf(profile.goal) }
    var bodyType by remember { mutableStateOf(profile.bodyType) }
    var gymGoal by remember { mutableStateOf(profile.gymGoal) }

    val targets = MifflinStJeor.calculateTargets(
        gender = profile.gender,
        weightKg = weightKg,
        heightCm = heightCm,
        age = age,
        activityLevel = activityLevel,
        goal = goal
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Metabolic & Gym Profile") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                OutlinedTextField(
                    value = age.toString(),
                    onValueChange = { age = it.toIntOrNull() ?: age },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = heightCm.toInt().toString(),
                    onValueChange = { heightCm = it.toFloatOrNull() ?: heightCm },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = weightKg.toString(),
                    onValueChange = { weightKg = it.toFloatOrNull() ?: weightKg },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = targetWeightKg.toString(),
                    onValueChange = { targetWeightKg = it.toFloatOrNull() ?: targetWeightKg },
                    label = { Text("Goal Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Text(text = "Present Body Type:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                com.example.model.BodyType.entries.forEach { b ->
                    FilterChip(
                        selected = bodyType == b,
                        onClick = { bodyType = b },
                        label = { Text("${b.emoji} ${b.title}") }
                    )
                }

                Text(text = "In-Gym Goal:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                com.example.model.GymTrainingGoal.entries.forEach { g ->
                    FilterChip(
                        selected = gymGoal == g,
                        onClick = { gymGoal = g },
                        label = { Text("${g.emoji} ${g.title} (${g.recommendedSets}s x ${g.repRange})") }
                    )
                }

                Text(text = "Recalculated: ${targets.calories} kcal • ${targets.protein}g P • ${targets.carbs}g C • ${targets.fat}g F", style = MaterialTheme.typography.bodySmall, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        profile.copy(
                            name = name,
                            age = age,
                            heightCm = heightCm,
                            weightKg = weightKg,
                            targetWeightKg = targetWeightKg,
                            activityLevel = activityLevel,
                            goal = goal,
                            bodyType = bodyType,
                            gymGoal = gymGoal,
                            targetCalories = targets.calories,
                            targetProteinG = targets.protein,
                            targetCarbsG = targets.carbs,
                            targetFatG = targets.fat
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Update & Recalculate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
