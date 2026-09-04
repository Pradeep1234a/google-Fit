package com.motioniq.app.ui.secondary

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateGround)
    ) {
        // Top Badges (Stitch ff19f8f7)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(SlateSurface1, RoundedCornerShape(12.dp))
                    .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(12.dp))
                Text("IMU PRECISION ARRAY", style = androidx.compose.material3.MaterialTheme.typography.labelSmall, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMediumDark)
            }

            Row(
                modifier = Modifier
                    .background(SlateSurface1, RoundedCornerShape(12.dp))
                    .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.size(6.dp).background(KineticEmerald, CircleShape))
                Text("60Hz SYNC", style = androidx.compose.material3.MaterialTheme.typography.labelSmall, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = KineticEmerald)
            }
        }

        // Center Content (Stitch ff19f8f7)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Ambient Radial Glow & M Monogram Tile
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                // Background Cyan Glow
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(StitchCyan.copy(alpha = pulseAlpha * 0.35f), Color.Transparent)
                        ),
                        radius = size.width * 0.7f
                    )
                }

                // Dark Squircle Container
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(SlateSurface1, RoundedCornerShape(26.dp))
                        .border(1.5.dp, StitchCyan.copy(alpha = pulseAlpha), RoundedCornerShape(26.dp))
                ) {
                    // Geometric M Line Canvas
                    Canvas(modifier = Modifier.size(48.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.15f, size.height * 0.8f)
                            lineTo(size.width * 0.35f, size.height * 0.25f)
                            lineTo(size.width * 0.5f, size.height * 0.55f)
                            lineTo(size.width * 0.65f, size.height * 0.25f)
                            lineTo(size.width * 0.85f, size.height * 0.8f)
                        }
                        drawPath(
                            path = path,
                            color = StitchCyan,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Top Dot
                        drawCircle(
                            color = StitchCyan,
                            radius = 4.dp.toPx(),
                            center = Offset(size.width * 0.5f, size.height * 0.15f)
                        )
                    }
                }
            }

            // App Name: MOTION IQ
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MOTION",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "IQ",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = StitchCyan,
                    letterSpacing = 1.sp
                )
            }

            // Tagline
            Text(
                text = "Understand your movement. Not just your steps.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = TextMediumDark,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-pill: VELOCITY MESH • GAIT BALANCE
            Box(
                modifier = Modifier
                    .background(SlateSurface1, RoundedCornerShape(16.dp))
                    .border(1.dp, CyanBorderSubtle, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "VELOCITY MESH  •  GAIT BALANCE",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StitchCyan,
                    letterSpacing = 0.8.sp
                )
            }
        }

        // Bottom Progress & Version (Stitch ff19f8f7)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cyan Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(SlateSurface2, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight()
                        .background(StitchCyan, RoundedCornerShape(2.dp))
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(6.dp).background(StitchCyan, CircleShape))
                    Text("READY FOR TELEMETRY", style = androidx.compose.material3.MaterialTheme.typography.labelSmall, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextLowDark)
                }
                Text("100%", style = androidx.compose.material3.MaterialTheme.typography.labelSmall, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StitchCyan)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "MOTION KINETICS ENGINE V1.0",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextLowDark,
                letterSpacing = 1.sp
            )
        }
    }
}
