package com.motioniq.app.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.motioniq.app.model.ActivityType
import com.motioniq.app.model.RoutePoint
import com.motioniq.app.model.WorkoutState
import com.motioniq.app.theme.*
import com.motioniq.app.ui.components.RouteMapCanvas
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LiveActivityScreen(
    activityType: ActivityType,
    state: WorkoutState,
    durationSeconds: Long,
    distanceMeters: Double,
    steps: Long,
    calories: Int,
    speedKmh: Double,
    paceMinPerKm: Double,
    routePoints: List<RoutePoint>,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
    ) {
        // TOP HALF: Dark Navy HUD (07_ActiveTracking.png)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header: Back, Activity Name, GPS indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPauseClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = activityType.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(CardDarkElevated, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "GPS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(KineticGreen, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Duration Timer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = GpsCalculator.formatDuration(durationSeconds),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "Duration",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2x2 Metric Cards Grid
            val formattedSteps = NumberFormat.getNumberInstance(Locale.US).format(steps)
            val paceText = if (paceMinPerKm > 0 && paceMinPerKm < 60) {
                val mins = paceMinPerKm.toInt()
                val secs = ((paceMinPerKm - mins) * 60).toInt()
                "%d:%02d".format(Locale.US, mins, secs)
            } else "--:--"

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Distance
                    HudMetricBox(
                        value = "%.2f".format(Locale.US, distanceMeters / 1000.0),
                        label = "Distance (km)",
                        modifier = Modifier.weight(1f)
                    )
                    // Pace
                    HudMetricBox(
                        value = paceText,
                        label = "Pace (/km)",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Steps
                    HudMetricBox(
                        value = formattedSteps,
                        label = "Steps",
                        modifier = Modifier.weight(1f)
                    )
                    // Calories
                    HudMetricBox(
                        value = "$calories",
                        label = "Calories",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // BOTTOM HALF: Interactive Route Map Canvas with Floating Controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            RouteMapCanvas(
                routePoints = routePoints,
                modifier = Modifier.fillMaxSize(),
                isLiveTracking = true
            )

            // Floating Bottom Action Pills (07_ActiveTracking.png)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pause / Resume Pill Button
                if (state == WorkoutState.ACTIVE) {
                    Button(
                        onClick = onPauseClick,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pause",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Button(
                        onClick = onResumeClick,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = KineticGreen),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Resume",
                            tint = BrandNavy
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Resume",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                    }
                }

                // Stop Pill Button (Red)
                Button(
                    onClick = onStopClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF334B)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stop",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun HudMetricBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(CardDarkElevated, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
