package com.motioniq.app.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.theme.*
import com.motioniq.app.ui.components.RouteMapCanvas
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivitySummaryScreen(
    activity: MovementActivity,
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val formattedDate = try {
        val sdf = SimpleDateFormat("EEE, d MMM yyyy • h:mm a", Locale.US)
        sdf.format(Date(activity.startTimeMillis))
    } catch (_: Exception) {
        "Today • Recorded Session"
    }

    Scaffold(
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header (08_ActivitySummary.png)
            Column {
                Text(
                    text = "${activity.type.displayName} Complete! \uD83C\uDF89",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDate,
                    fontSize = 14.sp,
                    color = TextMediumLight
                )
            }

            // Map Preview Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                RouteMapCanvas(
                    routePoints = activity.routePoints,
                    modifier = Modifier.fillMaxSize(),
                    isLiveTracking = false
                )
            }

            // 6 Circular Badge Metrics (3 rows of 2) (08_ActivitySummary.png)
            val formattedSteps = NumberFormat.getNumberInstance(Locale.US).format(activity.steps)
            val paceText = GpsCalculator.formatPace(activity.avgPaceMinPerKm) + " /km"

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Row 1: Distance & Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryMetricItem(
                        icon = Icons.Default.Place,
                        iconTint = ElectricBlue,
                        badgeBg = SoftTileBlue,
                        value = "%.2f km".format(Locale.US, activity.distanceMeters / 1000.0),
                        label = "Distance",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMetricItem(
                        icon = Icons.Default.Timer,
                        iconTint = KineticGreen,
                        badgeBg = SoftTileGreen,
                        value = GpsCalculator.formatDuration(activity.durationSeconds),
                        label = "Duration",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Steps & Calories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryMetricItem(
                        icon = Icons.Default.DirectionsWalk,
                        iconTint = ElectricBlue,
                        badgeBg = SoftTileBlue,
                        value = formattedSteps,
                        label = "Steps",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMetricItem(
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = PulseOrange,
                        badgeBg = SoftTileOrange,
                        value = "${activity.caloriesKcal}",
                        label = "Calories",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Avg speed & Avg pace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryMetricItem(
                        icon = Icons.Default.Speed,
                        iconTint = Color(0xFF0284C7),
                        badgeBg = SoftTileBlue,
                        value = "%.2f km/h".format(Locale.US, activity.avgSpeedKmh),
                        label = "Avg speed",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMetricItem(
                        icon = Icons.Default.DirectionsRun,
                        iconTint = AccentPurple,
                        badgeBg = SoftTilePurple,
                        value = paceText,
                        label = "Avg pace",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Primary "Save Activity" and Secondary "Share" (08_ActivitySummary.png)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onSaveClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "Save Activity",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onDiscardClick,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TextHighLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHighLight
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryMetricItem(
    icon: ImageVector,
    iconTint: Color,
    badgeBg: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(46.dp)
                .background(badgeBg, CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
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
