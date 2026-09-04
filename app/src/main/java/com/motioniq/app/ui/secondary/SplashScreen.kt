package com.motioniq.app.ui.secondary

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF042F4A), BrandNavy),
                    center = Offset(800f, 200f),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Master Logo Circle (01_Splash.png)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(170.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    val strokeWidth = 5.dp.toPx()
                    // Gradient ring
                    drawArc(
                        brush = Brush.sweepGradient(
                            0.0f to ElectricBlue,
                            0.7f to KineticGreen,
                            1.0f to ElectricBlue
                        ),
                        startAngle = 40f,
                        sweepAngle = 320f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // ECG Pulse waveform at bottom
                    val baseY = size.height * 0.88f
                    val centerX = size.width / 2f
                    drawLine(
                        color = KineticGreen,
                        start = Offset(centerX - 30.dp.toPx(), baseY),
                        end = Offset(centerX - 15.dp.toPx(), baseY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = KineticGreen,
                        start = Offset(centerX - 15.dp.toPx(), baseY),
                        end = Offset(centerX - 5.dp.toPx(), baseY - 16.dp.toPx()),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = KineticGreen,
                        start = Offset(centerX - 5.dp.toPx(), baseY - 16.dp.toPx()),
                        end = Offset(centerX + 8.dp.toPx(), baseY + 14.dp.toPx()),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = KineticGreen,
                        start = Offset(centerX + 8.dp.toPx(), baseY + 14.dp.toPx()),
                        end = Offset(centerX + 20.dp.toPx(), baseY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = KineticGreen,
                        start = Offset(centerX + 20.dp.toPx(), baseY),
                        end = Offset(centerX + 35.dp.toPx(), baseY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                // Runner Silhouette
                Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(76.dp)
                )

                // Destination Pin at top right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 18.dp, end = 26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = KineticGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Wordmark
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                        append("MOTION")
                    }
                    withStyle(SpanStyle(color = KineticGreen, fontWeight = FontWeight.Bold)) {
                        append("IQ")
                    }
                },
                fontSize = 34.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tagline
            Text(
                text = "Understand your movement.",
                fontSize = 15.sp,
                color = Color(0xFFCBD5E1),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Not just your steps.",
                fontSize = 15.sp,
                color = Color(0xFFCBD5E1),
                fontWeight = FontWeight.Medium
            )
        }

        // Bottom Loading Progress Pill (01_Splash.png)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .width(130.dp)
                .height(6.dp)
                .background(Color(0xFF0F264A), RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        Brush.horizontalGradient(listOf(ElectricBlue, KineticGreen)),
                        RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}
