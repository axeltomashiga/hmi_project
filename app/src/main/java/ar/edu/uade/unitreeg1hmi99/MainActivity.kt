package ar.edu.uade.unitreeg1hmi99

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import ar.edu.uade.unitreeg1hmi99.data.local.AppDatabase
import ar.edu.uade.unitreeg1hmi99.data.repository.SimulatedRobotRepository
import ar.edu.uade.unitreeg1hmi99.ui.navigation.AppNavigation
import ar.edu.uade.unitreeg1hmi99.ui.theme.Unitreeg1hmi99Theme
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.HistoryViewModel
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.RobotViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inicialización de Room Database
        val database = AppDatabase.getInstance(applicationContext)

        // 2. Repositorio Desacoplado Simulado (100% offline, sin servidor ni robot físico)
        val repository = SimulatedRobotRepository(
            missionDao = database.missionHistoryDao(),
            eventDao = database.eventLogDao()
        )

        // 3. ViewModels con sus respectivas factories
        val robotViewModel = RobotViewModel.Factory(repository).create(RobotViewModel::class.java)
        val historyViewModel = HistoryViewModel.Factory(repository).create(HistoryViewModel::class.java)

        setContent {
            Unitreeg1hmi99Theme {
                AppNavigation(
                    robotViewModel = robotViewModel,
                    historyViewModel = historyViewModel
                )
            }
        }
    }
}
