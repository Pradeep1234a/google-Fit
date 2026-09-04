package com.motioniq.app.ui.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatsScreen(
    activities: List<MovementActivity>,
    weeklySteps: List<Pair<String, Long>> = emptyList(),
    weeklyTotal: Long = 0L,
    averageDaily: Long = 0L,
    bestDay: Pair<String, Long>? = null,
    onActivityClick: ((MovementActivity) -> Unit)? = null,
    onViewAllActivities: (() -> Unit)? = null
) {
    var selectedPeriod by remember { mutableStateOf("Week") }
    val periods = listOf("Day", "Week", "Month", "Year")

    val totalDistanceKm = if (activities.isNotEmpty()) {
        activities.sumOf { it.distanceMeters } / 1000.0
    } else {
        54.8
    }

    val totalSteps = if (weeklyTotal > 0) weeklyTotal else 42650L
    val totalActiveHours = if (activities.isNotEmpty()) {
        val totalSec = activities.sumOf { it.durationSeconds }
        "${totalSec / 3600}h ${(totalSec % 3600) / 60}m"
    } else {
        "4h 32m"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateGround)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Bar: MOTIONIQ Analytics, Bell, Avatar (Stitch 8908beee)
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
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(SlateSurface2, RoundedCornerShape(10.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
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
                            text = "ANALYTICS",
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
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(SlateSurface2, CircleShape)
                            .border(1.dp, CyanBorderSubtle, CircleShape)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(StitchCyan, CircleShape)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = StitchDarkCyan, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        // 2. Period Selector & Date Navigator (Stitch 8908beee)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Period Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    periods.forEach { period ->
                        val isSelected = selectedPeriod == period
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) StitchCyan else SlateSurface1)
                                .border(1.dp, if (isSelected) StitchCyan else CyanBorderSubtle, RoundedCornerShape(20.dp))
                                .clickable { selectedPeriod = period }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = period,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) StitchDarkCyan else TextMediumDark
                            )
                        }
                    }
                }

                // Date range navigator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = TextLowDark, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Oct 18 – Oct 24, 2024",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = TextLowDark, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 3. Kinetic Trajectory Card (Stitch 8908beee)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "KINETIC TRAJECTORY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextLowDark,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "↗ +14% vs last week",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KineticEmerald
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "%.1f".format(Locale.US, totalDistanceKm),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "KM TOTAL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Step Velocity
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SlateSurface2, RoundedCornerShape(12.dp))
                                .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(16.dp))
                                    Text("Step Velocity", style = MaterialTheme.typography.labelSmall, color = TextLowDark)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = NumberFormat.getNumberInstance(Locale.US).format(totalSteps),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Active Kinematics
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SlateSurface2, RoundedCornerShape(12.dp))
                                .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(16.dp))
                                    Text("Active Movement", style = MaterialTheme.typography.labelSmall, color = TextLowDark)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = totalActiveHours,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Biomechanic Volume Daily Bar Chart (Stitch 8908beee)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BIOMECHANIC VOLUME",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextLowDark,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cadence & Distance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(SlateSurface2, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "● Peak: 10.2k",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StitchCyan
                            )
                        }
                    }

                    // Daily Bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val days = listOf(
                            Triple("M", "4.2k", 0.45f),
                            Triple("T", "5.1k", 0.55f),
                            Triple("W", "3.8k", 0.38f),
                            Triple("T", "4.5k", 0.48f),
                            Triple("F", "8.6k", 0.85f),
                            Triple("S", "6.1k", 0.60f),
                            Triple("S", "10.2k", 0.98f)
                        )

                        days.forEach { (day, stepsLabel, fraction) ->
                            val isPeak = fraction > 0.8f
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isPeak) {
                                    Text(
                                        text = stepsLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StitchCyan
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .fillMaxHeight(fraction)
                                        .background(
                                            if (isPeak) StitchCyan else SlateSurface2,
                                            RoundedCornerShape(6.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPeak) StitchCyan else TextLowDark
                                )
                            }
                        }
                    }

                    // Footer metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = TextLowDark, modifier = Modifier.size(14.dp))
                            Text("Weekly Pace: 5'18\" /km", style = MaterialTheme.typography.bodySmall, color = TextMediumDark)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = TextLowDark, modifier = Modifier.size(14.dp))
                            Text("Avg 78 Cadence", style = MaterialTheme.typography.bodySmall, color = TextMediumDark)
                        }
                    }
                }
            }
        }

        // 5. Movement Intensity Breakdown (Stitch 8908beee)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Movement Intensity Breakdown",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Tri-color Segmented Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        Box(modifier = Modifier.weight(0.64f).fillMaxHeight().background(StitchCyan))
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(modifier = Modifier.weight(0.22f).fillMaxHeight().background(StitchTeal))
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(modifier = Modifier.weight(0.14f).fillMaxHeight().background(KineticEmerald))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(6.dp).background(StitchCyan, CircleShape))
                                Text("Moderate 64%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Text("130–155 bpm • 2h 54m", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextLowDark)
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(6.dp).background(StitchTeal, CircleShape))
                                Text("High 22%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Text("156–175 bpm • 1h 00m", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextLowDark)
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(6.dp).background(KineticEmerald, CircleShape))
                                Text("Recovery 14%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Text("<130 bpm • 0h 38m", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextLowDark)
                        }
                    }
                }
            }
        }

        // 6. Modality Load Breakdown (Stitch 8908beee)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "MODALITY LOAD BREAKDOWN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextLowDark,
                    letterSpacing = 1.sp
                )

                // Running
                ModalityLoadCard(
                    icon = Icons.Default.DirectionsRun,
                    title = "Running",
                    subtitle = "3 sessions • 174 spm cadence",
                    distance = "28.4 km",
                    loadPercent = "51.8% Load",
                    color = StitchCyan
                )

                // Walking
                ModalityLoadCard(
                    icon = Icons.Default.DirectionsWalk,
                    title = "Walking",
                    subtitle = "6 sessions • 112 spm cadence",
                    distance = "18.2 km",
                    loadPercent = "33.2% Load",
                    color = KineticEmerald
                )

                // Cycling
                ModalityLoadCard(
                    icon = Icons.Default.DirectionsBike,
                    title = "Cycling",
                    subtitle = "1 session • 88 rpm cadence",
                    distance = "8.2 km",
                    loadPercent = "15.0% Load",
                    color = VelocityPurple
                )
            }
        }

        // 7. Export Telemetry Button (Stitch 8908beee)
        item {
            Button(
                onClick = {},
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StitchTeal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(18.dp))
                    Text("Export Telemetry PDF / CSV", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ModalityLoadCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    distance: String,
    loadPercent: String,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface1),
        border = BorderStroke(1.dp, CyanBorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMediumDark)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(distance, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
                Spacer(modifier = Modifier.height(2.dp))
                Text(loadPercent, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextLowDark)
            }
        }
    }
}
