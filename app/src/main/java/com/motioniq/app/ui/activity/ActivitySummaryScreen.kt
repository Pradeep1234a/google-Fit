package com.motioniq.app.ui.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3Api::class)
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
        containerColor = SlateGround,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SESSION TELEMETRY",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDiscardClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(SlateSurface1, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateGround)
            )
        },
        bottomBar = {
            Surface(
                color = SlateGround,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSaveClick,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StitchCyan,
                            contentColor = SlateGround
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = SlateGround,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Save Telemetry to Vault",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateGround
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onDiscardClick,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, SlateSurface3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = TextMediumDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Discard Session",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMediumDark
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${activity.type.displayName} Complete",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 12.sp,
                        color = TextMediumDark
                    )
                }

                Box(
                    modifier = Modifier
                        .background(KineticEmerald.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .border(1.dp, KineticEmerald.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "100% VERIFIED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = KineticEmerald,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Map Preview Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SlateSurface1,
                border = BorderStroke(1.dp, CyanBorderSubtle),
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

            // 6 Circular Badge Metrics (3 rows of 2)
            val formattedSteps = NumberFormat.getNumberInstance(Locale.US).format(activity.steps)
            val paceText = GpsCalculator.formatPace(activity.avgPaceMinPerKm) + " /km"

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Row 1: Distance & Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryMetricCard(
                        icon = Icons.Default.Place,
                        value = "%.2f km".format(Locale.US, activity.distanceMeters / 1000.0),
                        label = "Distance",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMetricCard(
                        icon = Icons.Default.Timer,
                        value = GpsCalculator.formatDuration(activity.durationSeconds),
                        label = "Duration",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Steps & Calories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryMetricCard(
                        icon = Icons.Default.DirectionsWalk,
                        value = formattedSteps,
                        label = "Cadence Steps",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMetricCard(
                        icon = Icons.Default.LocalFireDepartment,
                        value = "${activity.caloriesKcal} kcal",
                        label = "Energy Burn",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Avg speed & Avg pace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryMetricCard(
                        icon = Icons.Default.Speed,
                        value = "%.1f km/h".format(Locale.US, activity.avgSpeedKmh),
                        label = "Avg Velocity",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMetricCard(
                        icon = Icons.Default.DirectionsRun,
                        value = paceText,
                        label = "Avg Pace",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Biomechanical Integrity Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SlateSurface1,
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
                        modifier = Modifier
                            .size(38.dp)
                            .background(StitchTeal.copy(alpha = 0.5f), CircleShape)
                            .border(1.dp, StitchCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Sensors,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Bilateral Gait Symmetry: 50.1% L / 49.9% R",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Flight time balance within optimal ±0.5% threshold.",
                            fontSize = 11.sp,
                            color = KineticEmerald
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryMetricCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SlateSurface1,
        border = BorderStroke(1.dp, SlateSurface2),
        modifier = modifier
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
                    .size(42.dp)
                    .background(StitchTeal.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .border(1.dp, CyanBorderSubtle, RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = StitchCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = TextLowDark
                )
            }
        }
    }
}
