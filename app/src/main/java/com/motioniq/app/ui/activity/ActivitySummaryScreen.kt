package com.motioniq.app.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.ui.components.MetricCard
import com.motioniq.app.ui.components.RouteMapCanvas
import java.util.Locale

@Composable
fun ActivitySummaryScreen(
    activity: MovementActivity,
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Celebration Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${activity.type.displayName.uppercase()} COMPLETE 🎉",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Great movement session recorded!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Route Map Preview Canvas
        RouteMapCanvas(
            routePoints = activity.routePoints,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            isLiveTracking = false
        )

        // Start / End Points Card (PRD Section 20)
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF4CAF50), RoundedCornerShape(5.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Started at ${activity.startPlaceName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFE53935), RoundedCornerShape(5.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Ended at ${activity.endPlaceName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Metrics Grid (PRD Section 45)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Distance",
                value = "%.2f".format(Locale.US, activity.distanceMeters / 1000.0),
                unit = "km",
                icon = Icons.Default.Route,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Duration",
                value = GpsCalculator.formatDuration(activity.durationSeconds),
                unit = "",
                icon = Icons.Default.Timer,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Steps",
                value = "${activity.steps}",
                unit = "steps",
                icon = Icons.Default.DirectionsWalk,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Est. Calories",
                value = "≈${activity.caloriesKcal}",
                unit = "kcal",
                icon = Icons.Default.LocalFireDepartment,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Avg Speed",
                value = "%.2f".format(Locale.US, activity.avgSpeedKmh),
                unit = "km/h",
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Avg Pace",
                value = GpsCalculator.formatPace(activity.avgPaceMinPerKm),
                unit = "",
                icon = Icons.Default.Timer,
                modifier = Modifier.weight(1f)
            )
        }

        // Confidence Indicator Badge (PRD Section 49)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confidence: ${activity.confidenceLevel.name} • Step Source: ${activity.stepSource.name.replace('_', ' ')}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons: [DISCARD] [SAVE]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedButton(
                onClick = onDiscardClick,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
            ) {
                Text("DISCARD", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onSaveClick,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("SAVE SESSION", fontWeight = FontWeight.Bold)
            }
        }
    }
}
