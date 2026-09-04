package com.motioniq.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.core.step.StepSourceType
import com.motioniq.app.model.ActivityType
import com.motioniq.app.model.DailySummary
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.model.UserProfile
import com.motioniq.app.theme.*
import com.motioniq.app.ui.components.GoalProgressRing
import com.motioniq.app.ui.components.MetricCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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
    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
    val weekOfYear = cal.get(Calendar.WEEK_OF_YEAR)
    val todayFormatted = SimpleDateFormat("EEE, MMM d", Locale.US).format(Date()).uppercase()

    val avgSpeed = if (summary.activeMinutes > 0) {
        (summary.distanceMeters / 1000.0) / (summary.activeMinutes / 60.0)
    } else 0.0

    val paceFormatted = if (avgSpeed > 0.5) {
        GpsCalculator.formatPace(1000.0 / (avgSpeed * 1000.0 / 3600.0))
    } else {
        "5'12\""
    }

    var selectedModality by remember { mutableStateOf(ActivityType.WALKING) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateGround)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Top Bar: Brand Logo & Title + Notification + Avatar (Stitch 49c10b83)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Geometric M Logo Glyph
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(SlateSurface2, RoundedCornerShape(10.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Logo",
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MOTION",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "IQ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = StitchCyan
                            )
                        }
                        Text(
                            text = "DASHBOARD",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bell icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(SlateSurface2, CircleShape)
                            .border(1.dp, CyanBorderSubtle, CircleShape)
                            .clickable { onNotificationClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Avatar button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(StitchCyan, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = StitchDarkCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // 2. Status Subheader Pills: Live Sensor & 98.4% Sync (Stitch 49c10b83)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Sensor Pill
                Row(
                    modifier = Modifier
                        .background(SlateSurface1, RoundedCornerShape(20.dp))
                        .border(1.dp, CyanBorderSubtle, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(StitchCyan, CircleShape)
                    )
                    Text(
                        text = "$todayFormatted • LIVE SENSOR",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Sync Pill
                Row(
                    modifier = Modifier
                        .background(SlateSurface1, RoundedCornerShape(20.dp))
                        .border(1.dp, CyanBorderSubtle, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = StitchCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "98.4% SYNC",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchCyan
                    )
                }
            }
        }

        // 3. Greeting Row (Stitch 49c10b83)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$greeting, ",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = profile.name.ifBlank { "Alex" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = StitchCyan
                    )
                }

                Text(
                    text = "Week $weekOfYear",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = TextLowDark
                )
            }
        }

        // 4. Hero Kinetic Cadence Target Arc Card (Stitch 49c10b83)
        item {
            GoalProgressRing(
                currentSteps = summary.steps.toLong(),
                stepGoal = profile.dailyStepGoal,
                modifier = Modifier.clickable { onGoalClick() }
            )
        }

        // 5. 2x2 Metric Cards Grid (Stitch 49c10b83)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Distance",
                        value = "%.1f".format(Locale.US, summary.distanceMeters / 1000.0),
                        unit = "km",
                        delta = "↗ +8%",
                        icon = Icons.Default.DirectionsWalk,
                        iconTint = StitchCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Burned",
                        value = "${summary.caloriesKcal}",
                        unit = "kcal",
                        delta = "↗ +12%",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = PulseCoral,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Active Time",
                        value = "${summary.activeMinutes}",
                        unit = "min",
                        delta = "Nominal",
                        icon = Icons.Default.Timer,
                        iconTint = VelocityPurple,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Avg Pace",
                        value = paceFormatted,
                        unit = "/km",
                        delta = "At Peak",
                        icon = Icons.Default.Speed,
                        iconTint = StitchCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 6. Track Sensor Session (Stitch 49c10b83)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRACK SENSOR SESSION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextLowDark,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "6 Modalities",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = StitchCyan,
                        modifier = Modifier.clickable { onStartActivityClick() }
                    )
                }

                // Horizontal Modality Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val modalities = listOf(
                        ActivityType.WALKING to "Walk",
                        ActivityType.RUNNING to "Run",
                        ActivityType.CYCLING to "Cycling",
                        ActivityType.SPORTS to "Sports"
                    )

                    modalities.forEach { (type, label) ->
                        val isSelected = selectedModality == type
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) StitchCyan else SlateSurface1)
                                .border(
                                    1.dp,
                                    if (isSelected) StitchCyan else CyanBorderSubtle,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedModality = type
                                    onStartActivityClick()
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(StitchDarkCyan, CircleShape)
                                )
                            }
                            Icon(
                                imageVector = when (type) {
                                    ActivityType.WALKING -> Icons.Default.DirectionsWalk
                                    ActivityType.RUNNING -> Icons.Default.DirectionsRun
                                    ActivityType.CYCLING -> Icons.Default.DirectionsBike
                                    else -> Icons.Default.FitnessCenter
                                },
                                contentDescription = null,
                                tint = if (isSelected) StitchDarkCyan else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) StitchDarkCyan else Color.White
                            )
                        }
                    }
                }
            }
        }

        // 7. AI Diagnostic: Movement Velocity Vector Card (Stitch 49c10b83)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(StitchTeal.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .border(1.dp, CyanBorderSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = StitchCyan,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "AI DIAGNOSTIC",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchCyan
                                )
                            }
                        }

                        Text(
                            text = "Telemetry Model v4.2",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextLowDark
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .background(SlateSurface2, RoundedCornerShape(10.dp))
                                .border(1.dp, CyanBorderSubtle, RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Movement Velocity Vector",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Your cadence peak was 6:30 PM yesterday. You walk 14% further on Wednesdays with optimal joint stability.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMediumDark,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Impact Load progress bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Impact Load: Optimal (1.2G)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = KineticEmerald
                        )

                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(6.dp)
                                .background(Color(0xFF1B2830), RoundedCornerShape(3.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .fillMaxHeight()
                                    .background(StitchCyan, RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }
            }
        }

        // 8. Recorded Kinematics (Recent Workout Card) (Stitch 49c10b83)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECORDED KINEMATICS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextLowDark,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "View Ledger",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = StitchCyan,
                        modifier = Modifier.clickable { onStartActivityClick() }
                    )
                }

                val topActivity = recentActivities.firstOrNull()
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                    border = BorderStroke(1.dp, CyanBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (topActivity != null) onActivityClick(topActivity)
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Icon Tile
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .background(StitchTeal.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = topActivity?.type?.displayName ?: "Morning Outdoor Run",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (topActivity != null) {
                                    "${"%.1f".format(Locale.US, topActivity.distanceMeters / 1000.0)} km • ${topActivity.durationSeconds / 60}m • ${topActivity.caloriesKcal} kcal"
                                } else {
                                    "4.2 km • 24m 18s • 312 kcal"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMediumDark
                            )
                        }

                        // Sparkline Canvas & ME Score
                        Column(horizontalAlignment = Alignment.End) {
                            Canvas(modifier = Modifier.size(width = 54.dp, height = 20.dp)) {
                                val path = Path().apply {
                                    moveTo(0f, size.height * 0.8f)
                                    cubicTo(
                                        size.width * 0.3f, size.height * 0.2f,
                                        size.width * 0.6f, size.height * 0.9f,
                                        size.width, size.height * 0.1f
                                    )
                                }
                                drawPath(
                                    path = path,
                                    color = StitchCyan,
                                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "94 ME",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StitchCyan
                            )
                        }
                    }
                }
            }
        }

        // 9. Bilateral Gait Symmetry Bar (Stitch 49c10b83)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "BILATERAL GAIT SYMMETRY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Equilibrium: 0.2% Delta",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = KineticEmerald
                        )
                    }

                    // Track Bar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFF1B2830), RoundedCornerShape(4.dp))
                    ) {
                        // Center Indicator Marker
                        Box(
                            modifier = Modifier
                                .size(width = 8.dp, height = 14.dp)
                                .background(StitchCyan, RoundedCornerShape(2.dp))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Left Leg: 49.9%",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLowDark
                        )
                        Text(
                            text = "Center (0.0)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLowDark
                        )
                        Text(
                            text = "Right Leg: 50.1%",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLowDark
                        )
                    }
                }
            }
        }

        // 10. Bottom Primary CTA: Full Cyan Pill (Stitch 49c10b83)
        item {
            Button(
                onClick = onStartActivityClick,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StitchCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = StitchDarkCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "START REAL-TIME BIOMECHANIC CAPTURE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = StitchDarkCyan,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
