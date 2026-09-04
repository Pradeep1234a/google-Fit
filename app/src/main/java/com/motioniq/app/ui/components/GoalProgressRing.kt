package com.motioniq.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.ElectricBlue
import com.motioniq.app.theme.KineticGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GoalProgressRing(
    currentSteps: Long,
    stepGoal: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (stepGoal > 0) (currentSteps.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "step_progress"
    )

    val formattedSteps = NumberFormat.getNumberInstance(Locale.US).format(currentSteps)
    val formattedGoal = NumberFormat.getNumberInstance(Locale.US).format(stepGoal)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(240.dp)
    ) {
        // Custom Arc Canvas with Gradient Sweep
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val strokeWidthPx = 14.dp.toPx()
            val diameter = size.minDimension - strokeWidthPx
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            // 1. Background dark ring track
            drawArc(
                color = Color(0xFF091C3E),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // 2. Glowing/vibrant Gradient Progress Arc (Electric Blue -> Kinetic Green)
            if (animatedProgress > 0f) {
                val gradientBrush = Brush.sweepGradient(
                    0.0f to ElectricBlue,
                    0.5f to Color(0xFF00E5FF),
                    1.0f to KineticGreen
                )

                // Outer subtle glow
                drawArc(
                    brush = gradientBrush,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidthPx + 4.dp.toPx(), cap = StrokeCap.Round),
                    alpha = 0.35f
                )

                // Main sharp arc
                drawArc(
                    brush = gradientBrush,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }

        // Center Content: Footsteps icon, Bold Step Count, "Steps", "of 10,000"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Dual Footsteps Icon
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left footprint (Cyan)
                FootprintIcon(color = ElectricBlue, isLeft = true)
                // Right footprint (Kinetic Green)
                FootprintIcon(color = KineticGreen, isLeft = false)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Step Digits
            Text(
                text = formattedSteps,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            // "Steps"
            Text(
                text = "Steps",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(1.dp))

            // "of 10,000"
            Text(
                text = "of $formattedGoal",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun FootprintIcon(color: Color, isLeft: Boolean) {
    Canvas(modifier = Modifier.size(width = 16.dp, height = 26.dp)) {
        val rotation = if (isLeft) -8f else 8f
        rotate(rotation) {
            // Heel
            drawOval(
                color = color,
                topLeft = Offset(size.width * 0.2f, size.height * 0.58f),
                size = Size(size.width * 0.6f, size.height * 0.36f)
            )
            // Sole / ball of foot
            drawOval(
                color = color,
                topLeft = Offset(size.width * 0.15f, size.height * 0.12f),
                size = Size(size.width * 0.7f, size.height * 0.42f)
            )
        }
    }
}
