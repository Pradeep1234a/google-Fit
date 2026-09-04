package com.motioniq.app.ui.secondary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthSyncScreen(
    isAvailable: Boolean,
    isSyncEnabled: Boolean,
    onToggleSync: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    var relayActive by remember { mutableStateOf(isSyncEnabled) }
    var writeDistance by remember { mutableStateOf(true) }
    var writeCadence by remember { mutableStateOf(true) }
    var writeCalories by remember { mutableStateOf(true) }
    var writeElevation by remember { mutableStateOf(true) }

    var readHeartRate by remember { mutableStateOf(true) }
    var readSleep by remember { mutableStateOf(true) }
    var readSpO2 by remember { mutableStateOf(true) }

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
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "HEALTH CONNECT SYNC",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .background(SlateSurface1, RoundedCornerShape(12.dp))
                            .border(1.dp, SlateSurface2, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(KineticEmerald, CircleShape)
                        )
                        Text(
                            text = "Synced 2m ago",
                            fontSize = 10.sp,
                            color = KineticEmerald
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
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp)
        ) {
            // Hero Unified Biometric Sync (Stitch 79efbbf0)
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
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(StitchTeal.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .border(1.dp, StitchCyan, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ShowChart,
                                    contentDescription = null,
                                    tint = StitchCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .background(SlateSurface2, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ANDROID PLATFORM",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = StitchCyan
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(SlateSurface2, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "v4.2",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMediumDark
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Unified Biometric Sync",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Continuous bidirectional data pipeline bridging MOTIONIQ kinematic telemetry with Android Health Connect. Raw inertial sensors and joint vectors are converted to standardized on-device health standards.",
                            fontSize = 12.sp,
                            color = TextMediumDark,
                            lineHeight = 17.sp
                        )

                        // Health Connect Relay card
                        Surface(
                            color = SlateSurface2,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
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
                                            .background(StitchTeal, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.SyncAlt,
                                            contentDescription = null,
                                            tint = StitchCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Health Connect Relay",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (relayActive) "Central telemetry active" else "Relay paused",
                                            fontSize = 11.sp,
                                            color = if (relayActive) StitchCyan else TextLowDark
                                        )
                                    }
                                }

                                Switch(
                                    checked = relayActive,
                                    onCheckedChange = {
                                        relayActive = it
                                        onToggleSync(it)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = SlateGround,
                                        checkedTrackColor = StitchCyan,
                                        uncheckedThumbColor = TextLowDark,
                                        uncheckedTrackColor = SlateSurface3
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Sync Health Indicators
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = SlateSurface1,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SlateSurface2),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "0",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Conflicted Records",
                                fontSize = 11.sp,
                                color = TextLowDark
                            )
                        }
                    }

                    Surface(
                        color = SlateSurface1,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SlateSurface2),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "±1ms",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = StitchCyan
                            )
                            Text(
                                text = "Data Precision",
                                fontSize = 11.sp,
                                color = TextLowDark
                            )
                        }
                    }
                }
            }

            // Section: Data Written to Health Connect
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Data Written to Health Connect",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "4 active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = StitchCyan
                        )
                    }

                    Surface(
                        color = SlateSurface1,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SlateSurface2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                            SyncToggleRow(
                                title = "Distance & Speed",
                                description = "High-frequency GPS intervals and split telemetry",
                                checked = writeDistance,
                                onCheckedChange = { writeDistance = it }
                            )
                            Divider(color = SlateSurface2, thickness = 0.5.dp)
                            SyncToggleRow(
                                title = "Cadence & Steps",
                                description = "120Hz IMU step cadence and bilateral stride length",
                                checked = writeCadence,
                                onCheckedChange = { writeCadence = it }
                            )
                            Divider(color = SlateSurface2, thickness = 0.5.dp)
                            SyncToggleRow(
                                title = "Active Calories & Metabolic Strain",
                                description = "Kinetic workload energy calculations",
                                checked = writeCalories,
                                onCheckedChange = { writeCalories = it }
                            )
                            Divider(color = SlateSurface2, thickness = 0.5.dp)
                            SyncToggleRow(
                                title = "Elevation & Topography",
                                description = "Barometric elevation gain and incline metrics",
                                checked = writeElevation,
                                onCheckedChange = { writeElevation = it }
                            )
                        }
                    }
                }
            }

            // Section: Data Read from Wearables
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Data Read from Wearables",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "3 connected",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = StitchCyan
                        )
                    }

                    Surface(
                        color = SlateSurface1,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SlateSurface2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                            SyncToggleRow(
                                title = "Resting & Active Heart Rate",
                                description = "From Pixel Watch 3 / Garmin via Health Connect",
                                checked = readHeartRate,
                                onCheckedChange = { readHeartRate = it }
                            )
                            Divider(color = SlateSurface2, thickness = 0.5.dp)
                            SyncToggleRow(
                                title = "Sleep Stages & Recovery Score",
                                description = "Deep, REM, and HRV readiness metrics",
                                checked = readSleep,
                                onCheckedChange = { readSleep = it }
                            )
                            Divider(color = SlateSurface2, thickness = 0.5.dp)
                            SyncToggleRow(
                                title = "Blood Oxygen (SpO2)",
                                description = "VO2 Max correlation and altitude response",
                                checked = readSpO2,
                                onCheckedChange = { readSpO2 = it }
                            )
                        }
                    }
                }
            }

            // Sandboxed Encryption Info (Stitch 79efbbf0)
            item {
                Surface(
                    color = SlateSurface1,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CyanBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(StitchTeal.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "On-Device Sandboxed Encryption",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Android Health Connect maintains hardware-backed encryption. MOTIONIQ stores kinematic calculations locally and never transmits your raw biometric profile to advertising brokers or unauthenticated endpoints.",
                                fontSize = 11.sp,
                                color = TextMediumDark,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = TextLowDark,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SlateGround,
                checkedTrackColor = StitchCyan,
                uncheckedThumbColor = TextLowDark,
                uncheckedTrackColor = SlateSurface3
            )
        )
    }
}
