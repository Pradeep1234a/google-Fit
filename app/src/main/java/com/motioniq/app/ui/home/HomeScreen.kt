package com.motioniq.app.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.core.step.StepSourceType
import com.motioniq.app.model.DailySummary
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.model.UserProfile
import com.motioniq.app.theme.*
import com.motioniq.app.ui.components.GoalProgressRing
import com.motioniq.app.ui.components.MetricCard
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    summary: DailySummary,
    profile: UserProfile,
    recentActivities: List<MovementActivity>,
    activeSource: StepSourceType = StepSourceType.HARDWARE_STEP_COUNTER,
    onNotificationClick: () -> Unit = {},
    onGoalClick: () -> Unit = {},
    onStartActivityClick: () -> Unit,
    onExploreClick: () -> Unit,
    onActivityClick: (MovementActivity) -> Unit
) {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val percent = if (profile.dailyStepGoal > 0) {
        ((summary.steps.toDouble() / profile.dailyStepGoal.toDouble()) * 100).toInt().coerceAtMost(999)
    } else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Header: Greeting, User Name & Notification Bell (05_Home.png)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${profile.name.ifBlank { "Alex" }} \uD83D\uDC4B",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                // Notification Bell Icon Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(CardDarkElevated, shape = CircleShape)
                        .clickable { onNotificationClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // 2. Hero: Goal Progress Ring (05_Home.png)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GoalProgressRing(
                    currentSteps = summary.steps,
                    stepGoal = profile.dailyStepGoal
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Hardware Sensor Diagnostic Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            CardDarkElevated.copy(alpha = 0.8f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val (sensorLabel, isHighConfidence) = when (activeSource) {
                        StepSourceType.HARDWARE_STEP_COUNTER -> "Sensor: Hardware Step Counter" to true
                        StepSourceType.HARDWARE_STEP_DETECTOR -> "Sensor: Step Detector" to true
                        StepSourceType.SOFTWARE_ACCELEROMETER -> "Sensor: Software Pedometer" to false
                        StepSourceType.NONE -> "Sensor: Unavailable" to false
                    }
                    Icon(
                        imageVector = if (isHighConfidence) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isHighConfidence) KineticGreen else AmberWarning,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = sensorLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // 3. 2x2 Metric Cards Grid (05_Home.png)
        item {
            val avgSpeed = if (summary.activeMinutes > 0) {
                (summary.distanceMeters / 1000.0) / (summary.activeMinutes / 60.0)
            } else 0.0

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Distance
                    MetricCard(
                        title = "Distance",
                        value = "%.1f".format(Locale.US, summary.distanceMeters / 1000.0),
                        unit = "km",
                        icon = Icons.Default.Place,
                        iconTint = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                    // Calories
                    MetricCard(
                        title = "Calories",
                        value = "${summary.caloriesKcal}",
                        unit = "",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = PulseOrange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Active time
                    MetricCard(
                        title = "Active time",
                        value = "${summary.activeMinutes}",
                        unit = "min",
                        icon = Icons.Default.Timer,
                        iconTint = AccentPurple,
                        modifier = Modifier.weight(1f)
                    )
                    // Avg speed
                    MetricCard(
                        title = "Avg speed",
                        value = "%.1f".format(Locale.US, avgSpeed),
                        unit = "km/h",
                        icon = Icons.Default.DirectionsBike,
                        iconTint = Color(0xFF00E5FF),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. "Today's Goal" Card with Gradient Progress Bar (05_Home.png)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDarkElevated),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGoalClick() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Goal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View Goals",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$percent% complete",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Horizontal Gradient Progress Bar with Dot Markers
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barHeight = size.height
                            val strokeWidth = barHeight
                            val cornerY = size.height / 2f
                            val progressRatio = (percent / 100f).coerceIn(0f, 1f)

                            // Background track
                            drawLine(
                                color = Color(0xFF091C3E),
                                start = Offset(barHeight / 2, cornerY),
                                end = Offset(size.width - barHeight / 2, cornerY),
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round
                            )

                            // Active gradient progress fill
                            if (progressRatio > 0f) {
                                val activeWidth = (size.width * progressRatio).coerceAtLeast(barHeight)
                                drawLine(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(ElectricBlue, KineticGreen)
                                    ),
                                    start = Offset(barHeight / 2, cornerY),
                                    end = Offset(activeWidth - barHeight / 2, cornerY),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            }

                            // Right-end circle markers (oo)
                            val dotRadius = 2.5.dp.toPx()
                            val dotY = cornerY
                            drawCircle(
                                color = Color(0xFF94A3B8),
                                radius = dotRadius,
                                center = Offset(size.width - 16.dp.toPx(), dotY)
                            )
                            drawCircle(
                                color = Color(0xFF94A3B8),
                                radius = dotRadius,
                                center = Offset(size.width - 6.dp.toPx(), dotY)
                            )
                        }
                    }
                }
            }
        }

        // 5. Quick Workout Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onStartActivityClick,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KineticGreen,
                        contentColor = BrandNavy
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = BrandNavy
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "START ACTIVITY",
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy
                    )
                }

                OutlinedButton(
                    onClick = onExploreClick,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "EXPLORE",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 6. Today's Recent Workouts (if any)
        if (recentActivities.isNotEmpty()) {
            item {
                Text(
                    text = "TODAY'S ACTIVITIES (${recentActivities.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
            }

            items(recentActivities.take(3)) { activity ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    onClick = { onActivityClick(activity) },
                    colors = CardDefaults.cardColors(containerColor = CardDarkElevated),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activity.type.emoji,
                            fontSize = 28.sp,
                            modifier = Modifier
                                .background(
                                    CardDark,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = activity.type.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "%.2f km".format(Locale.US, activity.distanceMeters / 1000.0),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = KineticGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = GpsCalculator.formatDuration(activity.durationSeconds),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                                Text("•", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                Text(
                                    text = "${activity.steps} steps",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                                Text("•", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                Text(
                                    text = "≈${activity.caloriesKcal} kcal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
