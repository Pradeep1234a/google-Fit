package com.motioniq.app.ui.profile

import androidx.compose.foundation.background
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
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundLight
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Title (15_Profile.png)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextHighLight
                    )
                }
            }

            // 2. Avatar with Camera Badge (15_Profile.png)
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 28.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.size(96.dp)
                    ) {
                        // Main Avatar Circle
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFBAE6FD)) // Light cyan/blue
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        // Camera Badge
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(BrandNavy)
                                .clickable { onNavigateToPersonalInfo() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Change Photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // User Name
                    Text(
                        text = profile.name.ifBlank { "Alex" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextHighLight
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // User Email
                    Text(
                        text = "alex@example.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMediumLight
                    )
                }
            }

            // 3. Menu List Items (15_Profile.png)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Personal Information
                    ProfileMenuItem(
                        icon = Icons.Outlined.Person,
                        title = "Personal Information",
                        onClick = onNavigateToPersonalInfo
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Goals
                    ProfileMenuItem(
                        icon = Icons.Outlined.TrackChanges,
                        title = "Goals",
                        onClick = onNavigateToGoals
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Achievements
                    ProfileMenuItem(
                        icon = Icons.Outlined.EmojiEvents,
                        title = "Achievements & Streaks",
                        onClick = onNavigateToAchievements
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Movement Insights
                    ProfileMenuItem(
                        icon = Icons.Outlined.Lightbulb,
                        title = "Movement Insights",
                        onClick = onNavigateToInsights
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Health Connect
                    ProfileMenuItem(
                        icon = Icons.Outlined.FavoriteBorder,
                        title = "Health Connect",
                        subtitle = if (isHealthConnectSyncEnabled) "Connected" else "Disconnected",
                        subtitleColor = if (isHealthConnectSyncEnabled) KineticGreen else TextLowLight,
                        onClick = onNavigateToHealthConnect
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Settings
                    ProfileMenuItem(
                        icon = Icons.Outlined.Settings,
                        title = "Settings",
                        onClick = onNavigateToSettings
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Notifications
                    ProfileMenuItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Notifications",
                        onClick = onNavigateToNotifications
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Help & Support
                    ProfileMenuItem(
                        icon = Icons.Outlined.HelpOutline,
                        title = "Help & Support",
                        onClick = onNavigateToHelp
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // About
                    ProfileMenuItem(
                        icon = Icons.Outlined.Info,
                        title = "About",
                        onClick = { showAboutDialog = true }
                    )
                }
            }
        }
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "MOTIONIQ v1.0.0",
                    fontWeight = FontWeight.Bold,
                    color = BrandNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "“Understand your movement. Not just your steps.”",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy
                    )
                    Text(
                        text = "MOTIONIQ processes step cadence, fused GPS, and activity classification completely on your device. Zero telemetry or movement trails are sold or broadcast to third parties.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "Build: Production Release 1.0 (API 34+)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    subtitleColor: Color = TextLowLight,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextHighLight,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextHighLight
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = subtitleColor
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}
