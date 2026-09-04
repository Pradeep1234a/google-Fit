package com.motioniq.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.UserProfile

@Composable
fun ProfileScreen(
    profile: UserProfile,
    hasHardwareStepSensor: Boolean,
    diagnostics: Map<String, Any> = emptyMap(),
    isHealthConnectAvailable: Boolean = false,
    isHealthConnectSyncEnabled: Boolean = false,
    onToggleHealthConnectSync: (Boolean) -> Unit = {},
    onUpdateProfile: (UserProfile) -> Unit,
    onResetData: () -> Unit
) {
    var showDiagnostics by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var tempStepGoal by remember { mutableStateOf(profile.dailyStepGoal.toString()) }
    var tempDistGoal by remember { mutableStateOf(profile.dailyDistanceGoalKm.toString()) }

    var showProfileDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(profile.name) }
    var tempWeight by remember { mutableStateOf(profile.weightKg.toString()) }
    var tempHeight by remember { mutableStateOf(profile.heightCm.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "PROFILE & SETTINGS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Manage your movement goals, sensors, and privacy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Profile Avatar Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Text(
                            text = profile.name.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${profile.weightKg} kg • ${profile.heightCm} cm • ${profile.age} yrs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { showProfileDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Goals Management Section (PRD Section 32)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY MOVEMENT GOALS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { showGoalDialog = true }) {
                    Text("Edit Goals")
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    GoalRow(
                        icon = Icons.Default.DirectionsWalk,
                        label = "Daily Steps Target",
                        value = "${profile.dailyStepGoal} steps"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    GoalRow(
                        icon = Icons.Default.Route,
                        label = "Daily Distance Target",
                        value = "${profile.dailyDistanceGoalKm} km"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    GoalRow(
                        icon = Icons.Default.Timer,
                        label = "Active Movement Time",
                        value = "${profile.dailyActiveMinutesGoal} minutes"
                    )
                }
            }
        }

        // Sensors & Permissions Card (PRD Section 33)
        item {
            Text(
                text = "HARDWARE SENSORS & PRIVACY",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SensorStatusRow(
                        title = "Hardware Step Counter",
                        status = if (hasHardwareStepSensor) "Detected & Active" else "Fallback Motion Estimation",
                        isSuccess = hasHardwareStepSensor
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    SensorStatusRow(
                        title = "Fused GPS Location Stack",
                        status = "High Accuracy Enabled",
                        isSuccess = true
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    SensorStatusRow(
                        title = "Health Connect Platform",
                        status = if (isHealthConnectAvailable) {
                            if (isHealthConnectSyncEnabled) "Active (Sync Enabled)" else "Available (Sync Disabled)"
                        } else "Not Installed",
                        isSuccess = isHealthConnectAvailable
                    )
                }
            }
        }

        // Sensor Diagnostics & Debugging (PRD Section 19)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BugReport, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sensor Diagnostics & Telemetry",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(onClick = { showDiagnostics = !showDiagnostics }) {
                            Text(if (showDiagnostics) "Hide" else "Inspect")
                        }
                    }

                    if (showDiagnostics) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                        if (diagnostics.isEmpty()) {
                            Text("No telemetry data captured yet.", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                diagnostics.forEach { (key, value) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = value.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Privacy Principles Card (PRD Section 34)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy: Local-First Movement Data",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Your routes, speed, step cadence, and coordinates are stored exclusively on your device storage. No location trails are sold or broadcast to third-party servers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = onResetData,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset All Saved Data")
                    }
                }
            }
        }

        // Version & App Info
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MOTIONIQ v1.0.0 (Build 1)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Package: com.motioniq.app",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }

    // Goal Edit Dialog
    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Set Daily Movement Goals") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = tempStepGoal,
                        onValueChange = { tempStepGoal = it },
                        label = { Text("Daily Steps Goal") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempDistGoal,
                        onValueChange = { tempDistGoal = it },
                        label = { Text("Daily Distance (km)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val steps = tempStepGoal.toIntOrNull() ?: profile.dailyStepGoal
                        val dist = tempDistDistGoal(tempDistGoal, profile.dailyDistanceGoalKm)
                        onUpdateProfile(profile.copy(dailyStepGoal = steps, dailyDistanceGoalKm = dist))
                        showGoalDialog = false
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Profile Edit Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Edit Personal Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Your Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempWeight,
                        onValueChange = { tempWeight = it },
                        label = { Text("Weight (kg)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempHeight,
                        onValueChange = { tempHeight = it },
                        label = { Text("Height (cm)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = tempWeight.toDoubleOrNull() ?: profile.weightKg
                        val h = tempHeight.toDoubleOrNull() ?: profile.heightCm
                        onUpdateProfile(profile.copy(name = tempName, weightKg = w, heightCm = h))
                        showProfileDialog = false
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) { Text("Cancel") }
            }
        )
    }
}

private fun tempDistDistGoal(text: String, default: Double): Double = text.toDoubleOrNull() ?: default

@Composable
private fun GoalRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SensorStatusRow(title: String, status: String, isSuccess: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
    }
}
