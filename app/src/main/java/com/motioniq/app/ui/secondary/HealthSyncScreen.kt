package com.motioniq.app.ui.secondary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    var syncSteps by remember { mutableStateOf(true) }
    var syncActiveTime by remember { mutableStateOf(true) }
    var syncWorkouts by remember { mutableStateOf(true) }
    var syncCalories by remember { mutableStateOf(true) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header (17_HealthSync.png)
            Text(
                text = "Health Connect",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextHighLight
            )

            // Top Status Hero: Heart icon & "Connected" badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(54.dp)
                        .background(SoftTileBlue, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF16A34A), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAvailable) "Connected" else "Available",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A)
                        )
                    }
                }
            }

            Text(
                text = "Sync your health and fitness data with Android Health Connect.",
                fontSize = 15.sp,
                color = TextMediumLight,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Sync Data Toggle Card (17_HealthSync.png)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .background(SoftTileCyan, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sync data",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextHighLight
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Automatically sync activity data",
                            fontSize = 13.sp,
                            color = TextMediumLight
                        )
                    }

                    Switch(
                        checked = isSyncEnabled,
                        onCheckedChange = onToggleSync,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0284C7)
                        )
                    )
                }
            }

            // Data To Sync Section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Data to sync",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )

                Spacer(modifier = Modifier.height(4.dp))

                SyncCheckboxRow(title = "Steps", checked = syncSteps, onCheckedChange = { syncSteps = it })
                SyncCheckboxRow(title = "Active time", checked = syncActiveTime, onCheckedChange = { syncActiveTime = it })
                SyncCheckboxRow(title = "Workouts", checked = syncWorkouts, onCheckedChange = { syncWorkouts = it })
                SyncCheckboxRow(title = "Calories", checked = syncCalories, onCheckedChange = { syncCalories = it })
            }
        }
    }
}

@Composable
private fun SyncCheckboxRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF0284C7),
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextHighLight
        )
    }
}
