package com.motioniq.app.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.model.ActivityType
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.theme.*
import com.motioniq.app.ui.components.EmptyStateView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    activities: List<MovementActivity>,
    onActivityClick: (MovementActivity) -> Unit,
    onStartNewActivity: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val filterOptions = listOf("All", "Walking", "Running", "Cycling")

    val filteredActivities = activities.filter { activity ->
        val matchesFilter = when (selectedFilter) {
            "Walking" -> activity.type == ActivityType.WALKING
            "Running" -> activity.type == ActivityType.RUNNING
            "Cycling" -> activity.type == ActivityType.CYCLING
            else -> true
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            activity.type.displayName.contains(searchQuery, ignoreCase = true) ||
            activity.startPlaceName.contains(searchQuery, ignoreCase = true)
        }
        matchesFilter && matchesSearch
    }

    val currentMonthYear = SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search activities...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    } else {
                        Text(
                            text = "Activity History",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextHighLight
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSearching = !isSearching; if (!isSearching) searchQuery = "" }) {
                        Icon(
                            imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextHighLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartNewActivity,
                containerColor = BrandNavy,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Start Activity")
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Workout", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        if (activities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                EmptyStateView(onStartClick = onStartNewActivity)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
            ) {
                // Filter Chips (09_History.png)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filterOptions.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = filter },
                                label = {
                                    Text(
                                        text = filter,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                },
                                shape = CircleShape,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftTileBlue,
                                    selectedLabelColor = BrandNavy,
                                    containerColor = Color.White,
                                    labelColor = TextMediumLight
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) ElectricBlue else Color(0xFFE2E8F0)
                                )
                            )
                        }
                    }
                }

                // Month Section Header (09_History.png)
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentMonthYear,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHighLight
                    )
                }

                // Activity Cards List (09_History.png)
                if (filteredActivities.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No workouts match your filter.",
                                color = TextMediumLight,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(filteredActivities) { activity ->
                        val dateLabel = try {
                            SimpleDateFormat("d MMM, h:mm a", Locale.US).format(Date(activity.startTimeMillis))
                        } catch (_: Exception) {
                            "Recent"
                        }

                        val (tileBg, iconTint, icon) = when (activity.type) {
                            ActivityType.RUNNING -> Triple(SoftTileOrange, PulseOrange, Icons.Default.DirectionsRun)
                            ActivityType.WALKING -> Triple(SoftTileGreen, KineticGreen, Icons.Default.DirectionsWalk)
                            ActivityType.CYCLING -> Triple(SoftTileBlue, ElectricBlue, Icons.Default.DirectionsBike)
                            else -> Triple(SoftTilePurple, AccentPurple, Icons.Default.FitnessCenter)
                        }

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onActivityClick(activity) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(tileBg, shape = CircleShape)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = activity.type.displayName,
                                        tint = iconTint,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activity.type.displayName,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextHighLight
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "%.2f km • %s".format(
                                            Locale.US,
                                            activity.distanceMeters / 1000.0,
                                            GpsCalculator.formatDuration(activity.durationSeconds)
                                        ),
                                        fontSize = 13.sp,
                                        color = TextMediumLight
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = dateLabel,
                                        fontSize = 12.sp,
                                        color = TextLowLight
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
