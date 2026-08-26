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
import com.example.ui.history.HighScoresDialog
import com.example.ui.settings.SettingsDialog
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

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

    val composeHaptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D111A))
    ) {
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
            multiplayerModeTitle = null,
            modifier = Modifier.fillMaxSize()
        )

        if (showSettingsDialog) {
            SettingsDialog(
                currentSettings = userSettings,
                onSaveSettings = { newSettings -> viewModel.updateSettings(newSettings) },
                onDismiss = { showSettingsDialog = false }
            )
        }

        if (showHighScoresDialog) {
            HighScoresDialog(
                highScores = highScores,
                onClearScores = { viewModel.clearHighScores() },
                onDismiss = { showHighScoresDialog = false }
            )
        }
    }
}
