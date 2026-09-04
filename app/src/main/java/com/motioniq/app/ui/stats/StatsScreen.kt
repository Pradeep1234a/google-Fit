package com.motioniq.app.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.ui.components.MetricCard
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
    var selectedTimeframe by remember { mutableIntStateOf(1) } // 0: Daily, 1: Weekly, 2: Monthly
    val timeframes = listOf("Daily", "Weekly", "Monthly")

    // Use dynamic weekly step distribution from step engine persistence
    val displayWeeklySteps = if (weeklySteps.isNotEmpty()) {
        weeklySteps
    } else {
        listOf("Mon" to 0L, "Tue" to 0L, "Wed" to 0L, "Thu" to 0L, "Fri" to 0L, "Sat" to 0L, "Sun" to 0L)
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "MOVEMENT ANALYTICS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Track your activity volume, trends, and records.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Timeframe Selector Tabs
        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                timeframes.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = timeframes.size),
                        onClick = { selectedTimeframe = index },
                        selected = selectedTimeframe == index
                    ) {
                        Text(label)
                    }
                }
            }
        }

        // Weekly Bar Chart Canvas (PRD Section 27)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WEEKLY STEP DISTRIBUTION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            val formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(weeklyTotal)
                            Text(
                                text = "$formattedTotal total steps",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            val formattedAvg = NumberFormat.getNumberInstance(Locale.US).format(averageDaily)
                            Text(
                                text = "Avg $formattedAvg / day",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Bar Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxStep = (displayWeeklySteps.maxOfOrNull { it.second }?.toFloat() ?: 10000f).coerceAtLeast(1000f)
                            val barWidth = 24.dp.toPx()
                            val spacing = (size.width - (barWidth * displayWeeklySteps.size)) / (displayWeeklySteps.size + 1)
                            val chartHeight = size.height - 25.dp.toPx()

                            displayWeeklySteps.forEachIndexed { index, pair ->
                                val x = spacing + index * (barWidth + spacing)
                                val barHeight = if (maxStep > 0f) ((pair.second.toFloat() / maxStep) * chartHeight).coerceIn(0f, chartHeight) else 0f
                                val y = chartHeight - barHeight

                                // Background bar
                                drawRoundRect(
                                    color = trackColor,
                                    topLeft = Offset(x, 0f),
                                    size = Size(barWidth, chartHeight),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )

                                // Filled active bar
                                if (barHeight > 0f) {
                                    val isLastDay = index == displayWeeklySteps.size - 1
                                    drawRoundRect(
                                        color = if (isLastDay) primaryColor else primaryColor.copy(alpha = 0.65f),
                                        topLeft = Offset(x, y),
                                        size = Size(barWidth, barHeight),
                                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                    )
                                }
                            }
                        }
                    }

                    // Weekdays Labels Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        displayWeeklySteps.forEachIndexed { index, it ->
                            val isLastDay = index == displayWeeklySteps.size - 1
                            Text(
                                text = it.first,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isLastDay) FontWeight.Bold else FontWeight.Normal,
                                color = if (isLastDay) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Aggregate Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Distance",
                    value = "41.8",
                    unit = "km",
                    icon = Icons.Default.Route,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Active Days",
                    value = "6 / 7",
                    unit = "days",
                    icon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Calories",
                    value = "2,380",
                    unit = "kcal",
                    icon = Icons.Default.LocalFireDepartment,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Avg Pace",
                    value = "9:24",
                    unit = "/km",
                    icon = Icons.Default.Speed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // AI Movement Intelligence Insights (PRD Section 30)
        item {
            Text(
                text = "MOVEMENT INTELLIGENCE INSIGHTS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InsightCard(
                    icon = Icons.Default.TrendingUp,
                    title = "Distance Improvement",
                    description = "You walked 14% farther this week compared to last week (38.4 km vs 33.6 km)."
                )
                InsightCard(
                    icon = Icons.Default.AccessTime,
                    title = "Peak Movement Window",
                    description = "Your most consistent movement happens between 6:00 PM – 8:00 PM."
                )
                InsightCard(
                    icon = Icons.Default.Speed,
                    title = "Pace Progression",
                    description = "Average running pace improved from 6:15/km to 5:48/km on outdoor courses."
                )
            }
        }

        // Personal Records Section (PRD Section 28)
        item {
            Text(
                text = "PERSONAL RECORDS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RecordRow(title = "Longest Walk", value = "6.8 km", date = "Aug 28")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    RecordRow(title = "Longest Run", value = "5.2 km", date = "Sep 01")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    val mostStepsLabel = if (bestDay != null && bestDay.second > 0L) {
                        NumberFormat.getNumberInstance(Locale.US).format(bestDay.second) + " steps"
                    } else "0 steps"
                    val mostStepsDate = bestDay?.first ?: "Today"
                    RecordRow(title = "Most Steps in a Day", value = mostStepsLabel, date = mostStepsDate)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    RecordRow(title = "Longest Streak", value = "Active Tracking", date = "Active")
                }
            }
        }
    }
}

@Composable
private fun InsightCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun RecordRow(title: String, value: String, date: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}
