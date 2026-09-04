package com.motioniq.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun MetricCard(
    title: String,
    value: String,
    unit: String = "",
    delta: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = StitchCyan,
    modifier: Modifier = Modifier,
    containerColor: Color = SlateSurface1
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CyanBorderSubtle),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Icon + Title + Delta Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (icon != null) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = iconTint.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextLowDark,
                        letterSpacing = 0.8.sp
                    )
                }

                if (delta != null) {
                    Text(
                        text = delta,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (delta.contains("+") || delta.contains("Optimal")) KineticEmerald else StitchCyan
                    )
                }
            }

            // Value + Unit
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = StitchCyan,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}
