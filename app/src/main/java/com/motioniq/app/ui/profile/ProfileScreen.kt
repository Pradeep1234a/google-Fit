package com.motioniq.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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

@Composable
fun ProfileScreen(
    profile: UserProfile,
    isHealthConnectSyncEnabled: Boolean = true,
    onNavigateToPersonalInfo: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToInsights: () -> Unit = {},
    onNavigateToHealthConnect: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onResetData: () -> Unit = {}
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var smartWatchMirroring by remember { mutableStateOf(true) }
    var audioHapticCues by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("Dark") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateGround)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Bar: MOTIONIQ Settings, Avatar, Telemetry Link Active (Stitch 37982097)
        item {
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
                            .size(38.dp)
                            .background(SlateSurface2, RoundedCornerShape(10.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Settings",
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MOTION",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "IQ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = StitchCyan
                            )
                        }
                        Text(
                            text = "SETTINGS & PROFILE",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .background(StitchCyan, CircleShape)
                        .clickable { onNavigateToPersonalInfo() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = StitchDarkCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // 2. Telemetry Link Active Badge (Stitch 37982097)
        item {
            Row(
                modifier = Modifier
                    .background(SlateSurface1, RoundedCornerShape(14.dp))
                    .border(1.dp, CyanBorderSubtle, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.size(6.dp).background(KineticEmerald, CircleShape))
                Text(
                    text = "TELEMETRY LINK ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // 3. User Bio-Baseline Card (Stitch 37982097)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(54.dp)
                                .background(StitchTeal.copy(alpha = 0.35f), CircleShape)
                                .border(1.5.dp, StitchCyan, CircleShape)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(32.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = profile.name.ifBlank { "Alex Mercer" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = StitchCyan, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Kinetic Tier: Platinum • Member since Jan 2024",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMediumDark
                            )
                        }
                    }

                    // 3-Metric Boxes: Weight, Height, Max HR
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricSmallBox(
                            title = "WEIGHT",
                            value = "${profile.weightKg.toInt()} kg",
                            modifier = Modifier.weight(1f)
                        )
                        MetricSmallBox(
                            title = "HEIGHT",
                            value = "${profile.heightCm.toInt()} cm",
                            modifier = Modifier.weight(1f)
                        )
                        MetricSmallBox(
                            title = "MAX HR",
                            value = "194 bpm",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Sub-row: VO2 Max & Edit Bio-Baseline
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(16.dp))
                            Text("VO2 Max: 58.2 ml/kg", style = MaterialTheme.typography.bodySmall, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }

                        Text(
                            text = "Edit Bio-Baseline ➔",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan,
                            modifier = Modifier.clickable { onNavigateToPersonalInfo() }
                        )
                    }
                }
            }
        }

        // 4. Sensors & Hardware (Stitch 37982097)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
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
                        Text("SENSORS & HARDWARE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextLowDark)
                        Text("3 Linked", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = StitchCyan)
                    }

                    // Kinetic Shoe Pods
                    HardwareRow(
                        icon = Icons.Default.DirectionsRun,
                        title = "Kinetic Shoe Pods (L & R)",
                        subtitle = "● Connected: 94% Battery",
                        isSuccess = true
                    )

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Heart Rate Monitor
                    HardwareRow(
                        icon = Icons.Default.FavoriteBorder,
                        title = "Heart Rate Monitor",
                        subtitle = "Polar H10 • BLE Stream Active",
                        badge = "SYNC",
                        isSuccess = true
                    )

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Smartwatch Mirroring
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SlateSurface2, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Watch, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text("Smartwatch Mirroring", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Pixel Watch 3 active", style = MaterialTheme.typography.bodySmall, color = TextMediumDark)
                            }
                        }

                        Switch(
                            checked = smartWatchMirroring,
                            onCheckedChange = { smartWatchMirroring = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = StitchCyan, checkedTrackColor = StitchTeal)
                        )
                    }

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Pair New Sensor Device
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.BluetoothSearching, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(18.dp))
                            Text("Pair New Sensor Device", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = StitchCyan)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextLowDark, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // 5. Biomechanical Preferences (Stitch 37982097)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("BIOMECHANICAL PREFERENCES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextLowDark)

                    // Modality Defaults
                    SettingsNavRow(
                        title = "Modality Defaults",
                        subtitle = "Running (Cadence & Symmetry)",
                        onClick = onNavigateToGoals
                    )

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Sampling Rate
                    SettingsNavRow(
                        title = "Telemetry Sampling Rate",
                        subtitle = "120 Hz (High Precision / Pro)",
                        badge = "HIGH",
                        onClick = {}
                    )

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Audio & Haptic Cues
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Audio & Haptic Kinetic Cues", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Downhill over-stride alerts enabled", style = MaterialTheme.typography.bodySmall, color = TextMediumDark)
                        }
                        Switch(
                            checked = audioHapticCues,
                            onCheckedChange = { audioHapticCues = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = StitchCyan, checkedTrackColor = StitchTeal)
                        )
                    }

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Units of Measurement
                    SettingsNavRow(
                        title = "Units of Measurement",
                        subtitle = "Metric (km, kg, cm)",
                        onClick = {}
                    )
                }
            }
        }

        // 6. App & Cloud Ecosystem (Stitch 37982097)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("APP & CLOUD ECOSYSTEM", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextLowDark)

                    // Android Health Connect
                    SettingsNavRow(
                        title = "Android Health Connect",
                        subtitle = if (isHealthConnectSyncEnabled) "Fully Synced • 7 Metrics" else "Sync Disabled",
                        icon = Icons.Default.Favorite,
                        onClick = onNavigateToHealthConnect
                    )

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Achievements Shortcut
                    SettingsNavRow(
                        title = "Achievements & Streaks",
                        subtitle = "24 / 36 Badges Unlocked • Platinum Rank",
                        icon = Icons.Default.EmojiEvents,
                        onClick = onNavigateToAchievements
                    )

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Offline Map Storage
                    SettingsNavRow(
                        title = "Offline Map Storage",
                        subtitle = "3 Regions Downloaded (142 MB)",
                        icon = Icons.Default.Map,
                        onClick = {}
                    )

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Theme Preference: Segmented control
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Theme Preference", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateSurface2, RoundedCornerShape(20.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Light", "Dark", "System").forEach { theme ->
                                val isSelected = selectedTheme == theme
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) SlateSurface1 else Color.Transparent)
                                        .border(1.dp, if (isSelected) CyanBorderSubtle else Color.Transparent, RoundedCornerShape(16.dp))
                                        .clickable { selectedTheme = theme }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = theme,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) StitchCyan else TextMediumDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Account & Security (Stitch 37982097)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("ACCOUNT & SECURITY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextLowDark)

                    // Cryptographic Vault
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SlateSurface2, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text("Cryptographic Vault", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("AES-256 On-Device Active", style = MaterialTheme.typography.bodySmall, color = TextMediumDark)
                            }
                        }
                        Icon(Icons.Default.Lock, contentDescription = null, tint = KineticEmerald, modifier = Modifier.size(18.dp))
                    }

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Export Raw Biometrics
                    SettingsNavRow(
                        title = "Export Raw Biometrics",
                        subtitle = "JSON / CSV Raw Timeseries",
                        icon = Icons.Default.FileDownload,
                        onClick = {}
                    )

                    HorizontalDivider(color = CyanBorderSubtle)

                    // Help & Privacy Policy
                    SettingsNavRow(
                        title = "Help & Privacy Policy",
                        subtitle = "Sensor FAQ & battery optimization",
                        icon = Icons.Default.HelpOutline,
                        onClick = onNavigateToHelp
                    )
                }
            }
        }

        // 8. Log Out / Reset Data Button (Stitch 37982097)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showResetDialog = true },
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A1616)),
                    border = BorderStroke(1.dp, Color(0xFF5A2020)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        Text("Reset All Saved Data", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                    }
                }

                Text(
                    text = "MOTIONIQ v2.4.0 [Build 581] • Android M3\nSensor Kernel: Synced • Bluetooth BLE 5.3 Ready",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = TextLowDark,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Movement Data", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text("This will permanently erase all local routes, step history, and biomechanical telemetry.", color = TextMediumDark) },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Reset", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel", color = TextMediumDark) }
            },
            containerColor = SlateSurface1
        )
    }
}

@Composable
private fun MetricSmallBox(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(SlateSurface2, RoundedCornerShape(10.dp))
            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextLowDark, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun HardwareRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    isSuccess: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(SlateSurface2, RoundedCornerShape(8.dp))
            ) {
                Icon(icon, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = if (isSuccess) KineticEmerald else TextMediumDark)
            }
        }

        if (badge != null) {
            Box(
                modifier = Modifier
                    .background(StitchTeal.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(badge, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = StitchCyan)
            }
        }
    }
}

@Composable
private fun SettingsNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector? = null,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (icon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .background(SlateSurface2, RoundedCornerShape(8.dp))
                ) {
                    Icon(icon, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMediumDark)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .background(StitchTeal.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(badge, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = StitchCyan)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextLowDark, modifier = Modifier.size(18.dp))
        }
    }
}
