package com.motioniq.app.ui.activity

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.ActivityType
import com.motioniq.app.theme.*

private data class ActivityMeta(
    val type: ActivityType,
    val subtitle: String,
    val icon: ImageVector,
    val tileBackground: Color,
    val iconTint: Color
)

private val activitiesMeta = listOf(
    ActivityMeta(
        type = ActivityType.WALKING,
        subtitle = "Great for daily movement",
        icon = Icons.Default.DirectionsWalk,
        tileBackground = Color(0xFFE0F2FE),
        iconTint = Color(0xFF0284C7)
    ),
    ActivityMeta(
        type = ActivityType.RUNNING,
        subtitle = "Track your runs and improve",
        icon = Icons.Default.DirectionsRun,
        tileBackground = Color(0xFFFEF3C7),
        iconTint = Color(0xFFD97706)
    ),
    ActivityMeta(
        type = ActivityType.CYCLING,
        subtitle = "Explore farther",
        icon = Icons.Default.DirectionsBike,
        tileBackground = Color(0xFFDCFCE7),
        iconTint = Color(0xFF16A34A)
    ),
    ActivityMeta(
        type = ActivityType.SPORTS,
        subtitle = "Football, basketball and more",
        icon = Icons.Default.SportsBasketball,
        tileBackground = Color(0xFFFEE2E2),
        iconTint = Color(0xFFDC2626)
    ),
    ActivityMeta(
        type = ActivityType.JUMP,
        subtitle = "Track your jumps",
        icon = Icons.Default.FitnessCenter,
        tileBackground = Color(0xFFF3E8FF),
        iconTint = Color(0xFF9333EA)
    ),
    ActivityMeta(
        type = ActivityType.SWIMMING,
        subtitle = "Keep moving in the water",
        icon = Icons.Default.Pool,
        tileBackground = Color(0xFFE0F2FE),
        iconTint = Color(0xFF0284C7)
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitySelectionScreen(
    onSelectActivity: (ActivityType) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            // Header (06_ActivitySelect.png)
            item {
                Text(
                    text = "Choose Activity",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "What would you like to do?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextMediumLight
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // List of Activity Cards
            items(activitiesMeta) { item ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectActivity(item.type) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Colored square icon container
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .background(item.tileBackground, shape = RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.type.displayName,
                                tint = item.iconTint,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.type.displayName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextHighLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.subtitle,
                                fontSize = 13.sp,
                                color = TextMediumLight
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
