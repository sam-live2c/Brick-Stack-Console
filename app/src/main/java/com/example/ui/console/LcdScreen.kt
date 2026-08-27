package com.example.ui.console

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameStatus
import com.example.game.TetrisGameState
import com.example.game.Tetromino
import com.example.game.TetrominoType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun LcdScreen(
    gameState: TetrisGameState,
    skin: ConsoleSkin,
    ghostEnabled: Boolean,
    onLevelClick: (() -> Unit)? = null,
    onReplayLevel: (() -> Unit)? = null,
    onNextLevel: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(skin.bezelColor)
            .border(4.dp, skin.screenBorderColor, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        // Inner LCD Screen Container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(4.dp))
                .background(skin.lcdBackground)
                .border(2.dp, skin.activePixelColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(4.dp)
        ) {
            val totalWidth = maxWidth
            val totalHeight = maxHeight

            val gap = 4.dp
            val minSidebarWidth = 62.dp
            val maxAvailableMatrixWidth = (totalWidth - minSidebarWidth - gap).coerceAtLeast(80.dp)

            val matrixWidthFromHeight = totalHeight * 0.5f
            val matrixWidth = matrixWidthFromHeight.coerceAtMost(maxAvailableMatrixWidth)
            val sidebarWidth = (totalWidth - matrixWidth - gap).coerceIn(62.dp, 105.dp)

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Main Tetris Matrix Grid (10 columns x 20 rows)
                Box(
                    modifier = Modifier
                        .width(matrixWidth)
                        .fillMaxHeight()
                        .aspectRatio(0.5f) // 10:20 ratio
                        .border(1.dp, skin.activePixelColor.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    TetrisMatrixCanvas(
                        gameState = gameState,
                        skin = skin,
                        ghostEnabled = ghostEnabled,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlays for PAUSED / GAME OVER / VICTORY / TIMES_UP / IDLE
                    val overlayFontSize = (matrixWidth.value * 0.11f).coerceIn(11f, 20f).sp
                    val subFontSize = (matrixWidth.value * 0.075f).coerceIn(8f, 13f).sp
                    val tinyFontSize = (matrixWidth.value * 0.06f).coerceIn(7f, 11f).sp

                    // Level Up Banner overlay (when active during gameplay)
                    if (gameState.isLevelUpBannerVisible && gameState.status == GameStatus.PLAYING) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .align(Alignment.TopCenter)
                                .background(skin.activePixelColor)
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = skin.lcdBackground,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LEVEL UP! LVL ${gameState.level}",
                                    color = skin.lcdBackground,
                                    fontSize = subFontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = skin.lcdBackground,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    if (gameState.status == GameStatus.PAUSED) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(skin.lcdBackground.copy(alpha = 0.88f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PAUSED",
                                color = skin.activePixelColor,
                                fontSize = overlayFontSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else if (gameState.status == GameStatus.VICTORY) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(skin.lcdBackground.copy(alpha = 0.95f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val isLevel1000Completed = gameState.level >= 1000

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (isLevel1000Completed) Icons.Default.Celebration else Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = skin.activePixelColor,
                                        modifier = Modifier.size((matrixWidth.value * 0.12f).coerceIn(16f, 22f).dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isLevel1000Completed) "CONGRATS!" else "VICTORY!",
                                        color = skin.activePixelColor,
                                        fontSize = (matrixWidth.value * (if (isLevel1000Completed) 0.11f else 0.12f)).coerceIn(14f, 20f).sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isLevel1000Completed) "ALL 1000 LEVELS CLEARED!" else "STAGE CLEARED",
                                    color = skin.activePixelColor.copy(alpha = 0.85f),
                                    fontSize = (matrixWidth.value * 0.065f).coerceIn(8f, 11f).sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Stats summary box
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = skin.activePixelColor.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(vertical = 4.dp, horizontal = 6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "SCORE",
                                            color = skin.activePixelColor.copy(alpha = 0.7f),
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "${gameState.finalCalculatedScore}",
                                            color = skin.activePixelColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "LINES",
                                            color = skin.activePixelColor.copy(alpha = 0.7f),
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "${gameState.linesCleared}",
                                            color = skin.activePixelColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Interactive Action Buttons: REPLAY and NEXT LEVEL (or LEVEL 1 for 1000)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // REPLAY BUTTON
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(skin.activePixelColor.copy(alpha = 0.15f))
                                            .border(
                                                width = 1.5.dp,
                                                color = skin.activePixelColor,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .clickable { onReplayLevel?.invoke() }
                                            .padding(vertical = 6.dp, horizontal = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "◀ REPLAY",
                                                color = skin.activePixelColor,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = "(D-Pad ◄)",
                                                color = skin.activePixelColor.copy(alpha = 0.65f),
                                                fontSize = 7.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }

                                    // NEXT LEVEL / LEVEL 1 BUTTON
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(skin.activePixelColor)
                                            .clickable { onNextLevel?.invoke() }
                                            .padding(vertical = 6.dp, horizontal = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = if (isLevel1000Completed) "LEVEL 1 ▶" else "NEXT ▶",
                                                color = skin.lcdBackground,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = if (isLevel1000Completed) "(All Unlocked)" else "(D-Pad ►)",
                                                color = skin.lcdBackground.copy(alpha = 0.85f),
                                                fontSize = 7.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else if (gameState.status == GameStatus.TIMES_UP) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(skin.lcdBackground.copy(alpha = 0.92f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = skin.activePixelColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "TIME'S UP!",
                                        color = skin.activePixelColor,
                                        fontSize = overlayFontSize,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "SCORE: ${gameState.finalCalculatedScore}",
                                    color = skin.activePixelColor,
                                    fontSize = subFontSize,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "PRESS RESET",
                                    color = skin.activePixelColor.copy(alpha = 0.7f),
                                    fontSize = tinyFontSize,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    } else if (gameState.status == GameStatus.GAME_OVER) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(skin.lcdBackground.copy(alpha = 0.92f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Text(
                                    text = "GAME OVER",
                                    color = skin.activePixelColor,
                                    fontSize = overlayFontSize,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "SCORE: ${gameState.finalCalculatedScore}",
                                    color = skin.activePixelColor,
                                    fontSize = subFontSize,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "PRESS RESET",
                                    color = skin.activePixelColor.copy(alpha = 0.7f),
                                    fontSize = tinyFontSize,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    } else if (gameState.status == GameStatus.IDLE) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(skin.lcdBackground.copy(alpha = 0.85f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "PRESS START",
                                    color = skin.activePixelColor,
                                    fontSize = overlayFontSize,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(gap))

                // Right Side: Sidebar Stats & Previews
                Column(
                    modifier = Modifier
                        .width(sidebarWidth)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val titleFontSize = (sidebarWidth.value * 0.09f).coerceIn(7f, 10f).sp

                    // NEXT PIECE BOX
                    LcdBox(
                        title = "NEXT",
                        skin = skin,
                        titleFontSize = titleFontSize,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    ) {
                        PiecePreviewCanvas(
                            pieceType = gameState.nextPiece,
                            skin = skin,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // HOLD PIECE BOX
                    LcdBox(
                        title = "HOLD",
                        skin = skin,
                        titleFontSize = titleFontSize,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                    ) {
                        PiecePreviewCanvas(
                            pieceType = gameState.holdPiece,
                            skin = skin,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // SCORE & STATS PANEL
                    LcdBox(
                        title = "STATS",
                        skin = skin,
                        titleFontSize = titleFontSize,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2.4f)
                    ) {
                        val displaySec = if (gameState.gameMode == com.example.game.GameMode.ULTRA_2MIN) gameState.timeRemainingSeconds else gameState.elapsedTimeSeconds

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatDisplay(
                                label = "SCORE",
                                value = "%06d".format(gameState.finalCalculatedScore),
                                skin = skin,
                                sidebarWidth = sidebarWidth
                            )
                            StatDisplay(
                                label = "HI-SCORE",
                                value = "%06d".format(gameState.highScore),
                                skin = skin,
                                sidebarWidth = sidebarWidth
                            )
                            StatDisplay(
                                label = "LEVEL",
                                value = "L%02d".format(gameState.level),
                                skin = skin,
                                sidebarWidth = sidebarWidth,
                                level = gameState.level,
                                onClick = onLevelClick
                            )
                            StatDisplay(
                                label = "TIME",
                                value = "%02d:%02d".format(displaySec / 60, displaySec % 60),
                                skin = skin,
                                sidebarWidth = sidebarWidth,
                                level = gameState.level
                            )
                            StatDisplay(
                                label = "LINES",
                                value = "%03d".format(gameState.linesCleared),
                                skin = skin,
                                sidebarWidth = sidebarWidth
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LcdBox(
    title: String,
    skin: ConsoleSkin,
    titleFontSize: androidx.compose.ui.unit.TextUnit = 8.sp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .border(1.dp, skin.activePixelColor.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
            .padding(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = skin.activePixelColor,
            fontSize = titleFontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun StatDisplay(
    label: String,
    value: String,
    skin: ConsoleSkin,
    sidebarWidth: Dp = 80.dp,
    level: Int = 1,
    onClick: (() -> Unit)? = null
) {
    val scaleAnim = remember { Animatable(1.0f) }
    var prevLevel by remember { mutableStateOf(level) }

    LaunchedEffect(level) {
        if (level > prevLevel) {
            prevLevel = level
            scaleAnim.animateTo(1.35f, animationSpec = tween(120))
            scaleAnim.animateTo(1.0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        } else {
            prevLevel = level
        }
    }

    val labelFontSize = (sidebarWidth.value * 0.08f).coerceIn(6f, 8f).sp
    val valueFontSize = (sidebarWidth.value * 0.12f).coerceIn(8f, 11f).sp

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .graphicsLayer {
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }
    ) {
        Text(
            text = label,
            color = skin.activePixelColor.copy(alpha = 0.7f),
            fontSize = labelFontSize,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
        Text(
            text = value,
            color = skin.activePixelColor,
            fontSize = valueFontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
    }
}

private data class LineParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    val color: Color,
    val isSpark: Boolean,
    val maxLifeMs: Float,
    var lifeMs: Float
)

private data class ActiveRowFlash(
    val rowIndex: Int,
    val color: Color,
    val maxDurationMs: Float = 400f,
    var elapsedMs: Float = 0f
)

private data class ClearTextPopup(
    val message: String,
    val color: Color,
    val centerRow: Int,
    val isTetris: Boolean,
    val maxDurationMs: Float = 850f,
    var elapsedMs: Float = 0f
)

@Composable
private fun TetrisMatrixCanvas(
    gameState: TetrisGameState,
    skin: ConsoleSkin,
    ghostEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val particles = remember { mutableStateListOf<LineParticle>() }
    val rowFlashes = remember { mutableStateListOf<ActiveRowFlash>() }
    val textPopups = remember { mutableStateListOf<ClearTextPopup>() }
    var screenShakeOffset by remember { mutableStateOf(Offset.Zero) }
    var screenFlashAlpha by remember { mutableStateOf(0f) }

    LaunchedEffect(gameState.lineClearTrigger) {
        if (gameState.lineClearTrigger <= 0L) return@LaunchedEffect

        val clearedLines = gameState.clearingLines
        val count = gameState.lastClearedCount.coerceAtLeast(1)
        val isTetris = count >= 4
        val isMulti = count >= 2

        // Flash screen color tint
        screenFlashAlpha = if (isTetris) 0.65f else 0.35f

        // Screen shake burst for tactile arcade feel
        if (isMulti) {
            val shakeIntensity = if (isTetris) 12f else 6f
            screenShakeOffset = Offset(
                x = (Random.nextFloat() - 0.5f) * shakeIntensity,
                y = (Random.nextFloat() - 0.5f) * shakeIntensity
            )
        }

        // Add Row Flashes for each cleared row
        val flashColor = when {
            isTetris -> Color(0xFFFFD700) // Gold
            count == 3 -> Color(0xFF00FFFF) // Cyan
            count == 2 -> Color(0xFFFF4081) // Magenta Pink
            else -> skin.activePixelColor
        }

        clearedLines.forEach { r ->
            rowFlashes.add(ActiveRowFlash(rowIndex = r, color = flashColor))
        }

        // Spawn particles along cleared rows
        val particleCountPerRow = if (isTetris) 35 else 20
        clearedLines.forEach { r ->
            for (i in 0 until particleCountPerRow) {
                val normalizedX = Random.nextFloat()
                val speedX = (Random.nextFloat() - 0.5f) * (if (isTetris) 500f else 300f)
                val speedY = (Random.nextFloat() - 0.7f) * (if (isTetris) 450f else 250f)
                val pColor = if (Random.nextBoolean()) flashColor else skin.activePixelColor

                particles.add(
                    LineParticle(
                        x = normalizedX,
                        y = r.toFloat() + 0.5f,
                        vx = speedX,
                        vy = speedY,
                        size = Random.nextFloat() * (if (isTetris) 7f else 5f) + 2f,
                        color = pColor,
                        isSpark = Random.nextBoolean(),
                        maxLifeMs = Random.nextFloat() * 350f + 300f,
                        lifeMs = 0f
                    )
                )
            }
        }

        // Add Floating Action Text Popup
        val avgRow = if (clearedLines.isNotEmpty()) clearedLines.average().toInt() else 10
        val msg = gameState.lastActionMessage ?: when (count) {
            4 -> "TETRIS!!"
            3 -> "TRIPLE!"
            2 -> "DOUBLE!"
            else -> "SINGLE!"
        }
        textPopups.add(
            ClearTextPopup(
                message = msg,
                color = flashColor,
                centerRow = avgRow,
                isTetris = isTetris
            )
        )
    }

    // Ticker frame loop for particle simulation & flash decays
    LaunchedEffect(particles.isNotEmpty() || rowFlashes.isNotEmpty() || textPopups.isNotEmpty() || screenFlashAlpha > 0f) {
        var lastTimeNanos = System.nanoTime()
        while (particles.isNotEmpty() || rowFlashes.isNotEmpty() || textPopups.isNotEmpty() || screenFlashAlpha > 0f) {
            withFrameNanos { frameTimeNanos ->
                val dtMs = ((frameTimeNanos - lastTimeNanos) / 1_000_000f).coerceAtMost(32f)
                lastTimeNanos = frameTimeNanos

                // Fade screen flash & decay screen shake
                if (screenFlashAlpha > 0f) {
                    screenFlashAlpha = (screenFlashAlpha - (dtMs / 280f)).coerceAtLeast(0f)
                }
                screenShakeOffset = Offset(
                    x = screenShakeOffset.x * 0.8f,
                    y = screenShakeOffset.y * 0.8f
                )

                // Update row flashes
                val flashIter = rowFlashes.iterator()
                while (flashIter.hasNext()) {
                    val item = flashIter.next()
                    item.elapsedMs += dtMs
                    if (item.elapsedMs >= item.maxDurationMs) {
                        flashIter.remove()
                    }
                }

                // Update text popups
                val popupIter = textPopups.iterator()
                while (popupIter.hasNext()) {
                    val item = popupIter.next()
                    item.elapsedMs += dtMs
                    if (item.elapsedMs >= item.maxDurationMs) {
                        popupIter.remove()
                    }
                }

                // Update particles
                val pIter = particles.iterator()
                while (pIter.hasNext()) {
                    val p = pIter.next()
                    p.lifeMs += dtMs
                    if (p.lifeMs >= p.maxLifeMs) {
                        pIter.remove()
                    } else {
                        val dtSec = dtMs / 1000f
                        p.x += (p.vx / 300f) * dtSec
                        p.y += (p.vy / 300f) * dtSec
                        p.vy += 220f * dtSec
                    }
                }
            }
        }
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / TetrisGameState.BOARD_WIDTH
            val cellH = size.height / TetrisGameState.BOARD_HEIGHT
            val gap = 1.0f

            translate(left = screenShakeOffset.x, top = screenShakeOffset.y) {
                // 1. Draw Inactive Ghost Grid
                for (r in 0 until TetrisGameState.BOARD_HEIGHT) {
                    for (c in 0 until TetrisGameState.BOARD_WIDTH) {
                        val left = c * cellW + gap
                        val top = r * cellH + gap
                        val w = cellW - gap * 2
                        val h = cellH - gap * 2

                        drawBlock(
                            left = left,
                            top = top,
                            w = w,
                            h = h,
                            color = skin.inactivePixelColor,
                            isOutlineOnly = true,
                            strokeWidth = 1f
                        )
                    }
                }

                // 2. Draw Locked Grid Blocks
                for (r in 0 until TetrisGameState.BOARD_HEIGHT) {
                    for (c in 0 until TetrisGameState.BOARD_WIDTH) {
                        val typeId = gameState.grid[r][c]
                        if (typeId != 0) {
                            val left = c * cellW + gap
                            val top = r * cellH + gap
                            val w = cellW - gap * 2
                            val h = cellH - gap * 2

                            drawBlock(
                                left = left,
                                top = top,
                                w = w,
                                h = h,
                                color = skin.getBlockColor(typeId),
                                isOutlineOnly = false
                            )
                        }
                    }
                }

                // 3. Draw Ghost Piece (if enabled & playing)
                if (ghostEnabled && gameState.status == GameStatus.PLAYING && gameState.ghostPiece != null && gameState.currentPiece != null) {
                    val ghostCells = gameState.ghostPiece.getOccupiedCells()
                    for (cell in ghostCells) {
                        if (cell.y in 0 until TetrisGameState.BOARD_HEIGHT && cell.x in 0 until TetrisGameState.BOARD_WIDTH) {
                            val left = cell.x * cellW + gap
                            val top = cell.y * cellH + gap
                            val w = cellW - gap * 2
                            val h = cellH - gap * 2

                            drawBlock(
                                left = left,
                                top = top,
                                w = w,
                                h = h,
                                color = skin.activePixelColor.copy(alpha = 0.35f),
                                isOutlineOnly = true,
                                strokeWidth = 2f
                            )
                        }
                    }
                }

                // 4. Draw Current Active Piece
                if (gameState.currentPiece != null && gameState.status == GameStatus.PLAYING) {
                    val cells = gameState.currentPiece.getOccupiedCells()
                    for (cell in cells) {
                        if (cell.y in 0 until TetrisGameState.BOARD_HEIGHT && cell.x in 0 until TetrisGameState.BOARD_WIDTH) {
                            val left = cell.x * cellW + gap
                            val top = cell.y * cellH + gap
                            val w = cellW - gap * 2
                            val h = cellH - gap * 2

                            drawBlock(
                                left = left,
                                top = top,
                                w = w,
                                h = h,
                                color = skin.getBlockColor(gameState.currentPiece.type.id),
                                isOutlineOnly = false
                            )
                        }
                    }
                }

                // 5. Draw Row Visual Flashes
                rowFlashes.forEach { rf ->
                    val progress = (rf.elapsedMs / rf.maxDurationMs).coerceIn(0f, 1f)
                    val flashAlpha = (1f - progress) * 0.9f
                    val rowTop = rf.rowIndex * cellH

                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                rf.color.copy(alpha = 0.15f * flashAlpha),
                                rf.color.copy(alpha = flashAlpha),
                                Color.White.copy(alpha = flashAlpha),
                                rf.color.copy(alpha = flashAlpha),
                                rf.color.copy(alpha = 0.15f * flashAlpha)
                            )
                        ),
                        topLeft = Offset(0f, rowTop - cellH * 0.1f),
                        size = Size(size.width, cellH * 1.2f)
                    )
                }

                // 6. Draw Particle Burst Effects
                particles.forEach { p ->
                    val progress = (p.lifeMs / p.maxLifeMs).coerceIn(0f, 1f)
                    val pAlpha = (1f - progress)
                    val px = p.x * size.width
                    val py = p.y * cellH

                    if (p.isSpark) {
                        val particleSize = p.size * (1.2f - progress * 0.4f)
                        drawRect(
                            color = p.color.copy(alpha = pAlpha),
                            topLeft = Offset(px - particleSize / 2f, py - particleSize / 2f),
                            size = Size(particleSize, particleSize)
                        )
                    } else {
                        val particleRadius = p.size * (1f - progress * 0.3f)
                        drawCircle(
                            color = p.color.copy(alpha = pAlpha),
                            center = Offset(px, py),
                            radius = particleRadius
                        )
                    }
                }

                // 7. Draw Screen Tint Flash
                if (screenFlashAlpha > 0f) {
                    drawRect(
                        color = Color.White.copy(alpha = screenFlashAlpha * 0.45f),
                        topLeft = Offset.Zero,
                        size = size
                    )
                }
            }
        }

        // Floating Action Text Popups
        textPopups.forEach { popup ->
            val progress = (popup.elapsedMs / popup.maxDurationMs).coerceIn(0f, 1f)
            val alpha = (1f - progress).coerceIn(0f, 1f)
            val offsetY = (popup.centerRow * 12f) - (progress * 25f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = offsetY.coerceIn(20f, 250f).dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = popup.message,
                    color = popup.color.copy(alpha = alpha),
                    fontSize = if (popup.isTetris) 20.sp else 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                    modifier = Modifier
                        .background(
                            color = skin.lcdBackground.copy(alpha = 0.9f * alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = popup.color.copy(alpha = alpha * 0.8f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun PiecePreviewCanvas(
    pieceType: TetrominoType?,
    skin: ConsoleSkin,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (pieceType == null) return@Canvas

        val p = Tetromino.create(pieceType, startX = 0, startY = 0)
        val cells = p.getOccupiedCells()

        val cellSize = (size.width / 4.5f).coerceAtMost(size.height / 4.5f)
        val offsetX = (size.width - cellSize * 4) / 2f
        val offsetY = (size.height - cellSize * 4) / 2f

        for (cell in cells) {
            val left = offsetX + cell.x * cellSize + 1f
            val top = offsetY + cell.y * cellSize + 1f
            val w = cellSize - 2f
            val h = cellSize - 2f

            drawBlock(
                left = left,
                top = top,
                w = w,
                h = h,
                color = skin.getBlockColor(pieceType.id),
                isOutlineOnly = false
            )
        }
    }
}

// Draw authentic retro LCD block (outer border + inner inset square, like physical LCD segment)
private fun DrawScope.drawBlock(
    left: Float,
    top: Float,
    w: Float,
    h: Float,
    color: Color,
    isOutlineOnly: Boolean,
    strokeWidth: Float = 1.5f
) {
    if (isOutlineOnly) {
        drawRect(
            color = color,
            topLeft = Offset(left, top),
            size = Size(w, h),
            style = Stroke(width = strokeWidth)
        )
    } else {
        // Outer Filled Block
        drawRect(
            color = color,
            topLeft = Offset(left, top),
            size = Size(w, h)
        )
        // Inner inset square for tactile LCD block look
        val inset = w * 0.22f
        drawRect(
            color = color.copy(alpha = 0.4f),
            topLeft = Offset(left + inset, top + inset),
            size = Size(w - inset * 2, h - inset * 2),
            style = Stroke(width = 1.2f)
        )
    }
}
