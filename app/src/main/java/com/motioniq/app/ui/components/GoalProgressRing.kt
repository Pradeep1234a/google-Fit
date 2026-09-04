package com.motioniq.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GoalProgressRing(
    currentSteps: Long,
    stepGoal: Int,
    modifier: Modifier = Modifier,
    peakRateSpm: Int = 134,
    symmetryPercent: Double = 49.8
) {
    val progress = if (stepGoal > 0) (currentSteps.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "arc_progress"
    )

    val percent = (progress * 100).toInt()
    val formattedSteps = NumberFormat.getNumberInstance(Locale.US).format(currentSteps)
    val formattedGoal = NumberFormat.getNumberInstance(Locale.US).format(stepGoal)
    val remaining = (stepGoal - currentSteps).coerceAtLeast(0)
    val formattedRemaining = NumberFormat.getNumberInstance(Locale.US).format(remaining)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface1),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyanBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: "KINETIC CADENCE TARGET 64%" + bar icon
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
                        text = "KINETIC CADENCE TARGET",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(StitchTeal.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$percent%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = StitchCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Luminous Cyan Arc Gauge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    val strokeWidthPx = 12.dp.toPx()
                    val width = size.width
                    val height = size.height * 1.8f
                    val arcTopLeft = Offset(0f, 10.dp.toPx())
                    val arcSize = Size(width, height)

                    // Background Track Arc (180 degrees from 180 to 360)
                    drawArc(
                        color = Color(0xFF1B2830),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )

                    // Foreground Glowing Cyan Arc
                    if (animatedProgress > 0f) {
                        val activeSweep = animatedProgress * 180f

                        // Subtle outer glow
                        drawArc(
                            color = StitchCyan.copy(alpha = 0.3f),
                            startAngle = 180f,
                            sweepAngle = activeSweep,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidthPx + 6.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Main bright cyan arc
                        drawArc(
                            brush = Brush.horizontalGradient(
                                colors = listOf(StitchTeal, StitchCyan, Color(0xFF38BDF8))
                            ),
                            startAngle = 180f,
                            sweepAngle = activeSweep,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                        )
                    }
                }

                // Digits & Daily Goal Inside Arc
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formattedSteps,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "STEPS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = KineticEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Daily Goal: $formattedGoal steps",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMediumDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3-Column Micro Stats (Remaining, Peak Rate, Symmetry)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Remaining
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SlateSurface2, RoundedCornerShape(12.dp))
                        .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "REMAINING",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextLowDark,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formattedRemaining,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Peak Rate
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SlateSurface2, RoundedCornerShape(12.dp))
                        .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PEAK RATE",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextLowDark,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$peakRateSpm spm",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan
                        )
                    }
                }

                // Symmetry
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SlateSurface2, RoundedCornerShape(12.dp))
                        .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SYMMETRY",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextLowDark,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${symmetryPercent}% L",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = KineticEmerald
                        )
                    }
                }
            }
        }
    }
}
