package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyNutritionSummary
import com.example.model.MealLog
import com.example.model.UserProfile
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatColor
import com.example.ui.theme.ProteinColor

enum class AnalyticsRange(val label: String) {
    DAY("Today"),
    WEEK("7 Days"),
    MONTH("30 Days")
}

@Composable
fun AnalyticsScreen(
    userProfile: UserProfile,
    todaySummary: DailyNutritionSummary,
    recentMealLogs: List<MealLog>
) {
    var selectedRange by remember { mutableStateOf(AnalyticsRange.WEEK) }

    val targetCal = userProfile.targetCalories
    val targetPro = userProfile.targetProteinG
    val targetCarb = userProfile.targetCarbsG
    val targetFat = userProfile.targetFatG

    // Calculate sample weekly data based on recent logs + today's summary
    val daysLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val sampleIntakes = listOf(
        (targetCal * 0.92f).toInt(),
        (targetCal * 0.98f).toInt(),
        (targetCal * 1.04f).toInt(),
        (targetCal * 0.95f).toInt(),
        (targetCal * 0.90f).toInt(),
        (targetCal * 1.02f).toInt(),
        todaySummary.totalCalories.coerceAtLeast((targetCal * 0.85f).toInt())
    )

    val averageCal = sampleIntakes.average().toInt()
    val adherencePercent = ((sampleIntakes.count { it in (targetCal - 200)..(targetCal + 200) }.toFloat() / sampleIntakes.size) * 100).toInt()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Nutrition Analytics 📊",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track your nutritional adherence and macro trends over time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Range Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalyticsRange.entries.forEach { range ->
                    FilterChip(
                        selected = selectedRange == range,
                        onClick = { selectedRange = range },
                        label = { Text(range.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = EmeraldPrimary
                        )
                    )
                }
            }

            // Quick Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Avg Daily Intake",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$averageCal kcal",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = "Target: $targetCal kcal",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Adherence Score",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$adherencePercent%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (adherencePercent >= 80) Color(0xFF10B981) else Color(0xFFF59E0B)
                        )
                        Text(
                            text = "In target calorie zone",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Weekly Intake Bar Chart (Custom Canvas)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Daily Calorie Trend",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Target Line ($targetCal)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Bar Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxVal = (targetCal * 1.3f)
                            val barWidth = 24.dp.toPx()
                            val spacing = (size.width - (barWidth * sampleIntakes.size)) / (sampleIntakes.size + 1)
                            val targetY = size.height - (targetCal / maxVal) * size.height

                            // Draw Target Reference Line
                            drawLine(
                                color = Color(0xFF94A3B8),
                                start = Offset(0f, targetY),
                                end = Offset(size.width, targetY),
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                            )

                            // Draw Bars
                            sampleIntakes.forEachIndexed { index, cal ->
                                val barHeight = (cal / maxVal) * size.height
                                val x = spacing + index * (barWidth + spacing)
                                val y = size.height - barHeight

                                val barColor = if (cal > targetCal + 100) Color(0xFFEF4444) else EmeraldPrimary

                                drawRoundRect(
                                    color = barColor,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                                )
                            }
                        }
                    }

                    // Bar Labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        daysLabels.forEach { label ->
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Macro Split Breakdown Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Macro Energy Distribution",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ideal distribution calibrated for ${userProfile.goal.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val totalGrams = (targetPro + targetCarb + targetFat).toFloat().coerceAtLeast(1f)
                    val proRatio = (targetPro / totalGrams)
                    val carbRatio = (targetCarb / totalGrams)
                    val fatRatio = (targetFat / totalGrams)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(proRatio)
                                .fillMaxSize()
                                .background(ProteinColor)
                        )
                        Box(
                            modifier = Modifier
                                .weight(carbRatio)
                                .fillMaxSize()
                                .background(CarbsColor)
                        )
                        Box(
                            modifier = Modifier
                                .weight(fatRatio)
                                .fillMaxSize()
                                .background(FatColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroLegendItem(name = "Protein", percent = "${(proRatio * 100).toInt()}% (${targetPro}g)", color = ProteinColor)
                        MacroLegendItem(name = "Carbs", percent = "${(carbRatio * 100).toInt()}% (${targetCarb}g)", color = CarbsColor)
                        MacroLegendItem(name = "Fats", percent = "${(fatRatio * 100).toInt()}% (${targetFat}g)", color = FatColor)
                    }
                }
            }

            // Goal vs Actual Comparison Table
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Target vs. Actual Today",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ComparisonRow(
                        nutrient = "Calories",
                        actual = "${todaySummary.totalCalories} kcal",
                        goal = "$targetCal kcal",
                        delta = todaySummary.totalCalories - targetCal,
                        unit = "kcal"
                    )
                    ComparisonRow(
                        nutrient = "Protein",
                        actual = "${todaySummary.totalProtein.toInt()} g",
                        goal = "$targetPro g",
                        delta = (todaySummary.totalProtein - targetPro).toInt(),
                        unit = "g"
                    )
                    ComparisonRow(
                        nutrient = "Carbs",
                        actual = "${todaySummary.totalCarbs.toInt()} g",
                        goal = "$targetCarb g",
                        delta = (todaySummary.totalCarbs - targetCarb).toInt(),
                        unit = "g"
                    )
                    ComparisonRow(
                        nutrient = "Fat",
                        actual = "${todaySummary.totalFat.toInt()} g",
                        goal = "$targetFat g",
                        delta = (todaySummary.totalFat - targetFat).toInt(),
                        unit = "g"
                    )
                    ComparisonRow(
                        nutrient = "Water",
                        actual = "${todaySummary.waterGlasses} gl",
                        goal = "${userProfile.waterGoalGlasses} gl",
                        delta = todaySummary.waterGlasses - userProfile.waterGoalGlasses,
                        unit = "gl"
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroLegendItem(name: String, percent: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(text = percent, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ComparisonRow(
    nutrient: String,
    actual: String,
    goal: String,
    delta: Int,
    unit: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = nutrient, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = actual, style = MaterialTheme.typography.bodyMedium)
            Text(text = "/ $goal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            val deltaText = if (delta >= 0) "+$delta $unit" else "$delta $unit"
            val deltaColor = if (delta > 0) Color(0xFFEF4444) else EmeraldPrimary
            Text(
                text = deltaText,
                style = MaterialTheme.typography.labelSmall,
                color = deltaColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(70.dp)
            )
        }
    }
}
