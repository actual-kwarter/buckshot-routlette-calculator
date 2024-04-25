package bsrCalculator

import java.util.*

private var scanner: Scanner = Scanner(System.`in`)
private var liveTotal: Int = 0
private var liveRemaining: Int = 0
private var blankTotal: Int = 0
private var blankRemaining: Int = 0

fun reset(shellLineup: MutableList<ShellType>) {
    liveTotal = 0
    liveRemaining = 0
    blankTotal = 0
    blankRemaining = 0
    shellLineup.clear()
}

fun populateShellLineUp(gs: GameState, rawShells: String?) {
    if (rawShells.isNullOrBlank()) return

    reset(gs.shellLineup)

    liveRemaining = rawShells.substring(0, 1).toInt()
    blankRemaining = rawShells.substring(2, 3).toInt()
    liveTotal = liveRemaining
    blankTotal = blankRemaining
    fillArray(gs.shellLineup, (liveRemaining + blankRemaining))
}

fun cycleShell(gs: GameState, shellType: ShellType) {
    if (gs.shellLineup.size == 1) {
        println("Last shell in shotgun, no action taken.")
        return
    }

    if (shellType == ShellType.LIVE) {
        if (liveRemaining == 0) {
            println("No live shells remaining.")
            gs.shellLineup.fill(ShellType.BLANK)
            return
        }

        if (gs.shellLineup[0] == ShellType.BLANK) {
            println("Current shell is blank, cannot fire.")
            return
        }

        println("Live fired.")
        liveRemaining--
    } else {
        if (blankRemaining == 0) {
            println("No blank shells remaining.")
            gs.shellLineup.fill(ShellType.LIVE)
            return
        }

        if (gs.shellLineup[0] == ShellType.LIVE) {
            println("Current shell is live, cannot cycle.")
            return
        }

        println("Blank cycled.")
        blankRemaining--
    }

    gs.shellLineup.removeFirst()
    checkRemainingShells(gs.shellLineup)
}

fun checkRemainingShells(shellLineup: MutableList<ShellType>) {
    if (!shellLineup.contains(ShellType.UNKNOWN)) return
    if (liveRemaining == 0) {
        shellLineup.fill(ShellType.BLANK)
    }
    if (blankRemaining == 0) {
        shellLineup.fill(ShellType.LIVE)
    }
}

fun updateOdds(gs: GameState) {
    //No live remaining
    if (liveRemaining == 0 || gs.shellLineup[0] == ShellType.BLANK) {
        gs.liveChance = 0
        gs.blankChance = 100
        gs.shellLineup[0] = ShellType.BLANK
        return
    }

    //No blank remaining
    if (blankRemaining == 0 || gs.shellLineup[0] == ShellType.LIVE) {
        gs.liveChance = 100
        gs.blankChance = 0
        gs.shellLineup[0] = ShellType.LIVE
        return
    }

    val remainingUnknownBlank = blankRemaining - getShellCount(gs.shellLineup, ShellType.BLANK)
    val remainingUnknownLive = liveRemaining - getShellCount(gs.shellLineup, ShellType.LIVE)

    //Equal amounts of unknown shells remain
    if (gs.shellLineup.size > 1 && remainingUnknownBlank == remainingUnknownLive) {
        gs.liveChance = 50
        gs.blankChance = 50
        return
    }

    //All other cases
    val remainingUnknown = getShellCount(gs.shellLineup, ShellType.UNKNOWN)
    gs.blankChance = ((remainingUnknownBlank.toDouble() / remainingUnknown.toDouble()) * 100).toInt()
    gs.liveChance = ((remainingUnknownLive.toDouble() / remainingUnknown.toDouble()) * 100).toInt()

    if (gs.blankChance == 100) gs.shellLineup[0] = ShellType.BLANK
    if (gs.liveChance == 100) gs.shellLineup[0] = ShellType.LIVE
}

fun addShellKnowledge(gs: GameState, input: String) {
    val position = input.substring(0, 1).toInt()
    val shellType = ShellType.getShellType(input.substring(1, 2))
    gs.shellLineup[position - 1] = shellType

    if (shellType == ShellType.LIVE) {
        if (getShellCount(gs.shellLineup, ShellType.LIVE) == liveTotal) {
            Collections.replaceAll(gs.shellLineup, ShellType.UNKNOWN, ShellType.BLANK)
        }
    }
    if (shellType == ShellType.BLANK) {
        if (getShellCount(gs.shellLineup, ShellType.BLANK) == blankTotal) {
            Collections.replaceAll(gs.shellLineup, ShellType.UNKNOWN, ShellType.LIVE)
        }
    }
}

private fun getShellCount(shellLineup: MutableList<ShellType>, shellType: ShellType): Int {
    return shellLineup.stream().filter { st: ShellType -> st == shellType }.count().toInt()
}

private fun fillArray(shellLineup: MutableList<ShellType>, total: Int) {
    println("Adding $total shells.")
    for (i in 0..<total) {
        shellLineup.add(ShellType.UNKNOWN)
    }
}
