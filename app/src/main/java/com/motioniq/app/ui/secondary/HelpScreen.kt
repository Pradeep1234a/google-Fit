package com.motioniq.app.ui.secondary

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*

private data class DiagnosticFaq(
    val id: String,
    val question: String,
    val answer: String,
    val metricBadge: String? = null
)

private val faqs = listOf(
    DiagnosticFaq(
        id = "1",
        question = "How is Bilateral Gait Symmetry calculated?",
        answer = "MOTIONIQ measures contact phase differential across successive step cycles using synchronized foot-pod accelerometers sampled at 200Hz. Values exceeding ±2.4% over 120 consecutive strides trigger kinematic feedback.",
        metricBadge = "±0.5% Delta"
    ),
    DiagnosticFaq(
        id = "2",
        question = "Calibrating external shoe pods for millimeter accuracy",
        answer = "Attach pods to laces over the third eyelet facing true north during the initial 10-meter calibration stride. Ensure firmware is updated to version 2.4+."
    ),
    DiagnosticFaq(
        id = "3",
        question = "Troubleshooting GPS drift and elevation discrepancies",
        answer = "MOTIONIQ integrates barometric altimeter data with fused GNSS dual-frequency bands (L1+L5) to filter out multipath reflections in urban canyons."
    ),
    DiagnosticFaq(
        id = "4",
        question = "Downhill Cadence and Hamstring Shear cues",
        answer = "When running on declines >4%, cadence should automatically shorten by 8-10% to prevent excessive eccentric hamstring loading."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedFaqId by remember { mutableStateOf<String?>("1") }

    val filteredFaqs = remember(searchQuery) {
        if (searchQuery.isBlank()) faqs else faqs.filter { it.question.contains(searchQuery, ignoreCase = true) }
    }

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
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "HELP & GOVERNANCE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(StitchTeal.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Contact Desk",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchCyan
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp)
        ) {
            // Hero Privacy by Architecture Card (Stitch 8564c857)
            item {
                Surface(
                    color = SlateSurface1,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, CyanBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(StitchTeal.copy(alpha = 0.5f), CircleShape)
                                    .border(1.dp, StitchCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = StitchCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "ENCRYPTED TELEMETRY PROTOCOL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = StitchCyan
                            )
                        }

                        Text(
                            text = "Privacy by Architecture. Support by Experts.",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Your movement telemetry belongs to you. Stored locally, encrypted on-device, and never monetized or exposed to ad exchanges.",
                            fontSize = 12.sp,
                            color = TextMediumDark,
                            lineHeight = 17.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(SlateSurface2, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Hardware Keystore: AES-256",
                                    fontSize = 10.sp,
                                    color = StitchCyan
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(SlateSurface2, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Zero Outbound SDKs",
                                    fontSize = 10.sp,
                                    color = KineticEmerald
                                )
                            }
                        }
                    }
                }
            }

            // Section: Biometric Governance
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Biometric Governance",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "4 Core Stances",
                            fontSize = 11.sp,
                            color = TextLowDark
                        )
                    }

                    Surface(
                        color = SlateSurface1,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SlateSurface2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            GovernanceRow(
                                icon = Icons.Default.PublicOff,
                                title = "Zero Advertising Trackers",
                                description = "No third-party SDKs, analytics beacons, or advertising brokers have access to joint vectors."
                            )
                            Divider(color = SlateSurface2, thickness = 0.5.dp)
                            GovernanceRow(
                                icon = Icons.Default.LockClock,
                                title = "Local Cryptographic Vault",
                                description = "All GNSS spatial coordinates, angular vectors, and foot-strike telemetry are sealed using hardware-backed keys."
                            )
                            Divider(color = SlateSurface2, thickness = 0.5.dp)
                            GovernanceRow(
                                icon = Icons.Default.ShareLocation,
                                title = "Selective Health Connect Sharing",
                                description = "Fine-grained granular revocable rights. Revoke cadence, heart rate, or step cadence export permissions at any millisecond."
                            )
                            Divider(color = SlateSurface2, thickness = 0.5.dp)
                            GovernanceRow(
                                icon = Icons.Default.DeleteForever,
                                title = "Right to Forgotten Data",
                                description = "One-tap cryptographic erasure. Deleting your master key renders past gait records irrecoverable noise on physical flash blocks."
                            )
                        }
                    }
                }
            }

            // Section: Help & Diagnostics Base
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Help & Diagnostics Base",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search sensor pairing, gait metrics, errors...", color = TextLowDark, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(18.dp))
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateSurface1,
                            unfocusedContainerColor = SlateSurface1,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = StitchCyan,
                            unfocusedBorderColor = SlateSurface2
                        )
                    )
                }
            }

            // FAQ Items
            items(filteredFaqs) { faq ->
                val isExpanded = expandedFaqId == faq.id
                Surface(
                    color = SlateSurface1,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isExpanded) StitchCyan.copy(alpha = 0.4f) else SlateSurface2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedFaqId = if (isExpanded) null else faq.id }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = faq.question,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = faq.answer,
                                    fontSize = 12.sp,
                                    color = TextMediumDark,
                                    lineHeight = 17.sp
                                )

                                if (faq.metricBadge != null) {
                                    Surface(
                                        color = SlateSurface2,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "TOLERABLE WINDOW",
                                                fontSize = 9.sp,
                                                color = TextLowDark,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = faq.metricBadge,
                                                fontSize = 12.sp,
                                                color = StitchCyan,
                                                fontWeight = FontWeight.Black
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .background(KineticEmerald.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Optimal Symmetry",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = KineticEmerald
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Support Concierge & Node ID
            item {
                Surface(
                    color = SlateSurface1,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SlateSurface2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Device Node ID: NJ-00924X",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "IMU Shm: 60Hz · Fused v2.1",
                                fontSize = 10.sp,
                                color = TextLowDark
                            )
                        }

                        Button(
                            onClick = { /* Copy logs */ },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SlateSurface2,
                                contentColor = StitchCyan
                            ),
                            border = BorderStroke(1.dp, CyanBorderSubtle),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Copy Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GovernanceRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(StitchTeal.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = StitchCyan,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = TextMediumDark,
                lineHeight = 15.sp
            )
        }
    }
}
