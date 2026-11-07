package ar.edu.uade.unitreeg1hmi99.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import ar.edu.uade.unitreeg1hmi99.ui.screens.AlertsScreen
import ar.edu.uade.unitreeg1hmi99.ui.screens.DashboardScreen
import ar.edu.uade.unitreeg1hmi99.ui.screens.HistoryScreen
import ar.edu.uade.unitreeg1hmi99.ui.screens.RobotSelectionScreen
import ar.edu.uade.unitreeg1hmi99.ui.screens.ScenariosScreen
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
    SCENARIOS("scenarios", "Escenarios TP", Icons.Default.Science),
    ROBOT("robot", "Unitree G1", Icons.Default.AccessibilityNew)
}

@Composable
fun AppNavigation(
    robotViewModel: RobotViewModel,
    historyViewModel: HistoryViewModel
) {
    var currentDestination by remember { mutableStateOf(NavigationDestination.DASHBOARD) }
    val robotState by robotViewModel.uiState.collectAsState()
    val unacknowledgedAlerts = robotState.telemetry.unacknowledgedAlertsCount

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            if (destination == NavigationDestination.ALERTS && unacknowledgedAlerts > 0) {
                                BadgedBox(badge = { Badge { Text("$unacknowledgedAlerts") } }) {
                                    Icon(destination.icon, contentDescription = destination.title)
                                }
                            } else {
                                Icon(destination.icon, contentDescription = destination.title)
                            }
                        },
                        label = { Text(destination.title, fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            NavigationDestination.DASHBOARD -> DashboardScreen(
                viewModel = robotViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            NavigationDestination.ALERTS -> AlertsScreen(
                viewModel = robotViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            NavigationDestination.HISTORY -> HistoryScreen(
                viewModel = historyViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            NavigationDestination.SCENARIOS -> ScenariosScreen(
                viewModel = robotViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            NavigationDestination.ROBOT -> RobotSelectionScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
