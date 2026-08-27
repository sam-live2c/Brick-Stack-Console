package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameStatus
import com.example.ui.TetrisViewModel
import com.example.ui.console.HandheldConsoleFrame
import com.example.ui.console.LevelSelectDialog
import com.example.ui.history.HighScoresDialog
import com.example.ui.settings.SettingsDialog
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    BrickConsoleApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}

@Composable
fun BrickConsoleApp(
    viewModel: TetrisViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val skin by viewModel.currentSkin.collectAsStateWithLifecycle()
    val highScores by viewModel.highScores.collectAsStateWithLifecycle()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHighScoresDialog by remember { mutableStateOf(false) }
    var showLevelSelectDialog by remember { mutableStateOf(false) }

    val composeHaptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D111A))
    ) {
        if (showSettingsDialog) {
            SettingsScreen(
                currentSettings = userSettings,
                skin = skin,
                onSaveSettings = { newSettings -> viewModel.updateSettings(newSettings) },
                onDismiss = { showSettingsDialog = false },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            HandheldConsoleFrame(
                gameState = gameState,
                skin = skin,
                userSettings = userSettings,
                onMoveLeft = { viewModel.moveLeft(composeHaptic) },
                onMoveRight = { viewModel.moveRight(composeHaptic) },
                onSoftDrop = { viewModel.softDrop(composeHaptic) },
                onHardDrop = { viewModel.hardDrop(composeHaptic) },
                onRotateClockwise = { viewModel.rotateClockwise(composeHaptic) },
                onRotateCounterClockwise = { viewModel.rotateCounterClockwise(composeHaptic) },
                onHoldPiece = { viewModel.holdPiece(composeHaptic) },
                onTogglePause = { viewModel.togglePause() },
                onReset = { viewModel.resetGame() },
                onToggleSound = { viewModel.toggleSound() },
                onOpenSettings = {
                    if (gameState.status == GameStatus.PLAYING) {
                        viewModel.togglePause()
                    }
                    showSettingsDialog = true
                },
                onOpenHighScores = {
                    if (gameState.status == GameStatus.PLAYING) {
                        viewModel.togglePause()
                    }
                    showHighScoresDialog = true
                },
                onGoHome = { viewModel.resetGame() },
                onLevelClick = {
                    if (gameState.status == GameStatus.PLAYING) {
                        viewModel.togglePause()
                    }
                    showLevelSelectDialog = true
                },
                onReplayLevel = { viewModel.replayCurrentLevel() },
                onNextLevel = { viewModel.nextLevel() },
                multiplayerModeTitle = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showHighScoresDialog) {
            HighScoresDialog(
                highScores = highScores,
                onClearScores = { viewModel.clearHighScores() },
                onDismiss = { showHighScoresDialog = false }
            )
        }

        if (showLevelSelectDialog) {
            LevelSelectDialog(
                currentStartLevel = userSettings.startLevel,
                maxUnlockedLevel = userSettings.maxUnlockedLevel,
                skin = skin,
                onLevelSelected = { selectedLvl ->
                    viewModel.selectLevel(selectedLvl)
                    showLevelSelectDialog = false
                },
                onDismiss = { showLevelSelectDialog = false }
            )
        }
    }
}
