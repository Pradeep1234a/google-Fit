package com.motioniq.app.ui.secondary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.UserProfile
import com.motioniq.app.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    profile: UserProfile,
    currentSteps: Long,
    currentDistanceKm: Double,
    currentActiveMinutes: Long,
    onBackClick: () -> Unit,
    onSaveGoals: (stepGoal: Int, distanceGoalKm: Double, activeMinutesGoal: Int) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var stepGoalInput by remember { mutableStateOf(profile.dailyStepGoal.toString()) }
    var distGoalInput by remember { mutableStateOf(profile.dailyDistanceGoalKm.toString()) }
    var minGoalInput by remember { mutableStateOf(profile.dailyActiveMinutesGoal.toString()) }

    var aiGoalAccepted by remember { mutableStateOf(false) }
    var aiGoalDismissed by remember { mutableStateOf(false) }

    val weeklyTargetKm = (profile.dailyDistanceGoalKm * 7).coerceAtLeast(10.0)
    val weeklyCurrentKm = (currentDistanceKm * 5.2).coerceAtLeast(12.0)
    val progressRatio = (weeklyCurrentKm / weeklyTargetKm).toFloat().coerceIn(0.05f, 1f)
    val remainingKm = (weeklyTargetKm - weeklyCurrentKm).coerceAtLeast(0.0)

    Scaffold(
        containerColor = SlateGround,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateGround)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(SlateSurface1, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "PERFORMANCE GOALS",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(SlateSurface1, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Edit Goals",
                            tint = StitchCyan
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = SlateGround,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = { showEditDialog = true },
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
                                Icons.Default.AddCircleOutline,
                                contentDescription = null,
                                tint = SlateGround,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Calibrate Target Thresholds",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateGround
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
        ) {
            // Hero Weekly Kinetic Target Card (Stitch fac1eb6b)
            item {
                Surface(
                    color = SlateSurface1,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, CyanBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "03. WEEKLY KINETIC TARGET",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = TextMediumDark
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .background(SlateSurface2, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(KineticEmerald, CircleShape)
                                )
                                Text(
                                    text = "Live Cadence",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = KineticEmerald
                                )
                            }
                        }

                        // Metrics & Progress Ring Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.1f", weeklyCurrentKm),
                                        fontSize = 34.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "/ ${String.format(Locale.US, "%.1f", weeklyTargetKm)} km",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextMediumDark,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(StitchCyan, CircleShape)
                                    )
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", remainingKm)} km to goal · 3 days left",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = StitchCyan
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Pace Avg",
                                            fontSize = 11.sp,
                                            color = TextLowDark
                                        )
                                        Text(
                                            text = "4'42\"/km",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(26.dp)
                                            .background(SlateSurface3)
                                    )
                                    Column {
                                        Text(
                                            text = "Efficiency",
                                            fontSize = 11.sp,
                                            color = TextLowDark
                                        )
                                        Text(
                                            text = "+4.2%",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KineticEmerald
                                        )
                                    }
                                }
                            }

                            // Circular Progress Arc
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(96.dp)
                            ) {
                                val percentage = (progressRatio * 100).toInt()
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeWidth = 8.dp.toPx()
                                    drawArc(
                                        color = SlateSurface3,
                                        startAngle = -90f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                    drawArc(
                                        color = StitchCyan,
                                        startAngle = -90f,
                                        sweepAngle = 360f * progressRatio,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$percentage%",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "KINETIC",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = StitchCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Adaptive AI Recommendation Card (Stitch fac1eb6b)
            if (!aiGoalDismissed) {
                item {
                    Surface(
                        color = SlateSurface1,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if (aiGoalAccepted) KineticEmerald else StitchTeal.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
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
                                            .size(34.dp)
                                            .background(StitchTeal.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                            .border(1.dp, StitchCyan, RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = StitchCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Text(
                                        text = "ADAPTIVE AI RECOMMENDATION",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = StitchCyan
                                    )
                                }

                                Text(
                                    text = "Biomechanics Engine",
                                    fontSize = 10.sp,
                                    color = TextLowDark
                                )
                            }

                            Text(
                                text = if (aiGoalAccepted)
                                    "✓ Cadence threshold calibrated to 172 spm. Telemetry engine will monitor ground reaction forces during your next workout."
                                else
                                    "Based on your recent 10.2k run, we recommend calibrating your cadence threshold to 172 spm to optimize energy expenditure.",
                                fontSize = 13.sp,
                                color = if (aiGoalAccepted) Color.White else TextMediumDark,
                                lineHeight = 19.sp
                            )

                            if (!aiGoalAccepted) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { aiGoalAccepted = true },
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = StitchTeal,
                                            contentColor = StitchCyan
                                        ),
                                        border = BorderStroke(1.dp, StitchCyan),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        modifier = Modifier.height(38.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = StitchCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Accept Goal",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StitchCyan
                                            )
                                        }
                                    }

                                    TextButton(
                                        onClick = { aiGoalDismissed = true },
                                        modifier = Modifier.height(38.dp)
                                    ) {
                                        Text(
                                            text = "Dismiss",
                                            fontSize = 12.sp,
                                            color = TextLowDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section: Active Performance Goals
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Performance Goals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(StitchCyan, CircleShape)
                        )
                        Text(
                            text = "Real-time Sync",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = StitchCyan
                        )
                    }
                }
            }

            // Goal Card 1: Cadence Consistency Target
            item {
                GoalPerformanceCard(
                    icon = Icons.Default.Speed,
                    category = "Biomechanics · 4-Week Block",
                    title = "Cadence Consistency Target",
                    progressText = "85%",
                    description = "Maintain 170+ spm for 30m continuous tempo runs. Verified by kinetic shoe pods.",
                    progressFraction = 0.85f,
                    leftStatus = "3 of 4 sessions completed",
                    rightStatus = "1 session remaining"
                )
            }

            // Goal Card 2: Elevation Ascent Challenge
            item {
                GoalPerformanceCard(
                    icon = Icons.Default.Landscape,
                    category = "Vertical Load · Weekly Phase",
                    title = "Elevation Ascent Challenge",
                    progressText = "70%",
                    description = "Accumulate +500m positive incline gain to elevate power thresholds and glute engagement.",
                    progressFraction = 0.70f,
                    leftStatus = "+350m / 500m",
                    rightStatus = "+150m remaining"
                )
            }

            // Goal Card 3: Bilateral Symmetry Calibration
            item {
                GoalPerformanceCard(
                    icon = Icons.Default.Balance,
                    category = "Kinetic Balance · Optimal Symmetry",
                    title = "Bilateral Symmetry Calibration",
                    progressText = "Optimal",
                    description = "Maintain <0.5% gait equilibrium delta across 5 consecutive tracking sessions.",
                    progressFraction = 0.80f,
                    leftStatus = "4 / 5 sessions verified (0.3% delta)",
                    rightStatus = "1 session to seal"
                )
            }

            // Goal Card 4: Active Recovery Days
            item {
                GoalPerformanceCard(
                    icon = Icons.Default.Spa,
                    category = "Goal Met · Decompression",
                    title = "Active Recovery Days",
                    progressText = "100%",
                    description = "2 of 2 rest & mobility decompression routines logged for tissue rejuvenation.",
                    progressFraction = 1f,
                    leftStatus = "2 / 2 completed",
                    rightStatus = "Target Met",
                    isGoalMet = true
                )
            }

            // Laboratory Precision Vector Visualizer
            item {
                Surface(
                    color = SlateSurface1,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SlateSurface2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "LABORATORY PRECISION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = StitchCyan
                            )
                            Text(
                                text = "Gait Vector Visualizer",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Connect external motion markers for millimetric hip extension modeling.",
                                fontSize = 12.sp,
                                color = TextLowDark,
                                lineHeight = 16.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(SlateSurface2, RoundedCornerShape(12.dp))
                                .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccessibilityNew,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Goals Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = SlateSurface1,
            title = {
                Text(
                    text = "Calibrate Target Thresholds",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Customize your daily kinematic and volume metrics:", color = TextMediumDark, fontSize = 13.sp)

                    OutlinedTextField(
                        value = stepGoalInput,
                        onValueChange = { stepGoalInput = it },
                        label = { Text("Daily Steps", color = TextLowDark) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = StitchCyan,
                            unfocusedBorderColor = SlateSurface2
                        )
                    )

                    OutlinedTextField(
                        value = distGoalInput,
                        onValueChange = { distGoalInput = it },
                        label = { Text("Daily Distance (km)", color = TextLowDark) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = StitchCyan,
                            unfocusedBorderColor = SlateSurface2
                        )
                    )

                    OutlinedTextField(
                        value = minGoalInput,
                        onValueChange = { minGoalInput = it },
                        label = { Text("Daily Active Minutes", color = TextLowDark) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = StitchCyan,
                            unfocusedBorderColor = SlateSurface2
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val s = stepGoalInput.toIntOrNull() ?: profile.dailyStepGoal
                    val d = distGoalInput.toDoubleOrNull() ?: profile.dailyDistanceGoalKm
                    val m = minGoalInput.toIntOrNull() ?: profile.dailyActiveMinutesGoal
                    onSaveGoals(s, d, m)
                    showEditDialog = false
                }) {
                    Text("Apply Changes", color = StitchCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextMediumDark)
                }
            }
        )
    }
}

@Composable
private fun GoalPerformanceCard(
    icon: ImageVector,
    category: String,
    title: String,
    progressText: String,
    description: String,
    progressFraction: Float,
    leftStatus: String,
    rightStatus: String,
    isGoalMet: Boolean = false
) {
    Surface(
        color = SlateSurface1,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isGoalMet) KineticEmerald.copy(alpha = 0.5f) else SlateSurface2),
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                if (isGoalMet) KineticEmerald.copy(alpha = 0.15f) else StitchTeal.copy(alpha = 0.4f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (isGoalMet) KineticEmerald else StitchCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isGoalMet) KineticEmerald else StitchCyan,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Text(
                    text = progressText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isGoalMet) KineticEmerald else Color.White
                )
            }

            Text(
                text = description,
                fontSize = 12.sp,
                color = TextMediumDark,
                lineHeight = 17.sp
            )

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(SlateSurface2, RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(
                            if (isGoalMet) KineticEmerald else StitchCyan,
                            RoundedCornerShape(3.dp)
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = leftStatus,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMediumDark
                )
                Text(
                    text = rightStatus,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isGoalMet) KineticEmerald else TextLowDark
                )
            }
        }
    }
}
