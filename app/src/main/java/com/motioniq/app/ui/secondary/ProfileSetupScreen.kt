package com.motioniq.app.ui.secondary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.UserProfile
import com.motioniq.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    initialProfile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var genderProfile by remember { mutableStateOf("Male") }
    var heightCm by remember { mutableDoubleStateOf(initialProfile.heightCm) }
    var weightKg by remember { mutableDoubleStateOf(initialProfile.weightKg) }
    var ageYears by remember { mutableIntStateOf(initialProfile.age) }
    var heightUnit by remember { mutableStateOf("cm") } // cm or in
    var weightUnit by remember { mutableStateOf("kg") } // kg or lb
    var selectedModality by remember { mutableStateOf("Running") }
    var ambitionLevel by remember { mutableFloatStateOf(1f) } // 0: Recovery, 1: Balanced, 2: High Volume

    var showHeightDialog by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var showAgeDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = SlateGround,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateGround)
                    .statusBarsPadding()
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBackClick != null) {
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
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    // Logo + App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "MOTIONIQ",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    }

                    // User Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(StitchTeal.copy(alpha = 0.5f), CircleShape)
                            .border(1.dp, StitchCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Step & Progress Tracker
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .background(SlateSurface2, RoundedCornerShape(12.dp))
                                .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(StitchCyan, CircleShape)
                            )
                            Text(
                                text = "STEP 2 OF 2",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StitchCyan,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "90% COMPLETE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { 0.9f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = StitchCyan,
                        trackColor = SlateSurface2
                    )
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val updated = initialProfile.copy(
                                heightCm = heightCm,
                                weightKg = weightKg,
                                age = ageYears
                            )
                            onSaveProfile(updated)
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StitchCyan,
                            contentColor = SlateGround
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Complete Calibration & Enter MOTIONIQ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateGround
                            )
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = SlateGround,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TextLowDark,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Encrypted Biometric Telemetry Standard",
                            fontSize = 11.sp,
                            color = TextLowDark
                        )
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
            verticalArrangement = Arrangement.spacedBy(22.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
        ) {
            // Headline
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Biomechanical Baseline",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Calibrate telemetry algorithms to your unique physical profile.",
                        fontSize = 14.sp,
                        color = TextMediumDark,
                        lineHeight = 20.sp
                    )
                }
            }

            // Section 1: Physical Metrics Quad
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. PHYSICAL METRICS QUAD",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = TextMediumDark
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Sensors,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Precision Sync",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StitchCyan
                            )
                        }
                    }

                    // Sex Segmented Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateSurface1, RoundedCornerShape(14.dp))
                            .border(1.dp, SlateSurface2, RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Male", "Female", "Adaptive").forEach { gender ->
                            val isSelected = genderProfile == gender
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) StitchTeal else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) StitchCyan else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { genderProfile = gender },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gender,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextMediumDark
                                )
                            }
                        }
                    }

                    // Stature & Body Mass Dual Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stature Card
                        BaselineMetricCard(
                            modifier = Modifier.weight(1f),
                            title = "STATURE",
                            icon = Icons.Default.SwapVert,
                            primaryValue = if (heightUnit == "cm") "${heightCm.toInt()}" else String.format("%.1f", heightCm / 2.54),
                            unitOptions = listOf("cm", "in"),
                            selectedUnit = heightUnit,
                            onUnitChanged = { heightUnit = it },
                            onClick = { showHeightDialog = true }
                        )

                        // Body Mass Card
                        BaselineMetricCard(
                            modifier = Modifier.weight(1f),
                            title = "BODY MASS",
                            icon = Icons.Default.OpenInFull,
                            primaryValue = if (weightUnit == "kg") String.format("%.1f", weightKg) else String.format("%.1f", weightKg * 2.20462),
                            unitOptions = listOf("kg", "lb"),
                            selectedUnit = weightUnit,
                            onUnitChanged = { weightUnit = it },
                            onClick = { showWeightDialog = true }
                        )
                    }

                    // Date of Birth / Age Full-Width Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAgeDialog = true },
                        color = SlateSurface1,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SlateSurface2)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(StitchTeal.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .border(1.dp, CyanBorderSubtle, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = StitchCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "DATE OF BIRTH / AGE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = TextLowDark
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "May 14, 1994 ·",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "$ageYears yrs",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchCyan
                                        )
                                    }
                                }
                            }

                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Age",
                                tint = TextMediumDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Section 2: Primary Modality Selection
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. PRIMARY MODALITY SELECTION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = TextMediumDark
                        )
                        Text(
                            text = "Select Focus",
                            fontSize = 11.sp,
                            color = TextLowDark
                        )
                    }

                    // 2x2 Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModalityTile(
                            modifier = Modifier.weight(1f),
                            name = "Running",
                            detail = "Cadence & Impact",
                            icon = Icons.Default.DirectionsRun,
                            isSelected = selectedModality == "Running",
                            onClick = { selectedModality = "Running" }
                        )
                        ModalityTile(
                            modifier = Modifier.weight(1f),
                            name = "Walking & Hiking",
                            detail = "Equilibrium Flow",
                            icon = Icons.Default.DirectionsWalk,
                            isSelected = selectedModality == "Walking & Hiking",
                            onClick = { selectedModality = "Walking & Hiking" }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModalityTile(
                            modifier = Modifier.weight(1f),
                            name = "Cycling",
                            detail = "Vector Wattage",
                            icon = Icons.Default.DirectionsBike,
                            isSelected = selectedModality == "Cycling",
                            onClick = { selectedModality = "Cycling" }
                        )
                        ModalityTile(
                            modifier = Modifier.weight(1f),
                            name = "Athletics",
                            detail = "Lateral Agility",
                            icon = Icons.Default.FitnessCenter,
                            isSelected = selectedModality == "Athletics",
                            onClick = { selectedModality = "Athletics" }
                        )
                    }
                }
            }

            // Section 3: Weekly Activity Ambition
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. WEEKLY ACTIVITY AMBITION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = TextMediumDark
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = null,
                                tint = KineticEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Adaptive Load",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KineticEmerald
                            )
                        }
                    }

                    Surface(
                        color = SlateSurface1,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SlateSurface2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "CALCULATED TARGET",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = TextLowDark
                                    )
                                    val (sessions, km) = when (ambitionLevel.toInt()) {
                                        0 -> "2" to "20"
                                        1 -> "4" to "45"
                                        else -> "6" to "70"
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "$sessions",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = StitchCyan
                                        )
                                        Text(
                                            text = "sessions / wk ·",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "$km",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = StitchCyan
                                        )
                                        Text(
                                            text = "km",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                    }
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier
                                        .background(SlateSurface2, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Strain Index",
                                        fontSize = 9.sp,
                                        color = TextLowDark
                                    )
                                    Text(
                                        text = if (ambitionLevel > 1f) "Intense" else "Optimal",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KineticEmerald
                                    )
                                }
                            }

                            // Slider
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Slider(
                                    value = ambitionLevel,
                                    onValueChange = { ambitionLevel = it },
                                    valueRange = 0f..2f,
                                    steps = 1,
                                    colors = SliderDefaults.colors(
                                        thumbColor = StitchCyan,
                                        activeTrackColor = StitchCyan,
                                        inactiveTrackColor = SlateSurface3
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Recovery",
                                        fontSize = 11.sp,
                                        color = if (ambitionLevel < 0.5f) StitchCyan else TextLowDark
                                    )
                                    Text(
                                        text = "Balanced",
                                        fontSize = 11.sp,
                                        color = if (ambitionLevel in 0.5f..1.5f) StitchCyan else TextLowDark
                                    )
                                    Text(
                                        text = "High Volume",
                                        fontSize = 11.sp,
                                        color = if (ambitionLevel > 1.5f) StitchCyan else TextLowDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Continuous Biomechanical Sync
            item {
                Surface(
                    color = SlateSurface1,
                    shape = RoundedCornerShape(16.dp),
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
                                    Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = StitchCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Continuous Biomechanical Sync",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Algorithms will auto-calibrate stride length and bilateral gait equilibrium during your first active session.",
                            fontSize = 12.sp,
                            color = TextMediumDark,
                            lineHeight = 18.sp
                        )

                        // Symmetry Indicator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "L 49.8%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .background(SlateSurface2, RoundedCornerShape(3.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .fillMaxHeight()
                                        .background(StitchCyan, RoundedCornerShape(3.dp))
                                )
                            }

                            Text(
                                text = "50.2% R",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = KineticEmerald,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Equilibrium OK",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KineticEmerald
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs for quick editing values
    if (showHeightDialog) {
        var inputVal by remember { mutableStateOf(heightCm.toInt().toString()) }
        AlertDialog(
            onDismissRequest = { showHeightDialog = false },
            containerColor = SlateSurface1,
            title = { Text("Set Stature (cm)", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = inputVal,
                    onValueChange = { inputVal = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StitchCyan,
                        unfocusedBorderColor = SlateSurface2
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    inputVal.toDoubleOrNull()?.let { heightCm = it }
                    showHeightDialog = false
                }) {
                    Text("Save", color = StitchCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHeightDialog = false }) {
                    Text("Cancel", color = TextMediumDark)
                }
            }
        )
    }

    if (showWeightDialog) {
        var inputVal by remember { mutableStateOf(weightKg.toString()) }
        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            containerColor = SlateSurface1,
            title = { Text("Set Body Mass (kg)", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = inputVal,
                    onValueChange = { inputVal = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StitchCyan,
                        unfocusedBorderColor = SlateSurface2
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    inputVal.toDoubleOrNull()?.let { weightKg = it }
                    showWeightDialog = false
                }) {
                    Text("Save", color = StitchCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) {
                    Text("Cancel", color = TextMediumDark)
                }
            }
        )
    }

    if (showAgeDialog) {
        var inputVal by remember { mutableStateOf(ageYears.toString()) }
        AlertDialog(
            onDismissRequest = { showAgeDialog = false },
            containerColor = SlateSurface1,
            title = { Text("Set Age (Years)", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = inputVal,
                    onValueChange = { inputVal = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StitchCyan,
                        unfocusedBorderColor = SlateSurface2
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    inputVal.toIntOrNull()?.let { ageYears = it }
                    showAgeDialog = false
                }) {
                    Text("Save", color = StitchCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAgeDialog = false }) {
                    Text("Cancel", color = TextMediumDark)
                }
            }
        )
    }
}

@Composable
private fun BaselineMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    primaryValue: String,
    unitOptions: listOfUnits = listOf("cm", "in"),
    selectedUnit: String,
    onUnitChanged: (String) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = SlateSurface1,
        border = BorderStroke(1.dp, SlateSurface2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMediumDark
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = StitchCyan,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = primaryValue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                // Unit Toggle
                Row(
                    modifier = Modifier
                        .background(SlateSurface2, RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    unitOptions.forEach { u ->
                        val isSel = u == selectedUnit
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) StitchTeal else Color.Transparent)
                                .clickable { onUnitChanged(u) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = u,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) StitchCyan else TextLowDark
                            )
                        }
                    }
                }
            }

            // Hairline Accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(StitchCyan.copy(alpha = 0.6f), RoundedCornerShape(1.dp))
            )
        }
    }
}

private typealias listOfUnits = List<String>

@Composable
private fun ModalityTile(
    modifier: Modifier = Modifier,
    name: String,
    detail: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = if (isSelected) SlateSurface2 else SlateSurface1,
        border = BorderStroke(1.dp, if (isSelected) StitchCyan else SlateSurface2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isSelected) StitchTeal else SlateSurface3,
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) StitchCyan else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (isSelected) StitchCyan else TextMediumDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            if (isSelected) StitchCyan else SlateSurface3,
                            CircleShape
                        )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = detail,
                    fontSize = 11.sp,
                    color = if (isSelected) StitchCyan else TextLowDark
                )
            }
        }
    }
}
