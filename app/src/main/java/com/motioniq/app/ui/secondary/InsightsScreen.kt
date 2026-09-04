package com.motioniq.app.ui.secondary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
        containerColor = SlateGround,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MOVEMENT INTELLIGENCE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateGround)
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
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Kinetic Diagnostics",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Algorithmic synthesis of your stride dynamics and energy vectors.",
                        fontSize = 13.sp,
                        color = TextMediumDark
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 1. Distance Insight Card
            item {
                InsightRowCard(
                    icon = Icons.Default.TrendingUp,
                    iconTint = StitchCyan,
                    badgeBg = StitchTeal.copy(alpha = 0.5f),
                    text = buildAnnotatedString {
                        append("Biomechanical volume increased ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = StitchCyan)) {
                            append("14% farther")
                        }
                        append(" than your previous 7-day rolling cycle.")
                    }
                )
            }

            // 2. Active Time Insight Card
            item {
                InsightRowCard(
                    icon = Icons.Default.Schedule,
                    iconTint = KineticEmerald,
                    badgeBg = KineticEmerald.copy(alpha = 0.2f),
                    text = buildAnnotatedString {
                        append("Optimal kinetic flow zone occurs between ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                            append("6:00 – 8:30 PM")
                        }
                        append(" with minimal ground reaction deceleration.")
                    }
                )
            }

            // 3. Cadence Insight Card
            item {
                InsightRowCard(
                    icon = Icons.Default.Speed,
                    iconTint = StitchCyan,
                    badgeBg = StitchDarkCyan,
                    text = buildAnnotatedString {
                        append("Your tempo cadence stabilized at ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = StitchCyan)) {
                            append("174 spm")
                        }
                        append(", yielding a +4.2% metabolic economy enhancement.")
                    }
                )
            }

            // 4. Symmetry Insight Card
            item {
                InsightRowCard(
                    icon = Icons.Default.Balance,
                    iconTint = KineticEmerald,
                    badgeBg = KineticEmerald.copy(alpha = 0.2f),
                    text = buildAnnotatedString {
                        append("Bilateral contact delta improved to ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = KineticEmerald)) {
                            append("±0.2%")
                        }
                        append(", well within the optimal symmetry threshold.")
                    }
                )
            }

            // Encouragement Banner Card
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SlateSurface1,
                    border = BorderStroke(1.dp, CyanBorderGlow),
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
                                text = "Optimal Strain Threshold",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Musculoskeletal balance is primed for progressive overload.",
                                fontSize = 13.sp,
                                color = TextMediumDark,
                                lineHeight = 18.sp
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(50.dp)
                                .background(StitchTeal.copy(alpha = 0.5f), CircleShape)
                                .border(1.dp, StitchCyan, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(26.dp)
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
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SlateSurface1,
        border = BorderStroke(1.dp, CyanBorderSubtle),
        modifier = Modifier.fillMaxWidth()
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
                    .size(44.dp)
                    .background(badgeBg, RoundedCornerShape(12.dp))
                    .border(1.dp, iconTint.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = text,
                fontSize = 13.sp,
                color = TextMediumDark,
                modifier = Modifier.weight(1f),
                lineHeight = 19.sp
            )
        }
    }
}
