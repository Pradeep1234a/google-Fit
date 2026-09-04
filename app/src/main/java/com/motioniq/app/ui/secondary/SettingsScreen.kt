package com.motioniq.app.ui.secondary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onResetDataClick: () -> Unit
) {
    var isOfflineMode by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = SlateGround,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SYSTEM SETTINGS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(SlateSurface1, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateGround)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Preferences",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Device telemetry configuration and subsystem parameters.",
                        fontSize = 13.sp,
                        color = TextMediumDark
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                SettingsRow(
                    icon = Icons.Default.BrightnessMedium,
                    title = "Appearance",
                    trailingText = "Technical Dark (Stitch)",
                    onClick = {}
                )
            }

            item {
                SettingsRow(
                    icon = Icons.Default.Speed,
                    title = "Measurement Units",
                    trailingText = "Metric (km, kg, spm)",
                    onClick = {}
                )
            }

            item {
                SettingsRow(
                    icon = Icons.Default.Place,
                    title = "GNSS & Satellite Precision",
                    trailingText = "Dual Band L1+L5",
                    onClick = {}
                )
            }

            item {
                SettingsRow(
                    icon = Icons.Default.Lock,
                    title = "Biometric Vault & Encryption",
                    trailingText = "AES-256 Enabled",
                    onClick = {}
                )
            }

            item {
                SettingsRow(
                    icon = Icons.Default.Notifications,
                    title = "Telemetry Alerts & Haptics",
                    trailingText = "Active",
                    onClick = onNotificationsClick
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SlateSurface1,
                    border = BorderStroke(1.dp, SlateSurface2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SlateSurface2, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = StitchCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Offline Mode",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Store all kinematics locally",
                                    fontSize = 11.sp,
                                    color = TextLowDark
                                )
                            }
                        }
                        Switch(
                            checked = isOfflineMode,
                            onCheckedChange = { isOfflineMode = it },
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

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SlateSurface1,
                    border = BorderStroke(1.dp, PulseCoral.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showResetDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PulseCoral.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                tint = PulseCoral,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Purge Biometric Ledger & Reset",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseCoral
                            )
                            Text(
                                text = "Clear cryptographic keys and history",
                                fontSize = 11.sp,
                                color = TextLowDark
                            )
                        }
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = SlateSurface1,
            title = { Text("Reset Biometric Ledger?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Text(
                    "This will permanently clear your local workout history, step counters, and calibration profile.",
                    color = TextMediumDark,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDataClick()
                        showResetDialog = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PulseCoral)
                ) {
                    Text("Purge Everything", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextMediumDark)
                }
            }
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SlateSurface1,
        border = BorderStroke(1.dp, SlateSurface2),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(SlateSurface2, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = StitchCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            if (trailingText != null) {
                Text(
                    text = trailingText,
                    fontSize = 12.sp,
                    color = TextMediumDark
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextLowDark,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
