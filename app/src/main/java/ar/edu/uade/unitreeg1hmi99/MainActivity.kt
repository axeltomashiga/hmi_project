package ar.edu.uade.unitreeg1hmi99

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import ar.edu.uade.unitreeg1hmi99.data.repository.SimulatedRobotRepository
import ar.edu.uade.unitreeg1hmi99.ui.navigation.AppNavigation
import ar.edu.uade.unitreeg1hmi99.ui.theme.Unitreeg1hmi99Theme
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.HistoryViewModel
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.RobotViewModel

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        SimulatedRobotRepository.getInstance(applicationContext)
    }

    private val robotViewModel: RobotViewModel by viewModels {
        RobotViewModel.Factory(repository)
    }

    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Unitreeg1hmi99Theme {
                AppNavigation(
                    robotViewModel = robotViewModel,
                    historyViewModel = historyViewModel
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        repository.saveStateNow(sync = true)
    }
}
