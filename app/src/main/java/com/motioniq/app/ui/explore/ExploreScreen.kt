package com.motioniq.app.ui.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
    var selectedCategory by remember { mutableStateOf("All (12)") }
    val categories = listOf("All (12)", "Running (5)", "Parks (4)", "Walking (3)")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateGround)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Bar: MOTIONIQ Explore, Bell, Avatar (Stitch 5adc5a6b)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(SlateSurface2, RoundedCornerShape(10.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Logo",
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MOTION",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "IQ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = StitchCyan
                            )
                        }
                        Text(
                            text = "EXPLORE",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(SlateSurface2, CircleShape)
                            .border(1.dp, CyanBorderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(StitchCyan, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = StitchDarkCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // 2. Search Bar with Mic & Filter Buttons (Stitch 5adc5a6b)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .background(SlateSurface1, RoundedCornerShape(25.dp))
                        .border(1.dp, CyanBorderSubtle, RoundedCornerShape(25.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextLowDark, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (searchQuery.isEmpty()) "Search parks, trails, routes..." else searchQuery,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (searchQuery.isEmpty()) TextLowDark else Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.Mic, contentDescription = null, tint = TextLowDark, modifier = Modifier.size(20.dp))
                    }
                }

                // Filter button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .background(SlateSurface1, CircleShape)
                        .border(1.dp, CyanBorderSubtle, CircleShape)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Filter", tint = StitchCyan, modifier = Modifier.size(20.dp))
                }
            }
        }

        // 3. Category Filter Chips (Stitch 5adc5a6b)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) StitchCyan else SlateSurface1)
                            .border(1.dp, if (isSelected) StitchCyan else CyanBorderSubtle, RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(StitchDarkCyan, CircleShape)
                            )
                        }
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) StitchDarkCyan else TextMediumDark
                        )
                    }
                }
            }
        }

        // 4. Stylized 3D Map Canvas with Cyan Trail (Stitch 5adc5a6b)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161C1E)),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Map Graphics Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Background Grid & Roads
                        val roadColor = Color(0xFF232C30)
                        drawLine(roadColor, Offset(0f, size.height * 0.3f), Offset(size.width, size.height * 0.35f), strokeWidth = 2.dp.toPx())
                        drawLine(roadColor, Offset(0f, size.height * 0.7f), Offset(size.width, size.height * 0.65f), strokeWidth = 2.dp.toPx())
                        drawLine(roadColor, Offset(size.width * 0.4f, 0f), Offset(size.width * 0.35f, size.height), strokeWidth = 2.dp.toPx())
                        drawLine(roadColor, Offset(size.width * 0.75f, 0f), Offset(size.width * 0.7f, size.height), strokeWidth = 2.dp.toPx())

                        // Cyan Dashed Trail Route
                        val trailPath = Path().apply {
                            moveTo(size.width * 0.2f, size.height * 0.75f)
                            cubicTo(
                                size.width * 0.4f, size.height * 0.45f,
                                size.width * 0.6f, size.height * 0.35f,
                                size.width * 0.75f, size.height * 0.3f
                            )
                        }
                        drawPath(
                            path = trailPath,
                            color = StitchCyan,
                            style = Stroke(
                                width = 3.5.dp.toPx(),
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)
                            )
                        )

                        // Trail Nodes
                        drawCircle(color = StitchCyan, radius = 10.dp.toPx(), center = Offset(size.width * 0.75f, size.height * 0.3f))
                        drawCircle(color = Color(0xFF2A3438), radius = 8.dp.toPx(), center = Offset(size.width * 0.85f, size.height * 0.55f))
                        drawCircle(color = Color(0xFF2A3438), radius = 8.dp.toPx(), center = Offset(size.width * 0.2f, size.height * 0.75f))
                    }

                    // Floating Trail Label
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 40.dp, end = 40.dp)
                            .background(Color(0xFF0F171A), RoundedCornerShape(8.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Emerald Trail",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Map Overlays (3D, Layers, Location)
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .background(SlateSurface2, CircleShape)
                                .border(1.dp, CyanBorderSubtle, CircleShape)
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .background(SlateSurface2, CircleShape)
                                .border(1.dp, CyanBorderSubtle, CircleShape)
                        ) {
                            Text("3D", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Re-center Floating Action Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(14.dp)
                            .size(38.dp)
                            .background(SlateSurface2, CircleShape)
                            .border(1.dp, StitchCyan, CircleShape)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(20.dp))
                    }

                    // GPS Optimal Badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                            .background(SlateSurface1, RoundedCornerShape(14.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(KineticEmerald, CircleShape))
                        Text(
                            text = "GPS OPTIMAL • ±1.2m",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 5. Curated Trails Section (Stitch 5adc5a6b)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Curated Trails",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .background(SlateSurface2, RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "3 NEARBY",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StitchCyan
                            )
                        }
                    }

                    Text(
                        text = "View grid ➔",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = StitchCyan
                    )
                }

                // Horizontal Carousel of Trail Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Trail Card 1
                    CuratedTrailCard(
                        number = "1",
                        badge = "RECOMMENDED",
                        distanceAway = "0.4 km away",
                        trailName = "Riverside Emerald Trail",
                        description = "Smooth riverside loop with dedicated running lane and tree canopies.",
                        distance = "6.4 KM",
                        paceEst = "42 MIN",
                        effort = "Moderate",
                        elevation = "+48m Elev",
                        surfaceType = "Paved",
                        onStart = { onStartRouteClick(ActivityType.RUNNING) }
                    )

                    // Trail Card 2
                    CuratedTrailCard(
                        number = "2",
                        badge = "POPULAR",
                        distanceAway = "1.2 km away",
                        trailName = "Presidio Coastal Path",
                        description = "Elevated coastal singletrack with scenic suspension bridge views.",
                        distance = "8.1 KM",
                        paceEst = "55 MIN",
                        effort = "Challenging",
                        elevation = "+110m Elev",
                        surfaceType = "Trail",
                        onStart = { onStartRouteClick(ActivityType.RUNNING) }
                    )
                }
            }
        }

        // 6. Live Trail Conditions Card (Stitch 5adc5a6b)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .background(SlateSurface2, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.Air, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(20.dp))
                        }

                        Column {
                            Text(
                                text = "TRAIL CONDITIONS",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextLowDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Dry • 17°C • Wind 8 km/h NW",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = "AQI 24 (Ideal)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KineticEmerald
                    )
                }
            }
        }
    }
}

@Composable
private fun CuratedTrailCard(
    number: String,
    badge: String,
    distanceAway: String,
    trailName: String,
    description: String,
    distance: String,
    paceEst: String,
    effort: String,
    elevation: String,
    surfaceType: String,
    onStart: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface1),
        border = BorderStroke(1.dp, CyanBorderSubtle),
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Number + Badge + Distance Away
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(20.dp)
                            .background(StitchCyan, CircleShape)
                    ) {
                        Text(number, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = StitchDarkCyan)
                    }
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StitchCyan
                    )
                }

                Text(
                    text = distanceAway,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLowDark
                )
            }

            // Mock Preview Graphic with Elevation & Surface Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF14242A))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawOval(
                        color = Color(0xFF1A3840),
                        topLeft = Offset(-size.width * 0.1f, size.height * 0.4f),
                        size = androidx.compose.ui.geometry.Size(size.width * 1.2f, size.height * 0.8f)
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(elevation, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(surfaceType, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = Color.White)
                    }
                }
            }

            Text(
                text = trailName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextMediumDark,
                lineHeight = 16.sp
            )

            // Metrics row: Distance, Pace est, Effort
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("DISTANCE", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextLowDark)
                    Text(distance, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = StitchCyan)
                }
                Column {
                    Text("PACE EST.", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextLowDark)
                    Text(paceEst, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column {
                    Text("EFFORT", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextLowDark)
                    Text(effort, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = KineticEmerald)
                }
            }

            // Start Route Button
            Button(
                onClick = onStart,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StitchCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = StitchDarkCyan, modifier = Modifier.size(16.dp))
                    Text("Start Route", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = StitchDarkCyan)
                }
            }
        }
    }
}
