package bsrCalculator

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val state = rememberWindowState(size = DpSize(400.dp, 500.dp))

    Window(title = "Buckshot Roulette Calculator v1.0.2", onCloseRequest = ::exitApplication, state = state) {
        CalculatorFrontend()
    }
}
