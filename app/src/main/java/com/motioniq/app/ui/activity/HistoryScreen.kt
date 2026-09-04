package com.motioniq.app.ui.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
    onStartNewActivity: () -> Unit,
    onBackClick: (() -> Unit)? = null
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
        containerColor = SlateGround,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (onBackClick != null) {
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
                    }
                },
                title = {
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search sessions...", color = TextLowDark) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    } else {
                        Text(
                            text = "Telemetry History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSearching = !isSearching; if (!isSearching) searchQuery = "" }) {
                        Icon(
                            imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = StitchCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateGround)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartNewActivity,
                containerColor = StitchCyan,
                contentColor = SlateGround,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Start Activity")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Record Session", fontWeight = FontWeight.Bold)
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
                // Filter Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filterOptions.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) StitchCyan else SlateSurface1)
                                    .border(
                                        1.dp,
                                        if (isSelected) StitchCyan else SlateSurface2,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedFilter = filter }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = filter,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (isSelected) SlateGround else TextMediumDark
                                )
                            }
                        }
                    }
                }

                // Month Section Header
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentMonthYear,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchCyan
                    )
                }

                // Activity Cards List
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
                                color = TextMediumDark,
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
                            ActivityType.RUNNING -> Triple(StitchTeal.copy(alpha = 0.5f), StitchCyan, Icons.Default.DirectionsRun)
                            ActivityType.WALKING -> Triple(KineticEmerald.copy(alpha = 0.2f), KineticEmerald, Icons.Default.DirectionsWalk)
                            ActivityType.CYCLING -> Triple(StitchDarkCyan, StitchCyan, Icons.Default.DirectionsBike)
                            else -> Triple(SlateSurface2, TextMediumDark, Icons.Default.FitnessCenter)
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = SlateSurface1,
                            border = BorderStroke(1.dp, CyanBorderSubtle),
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
                                        .size(46.dp)
                                        .background(tileBg, RoundedCornerShape(12.dp))
                                        .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = activity.type.displayName,
                                        tint = iconTint,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activity.type.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "%.2f km • %s".format(
                                            Locale.US,
                                            activity.distanceMeters / 1000.0,
                                            GpsCalculator.formatDuration(activity.durationSeconds)
                                        ),
                                        fontSize = 13.sp,
                                        color = StitchCyan
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = dateLabel,
                                        fontSize = 11.sp,
                                        color = TextLowDark
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = TextMediumDark,
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
