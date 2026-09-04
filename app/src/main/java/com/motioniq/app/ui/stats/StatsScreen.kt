package com.motioniq.app.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
    bestDay: Pair<String, Long>? = null
) {
    var selectedPeriod by remember { mutableStateOf("Week") }
    val periods = listOf("Day", "Week", "Month", "Year")

    val displayWeeklySteps = if (weeklySteps.isNotEmpty()) {
        weeklySteps
    } else {
        listOf("Mon" to 4200L, "Tue" to 8426L, "Wed" to 5100L, "Thu" to 7800L, "Fri" to 8900L, "Sat" to 6200L, "Sun" to 7100L)
    }

    val totalDistanceKm = if (activities.isNotEmpty()) {
        activities.sumOf { it.distanceMeters } / 1000.0
    } else {
        42.8
    }

    val totalCalories = if (activities.isNotEmpty()) {
        activities.sumOf { it.caloriesKcal }
    } else {
        2481
    }

    val totalActiveMinutes = if (activities.isNotEmpty()) {
        (activities.sumOf { it.durationSeconds } / 60)
    } else {
        392L // 6h 32m
    }

    val totalStepsDisplay = if (weeklyTotal > 0) weeklyTotal else 56421L
    val activityCount = if (activities.isNotEmpty()) activities.size else 5

    val hours = totalActiveMinutes / 60
    val mins = totalActiveMinutes % 60
    val activeTimeText = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

    Scaffold(
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
        ) {
            // Header (10_Statistics.png)
            item {
                Text(
                    text = "Statistics",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
            }

            // Period Selector Pills (Day, Week, Month, Year)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(24.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    periods.forEach { period ->
                        val isSelected = selectedPeriod == period
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .background(
                                    if (isSelected) BrandNavy else Color.Transparent,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedPeriod = period }
                        ) {
                            Text(
                                text = period,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextMediumLight
                            )
                        }
                    }
                }
            }

            // Date Range Navigator (< 2 - 8 Sep 2024 >)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = TextMediumLight)
                    }

                    Text(
                        text = "2 – 8 Sep 2024",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHighLight
                    )

                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = TextMediumLight)
                    }
                }
            }

            // Hero Distance + Growth Badge
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "%.1f km".format(Locale.US, totalDistanceKm),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = TextHighLight,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Total Distance",
                            fontSize = 14.sp,
                            color = TextMediumLight
                        )
                    }

                    // Green growth badge (↑ 14%)
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "14%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }
                }
            }

            // Bar Chart (10_Statistics.png)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val maxVal = (displayWeeklySteps.maxOfOrNull { it.second }?.toFloat() ?: 10000f).coerceAtLeast(1000f)
                                val barWidth = 26.dp.toPx()
                                val stepCount = displayWeeklySteps.size
                                val totalBarsWidth = barWidth * stepCount
                                val spacing = (size.width - totalBarsWidth) / (stepCount + 1)
                                val chartHeight = size.height

                                val gradientBrush = Brush.verticalGradient(
                                    colors = listOf(ElectricBlue, Color(0xFF00E5FF))
                                )

                                displayWeeklySteps.forEachIndexed { index, pair ->
                                    val x = spacing + index * (barWidth + spacing)
                                    val barHeight = ((pair.second.toFloat() / maxVal) * chartHeight).coerceIn(12f, chartHeight)
                                    val y = chartHeight - barHeight

                                    drawRoundRect(
                                        brush = gradientBrush,
                                        topLeft = Offset(x, y),
                                        size = Size(barWidth, barHeight),
                                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Weekdays Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            displayWeeklySteps.forEach { pair ->
                                Text(
                                    text = pair.first,
                                    fontSize = 12.sp,
                                    color = TextMediumLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // 2x2 Metric Cards (10_Statistics.png)
            item {
                val formattedSteps = NumberFormat.getNumberInstance(Locale.US).format(totalStepsDisplay)
                val formattedCal = NumberFormat.getNumberInstance(Locale.US).format(totalCalories)

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatMetricCard(
                            icon = Icons.Default.DirectionsWalk,
                            iconTint = ElectricBlue,
                            badgeBg = SoftTileBlue,
                            value = formattedSteps,
                            label = "Steps",
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            icon = Icons.Default.Timer,
                            iconTint = Color(0xFF2563EB),
                            badgeBg = SoftTileBlue,
                            value = activeTimeText,
                            label = "Active time",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatMetricCard(
                            icon = Icons.Default.LocalFireDepartment,
                            iconTint = PulseOrange,
                            badgeBg = SoftTileOrange,
                            value = formattedCal,
                            label = "Calories",
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            icon = Icons.Default.Route,
                            iconTint = AccentPurple,
                            badgeBg = SoftTilePurple,
                            value = "$activityCount",
                            label = "Activities",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    badgeBg: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .background(badgeBg, shape = CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = TextMediumLight
                )
            }
        }
    }
}
