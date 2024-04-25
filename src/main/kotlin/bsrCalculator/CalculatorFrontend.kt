package bsrCalculator

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bsrCalculator.Shared.BLANK_CHANCE_PREFIX
import bsrCalculator.Shared.LIVE_CHANCE_PREFIX
import bsrCalculator.Shared.blankChance
import bsrCalculator.Shared.blankChanceDisplay
import bsrCalculator.Shared.liveChance
import bsrCalculator.Shared.liveChanceDisplay
import bsrCalculator.Shared.shellLineup
import bsrCalculator.Shared.shellLineupDisplay
import kotlinx.coroutines.flow.MutableStateFlow

object Shared {
    val shellLineup: MutableList<ShellType> = mutableListOf()
    val shellLineupDisplay = MutableStateFlow("Current Shell Lineup: $shellLineup")

    var liveChance = 0
    const val LIVE_CHANCE_PREFIX = "Live shell chance: "
    val liveChanceDisplay = MutableStateFlow("$LIVE_CHANCE_PREFIX$liveChance%")

    var blankChance = 0
    const val BLANK_CHANCE_PREFIX = "Blank shell chance: "
    val blankChanceDisplay = MutableStateFlow("$BLANK_CHANCE_PREFIX${blankChance}%")
}

@Composable
@Preview
fun CalculatorFrontend() {
    val gs = GameState(shellLineup, liveChance, blankChance)

    val shellLineupDisplayState by shellLineupDisplay.collectAsState()
    val liveChanceDisplayState by liveChanceDisplay.collectAsState()
    val blankChanceDisplayState by blankChanceDisplay.collectAsState()

    var lineupInput by remember { mutableStateOf("") }
    var isLineupValid by remember { mutableStateOf(true) }

    var knowledgeInput by remember { mutableStateOf("") }
    var isKnowledgeValid by remember { mutableStateOf(true) }

    MaterialTheme {
        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(
                label = { Text("Shell Lineup (Live/Blank - Ex: 3/4)") },
                value = lineupInput,
                onValueChange = { i ->
                    lineupInput = i
                    isLineupValid = checkLineupIsValid(i)
                },
                isError = !isLineupValid
            )
            if (!isLineupValid) {
                Text(text = "Enter shells as 'live/blank'. Ex: 1/2 or 4/4.", color = Color.Red)
            } else {
                Button(onClick = {
                    if (lineupInput.isNotBlank()) {
                        populateShellLineUp(gs, lineupInput)
                        updateOdds(gs)
                        updateGameStateDisplay(gs)
                    }
                }) { Text("Submit Round Lineup") }
            }
            OutlinedTextField(
                label = { Text("Shell Knowledge (Ex: 4l or 3b)") },
                value = knowledgeInput,
                onValueChange = { i ->
                    knowledgeInput = i
                    isKnowledgeValid = checkKnowledgeIsValid(i)
                },
                isError = !isKnowledgeValid
            )
            if (!isKnowledgeValid) {
                Text(text = "Enter shell knowledge as 'location/type'. Ex: 3/l or 5/b.", color = Color.Red)
            } else {
                Button(onClick = {
                    if (knowledgeInput.isNotBlank()) {
                        addShellKnowledge(gs, knowledgeInput)
                        updateOdds(gs)
                        updateGameStateDisplay(gs)
                    }
                }) { Text("Submit Shell Knowledge") }
            }
            Text(text = shellLineupDisplayState)
            Text(text = liveChanceDisplayState)
            Text(text = blankChanceDisplayState)
            Row {
                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        cycleShell(gs, ShellType.LIVE)
                        updateOdds(gs)
                        updateGameStateDisplay(gs)
                    }
                ) { Text("Live Fired") }
                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        cycleShell(gs, ShellType.BLANK)
                        updateOdds(gs)
                        updateGameStateDisplay(gs)
                    }
                ) { Text("Blank Cycled") }
            }
        }
    }
}

fun checkLineupIsValid(input: String): Boolean {
    return if (input.isBlank()) {
        false
    } else {
        input.length == 3 && input[1] == '/'
    }
}

fun checkKnowledgeIsValid(input: String): Boolean {
    return if (input.isBlank()) {
        false
    } else {
        input.length == 2
    }
}

fun updateGameStateDisplay(gs: GameState) {
    shellLineupDisplay.value = gs.shellLineup.toString()
    liveChanceDisplay.value = "$LIVE_CHANCE_PREFIX${gs.liveChance}%"
    blankChanceDisplay.value = "$BLANK_CHANCE_PREFIX${gs.blankChance}%"
}
