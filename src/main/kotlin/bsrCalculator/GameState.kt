package bsrCalculator

data class GameState(
    val shellLineup: MutableList<ShellType>,
    var liveChance: Int,
    var blankChance: Int
)
