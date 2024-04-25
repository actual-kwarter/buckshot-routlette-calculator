package bsrCalculator

enum class ShellType(val display: String) {
    UNKNOWN("?"), LIVE("Live"), BLANK("Blank");

    companion object {
        fun getShellType(rawType: String): ShellType {
            return when (rawType.trim().lowercase()) {
                "l", "live" -> LIVE
                "b", "blank" -> BLANK
                else -> UNKNOWN
            }
        }
    }

    override fun toString(): String {
        return display
    }
}
