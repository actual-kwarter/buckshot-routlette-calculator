package bsrCalculator

import java.text.DecimalFormat
import java.util.*

private var scanner: Scanner = Scanner(System.`in`)
private var liveTotal: Int = 0
private var liveRemaining: Int = 0
private var blankTotal: Int = 0
private var blankRemaining: Int = 0

fun main(args: Array<String>) {
    println("=== Buckshot Roulette Calculator v1.0 ===")
    println("=== Round Begins. Good luck. ===")
    println("Enter live/blank shells (Ex: 3/4):")
    val shells = scanner.nextLine()

    liveRemaining = shells.substring(0, 1).toInt()
    blankRemaining = shells.substring(2, 3).toInt()
    liveTotal = liveRemaining
    blankTotal = blankRemaining
    val shellLineup: MutableList<ShellType> = ArrayList()
    fillArray(shellLineup, (liveRemaining + blankRemaining))

    var isFirstRound = true
    while (shellLineup.isNotEmpty()) {
        if (!isFirstRound) {
            println("=== Player or Dealer turn happens now. ===")
            println("How many shells were spent? (0 for end of round)")
            var spent = scanner.nextLine().toInt()

            if (spent < 1 || spent == shellLineup.size) {
                println("No more shells remaining in gun, the round is over.")
                return
            }

            if (unknownShellsRemain(shellLineup)) {
                println("Enter the shell sequence for the last turn (Ex: b,b,l):")
                val shellSequence = Arrays.stream(
                    scanner.nextLine()
                        .trim().split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                ).map { rawType: String -> ShellType.getShellType(rawType) }.toList()
                blankRemaining -= getShellCount(shellSequence, ShellType.BLANK)
                liveRemaining -= getShellCount(shellSequence, ShellType.LIVE)
            }

            while (spent != 0) {
                shellLineup.removeFirst()
                spent--
            }
            println("Remaining shells: " + shellLineup.size)
        } else {
            isFirstRound = false
        }

        calculateRemainingShells(shellLineup)
        if (unknownShellsRemain(shellLineup)) {
            addShellKnowledge(shellLineup)
        }
        println("Shell lineup: $shellLineup | Blank: $blankRemaining, Live: $liveRemaining")
        calculateShellOdds(shellLineup)
    }
}

private fun fillArray(shellLineup: MutableList<ShellType>, total: Int) {
    println("Adding $total shells.")
    for (i in 0..<total) {
        shellLineup.add(ShellType.UNKNOWN)
    }
}

private fun addShellKnowledge(shellLineup: MutableList<ShellType>) {
    println("Do you know the location of any shells? (y/n)")
    var answer = scanner.nextLine()

    if (isAffirmative(answer)) {
        var finished = false
        while (!finished) {
            println("Enter the position of the shell and its type (Ex: 1/l or 3/b): ")
            val knowledge = scanner.nextLine()
            val position = knowledge.substring(0, 1).toInt()
            val shellType = ShellType.getShellType(knowledge.substring(2, 3))
            shellLineup[position - 1] = shellType
            if (shellType == ShellType.LIVE) {
                if (getShellCount(shellLineup, ShellType.LIVE) == liveTotal) {
                    Collections.replaceAll(shellLineup, ShellType.UNKNOWN, ShellType.BLANK)
                }
            }
            if (shellType == ShellType.BLANK) {
                if (getShellCount(shellLineup, ShellType.BLANK) == blankTotal) {
                    Collections.replaceAll(shellLineup, ShellType.UNKNOWN, ShellType.LIVE)
                }
            }
            println("Shell lineup updated.")

            if (unknownShellsRemain(shellLineup)) {
                println("Do you know the location of any more shells? (y/n)")
                answer = scanner.nextLine()
                if (!isAffirmative(answer)) {
                    finished = true
                }
            } else {
                finished = true
            }
        }

        println("Remaining shells: " + shellLineup.size)
    }
}

private fun isAffirmative(answer: String): Boolean {
    return answer.trim().equals("y", ignoreCase = true) || answer.trim().equals("yes", ignoreCase = true)
}

private fun unknownShellsRemain(shellLineup: MutableList<ShellType>): Boolean {
    return shellLineup.contains(ShellType.UNKNOWN)
}

private fun getShellCount(shellLineup: MutableList<ShellType>, shellType: ShellType): Int {
    return shellLineup.stream().filter { st: ShellType -> st == shellType }.count().toInt()
}

private fun calculateRemainingShells(shellLineup: MutableList<ShellType>) {
    if (blankRemaining == 0) {
        shellLineup.fill(ShellType.LIVE)
    }
    if (liveRemaining == 0) {
        shellLineup.fill(ShellType.BLANK)
    }
}

private fun calculateShellOdds(shellLineup: MutableList<ShellType>) {
    //First shell is known or only one shell type left
    if (shellLineup.first() == ShellType.BLANK || liveRemaining == 0) {
        println("Chance of live round: 0%")
        println("Chance of blank round: 100%")
        return
    } else if (shellLineup.first() == ShellType.LIVE || blankRemaining == 0) {
        println("Chance of live round: 100%")
        println("Chance of blank round: 0%")
        return
    }

    val remainingUnknownBlank = blankRemaining - getShellCount(shellLineup, ShellType.BLANK)
    val remainingUnknownLive = liveRemaining - getShellCount(shellLineup, ShellType.LIVE)

    //Equal amounts of unknown shells remain
    if (shellLineup.size > 1 && remainingUnknownBlank == remainingUnknownLive) {
        println("Chance of live round: 50%")
        println("Chance of blank round: 50%")
        return
    }

    val df = DecimalFormat("##0")
    val remainingUnknown = getShellCount(shellLineup, ShellType.UNKNOWN)
    val blankChance = ((remainingUnknownBlank.toDouble() / remainingUnknown.toDouble()) * 100)
    val liveChance = ((remainingUnknownLive.toDouble() / remainingUnknown.toDouble()) * 100)

    println("Chance of live round: ${df.format(liveChance)}%")
    println("Chance of blank round: ${df.format(blankChance)}%")
}

private enum class ShellType {
    UNKNOWN, LIVE, BLANK;

    companion object {
        fun getShellType(rawType: String): ShellType {
            return if (rawType.trim().equals("l", ignoreCase = true)) {
                LIVE
            } else if (rawType.trim().equals("b", ignoreCase = true)) {
                BLANK
            } else {
                UNKNOWN
            }
        }
    }
}
