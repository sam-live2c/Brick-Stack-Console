package com.example.ui

import android.app.Application
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioSynthesizer
import com.example.data.AppDatabase
import com.example.data.HighScoreEntity
import com.example.data.SettingsRepository
import com.example.data.UserSettings
import com.example.game.GameStatus
import com.example.game.HapticEffectEvent
import com.example.game.SoundEffectEvent
import com.example.game.TetrisEngine
import com.example.game.TetrisGameState
import com.example.haptics.HapticEngine
import com.example.ui.console.ConsoleSkin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TetrisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val highScoreDao = db.highScoreDao()
    private val settingsRepo = SettingsRepository(application)

    val audioSynthesizer = AudioSynthesizer()
    val hapticEngine = HapticEngine(application)

    private val _userSettings = MutableStateFlow(settingsRepo.loadSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

    private val _currentSkin = MutableStateFlow(ConsoleSkin.getById(_userSettings.value.themeIndex))
    val currentSkin: StateFlow<ConsoleSkin> = _currentSkin.asStateFlow()

    private val _highScores = MutableStateFlow<List<HighScoreEntity>>(emptyList())
    val highScores: StateFlow<List<HighScoreEntity>> = _highScores.asStateFlow()

    private val engine = TetrisEngine(
        onSoundEvent = { event -> audioSynthesizer.playSound(event) },
        onHapticEvent = { event -> hapticEngine.trigger(event) }
    )

    private val _gameState = MutableStateFlow(engine.state)
    val gameState: StateFlow<TetrisGameState> = _gameState.asStateFlow()

    private var tickJob: Job? = null
    private var secondTimerJob: Job? = null
    private var lastSavedScore = 0

    init {
        // Apply loaded settings
        audioSynthesizer.enabled = _userSettings.value.soundEnabled
        hapticEngine.intensity = _userSettings.value.hapticIntensity

        // Observe High Scores from Room DB
        viewModelScope.launch {
            highScoreDao.getTopHighScores().collectLatest { scores ->
                _highScores.value = scores
                val maxScore = scores.maxOfOrNull { it.score } ?: 0
                engine.setHighScore(maxScore)
                _gameState.value = engine.state
            }
        }
    }

    fun updateSettings(newSettings: UserSettings) {
        val previousStartLevel = _userSettings.value.startLevel
        val previousSpeed = _userSettings.value.speedMultiplier
        _userSettings.value = newSettings
        settingsRepo.saveSettings(newSettings)

        audioSynthesizer.enabled = newSettings.soundEnabled
        hapticEngine.intensity = newSettings.hapticIntensity
        _currentSkin.value = ConsoleSkin.getById(newSettings.themeIndex)

        if ((previousStartLevel != newSettings.startLevel || previousSpeed != newSettings.speedMultiplier) &&
            (engine.state.status == GameStatus.IDLE || engine.state.status == GameStatus.GAME_OVER || engine.state.status == GameStatus.VICTORY || engine.state.status == GameStatus.TIMES_UP)) {
            resetGame()
        }
    }

    fun cycleNextTheme() {
        val nextId = (_userSettings.value.themeIndex + 1) % ConsoleSkin.ALL_SKINS.size
        updateSettings(_userSettings.value.copy(themeIndex = nextId))
        audioSynthesizer.playSound(SoundEffectEvent.BUTTON_CLICK)
        hapticEngine.trigger(HapticEffectEvent.BUTTON_CLICK)
    }

    fun toggleSound() {
        updateSettings(_userSettings.value.copy(soundEnabled = !_userSettings.value.soundEnabled))
        audioSynthesizer.playSound(SoundEffectEvent.BUTTON_CLICK)
        hapticEngine.trigger(HapticEffectEvent.BUTTON_CLICK)
    }

    fun startGame(
        startLevel: Int = _userSettings.value.startLevel,
        gameMode: com.example.game.GameMode = _gameState.value.gameMode,
        speedMultiplier: Float = _userSettings.value.speedMultiplier
    ) {
        stopTickTimer()
        engine.startGame(
            startLevel = startLevel,
            gameMode = gameMode,
            speedMultiplier = speedMultiplier
        )
        _gameState.value = engine.state
        startTickTimer()
    }

    fun setGameMode(mode: com.example.game.GameMode) {
        startGame(gameMode = mode)
    }

    fun selectLevel(targetLevel: Int) {
        val currentMax = _userSettings.value.maxUnlockedLevel
        val validLevel = targetLevel.coerceIn(1, currentMax.coerceAtLeast(1))
        val updatedSettings = _userSettings.value.copy(startLevel = validLevel)
        _userSettings.value = updatedSettings
        settingsRepo.saveSettings(updatedSettings)
        audioSynthesizer.playSound(SoundEffectEvent.BUTTON_CLICK)
        hapticEngine.trigger(HapticEffectEvent.BUTTON_CLICK)
    }

    fun dismissLevelUpBanner() {
        engine.hideLevelUpBanner()
        _gameState.value = engine.state
    }

    fun togglePause() {
        engine.togglePause()
        _gameState.value = engine.state
        if (engine.state.status == GameStatus.PLAYING) {
            startTickTimer()
        } else {
            stopTickTimer()
        }
    }

    fun replayCurrentLevel() {
        val currentLevel = _gameState.value.level
        startGame(startLevel = currentLevel)
        audioSynthesizer.playSound(SoundEffectEvent.BUTTON_CLICK)
        hapticEngine.trigger(HapticEffectEvent.BUTTON_CLICK)
    }

    fun nextLevel() {
        val currentLvl = _gameState.value.level
        val (nextLvl, maxUnlocked) = if (currentLvl >= 1000) {
            Pair(1, 1000)
        } else {
            val targetNext = currentLvl + 1
            Pair(targetNext, (_userSettings.value.maxUnlockedLevel).coerceAtLeast(targetNext).coerceAtMost(1000))
        }

        val updatedSettings = _userSettings.value.copy(startLevel = nextLvl, maxUnlockedLevel = maxUnlocked)
        _userSettings.value = updatedSettings
        settingsRepo.saveSettings(updatedSettings)
        startGame(startLevel = nextLvl)
        audioSynthesizer.playSound(SoundEffectEvent.BUTTON_CLICK)
        hapticEngine.trigger(HapticEffectEvent.BUTTON_CLICK)
    }

    fun resetGame() {
        stopTickTimer()
        engine.resetGame(
            startLevel = _userSettings.value.startLevel,
            gameMode = _gameState.value.gameMode,
            speedMultiplier = _userSettings.value.speedMultiplier
        )
        _gameState.value = engine.state
        startTickTimer()
    }

    // Controls
    fun moveLeft(composeHaptic: HapticFeedback? = null) {
        if (_gameState.value.status == GameStatus.VICTORY) {
            replayCurrentLevel()
            return
        }
        if (_gameState.value.status != GameStatus.PLAYING) return
        engine.moveLeft()
        _gameState.value = engine.state
    }

    fun moveRight(composeHaptic: HapticFeedback? = null) {
        if (_gameState.value.status == GameStatus.VICTORY) {
            nextLevel()
            return
        }
        if (_gameState.value.status != GameStatus.PLAYING) return
        engine.moveRight()
        _gameState.value = engine.state
    }

    fun softDrop(composeHaptic: HapticFeedback? = null) {
        if (_gameState.value.status != GameStatus.PLAYING) return
        engine.softDrop()
        _gameState.value = engine.state
        checkGameEnd()
    }

    fun hardDrop(composeHaptic: HapticFeedback? = null) {
        if (_gameState.value.status == GameStatus.VICTORY) {
            nextLevel()
            return
        }
        if (_gameState.value.status != GameStatus.PLAYING) return
        engine.hardDrop()
        _gameState.value = engine.state
        checkGameEnd()
    }

    fun rotateClockwise(composeHaptic: HapticFeedback? = null) {
        if (_gameState.value.status == GameStatus.VICTORY) {
            nextLevel()
            return
        }
        if (_gameState.value.status != GameStatus.PLAYING) return
        engine.rotateClockwise()
        _gameState.value = engine.state
    }

    fun rotateCounterClockwise(composeHaptic: HapticFeedback? = null) {
        if (_gameState.value.status != GameStatus.PLAYING) return
        engine.rotateCounterClockwise()
        _gameState.value = engine.state
    }

    fun holdPiece(composeHaptic: HapticFeedback? = null) {
        if (_gameState.value.status != GameStatus.PLAYING) return
        engine.holdPiece()
        _gameState.value = engine.state
        checkGameEnd()
    }

    fun clearHighScores() {
        viewModelScope.launch(Dispatchers.IO) {
            highScoreDao.clearHighScores()
        }
    }

    private fun startTickTimer() {
        stopTickTimer()
        tickJob = viewModelScope.launch {
            while (engine.state.status == GameStatus.PLAYING) {
                val delayMs = engine.getDropIntervalMs()
                delay(delayMs)
                if (engine.state.status == GameStatus.PLAYING) {
                    engine.tick()
                    _gameState.value = engine.state
                    checkGameEnd()
                }
            }
        }

        secondTimerJob = viewModelScope.launch {
            while (engine.state.status == GameStatus.PLAYING) {
                delay(1000L)
                if (engine.state.status == GameStatus.PLAYING) {
                    engine.updateTimerTick()
                    _gameState.value = engine.state
                    checkGameEnd()
                }
            }
        }
    }

    private fun stopTickTimer() {
        tickJob?.cancel()
        tickJob = null
        secondTimerJob?.cancel()
        secondTimerJob = null
    }

    private fun checkGameEnd() {
        evaluateLevelProgress()
        val st = engine.state.status
        if (st == GameStatus.GAME_OVER || st == GameStatus.VICTORY || st == GameStatus.TIMES_UP) {
            stopTickTimer()
            val finalScore = engine.state.finalCalculatedScore
            if (finalScore > 0 && finalScore != lastSavedScore) {
                lastSavedScore = finalScore
                viewModelScope.launch(Dispatchers.IO) {
                    highScoreDao.insertHighScore(
                        HighScoreEntity(
                            score = finalScore,
                            level = engine.state.level,
                            lines = engine.state.linesCleared,
                            themeName = _currentSkin.value.name
                        )
                    )
                }
            }
        }
    }

    private fun evaluateLevelProgress() {
        val currentLevel = engine.state.level
        val isVictory = engine.state.status == GameStatus.VICTORY
        var settings = _userSettings.value

        val newMaxUnlocked = when {
            isVictory && currentLevel >= 1000 -> 1000
            currentLevel > settings.maxUnlockedLevel -> currentLevel.coerceAtMost(1000)
            else -> settings.maxUnlockedLevel
        }

        if (newMaxUnlocked != settings.maxUnlockedLevel) {
            val updatedSettings = settings.copy(maxUnlockedLevel = newMaxUnlocked)
            _userSettings.value = updatedSettings
            settingsRepo.saveSettings(updatedSettings)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioSynthesizer.release()
    }
}
