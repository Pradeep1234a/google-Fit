package com.motioniq.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.theme.*

enum class AppTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.DirectionsWalk),
    EXPLORE("Explore", Icons.Default.Explore),
    ANALYTICS("Analytics", Icons.Default.ShowChart),
    SETTINGS("Settings", Icons.Default.Tune);

    companion object {
        val ACTIVITY = ANALYTICS
        val STATS = ANALYTICS
        val PROFILE = SETTINGS
    }
}

@Composable
fun MotionIQBottomBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = SlateGround,
        tonalElevation = 8.dp
    ) {
        AppTab.entries.forEach { tab ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = StitchTeal.copy(alpha = 0.5f),
                    selectedIconColor = StitchCyan,
                    selectedTextColor = StitchCyan,
                    unselectedIconColor = TextLowDark,
                    unselectedTextColor = TextLowDark
                )
            )
        }
    }
}
