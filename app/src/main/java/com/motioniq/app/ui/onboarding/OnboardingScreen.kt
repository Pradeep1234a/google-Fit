package com.motioniq.app.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.R
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
            // 02_Onboarding.png
            OnboardingWelcomeStep(
                onSkip = { step = 2 },
                onNext = { step = 1 }
            )
        }
        1 -> {
            // 03_Permissions.png
            PermissionsScreen(
                onContinueClick = { step = 2 }
            )
        }
        2 -> {
            // 04_ProfileSetup.png
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
private fun OnboardingWelcomeStep(
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Bar with "Skip >" (02_Onboarding.png)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSkip() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Skip",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 2. Hero Visual & Value Proposition (02_Onboarding.png)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Hero Illustration Container
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFE0F2FE), Color(0xFFF0FDF4))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        // Background distant hills
                        drawOval(
                            color = Color(0xFFBBF7D0),
                            topLeft = androidx.compose.ui.geometry.Offset(-size.width * 0.2f, size.height * 0.45f),
                            size = androidx.compose.ui.geometry.Size(size.width * 1.4f, size.height * 0.8f)
                        )
                        // Foreground bright kinetic green hill
                        drawOval(
                            color = Color(0xFF4ADE80),
                            topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.55f),
                            size = androidx.compose.ui.geometry.Size(size.width * 1.2f, size.height * 0.8f)
                        )
                        // Curved path
                        drawOval(
                            color = Color(0xFFF1F5F9),
                            topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.6f),
                            size = androidx.compose.ui.geometry.Size(size.width * 0.35f, size.height * 0.7f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = "Runner",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(110.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Heading
                Text(
                    text = "A Smarter Way\nto Move",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF0F172A),
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle
                Text(
                    text = "Track your steps, routes, workouts\nand discover new places with\nMOTIONIQ.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            // 3. Bottom Row: Page Indicators & Circular Next Button (02_Onboarding.png)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCBD5E1))
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCBD5E1))
                    )
                }

                // Dark circular Next button (02_Onboarding.png)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A))
                        .clickable { onNext() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
