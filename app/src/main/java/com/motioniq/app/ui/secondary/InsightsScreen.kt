package com.motioniq.app.ui.secondary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onBackClick: () -> Unit
) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            // Header (13_Insights.png)
            item {
                Text(
                    text = "Insights",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 1. Distance Insight Card
            item {
                InsightRowCard(
                    icon = Icons.Default.Lightbulb,
                    iconTint = Color(0xFF16A34A),
                    badgeBg = SoftTileGreen,
                    text = buildAnnotatedString {
                        append("You walked ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextHighLight)) {
                            append("14% farther")
                        }
                        append(" this week than last week.")
                    }
                )
            }

            // 2. Active Time Insight Card
            item {
                InsightRowCard(
                    icon = Icons.Default.BarChart,
                    iconTint = Color(0xFF2563EB),
                    badgeBg = SoftTileBlue,
                    text = buildAnnotatedString {
                        append("Your most active time is between ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextHighLight)) {
                            append("6–8 PM")
                        }
                        append(".")
                    }
                )
            }

            // 3. Active Days Insight Card
            item {
                InsightRowCard(
                    icon = Icons.Default.CalendarToday,
                    iconTint = Color(0xFF0284C7),
                    badgeBg = SoftTileCyan,
                    text = buildAnnotatedString {
                        append("You completed ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextHighLight)) {
                            append("4 more")
                        }
                        append(" active days than last week.")
                    }
                )
            }

            // 4. Pace Insight Card
            item {
                InsightRowCard(
                    icon = Icons.Default.TrendingUp,
                    iconTint = Color(0xFF16A34A),
                    badgeBg = SoftTileGreen,
                    text = buildAnnotatedString {
                        append("Your average running pace improved by ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextHighLight)) {
                            append("8%")
                        }
                        append(".")
                    }
                )
            }

            // Encouragement Banner Card (13_Insights.png)
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Keep it up!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextHighLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "You're on track to reach your monthly goal.",
                                fontSize = 14.sp,
                                color = TextMediumLight
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .background(SoftTileCyan, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightRowCard(
    icon: ImageVector,
    iconTint: Color,
    badgeBg: Color,
    text: androidx.compose.ui.text.AnnotatedString
) {
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
                    .size(48.dp)
                    .background(badgeBg, CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                fontSize = 15.sp,
                color = TextMediumLight,
                modifier = Modifier.weight(1f),
                lineHeight = 22.sp
            )
        }
    }
}
