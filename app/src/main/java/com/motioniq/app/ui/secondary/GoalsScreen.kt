package com.motioniq.app.ui.secondary

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.UserProfile
import com.motioniq.app.theme.*
import java.text.NumberFormat
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
    var editSteps by remember { mutableStateOf(profile.dailyStepGoal.toString()) }
    var editDist by remember { mutableStateOf(profile.dailyDistanceGoalKm.toString()) }
    var editActiveMin by remember { mutableStateOf(profile.dailyActiveMinutesGoal.toString()) }

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
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Button(
                    onClick = { showEditDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "Edit Goals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp)
        ) {
            // Header (11_Goals.png)
            item {
                Text(
                    text = "Goals",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Set your goals and stay motivated.",
                    fontSize = 15.sp,
                    color = TextMediumLight
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 1. Daily Steps Goal Card
            item {
                val stepProgress = if (profile.dailyStepGoal > 0) {
                    (currentSteps.toFloat() / profile.dailyStepGoal.toFloat()).coerceIn(0f, 1f)
                } else 0f
                GoalCardItem(
                    icon = Icons.Default.DirectionsWalk,
                    iconTint = Color(0xFF16A34A),
                    badgeBg = SoftTileGreen,
                    title = "Daily Steps",
                    target = "${NumberFormat.getNumberInstance(Locale.US).format(profile.dailyStepGoal)} steps",
                    currentText = "${NumberFormat.getNumberInstance(Locale.US).format(currentSteps)} / ${NumberFormat.getNumberInstance(Locale.US).format(profile.dailyStepGoal)}",
                    progress = stepProgress,
                    progressColor = Color(0xFF16A34A),
                    onClick = { showEditDialog = true }
                )
            }

            // 2. Daily Distance Goal Card
            item {
                val distProgress = if (profile.dailyDistanceGoalKm > 0) {
                    (currentDistanceKm.toFloat() / profile.dailyDistanceGoalKm.toFloat()).coerceIn(0f, 1f)
                } else 0f
                GoalCardItem(
                    icon = Icons.Default.Place,
                    iconTint = Color(0xFF0284C7),
                    badgeBg = SoftTileBlue,
                    title = "Daily Distance",
                    target = "%.0f km".format(Locale.US, profile.dailyDistanceGoalKm),
                    currentText = "%.1f / %.0f km".format(Locale.US, currentDistanceKm, profile.dailyDistanceGoalKm),
                    progress = distProgress,
                    progressColor = ElectricBlue,
                    onClick = { showEditDialog = true }
                )
            }

            // 3. Active Time Goal Card
            item {
                val activeProgress = if (profile.dailyActiveMinutesGoal > 0) {
                    (currentActiveMinutes.toFloat() / profile.dailyActiveMinutesGoal.toFloat()).coerceIn(0f, 1f)
                } else 0f
                GoalCardItem(
                    icon = Icons.Default.Timer,
                    iconTint = Color(0xFFD97706),
                    badgeBg = SoftTileYellow,
                    title = "Active Time",
                    target = "${profile.dailyActiveMinutesGoal} minutes",
                    currentText = "$currentActiveMinutes / ${profile.dailyActiveMinutesGoal} min",
                    progress = activeProgress,
                    progressColor = Color(0xFF2563EB),
                    onClick = { showEditDialog = true }
                )
            }

            // 4. Weekly Running Goal Card
            item {
                GoalCardItem(
                    icon = Icons.Default.DirectionsRun,
                    iconTint = Color(0xFF2563EB),
                    badgeBg = SoftTileBlue,
                    title = "Weekly Running",
                    target = "20 km",
                    currentText = "12.4 / 20 km",
                    progress = 0.62f,
                    progressColor = Color(0xFF2563EB),
                    onClick = { showEditDialog = true }
                )
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Movement Targets", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editSteps,
                        onValueChange = { editSteps = it },
                        label = { Text("Daily Step Goal") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editDist,
                        onValueChange = { editDist = it },
                        label = { Text("Daily Distance (km)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editActiveMin,
                        onValueChange = { editActiveMin = it },
                        label = { Text("Active Minutes Goal") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s = editSteps.toIntOrNull() ?: profile.dailyStepGoal
                        val d = editDist.toDoubleOrNull() ?: profile.dailyDistanceGoalKm
                        val a = editActiveMin.toIntOrNull() ?: profile.dailyActiveMinutesGoal
                        onSaveGoals(s, d, a)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)
                ) {
                    Text("Save Goals")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun GoalCardItem(
    icon: ImageVector,
    iconTint: Color,
    badgeBg: Color,
    title: String,
    target: String,
    currentText: String,
    progress: Float,
    progressColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                    .size(52.dp)
                    .background(badgeBg, CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
                Text(
                    text = target,
                    fontSize = 14.sp,
                    color = TextMediumLight
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Track
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = progressColor,
                    trackColor = Color(0xFFF1F5F9),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextLowLight
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
