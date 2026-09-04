package com.motioniq.app.ui.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val telemetryTag: String,
    val icon: ImageVector,
    val tileBackground: Color,
    val iconTint: Color
)

private val activitiesMeta = listOf(
    ActivityMeta(
        type = ActivityType.RUNNING,
        subtitle = "Cadence, flight time & impact vector",
        telemetryTag = "120Hz IMU + GNSS",
        icon = Icons.Default.DirectionsRun,
        tileBackground = StitchTeal.copy(alpha = 0.5f),
        iconTint = StitchCyan
    ),
    ActivityMeta(
        type = ActivityType.WALKING,
        subtitle = "Equilibrium flow & bilateral symmetry",
        telemetryTag = "Step Engine Active",
        icon = Icons.Default.DirectionsWalk,
        tileBackground = KineticEmerald.copy(alpha = 0.2f),
        iconTint = KineticEmerald
    ),
    ActivityMeta(
        type = ActivityType.CYCLING,
        subtitle = "Vector wattage & velocity curve",
        telemetryTag = "Cadence Sync",
        icon = Icons.Default.DirectionsBike,
        tileBackground = StitchDarkCyan,
        iconTint = StitchCyan
    ),
    ActivityMeta(
        type = ActivityType.SPORTS,
        subtitle = "Lateral agility & burst acceleration",
        telemetryTag = "Kinetic Load",
        icon = Icons.Default.SportsBasketball,
        tileBackground = PulseCoral.copy(alpha = 0.2f),
        iconTint = PulseCoral
    ),
    ActivityMeta(
        type = ActivityType.JUMP,
        subtitle = "Vertical takeoff & ground reaction",
        telemetryTag = "Impulse Telemetry",
        icon = Icons.Default.FitnessCenter,
        tileBackground = VelocityPurple.copy(alpha = 0.2f),
        iconTint = VelocityPurple
    ),
    ActivityMeta(
        type = ActivityType.SWIMMING,
        subtitle = "Stroke rate & propulsion efficiency",
        telemetryTag = "Hydro Dynamic",
        icon = Icons.Default.Pool,
        tileBackground = StitchTeal.copy(alpha = 0.4f),
        iconTint = StitchCyan
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitySelectionScreen(
    onSelectActivity: (ActivityType) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = SlateGround,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateGround)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(SlateSurface1, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "TRACK TELEMETRY",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.size(40.dp))
                }
            }
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
                        text = "Choose Modality",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Deploy high-frequency IMU and GNSS tracking filters.",
                        fontSize = 14.sp,
                        color = TextMediumDark
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // List of Activity Cards
            items(activitiesMeta) { item ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SlateSurface1,
                    border = BorderStroke(1.dp, CyanBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectActivity(item.type) }
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
                                .size(52.dp)
                                .background(item.tileBackground, shape = RoundedCornerShape(14.dp))
                                .border(1.dp, item.iconTint.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.type.displayName,
                                tint = item.iconTint,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = item.type.displayName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .background(SlateSurface2, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.telemetryTag,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = item.iconTint
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = item.subtitle,
                                fontSize = 12.sp,
                                color = TextMediumDark
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
