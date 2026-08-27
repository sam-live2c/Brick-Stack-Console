package com.example.game

enum class GameStatus {
    IDLE, PLAYING, PAUSED, GAME_OVER, VICTORY, TIMES_UP
}

enum class GameMode(val label: String, val description: String) {
    MARATHON("Marathon", "Classic endless leveling mode"),
    SPRINT_40("Sprint 40", "Clear 40 lines as fast as possible"),
    ULTRA_2MIN("Ultra 2-Min", "Score maximum points before 2 minutes expire"),
    LEVEL_STAGE("Level Stage", "Clear 15 lines per stage to win stage")
}

enum class SoundEffectEvent {
    MOVE, ROTATE, LOCK, DROP, LINE_CLEAR, TETRIS_CLEAR, LEVEL_UP, GAME_OVER, BUTTON_CLICK
}

enum class HapticEffectEvent {
    MOVE,
    ROTATE,
    HOLD,
    SOFT_DROP,
    HARD_DROP,
    DROP_LOCK,
    LINE_CLEAR_SINGLE,
    LINE_CLEAR_DOUBLE,
    LINE_CLEAR_TRIPLE,
    TETRIS_BURST,
    LEVEL_UP,
    GAME_OVER,
    VICTORY,
    BUTTON_CLICK,
    TICK,
    LINE_CLEAR
}

data class TetrisGameState(
    val grid: Array<IntArray> = Array(BOARD_HEIGHT) { IntArray(BOARD_WIDTH) },
    val currentPiece: Tetromino? = null,
    val ghostPiece: Tetromino? = null,
    val nextPiece: TetrominoType? = null,
    val holdPiece: TetrominoType? = null,
    val canHold: Boolean = true,
    val score: Int = 0,
    val baseScore: Int = 0,
    val finalCalculatedScore: Int = 0,
    val highScore: Int = 0,
    val level: Int = 1,
    val linesCleared: Int = 0,
    val combo: Int = 0,
    val status: GameStatus = GameStatus.IDLE,
    val gameMode: GameMode = GameMode.MARATHON,
    val speedMultiplier: Float = 1.0f,
    val isLevelUpBannerVisible: Boolean = false,
    val elapsedTimeSeconds: Int = 0,
    val timeRemainingSeconds: Int = 120,
    val clearingLines: List<Int> = emptyList(), // Rows currently in clearing animation
    val lineClearTrigger: Long = 0L,
    val lastClearedCount: Int = 0,
    val lastActionMessage: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TetrisGameState

        if (score != other.score) return false
        if (baseScore != other.baseScore) return false
        if (finalCalculatedScore != other.finalCalculatedScore) return false
        if (highScore != other.highScore) return false
        if (level != other.level) return false
        if (linesCleared != other.linesCleared) return false
        if (combo != other.combo) return false
        if (status != other.status) return false
        if (gameMode != other.gameMode) return false
        if (speedMultiplier != other.speedMultiplier) return false
        if (isLevelUpBannerVisible != other.isLevelUpBannerVisible) return false
        if (elapsedTimeSeconds != other.elapsedTimeSeconds) return false
        if (timeRemainingSeconds != other.timeRemainingSeconds) return false
        if (currentPiece != other.currentPiece) return false
        if (ghostPiece != other.ghostPiece) return false
        if (nextPiece != other.nextPiece) return false
        if (holdPiece != other.holdPiece) return false
        if (canHold != other.canHold) return false
        if (clearingLines != other.clearingLines) return false
        if (lineClearTrigger != other.lineClearTrigger) return false
        if (lastClearedCount != other.lastClearedCount) return false
        if (lastActionMessage != other.lastActionMessage) return false

        for (i in 0 until BOARD_HEIGHT) {
            if (!grid[i].contentEquals(other.grid[i])) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = score
        result = 31 * result + baseScore
        result = 31 * result + finalCalculatedScore
        result = 31 * result + highScore
        result = 31 * result + level
        result = 31 * result + linesCleared
        result = 31 * result + status.hashCode()
        result = 31 * result + gameMode.hashCode()
        result = 31 * result + (currentPiece?.hashCode() ?: 0)
        result = 31 * result + lineClearTrigger.hashCode()
        return result
    }

    companion object {
        const val BOARD_WIDTH = 10
        const val BOARD_HEIGHT = 20
    }
}
