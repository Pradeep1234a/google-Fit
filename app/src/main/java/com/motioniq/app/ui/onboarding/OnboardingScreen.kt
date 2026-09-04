package com.motioniq.app.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.UserProfile
import com.motioniq.app.theme.*
import com.motioniq.app.ui.secondary.PermissionsScreen
import com.motioniq.app.ui.secondary.ProfileSetupScreen

@Composable
fun OnboardingScreen(
    initialProfile: UserProfile,
    onCompleteOnboarding: (UserProfile) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }

    when (step) {
        0 -> {
            // Stitch b31ebc74 Onboarding / Welcome
            OnboardingPrecisionWelcome(
                onGetStarted = { step = 1 }
            )
        }
        1 -> {
            // Stitch 23b512c8 Permissions
            PermissionsScreen(
                onContinueClick = { step = 2 }
            )
        }
        2 -> {
            // Stitch 67c2356f Profile / Biomechanical Baseline Setup
            ProfileSetupScreen(
                initialProfile = initialProfile,
                onSaveProfile = { updated ->
                    onCompleteOnboarding(updated.copy(isOnboarded = true))
                },
                onBackClick = { step = 1 }
            )
        }
    }
}

@Composable
private fun OnboardingPrecisionWelcome(
    onGetStarted: () -> Unit
) {
    var selectedPillar by remember { mutableIntStateOf(0) }
    val pillars = listOf("Track", "Understand", "Explore", "Improve")

    Scaffold(
        containerColor = SlateGround
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 1. Top Badges (Stitch b31ebc74)
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        Box(modifier = Modifier.size(6.dp).background(StitchCyan, CircleShape))
                        Text(
                            text = "WELCOME TO MOTIONIQ",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(
                        modifier = Modifier
                            .background(SlateSurface1, RoundedCornerShape(12.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(12.dp))
                        Text(
                            text = "V2.4 TELEMETRY",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan
                        )
                    }
                }

                // 2. Headline & Subtitle (Stitch b31ebc74)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Precision Movement",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 36.sp
                    )
                    Text(
                        text = "Intelligence",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = StitchCyan,
                        lineHeight = 36.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Analytical kinesthetic telemetry engine engineered for deliberate physical refinement.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMediumDark,
                        lineHeight = 20.sp
                    )
                }

                // 3. Pillar 01 Card: Kinematics & Sensor Fusion (Stitch b31ebc74)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                    border = BorderStroke(1.dp, CyanBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Graphic simulation container with wireframe runner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF14242A))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Grid telemetry lines
                                val gridColor = Color(0x1F00A3C4)
                                drawLine(gridColor, Offset(0f, size.height * 0.5f), Offset(size.width, size.height * 0.5f))
                                drawLine(gridColor, Offset(size.width * 0.5f, 0f), Offset(size.width * 0.5f, size.height))

                                // Joint linkage lines (simulating biomechanic wireframe)
                                val nodeColor = StitchCyan
                                val joint1 = Offset(size.width * 0.4f, size.height * 0.3f)
                                val joint2 = Offset(size.width * 0.52f, size.height * 0.55f)
                                val joint3 = Offset(size.width * 0.65f, size.height * 0.8f)

                                drawLine(nodeColor, joint1, joint2, strokeWidth = 2.dp.toPx())
                                drawLine(nodeColor, joint2, joint3, strokeWidth = 2.dp.toPx())
                                drawCircle(nodeColor, radius = 4.dp.toPx(), center = joint1)
                                drawCircle(nodeColor, radius = 5.dp.toPx(), center = joint2)
                                drawCircle(KineticEmerald, radius = 4.dp.toPx(), center = joint3)
                            }

                            // Top-Left Badge
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(SlateSurface2, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("PILLAR 01 // KINEMATICS", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = StitchCyan)
                            }

                            // Bottom-Right Sampling Rate
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text("SAMPLING RATE", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = TextLowDark)
                                Text("120 Hz IMU", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StitchCyan)
                            }
                        }

                        // Content
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(StitchTeal.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("01 • TRACK", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StitchCyan)
                            }
                            Text(
                                text = "Sensor Fusion",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Multi-sensor kinematics across walking, running, and cycling with micro-oscillation resolution and zero signal drift.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMediumDark,
                            lineHeight = 18.sp
                        )

                        // 3 Metrics: Vector, Latency, Drift
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("VECTOR", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextLowDark)
                                Text("3-Axis", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column {
                                Text("LATENCY", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextLowDark)
                                Text("<8ms", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = StitchCyan)
                            }
                            Column {
                                Text("DRIFT", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextLowDark)
                                Text("0.02%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = KineticEmerald)
                            }
                        }

                        // Progress Indicator & Arrows
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(width = 24.dp, height = 4.dp).background(StitchCyan, RoundedCornerShape(2.dp)))
                                Box(modifier = Modifier.size(6.dp).background(SlateSurface2, CircleShape))
                                Box(modifier = Modifier.size(6.dp).background(SlateSurface2, CircleShape))
                                Box(modifier = Modifier.size(6.dp).background(SlateSurface2, CircleShape))
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(28.dp).background(SlateSurface2, CircleShape)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextLowDark, modifier = Modifier.size(14.dp))
                                }
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(28.dp).background(SlateSurface2, CircleShape)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // 4. Pillar Category Pills (Stitch b31ebc74)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pillars.forEachIndexed { idx, name ->
                        val isSelected = selectedPillar == idx
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) StitchCyan else SlateSurface1)
                                .border(1.dp, if (isSelected) StitchCyan else CyanBorderSubtle, RoundedCornerShape(20.dp))
                                .clickable { selectedPillar = idx }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isSelected) {
                                Box(modifier = Modifier.size(6.dp).background(StitchDarkCyan, CircleShape))
                            }
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) StitchDarkCyan else TextMediumDark
                            )
                        }
                    }
                }
            }

            // 5. Bottom Primary CTA Button & Disclaimers (Stitch b31ebc74)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onGetStarted,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StitchCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Get Started",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = StitchDarkCyan
                        )
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = StitchDarkCyan, modifier = Modifier.size(18.dp))
                    }
                }

                Text(
                    text = "I already have an account • Log In",
                    style = MaterialTheme.typography.bodySmall,
                    color = StitchCyan,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onGetStarted() }
                )

                Text(
                    text = "Engineered for Android M3 • Zero advertisement trackers\nBiomechanical telemetry encrypted on-device",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = TextLowDark,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
