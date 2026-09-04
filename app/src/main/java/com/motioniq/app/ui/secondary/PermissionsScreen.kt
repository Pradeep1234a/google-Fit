package com.motioniq.app.ui.secondary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.motioniq.app.theme.*

@Composable
fun PermissionsScreen(
    onContinueClick: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Button(
                    onClick = onContinueClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Brand Logo Circle Top
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, CircleShape)
                    .padding(4.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE0F2FE), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = null,
                        tint = KineticGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = "Let's Get Started",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextHighLight
            )

            Text(
                text = "To give you the best experience,\nwe need a few permissions.",
                fontSize = 15.sp,
                color = TextMediumLight,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Activity Recognition
            PermissionCardItem(
                icon = Icons.Default.DirectionsWalk,
                iconTint = Color(0xFF0D9488),
                badgeBg = Color(0xFFCCFBF1),
                title = "Activity Recognition",
                description = "Detect your activity (walking, running, etc.)"
            )

            // 2. Location
            PermissionCardItem(
                icon = Icons.Default.Place,
                iconTint = Color(0xFF2563EB),
                badgeBg = Color(0xFFDBEAFE),
                title = "Location",
                description = "Track routes and find nearby places"
            )

            // 3. Notifications
            PermissionCardItem(
                icon = Icons.Default.Notifications,
                iconTint = Color(0xFFEA580C),
                badgeBg = Color(0xFFFFEDD5),
                title = "Notifications",
                description = "Keep you informed about your progress"
            )

            // 4. Health Connect
            PermissionCardItem(
                icon = Icons.Default.Favorite,
                iconTint = Color(0xFF4F46E5),
                badgeBg = Color(0xFFEEF2FF),
                title = "Health Connect",
                description = "Sync your health and fitness data (optional)"
            )
        }
    }
}

@Composable
private fun PermissionCardItem(
    icon: ImageVector,
    iconTint: Color,
    badgeBg: Color,
    title: String,
    description: String
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(badgeBg, RoundedCornerShape(14.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighLight
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextMediumLight
                )
            }
        }
    }
}
