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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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

@Composable
fun LcdScreen(
    gameState: TetrisGameState,
    skin: ConsoleSkin,
    ghostEnabled: Boolean,
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
                            Text(
                                text = "★ LEVEL UP! LVL ${gameState.level} ★",
                                color = skin.lcdBackground,
                                fontSize = subFontSize,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
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
                                .background(skin.lcdBackground.copy(alpha = 0.92f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Text(
                                    text = "🏆 VICTORY!",
                                    color = skin.activePixelColor,
                                    fontSize = overlayFontSize,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "STAGE CLEARED",
                                    color = skin.activePixelColor.copy(alpha = 0.8f),
                                    fontSize = tinyFontSize,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "LINES: ${gameState.linesCleared}",
                                    color = skin.activePixelColor,
                                    fontSize = tinyFontSize,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "WIN SCORE: ${gameState.finalCalculatedScore}",
                                    color = skin.activePixelColor,
                                    fontSize = subFontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "PRESS RESET / START",
                                    color = skin.activePixelColor.copy(alpha = 0.7f),
                                    fontSize = tinyFontSize,
                                    fontFamily = FontFamily.Monospace
                                )
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
                                Text(
                                    text = "⏱ TIME'S UP!",
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
                        val totalMult = gameState.speedMultiplier * (1.0f + (gameState.level - 1) * 0.15f)
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
                                level = gameState.level
                            )
                            StatDisplay(
                                label = "MULT",
                                value = "${"%.2f".format(totalMult)}x",
                                skin = skin,
                                sidebarWidth = sidebarWidth,
                                level = gameState.level
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
    level: Int = 1
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

@Composable
private fun TetrisMatrixCanvas(
    gameState: TetrisGameState,
    skin: ConsoleSkin,
    ghostEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cellW = size.width / TetrisGameState.BOARD_WIDTH
        val cellH = size.height / TetrisGameState.BOARD_HEIGHT
        val gap = 1.0f

        // 1. Draw Inactive Ghost Grid (classic LCD background grid)
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
