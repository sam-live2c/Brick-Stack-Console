package com.example.ui.console

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserSettings
import com.example.game.GameStatus
import com.example.game.TetrisGameState

@Composable
fun HandheldConsoleFrame(
    gameState: TetrisGameState,
    skin: ConsoleSkin,
    userSettings: UserSettings,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onSoftDrop: () -> Unit,
    onHardDrop: () -> Unit,
    onRotateClockwise: () -> Unit,
    onRotateCounterClockwise: () -> Unit,
    onHoldPiece: () -> Unit,
    onTogglePause: () -> Unit,
    onReset: () -> Unit,
    onToggleSound: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHighScores: () -> Unit,
    onGoHome: () -> Unit,
    multiplayerModeTitle: String? = null,
    modifier: Modifier = Modifier
) {
    // Physical Handheld Outer Body Casing
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val outerWidth = maxWidth
        val outerHeight = maxHeight

        val horizontalPadding = (outerWidth * 0.03f).coerceIn(6.dp, 16.dp)
        val verticalPadding = (outerHeight * 0.015f).coerceIn(4.dp, 12.dp)

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            skin.bodyColor,
                            skin.bodyAccentColor
                        )
                    )
                ),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. BRAND HEADER & NAVIGATION BADGES
                BrandHeader(
                    skin = skin,
                    onOpenSettings = onOpenSettings,
                    onOpenHighScores = onOpenHighScores,
                    onGoHome = onGoHome,
                    multiplayerModeTitle = multiplayerModeTitle
                )

                Spacer(modifier = Modifier.height(2.dp))

                // 2. LCD GAME SCREEN (With optional Touch Gesture Overlay)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.92f),
                    contentAlignment = Alignment.Center
                ) {
                    LcdScreen(
                        gameState = gameState,
                        skin = skin,
                        ghostEnabled = userSettings.ghostPieceEnabled,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Direct Touch Gestures on Screen area
                    TouchGestureOverlay(
                        enabled = userSettings.gestureControlEnabled,
                        onMoveLeft = onMoveLeft,
                        onMoveRight = onMoveRight,
                        onSoftDrop = onSoftDrop,
                        onHardDrop = onHardDrop,
                        onRotateClockwise = onRotateClockwise,
                        onHoldPiece = onHoldPiece,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 3. SYSTEM UTILITY BUTTONS ROW (START, RESET, MUTE, OPTION)
                SystemPillButtonsRow(
                    isPaused = gameState.status == GameStatus.PAUSED,
                    soundEnabled = userSettings.soundEnabled,
                    onTogglePause = onTogglePause,
                    onReset = onReset,
                    onToggleSound = onToggleSound,
                    onOpenSettings = onOpenSettings,
                    onOpenHighScores = onOpenHighScores,
                    skin = skin,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                // 4. PHYSICAL D-PAD & ACTION CONTROLLERS
                if (userSettings.virtualButtonsEnabled) {
                    PhysicalControllersRow(
                        onMoveLeft = onMoveLeft,
                        onMoveRight = onMoveRight,
                        onSoftDrop = onSoftDrop,
                        onHardDrop = onHardDrop,
                        onRotateClockwise = onRotateClockwise,
                        onRotateCounterClockwise = onRotateCounterClockwise,
                        onHoldPiece = onHoldPiece,
                        leftHandedMode = userSettings.leftHandedMode,
                        buttonScale = userSettings.buttonScale,
                        verticalOffset = userSettings.controllerVerticalOffset,
                        userSettings = userSettings,
                        skin = skin,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 5. BOTTOM RETRO SPEAKER GRILL TEXTURE
                SpeakerGrillTexture(skin = skin, modifier = Modifier.padding(bottom = 2.dp))
            }
        }
    }
}

@Composable
private fun BrandHeader(
    skin: ConsoleSkin,
    onOpenSettings: () -> Unit,
    onOpenHighScores: () -> Unit,
    onGoHome: () -> Unit,
    multiplayerModeTitle: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Settings Gear Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(skin.bezelColor.copy(alpha = 0.3f))
                .border(1.dp, skin.brandTextColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onOpenSettings
                )
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = skin.brandTextColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "SETTINGS",
                    color = skin.brandTextColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Title Branding Text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = multiplayerModeTitle ?: "BRICK STACK",
                color = skin.brandTextColor,
                fontSize = if (multiplayerModeTitle != null) 14.sp else 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
            Text(
                text = if (multiplayerModeTitle != null) "MATCH ROOM #BRICK-8842" else "SINGLE PLAYER ARCADE",
                color = skin.brandTextColor.copy(alpha = 0.8f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Score Button (with SVG Icon)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(skin.bezelColor.copy(alpha = 0.3f))
                .border(1.dp, skin.brandTextColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .clickable(onClick = onOpenHighScores)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Score",
                    tint = skin.brandTextColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "SCORE",
                    color = skin.brandTextColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun SpeakerGrillTexture(skin: ConsoleSkin, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .width(100.dp)
            .height(14.dp)
    ) {
        val slitCount = 6
        val slitW = 10f
        val slitH = 3f
        val gap = 14f
        val startX = (size.width - (slitCount * gap)) / 2f

        for (i in 0 until slitCount) {
            val x = startX + i * gap
            drawRoundRect(
                color = skin.bezelColor.copy(alpha = 0.4f),
                topLeft = Offset(x, 4f),
                size = Size(slitW, slitH),
                cornerRadius = CornerRadius(1.5f, 1.5f)
            )
        }
    }
}
