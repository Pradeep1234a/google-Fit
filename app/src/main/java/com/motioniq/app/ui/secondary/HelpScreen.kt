package com.motioniq.app.ui.secondary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

private data class HelpTopic(
    val title: String,
    val icon: ImageVector,
    val content: String
)

private val helpTopics = listOf(
    HelpTopic(
        "Getting started",
        Icons.Default.PlayCircleOutline,
        "MOTIONIQ automatically tracks steps in the background using hardware sensors. Tap 'Start Activity' on Home or Activity tab to track outdoor GPS walks, runs, and rides."
    ),
    HelpTopic(
        "How accurate is step tracking?",
        Icons.Default.Speed,
        "MOTIONIQ prioritizes Android's Hardware Step Counter chip when present, guaranteeing <1% battery drain and step-level accuracy. Software pedometer is used as fallback."
    ),
    HelpTopic(
        "How to track a workout?",
        Icons.Default.DirectionsRun,
        "Select your workout type from the Choose Activity screen. MOTIONIQ engages the fused GPS location tracker to map your path, measure cadence, and calculate calories."
    ),
    HelpTopic(
        "Why is my location not working?",
        Icons.Default.LocationOff,
        "Ensure 'Location' permission is set to 'Allow all the time' or 'While using the app', and device Location / GPS toggle is turned on."
    ),
    HelpTopic(
        "Battery usage",
        Icons.Default.BatteryChargingFull,
        "MOTIONIQ uses zero-poll hardware interrupts for daily step counting. Active GPS is only turned on during explicitly recorded outdoor workouts."
    ),
    HelpTopic(
        "Data and privacy",
        Icons.Default.Lock,
        "All step records, coordinates, and routes remain in sandboxed private storage on your device. Zero data is uploaded to third-party ad networks."
    ),
    HelpTopic(
        "Troubleshooting",
        Icons.Default.Build,
        "If steps stop incrementing after a reboot, simply open MOTIONIQ once or ensure battery optimization allows background autostart."
    ),
    HelpTopic(
        "Contact support",
        Icons.Default.Email,
        "Need help? Contact the MOTIONIQ development team at support@motioniq.app."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedTopic by remember { mutableStateOf<String?>(null) }

    val filteredTopics = remember(searchQuery) {
        if (searchQuery.isBlank()) helpTopics else helpTopics.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

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
            // Header (19_Help.png)
            item {
                Text(
                    text = "Help & Support",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Search Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextLowLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search help...", color = TextLowLight) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Topics List
            items(filteredTopics) { topic ->
                val isExpanded = expandedTopic == topic.title

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedTopic = if (isExpanded) null else topic.title
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = topic.icon,
                                contentDescription = null,
                                tint = TextHighLight,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = topic.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextHighLight,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = topic.content,
                                    fontSize = 14.sp,
                                    color = TextMediumLight,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
