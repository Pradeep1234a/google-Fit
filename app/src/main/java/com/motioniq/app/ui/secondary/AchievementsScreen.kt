package com.motioniq.app.ui.secondary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*

private data class Achievement(
    val id: String,
    val title: String,
    val category: String,
    val icon: ImageVector,
    val tileBg: Color,
    val iconTint: Color,
    val isUnlocked: Boolean = true
)

private val achievementsList = listOf(
    Achievement("1", "First Run", "Distance", Icons.Default.DirectionsRun, Color(0xFFFEE2E2), Color(0xFFEF4444)),
    Achievement("2", "5K Steps", "Steps", Icons.Default.DirectionsWalk, Color(0xFFF3E8FF), Color(0xFF9333EA)),
    Achievement("3", "10K Steps", "Steps", Icons.Default.MilitaryTech, Color(0xFFDCFCE7), Color(0xFF16A34A)),
    Achievement("4", "Week Streak", "Consistency", Icons.Default.LocalFireDepartment, Color(0xFFFEF3C7), Color(0xFFD97706)),
    Achievement("5", "Distance Pro", "Distance", Icons.Default.Speed, Color(0xFFDBEAFE), Color(0xFF2563EB)),
    Achievement("6", "Explorer", "Consistency", Icons.Default.Explore, Color(0xFFE0E7FF), Color(0xFF4F46E5))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onBackClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Steps", "Distance", "Consistency")

    val filteredAchievements = remember(selectedCategory) {
        if (selectedCategory == "All") achievementsList else achievementsList.filter { it.category == selectedCategory }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header (12_Achievements.png)
            Column {
                Text(
                    text = "Achievements",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Keep going! You're doing great.",
                    fontSize = 15.sp,
                    color = TextMediumLight
                )
            }

            // Hero Golden Streak Laurel Banner (12_Achievements.png)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(Color(0xFFFEFCE8), RoundedCornerShape(24.dp))
                        .padding(horizontal = 32.dp, vertical = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFEAB308),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "7",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFCA8A04)
                        )
                    }
                    Text(
                        text = "Day Streak",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF854D0E)
                    )
                }
            }

            // Category Filter Pills (12_Achievements.png)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                if (isSelected) SoftTileBlue else Color.White,
                                CircleShape
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandNavy else TextMediumLight
                        )
                    }
                }
            }

            // 2x3 Grid of Achievement Badges (12_Achievements.png)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredAchievements) { achievement ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)
                                .background(achievement.tileBg, CircleShape)
                        ) {
                            Icon(
                                imageVector = achievement.icon,
                                contentDescription = achievement.title,
                                tint = achievement.iconTint,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = achievement.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextHighLight
                        )
                    }
                }
            }
        }
    }
}
