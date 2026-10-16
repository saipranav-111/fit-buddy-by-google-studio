package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyNutritionSummary
import com.example.model.MealLog
import com.example.model.MealType
import com.example.model.UserProfile
import com.example.ui.components.CalorieDonutChart
import com.example.ui.components.MacroCard
import com.example.ui.components.MealSectionCard
import com.example.ui.components.StreakBanner
import com.example.ui.components.WaterTrackerWidget
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatColor
import com.example.ui.theme.ProteinColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    userProfile: UserProfile,
    summary: DailyNutritionSummary,
    mealLogs: List<MealLog>,
    selectedDate: String,
    streak: Int,
    onDateChange: (String) -> Unit,
    onIncrementWater: () -> Unit,
    onDecrementWater: () -> Unit,
    onAddMealClick: (MealType) -> Unit,
    onDeleteMealLog: (Long) -> Unit
) {
    val scrollState = rememberScrollState()

    // Date formatting helper
    val displayDate = formatDisplayDate(selectedDate)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddMealClick(MealType.currentForHour()) },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_meal")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Log Food")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Hello, ${userProfile.name}! 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Goal: ${userProfile.goal.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEA580C).copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔥", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streak d",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEA580C)
                        )
                    }
                }
            }

            // Date Navigator Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onDateChange(changeDate(selectedDate, -1)) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Day"
                        )
                    }

                    Text(
                        text = displayDate,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { onDateChange(changeDate(selectedDate, 1)) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Day"
                        )
                    }
                }
            }

            // Calorie Donut Center Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Daily Calorie Budget",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    CalorieDonutChart(
                        consumedCalories = summary.totalCalories,
                        targetCalories = summary.targetCalories,
                        proteinPercent = summary.proteinProgress,
                        carbsPercent = summary.carbsProgress,
                        fatPercent = summary.fatProgress
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Consumed",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${summary.totalCalories} kcal",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Target",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${summary.targetCalories} kcal",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Remaining",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${summary.remainingCalories} kcal",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (summary.remainingCalories < 0) Color(0xFFEF4444) else EmeraldPrimary
                            )
                        }
                    }
                }
            }

            // 4 Macro Breakdown Cards (2x2 Grid)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MacroCard(
                    title = "Protein",
                    consumed = summary.totalProtein,
                    target = summary.targetProtein,
                    unit = "g",
                    accentColor = ProteinColor,
                    modifier = Modifier.weight(1f)
                )
                MacroCard(
                    title = "Carbs",
                    consumed = summary.totalCarbs,
                    target = summary.targetCarbs,
                    unit = "g",
                    accentColor = CarbsColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MacroCard(
                    title = "Fats",
                    consumed = summary.totalFat,
                    target = summary.targetFat,
                    unit = "g",
                    accentColor = FatColor,
                    modifier = Modifier.weight(1f)
                )
                MacroCard(
                    title = "Hydration",
                    consumed = summary.waterGlasses.toFloat(),
                    target = summary.waterGoal,
                    unit = " gl",
                    accentColor = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                )
            }

            // Streak Banner
            StreakBanner(streakDays = streak)

            // Water Tracker Widget
            WaterTrackerWidget(
                currentGlasses = summary.waterGlasses,
                targetGlasses = summary.waterGoal,
                onIncrement = onIncrementWater,
                onDecrement = onDecrementWater
            )

            // Meals Header
            Text(
                text = "Today's Meals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // 4 Collapsible Meal Sections
            MealType.entries.forEach { type ->
                val logsForType = mealLogs.filter { it.mealType == type }
                MealSectionCard(
                    mealType = type,
                    logs = logsForType,
                    onAddClick = { onAddMealClick(type) },
                    onDeleteLog = onDeleteMealLog
                )
            }
        }
    }
}

private fun formatDisplayDate(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: return dateStr
        val todayStr = sdf.format(Date())

        val calYesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterdayStr = sdf.format(calYesterday.time)

        val calTomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val tomorrowStr = sdf.format(calTomorrow.time)

        when (dateStr) {
            todayStr -> "Today, ${SimpleDateFormat("MMM d", Locale.getDefault()).format(date)}"
            yesterdayStr -> "Yesterday, ${SimpleDateFormat("MMM d", Locale.getDefault()).format(date)}"
            tomorrowStr -> "Tomorrow, ${SimpleDateFormat("MMM d", Locale.getDefault()).format(date)}"
            else -> SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(date)
        }
    } catch (e: Exception) {
        dateStr
    }
}

private fun changeDate(dateStr: String, deltaDays: Int): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: Date()
        val cal = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, deltaDays)
        }
        sdf.format(cal.time)
    } catch (e: Exception) {
        dateStr
    }
}
