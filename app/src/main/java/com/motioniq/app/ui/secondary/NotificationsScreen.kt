package com.motioniq.app.ui.secondary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit
) {
    var dailySummary by remember { mutableStateOf(true) }
    var goalReminders by remember { mutableStateOf(true) }
    var activityCompletion by remember { mutableStateOf(true) }
    var achievements by remember { mutableStateOf(true) }
    var weeklyInsights by remember { mutableStateOf(true) }
    var gaitDriftAlerts by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = SlateGround,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TELEMETRY NOTIFICATIONS",
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Alerts & Cues",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Real-time haptic & audio feedback on kinematic events.",
                        fontSize = 13.sp,
                        color = TextMediumDark
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SlateSurface1,
                    border = BorderStroke(1.dp, CyanBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        NotificationToggleItem(
                            icon = Icons.Default.Vibration,
                            title = "Gait asymmetry alert",
                            subtitle = "Trigger haptic pulse if bilateral delta >2.0%",
                            checked = gaitDriftAlerts,
                            onCheckedChange = { gaitDriftAlerts = it }
                        )
                        Divider(color = SlateSurface2, thickness = 0.5.dp)
                        NotificationToggleItem(
                            icon = Icons.Default.Speed,
                            title = "Cadence pacing cues",
                            subtitle = "Metronome audio cue when slipping below target spm",
                            checked = goalReminders,
                            onCheckedChange = { goalReminders = it }
                        )
                        Divider(color = SlateSurface2, thickness = 0.5.dp)
                        NotificationToggleItem(
                            icon = Icons.Default.CheckCircleOutline,
                            title = "Session completion summary",
                            subtitle = "Instant kinematic report upon saving activity",
                            checked = activityCompletion,
                            onCheckedChange = { activityCompletion = it }
                        )
                        Divider(color = SlateSurface2, thickness = 0.5.dp)
                        NotificationToggleItem(
                            icon = Icons.Default.MilitaryTech,
                            title = "Achievement unlocked",
                            subtitle = "XP awards and tier advancement banners",
                            checked = achievements,
                            onCheckedChange = { achievements = it }
                        )
                        Divider(color = SlateSurface2, thickness = 0.5.dp)
                        NotificationToggleItem(
                            icon = Icons.Default.CalendarToday,
                            title = "Daily volume briefing",
                            subtitle = "Daily morning targets and recovery index",
                            checked = dailySummary,
                            onCheckedChange = { dailySummary = it }
                        )
                        Divider(color = SlateSurface2, thickness = 0.5.dp)
                        NotificationToggleItem(
                            icon = Icons.Default.Insights,
                            title = "Weekly biomechanic digest",
                            subtitle = "7-day joint load and efficiency analysis",
                            checked = weeklyInsights,
                            onCheckedChange = { weeklyInsights = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(StitchTeal.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = StitchCyan,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextMediumDark
            )
        }

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
