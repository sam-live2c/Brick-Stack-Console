package com.example.game

import kotlin.random.Random

class TetrisEngine(
    private val onSoundEvent: (SoundEffectEvent) -> Unit = {},
    private val onHapticEvent: (HapticEffectEvent) -> Unit = {}
) {
    var state = TetrisGameState()
        private set

    private val grid = Array(TetrisGameState.BOARD_HEIGHT) { IntArray(TetrisGameState.BOARD_WIDTH) }
    private var bag = mutableListOf<TetrominoType>()
    private var currentPiece: Tetromino? = null
    private var nextPieceType: TetrominoType = getRandomPieceFromBag()
    private var holdPieceType: TetrominoType? = null
    private var canHold = true
    private var baseScore = 0
    private var finalCalculatedScore = 0
    private var highScore = 0
    private var level = 1
    private var startLevel = 1
    private var linesCleared = 0
    private var combo = 0
    private var gameMode = GameMode.MARATHON
    private var speedMultiplier = 1.0f
    private var isLevelUpBannerVisible = false
    private var elapsedTimeSeconds = 0
    private var timeRemainingSeconds = 120

    fun setHighScore(savedHighScore: Int) {
        highScore = savedHighScore
        updateState()
    }

    fun startGame(
        startLevel: Int = 1,
        gameMode: GameMode = GameMode.MARATHON,
        speedMultiplier: Float = 1.0f
    ) {
        clearGrid()
        bag.clear()
        this.startLevel = startLevel.coerceIn(1, 10)
        this.gameMode = gameMode
        this.speedMultiplier = speedMultiplier
        level = this.startLevel
        baseScore = 0
        finalCalculatedScore = 0
        linesCleared = 0
        combo = 0
        elapsedTimeSeconds = 0
        timeRemainingSeconds = 120
        isLevelUpBannerVisible = false
        holdPieceType = null
        canHold = true
        nextPieceType = getRandomPieceFromBag()
        spawnNextPiece()
        state = state.copy(status = GameStatus.PLAYING)
        updateState()
    }

    fun updateTimerTick() {
        if (state.status != GameStatus.PLAYING) return
        elapsedTimeSeconds++
        if (gameMode == GameMode.ULTRA_2MIN) {
            timeRemainingSeconds = (120 - elapsedTimeSeconds).coerceAtLeast(0)
            if (timeRemainingSeconds <= 0) {
                triggerTimesUp()
                return
            }
        }
        updateState()
    }

    fun triggerTimesUp() {
        calculateFinalScore()
        state = state.copy(status = GameStatus.TIMES_UP)
        onSoundEvent(SoundEffectEvent.LEVEL_UP)
        updateState()
    }

    fun hideLevelUpBanner() {
        isLevelUpBannerVisible = false
        updateState()
    }

    fun togglePause() {
        if (state.status == GameStatus.PLAYING) {
            state = state.copy(status = GameStatus.PAUSED)
            onSoundEvent(SoundEffectEvent.BUTTON_CLICK)
        } else if (state.status == GameStatus.PAUSED) {
            state = state.copy(status = GameStatus.PLAYING)
            onSoundEvent(SoundEffectEvent.BUTTON_CLICK)
        }
    }

    fun resetGame(
        startLevel: Int = 1,
        gameMode: GameMode = GameMode.MARATHON,
        speedMultiplier: Float = 1.0f
    ) {
        startGame(startLevel, gameMode, speedMultiplier)
        onSoundEvent(SoundEffectEvent.BUTTON_CLICK)
    }

    fun tick(): Boolean {
        if (state.status != GameStatus.PLAYING) return false
        val p = currentPiece ?: return false

        // Attempt to move down
        val movedDown = p.moved(0, 1)
        if (isValidPosition(movedDown)) {
            currentPiece = movedDown
            updateState()
            return true
        } else {
            // Lock piece in place
            lockCurrentPiece()
            return false
        }
    }

    fun moveLeft() {
        if (state.status != GameStatus.PLAYING) return
        val p = currentPiece ?: return
        val moved = p.moved(-1, 0)
        if (isValidPosition(moved)) {
            currentPiece = moved
            onHapticEvent(HapticEffectEvent.MOVE)
            onSoundEvent(SoundEffectEvent.MOVE)
            updateState()
        }
    }

    fun moveRight() {
        if (state.status != GameStatus.PLAYING) return
        val p = currentPiece ?: return
        val moved = p.moved(1, 0)
        if (isValidPosition(moved)) {
            currentPiece = moved
            onHapticEvent(HapticEffectEvent.MOVE)
            onSoundEvent(SoundEffectEvent.MOVE)
            updateState()
        }
    }

    fun softDrop() {
        if (state.status != GameStatus.PLAYING) return
        val p = currentPiece ?: return
        val moved = p.moved(0, 1)
        if (isValidPosition(moved)) {
            currentPiece = moved
            baseScore += 1 // Bonus point for soft drop
            onHapticEvent(HapticEffectEvent.SOFT_DROP)
            onSoundEvent(SoundEffectEvent.MOVE)
            updateState()
        } else {
            lockCurrentPiece()
        }
    }

    fun hardDrop() {
        if (state.status != GameStatus.PLAYING) return
        val p = currentPiece ?: return
        var dropDistance = 0
        var testPiece = p
        while (isValidPosition(testPiece.moved(0, 1))) {
            testPiece = testPiece.moved(0, 1)
            dropDistance++
        }
        currentPiece = testPiece
        baseScore += dropDistance * 2 // Bonus points for hard drop
        onHapticEvent(HapticEffectEvent.HARD_DROP)
        onSoundEvent(SoundEffectEvent.DROP)
        lockCurrentPiece()
    }

    fun rotateClockwise() {
        if (state.status != GameStatus.PLAYING) return
        val p = currentPiece ?: return
        val rotated = p.rotatedClockwise()

        // SRS Wall kick offset tests
        val kickOffsets = arrayOf(
            Pair(0, 0), Pair(-1, 0), Pair(1, 0), Pair(0, -1),
            Pair(-2, 0), Pair(2, 0), Pair(0, -2), Pair(-1, -1), Pair(1, -1)
        )

        for ((dx, dy) in kickOffsets) {
            val kicked = rotated.moved(dx, dy)
            if (isValidPosition(kicked)) {
                currentPiece = kicked
                onHapticEvent(HapticEffectEvent.ROTATE)
                onSoundEvent(SoundEffectEvent.ROTATE)
                updateState()
                return
            }
        }
    }

    fun rotateCounterClockwise() {
        if (state.status != GameStatus.PLAYING) return
        val p = currentPiece ?: return
        val rotated = p.rotatedCounterClockwise()

        val kickOffsets = arrayOf(
            Pair(0, 0), Pair(1, 0), Pair(-1, 0), Pair(0, -1),
            Pair(2, 0), Pair(-2, 0), Pair(0, -2), Pair(1, -1), Pair(-1, -1)
        )

        for ((dx, dy) in kickOffsets) {
            val kicked = rotated.moved(dx, dy)
            if (isValidPosition(kicked)) {
                currentPiece = kicked
                onHapticEvent(HapticEffectEvent.ROTATE)
                onSoundEvent(SoundEffectEvent.ROTATE)
                updateState()
                return
            }
        }
    }

    fun holdPiece() {
        if (state.status != GameStatus.PLAYING || !canHold) return
        val p = currentPiece ?: return

        val currentType = p.type
        if (holdPieceType == null) {
            holdPieceType = currentType
            spawnNextPiece()
        } else {
            val temp = holdPieceType!!
            holdPieceType = currentType
            currentPiece = Tetromino.create(temp, startX = 3, startY = 0)
            if (!isValidPosition(currentPiece!!)) {
                state = state.copy(status = GameStatus.GAME_OVER)
                onSoundEvent(SoundEffectEvent.GAME_OVER)
                onHapticEvent(HapticEffectEvent.GAME_OVER)
                updateState()
                return
            }
        }
        canHold = false
        onHapticEvent(HapticEffectEvent.HOLD)
        onSoundEvent(SoundEffectEvent.ROTATE)
        updateState()
    }

    private fun spawnNextPiece() {
        currentPiece = Tetromino.create(nextPieceType, startX = 3, startY = 0)
        nextPieceType = getRandomPieceFromBag()
        canHold = true

        if (!isValidPosition(currentPiece!!)) {
            state = state.copy(status = GameStatus.GAME_OVER)
            onSoundEvent(SoundEffectEvent.GAME_OVER)
            onHapticEvent(HapticEffectEvent.GAME_OVER)
            updateState()
        }
    }

    private fun lockCurrentPiece() {
        val p = currentPiece ?: return
        val cells = p.getOccupiedCells()

        // Place blocks on grid
        for (cell in cells) {
            if (cell.y in 0 until TetrisGameState.BOARD_HEIGHT && cell.x in 0 until TetrisGameState.BOARD_WIDTH) {
                grid[cell.y][cell.x] = p.type.id
            }
        }

        onHapticEvent(HapticEffectEvent.DROP_LOCK)
        onSoundEvent(SoundEffectEvent.LOCK)

        // Check line clears
        val fullRows = mutableListOf<Int>()
        for (r in 0 until TetrisGameState.BOARD_HEIGHT) {
            if (grid[r].all { it != 0 }) {
                fullRows.add(r)
            }
        }

        if (fullRows.isNotEmpty()) {
            clearLines(fullRows)
        } else {
            combo = 0
            spawnNextPiece()
            updateState()
        }
    }

    private fun calculateFinalScore() {
        val multiplierBonus = (baseScore * speedMultiplier).toInt()
        val lineBonus = linesCleared * 25
        val levelBonus = level * 100
        val modeWinBonus = if (state.status == GameStatus.VICTORY) 1000 else 0
        finalCalculatedScore = (multiplierBonus + lineBonus + levelBonus + modeWinBonus).coerceAtLeast(0)
        if (finalCalculatedScore > highScore) {
            highScore = finalCalculatedScore
        }
    }

    private fun clearLines(rowsToClear: List<Int>) {
        val clearedCount = rowsToClear.size
        combo++

        // Calculate score
        val gainedScore = when (clearedCount) {
            1 -> 100 * level
            2 -> 300 * level
            3 -> 500 * level
            4 -> 800 * level // TETRIS!
            else -> 1000 * level
        }
        val comboBonus = (combo - 1) * 50 * level
        baseScore += gainedScore + comboBonus
        calculateFinalScore()

        linesCleared += clearedCount

        // Check Level Up every 10 lines relative to start level
        val newLevel = (startLevel + linesCleared / 10).coerceAtMost(10)
        if (newLevel > level) {
            level = newLevel
            isLevelUpBannerVisible = true
            onSoundEvent(SoundEffectEvent.LEVEL_UP)
            onHapticEvent(HapticEffectEvent.LEVEL_UP)
        }

        // Haptics & Audio based on cleared count
        when (clearedCount) {
            1 -> {
                onSoundEvent(SoundEffectEvent.LINE_CLEAR)
                onHapticEvent(HapticEffectEvent.LINE_CLEAR_SINGLE)
            }
            2 -> {
                onSoundEvent(SoundEffectEvent.LINE_CLEAR)
                onHapticEvent(HapticEffectEvent.LINE_CLEAR_DOUBLE)
            }
            3 -> {
                onSoundEvent(SoundEffectEvent.LINE_CLEAR)
                onHapticEvent(HapticEffectEvent.LINE_CLEAR_TRIPLE)
            }
            else -> {
                onSoundEvent(SoundEffectEvent.TETRIS_CLEAR)
                onHapticEvent(HapticEffectEvent.TETRIS_BURST)
            }
        }

        // Remove rows and shift grid down
        for (r in rowsToClear.sorted()) {
            for (y in r downTo 1) {
                grid[y] = grid[y - 1].copyOf()
            }
            grid[0] = IntArray(TetrisGameState.BOARD_WIDTH) { 0 }
        }

        // Check Game Mode Victory Conditions
        if (gameMode == GameMode.SPRINT_40 && linesCleared >= 40) {
            calculateFinalScore()
            state = state.copy(status = GameStatus.VICTORY)
            onSoundEvent(SoundEffectEvent.TETRIS_CLEAR)
            onHapticEvent(HapticEffectEvent.VICTORY)
            updateState()
            return
        } else if (gameMode == GameMode.LEVEL_STAGE && linesCleared >= startLevel * 15) {
            calculateFinalScore()
            state = state.copy(status = GameStatus.VICTORY)
            onSoundEvent(SoundEffectEvent.LEVEL_UP)
            onHapticEvent(HapticEffectEvent.VICTORY)
            updateState()
            return
        }

        spawnNextPiece()
        updateState()
    }

    private fun isValidPosition(piece: Tetromino): Boolean {
        val cells = piece.getOccupiedCells()
        for (cell in cells) {
            if (cell.x < 0 || cell.x >= TetrisGameState.BOARD_WIDTH) return false
            if (cell.y < 0 || cell.y >= TetrisGameState.BOARD_HEIGHT) return false
            if (cell.y >= 0 && grid[cell.y][cell.x] != 0) return false
        }
        return true
    }

    private fun calculateGhostPiece(): Tetromino? {
        val p = currentPiece ?: return null
        var ghost = p
        while (isValidPosition(ghost.moved(0, 1))) {
            ghost = ghost.moved(0, 1)
        }
        return ghost
    }

    private fun getRandomPieceFromBag(): TetrominoType {
        if (bag.isEmpty()) {
            bag.addAll(TetrominoType.values().toList().shuffled())
        }
        return bag.removeAt(0)
    }

    private fun clearGrid() {
        for (r in 0 until TetrisGameState.BOARD_HEIGHT) {
            grid[r].fill(0)
        }
    }

    private fun updateState() {
        val gridCopy = Array(TetrisGameState.BOARD_HEIGHT) { r -> grid[r].copyOf() }
        val ghost = calculateGhostPiece()
        calculateFinalScore()

        state = state.copy(
            grid = gridCopy,
            currentPiece = currentPiece,
            ghostPiece = ghost,
            nextPiece = nextPieceType,
            holdPiece = holdPieceType,
            canHold = canHold,
            score = finalCalculatedScore,
            baseScore = baseScore,
            finalCalculatedScore = finalCalculatedScore,
            highScore = highScore,
            level = level,
            linesCleared = linesCleared,
            combo = combo,
            gameMode = gameMode,
            speedMultiplier = speedMultiplier,
            isLevelUpBannerVisible = isLevelUpBannerVisible,
            elapsedTimeSeconds = elapsedTimeSeconds,
            timeRemainingSeconds = timeRemainingSeconds
        )
    }

    // Get drop interval in milliseconds adjusted by user speed multiplier
    fun getDropIntervalMs(): Long {
        val baseMs = when (level) {
            1 -> 800L
            2 -> 720L
            3 -> 640L
            4 -> 550L
            5 -> 460L
            6 -> 370L
            7 -> 290L
            8 -> 210L
            9 -> 150L
            10 -> 110L
            else -> 100L
        }
        return (baseMs / speedMultiplier.coerceAtLeast(0.1f)).toLong().coerceAtLeast(30L)
    }
}
