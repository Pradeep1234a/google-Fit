package com.motioniq.app.ui.secondary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*

private data class StitchBadge(
    val id: String,
    val title: String,
    val category: String, // Kinematics, Endurance, Consistency
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean,
    val dateOrProgress: String,
    val xpText: String,
    val progressFraction: Float = 1.0f
)

private val stitchBadges = listOf(
    StitchBadge(
        id = "1",
        title = "Precision Stride",
        category = "Kinematics",
        description = "Sustained <0.2% bilateral gait delta over 6km",
        icon = Icons.Default.GraphicEq,
        isUnlocked = true,
        dateOrProgress = "Oct 22",
        xpText = "+150 XP"
    ),
    StitchBadge(
        id = "2",
        title = "Mountain Goat",
        category = "Endurance",
        description = "Conquered +500m single session elevation gain",
        icon = Icons.Default.Landscape,
        isUnlocked = true,
        dateOrProgress = "Oct 19",
        xpText = "+300 XP"
    ),
    StitchBadge(
        id = "3",
        title = "Century Club",
        category = "Endurance",
        description = "Completed 100km total monthly distance",
        icon = Icons.Default.Speed,
        isUnlocked = true,
        dateOrProgress = "Oct 14",
        xpText = "+200 XP"
    ),
    StitchBadge(
        id = "4",
        title = "Dawn Patrol",
        category = "Consistency",
        description = "5 kinematic workouts started before 6:30 AM",
        icon = Icons.Default.WbTwilight,
        isUnlocked = true,
        dateOrProgress = "Oct 10",
        xpText = "+120 XP"
    ),
    StitchBadge(
        id = "5",
        title = "Apex Aerobic",
        category = "Endurance",
        description = "45 mins in Zone 4 without threshold breach",
        icon = Icons.Default.Favorite,
        isUnlocked = false,
        dateOrProgress = "37 / 45 min",
        xpText = "82%",
        progressFraction = 0.82f
    ),
    StitchBadge(
        id = "6",
        title = "Cartographer",
        category = "Kinematics",
        description = "Explored 10 verified routes in Presidio",
        icon = Icons.Default.Explore,
        isUnlocked = false,
        dateOrProgress = "3 routes left",
        xpText = "7/10",
        progressFraction = 0.70f
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onBackClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Kinematics", "Endurance", "Consistency")

    val displayedBadges = remember(selectedFilter) {
        if (selectedFilter == "All") stitchBadges else stitchBadges.filter { it.category == selectedFilter }
    }

    Scaffold(
        containerColor = SlateGround,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateGround)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(SlateSurface1, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "ACHIEVEMENTS & XP",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(StitchTeal.copy(alpha = 0.5f), CircleShape)
                            .border(1.dp, StitchCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = SlateGround,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { /* Share Badge Showcase */ },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StitchCyan,
                            contentColor = SlateGround
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                tint = SlateGround,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Share Badge Showcase",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateGround
                            )
                        }
                    }

                    Text(
                        text = "Export dynamic biomechanics credential card",
                        fontSize = 11.sp,
                        color = TextLowDark
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
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
        ) {
            // Level Progression & XP Card (Stitch 78b0b50d)
            item {
                Surface(
                    color = SlateSurface1,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, CyanBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ring
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val strokeWidth = 6.dp.toPx()
                                        drawArc(
                                            color = SlateSurface3,
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )
                                        drawArc(
                                            color = StitchCyan,
                                            startAngle = -90f,
                                            sweepAngle = 360f * 0.67f,
                                            useCenter = false,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )
                                    }
                                    Text(
                                        text = "67%",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Unlocked",
                                        fontSize = 11.sp,
                                        color = TextLowDark
                                    )
                                    Text(
                                        text = "24 / 36",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Next Rank",
                                    fontSize = 11.sp,
                                    color = TextLowDark
                                )
                                Text(
                                    text = "Platinum",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchCyan
                                )
                                Text(
                                    text = "8,150 XP",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Level Progression",
                                    fontSize = 11.sp,
                                    color = TextMediumDark
                                )
                                Text(
                                    text = "1,850 XP to Platinum Tier",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchCyan
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(SlateSurface2, RoundedCornerShape(3.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.67f)
                                        .fillMaxHeight()
                                        .background(StitchCyan, RoundedCornerShape(3.dp))
                                )
                            }
                        }
                    }
                }
            }

            // Hero Unlocked Yesterday Banner (Stitch 78b0b50d)
            item {
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, StitchCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        StitchDarkCyan,
                                        StitchTeal
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(StitchCyan.copy(alpha = 0.2f), CircleShape)
                                        .border(1.dp, StitchCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.MilitaryTech,
                                        contentDescription = null,
                                        tint = StitchCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "UNLOCKED YESTERDAY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = StitchCyan
                                        )
                                        Text(
                                            text = "+250 XP",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KineticEmerald
                                        )
                                    }
                                    Text(
                                        text = "Century Sprint",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Maintained 180 spm cadence for 10 consecutive km.",
                                        fontSize = 12.sp,
                                        color = TextMediumDark,
                                        maxLines = 1
                                    )
                                }
                            }

                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Category Filter Pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
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
                                text = if (filter == "All") "✦ All Badges" else "$filter",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) SlateGround else TextMediumDark
                            )
                        }
                    }
                }
            }

            // 2-Column Badges Grid (Stitch 78b0b50d)
            val chunkedBadges = displayedBadges.chunked(2)
            items(chunkedBadges) { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowBadges.forEach { badge ->
                        AchievementTile(
                            modifier = Modifier.weight(1f),
                            badge = badge
                        )
                    }
                    if (rowBadges.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Active Kinematic Streak (Stitch 78b0b50d)
            item {
                Surface(
                    color = SlateSurface1,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, CyanBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
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
                                        .background(PulseCoral.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = PulseCoral,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Active Kinematic Streak",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Optimal joint symmetry threshold",
                                        fontSize = 11.sp,
                                        color = TextLowDark
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "18",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PulseCoral
                                )
                                Text(
                                    text = " Days",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PulseCoral,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                        }

                        // Day badges
                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            days.forEachIndexed { index, day ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = day,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextLowDark
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    index < 4 -> StitchTeal
                                                    index == 4 -> StitchCyan
                                                    else -> SlateSurface2
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when {
                                            index < 4 -> Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = StitchCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            index == 4 -> Icon(
                                                Icons.Default.Bolt,
                                                contentDescription = null,
                                                tint = SlateGround,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            else -> Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(TextLowDark, CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementTile(
    modifier: Modifier = Modifier,
    badge: StitchBadge
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        color = SlateSurface1,
        border = BorderStroke(1.dp, if (badge.isUnlocked) StitchCyan.copy(alpha = 0.2f) else SlateSurface2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            if (badge.isUnlocked) StitchTeal.copy(alpha = 0.5f) else SlateSurface2,
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (badge.isUnlocked) StitchCyan else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        badge.icon,
                        contentDescription = null,
                        tint = if (badge.isUnlocked) StitchCyan else TextMediumDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (badge.isUnlocked) {
                    Box(
                        modifier = Modifier
                            .background(KineticEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Unlocked",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = KineticEmerald
                        )
                    }
                } else {
                    Text(
                        text = badge.xpText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMediumDark
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = badge.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = badge.description,
                    fontSize = 11.sp,
                    color = TextMediumDark,
                    lineHeight = 15.sp,
                    minLines = 2,
                    maxLines = 2
                )
            }

            if (!badge.isUnlocked) {
                // In-progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(SlateSurface2, RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(badge.progressFraction)
                            .fillMaxHeight()
                            .background(StitchCyan, RoundedCornerShape(2.dp))
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = badge.dateOrProgress,
                    fontSize = 10.sp,
                    color = TextLowDark
                )
                if (badge.isUnlocked) {
                    Text(
                        text = badge.xpText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchCyan
                    )
                }
            }
        }
    }
}
