package com.motioniq.app.ui.secondary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    var tipsAndRecommendations by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextHighLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            // Header (18_Notifications.png)
            item {
                Text(
                    text = "Notifications",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            item {
                NotificationToggleItem(
                    icon = Icons.Default.CalendarToday,
                    title = "Daily summary",
                    subtitle = "Your daily activity recap",
                    checked = dailySummary,
                    onCheckedChange = { dailySummary = it }
                )
            }

            item {
                NotificationToggleItem(
                    icon = Icons.Default.Notifications,
                    title = "Goal reminders",
                    subtitle = "Remind you to stay on track",
                    checked = goalReminders,
                    onCheckedChange = { goalReminders = it }
                )
            }

            item {
                NotificationToggleItem(
                    icon = Icons.Default.CheckCircleOutline,
                    title = "Activity completion",
                    subtitle = "When you finish a workout",
                    checked = activityCompletion,
                    onCheckedChange = { activityCompletion = it }
                )
            }

            item {
                NotificationToggleItem(
                    icon = Icons.Default.MilitaryTech,
                    title = "Achievements",
                    subtitle = "When you unlock a new badge",
                    checked = achievements,
                    onCheckedChange = { achievements = it }
                )
            }

            item {
                NotificationToggleItem(
                    icon = Icons.Default.Insights,
                    title = "Weekly insights",
                    subtitle = "A summary of your progress",
                    checked = weeklyInsights,
                    onCheckedChange = { weeklyInsights = it }
                )
            }

            item {
                NotificationToggleItem(
                    icon = Icons.Default.Lightbulb,
                    title = "Tips and recommendations",
                    subtitle = null,
                    checked = tipsAndRecommendations,
                    onCheckedChange = { tipsAndRecommendations = it }
                )
            }
        }
    }
}

@Composable
private fun NotificationToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = TextHighLight,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextHighLight
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = TextMediumLight
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF0284C7)
                )
            )
        }
    }
}
