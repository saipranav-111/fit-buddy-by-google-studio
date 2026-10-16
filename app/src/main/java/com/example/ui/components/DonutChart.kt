package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatColor
import com.example.ui.theme.ProteinColor

@Composable
fun CalorieDonutChart(
    consumedCalories: Int,
    targetCalories: Int,
    proteinPercent: Float,
    carbsPercent: Float,
    fatPercent: Float,
    modifier: Modifier = Modifier
) {
    val progress = if (targetCalories > 0) {
        (consumedCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "CalorieProgress"
    )

    val remaining = (targetCalories - consumedCalories).coerceAtLeast(0)
    val isOver = consumedCalories > targetCalories

    Box(
        modifier = modifier.size(190.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(170.dp)) {
            val strokeWidth = 14.dp.toPx()

            // Background ring
            drawArc(
                color = Color.LightGray.copy(alpha = 0.25f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Consumed progress ring
            val primaryArcColor = if (isOver) Color(0xFFEF4444) else EmeraldPrimary
            drawArc(
                color = primaryArcColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Inner Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isOver) "+${consumedCalories - targetCalories}" else "$remaining",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = if (isOver) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isOver) "kcal over" else "kcal left",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "of $targetCalories target",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}
