package ar.edu.uade.unitreeg1hmi99.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.unitreeg1hmi99.ui.screens.AlertsScreen
import ar.edu.uade.unitreeg1hmi99.ui.screens.DashboardScreen
import ar.edu.uade.unitreeg1hmi99.ui.screens.HistoryScreen
import ar.edu.uade.unitreeg1hmi99.ui.screens.RobotSelectionScreen
import ar.edu.uade.unitreeg1hmi99.ui.screens.ScenariosScreen
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDanger
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.HistoryViewModel
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.RobotViewModel

enum class NavigationDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    DASHBOARD("dashboard", "Supervisión", Icons.Default.Dashboard),
    ALERTS("alerts", "Alertas", Icons.Default.Notifications),
    HISTORY("history", "Historial", Icons.Default.History),
    SCENARIOS("scenarios", "Escenarios", Icons.Default.Science),
    ROBOT("robot", "Unitree G1", Icons.Default.AccessibilityNew)
}

@Composable
fun AppNavigation(
    robotViewModel: RobotViewModel,
    historyViewModel: HistoryViewModel
) {
    var currentDestination by rememberSaveable { mutableStateOf(NavigationDestination.DASHBOARD) }
    val robotState by robotViewModel.uiState.collectAsState()
    val unacknowledgedAlerts = robotState.telemetry.unacknowledgedAlertsCount

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    NavigationDestination.values().forEach { destination ->
                        val isSelected = currentDestination == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            icon = {
                                if (destination == NavigationDestination.ALERTS && unacknowledgedAlerts > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = StatusDanger,
                                                contentColor = Color.White
                                            ) {
                                                Text(
                                                    text = "$unacknowledgedAlerts",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = destination.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentDestination,
            animationSpec = tween(durationMillis = 200),
            label = "screenTransition",
            modifier = Modifier.padding(innerPadding)
        ) { destination ->
            when (destination) {
                NavigationDestination.DASHBOARD -> DashboardScreen(
                    viewModel = robotViewModel
                )
                NavigationDestination.ALERTS -> AlertsScreen(
                    viewModel = robotViewModel
                )
                NavigationDestination.HISTORY -> HistoryScreen(
                    viewModel = historyViewModel
                )
                NavigationDestination.SCENARIOS -> ScenariosScreen(
                    viewModel = robotViewModel
                )
                NavigationDestination.ROBOT -> RobotSelectionScreen()
            }
        }
    }
}
