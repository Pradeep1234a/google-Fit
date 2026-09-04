package com.motioniq.app.ui.explore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.ActivityType
import com.motioniq.app.model.ParkPlace
import com.motioniq.app.theme.*

@Composable
fun ExploreScreen(
    parks: List<ParkPlace>,
    isGpsActive: Boolean = false,
    onStartRouteClick: (ActivityType) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Parks") }
    val categories = listOf("Parks", "Walking", "Running", "Cycling")

    var selectedParkIndex by remember { mutableIntStateOf(0) }
    val selectedPark = if (parks.isNotEmpty()) parks[selectedParkIndex.coerceIn(0, parks.size - 1)] else null

    Scaffold(
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header (14_Explore.png)
            Text(
                text = "Explore",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextHighLight
            )

            // Search Bar (14_Explore.png)
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
                        contentDescription = "Search",
                        tint = TextLowLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Search parks, routes, places...",
                        fontSize = 14.sp,
                        color = TextLowLight
                    )
                }
            }

            // Category Filter Pills (14_Explore.png)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                if (isSelected) SoftTileBlue else Color.White,
                                CircleShape
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandNavy else TextMediumLight
                        )
                    }
                }
            }

            // Map Area with Green Parks, River, Pins, and Target Center Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(20.dp))
            ) {
                // Stylized Map Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Park polygon
                    val parkPath = Path().apply {
                        moveTo(size.width * 0.35f, size.height * 0.2f)
                        lineTo(size.width * 0.85f, size.height * 0.25f)
                        lineTo(size.width * 0.75f, size.height * 0.65f)
                        lineTo(size.width * 0.38f, size.height * 0.6f)
                        close()
                    }
                    drawPath(parkPath, color = Color(0xFFDCFCE7))

                    // River curve
                    drawLine(
                        color = Color(0xFF93C5FD),
                        start = Offset(0f, size.height * 0.35f),
                        end = Offset(size.width, size.height * 0.55f),
                        strokeWidth = 6.dp.toPx()
                    )

                    // Roads
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = Offset(size.width * 0.3f, 0f),
                        end = Offset(size.width * 0.4f, size.height),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = Offset(0f, size.height * 0.65f),
                        end = Offset(size.width, size.height * 0.65f),
                        strokeWidth = 3.dp.toPx()
                    )
                }

                // Green Park Location Pins
                val pinPositions = listOf(
                    0.25f to 0.45f,
                    0.32f to 0.52f,
                    0.48f to 0.5f,
                    0.6f to 0.38f,
                    0.42f to 0.3f,
                    0.82f to 0.28f
                )

                pinPositions.forEachIndexed { index, (xRatio, yRatio) ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(
                                x = (xRatio * 300).dp,
                                y = (yRatio * 280).dp
                            )
                            .clickable {
                                if (parks.isNotEmpty()) {
                                    selectedParkIndex = index % parks.size
                                }
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF16A34A), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Centering Target FAB Button (14_Explore.png)
                FloatingActionButton(
                    onClick = {},
                    shape = CircleShape,
                    containerColor = Color.White,
                    contentColor = BrandNavy,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 12.dp)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterCenterFocus,
                        contentDescription = "Locate",
                        tint = BrandNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Selected Park Card at Bottom (14_Explore.png)
            if (selectedPark != null) {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartRouteClick(ActivityType.WALKING) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Image Thumbnail Placeholder
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(74.dp)
                                .background(Color(0xFF86EFAC), RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Park,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedPark.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextHighLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "%.1f km • %s • %d min".format(
                                    selectedPark.distanceKm,
                                    selectedPark.difficulty,
                                    selectedPark.etaMinutes
                                ),
                                fontSize = 13.sp,
                                color = TextMediumLight
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Badges: Scenic & Popular
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = SoftTileBlue,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "Scenic",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0284C7),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    color = SoftTileGreen,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "Popular",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Start",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
