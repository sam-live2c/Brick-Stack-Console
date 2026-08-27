package com.example.ui.console

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.data.ActionButtonLayout
import com.example.data.ActionButtonType
import com.example.data.UserSettings

@Composable
fun SystemPillButtonsRow(
    isPaused: Boolean,
    soundEnabled: Boolean,
    onTogglePause: () -> Unit,
    onReset: () -> Unit,
    onToggleSound: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHighScores: () -> Unit,
    skin: ConsoleSkin,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val buttonWidth = (maxWidth / 4.4f).coerceIn(36.dp, 52.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SystemSmallButton(
                label = if (isPaused) "PLAY" else "PAUSE",
                icon = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                skin = skin,
                buttonWidth = buttonWidth,
                onClick = onTogglePause
            )
            SystemSmallButton(
                label = "RESET",
                icon = Icons.Default.Refresh,
                skin = skin,
                buttonWidth = buttonWidth,
                onClick = onReset
            )
            SystemSmallButton(
                label = if (soundEnabled) "SOUND ON" else "MUTED",
                icon = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                skin = skin,
                buttonWidth = buttonWidth,
                onClick = onToggleSound
            )
            SystemSmallButton(
                label = "OPTION",
                icon = Icons.Default.Settings,
                skin = skin,
                buttonWidth = buttonWidth,
                onClick = onOpenSettings
            )
        }
    }
}

@Composable
private fun SystemSmallButton(
    label: String,
    icon: ImageVector,
    skin: ConsoleSkin,
    buttonWidth: Dp,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(1.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = buttonWidth, height = 22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            skin.systemButtonColor,
                            skin.systemButtonColor.copy(alpha = 0.7f)
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(11.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = skin.brandTextColor,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
    }
}

@Composable
fun PhysicalControllersRow(
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onSoftDrop: () -> Unit,
    onHardDrop: () -> Unit,
    onRotateClockwise: () -> Unit,
    onRotateCounterClockwise: () -> Unit,
    onHoldPiece: () -> Unit,
    leftHandedMode: Boolean,
    buttonScale: Float,
    verticalOffset: Int = 0,
    userSettings: UserSettings? = null,
    skin: ConsoleSkin,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = verticalOffset.dp.coerceIn((-4).dp, 20.dp))
    ) {
        val availableWidth = maxWidth
        val maxSingleWidth = (availableWidth - 8.dp) / 2.05f
        val requestedSize = 135.dp * buttonScale
        val controllerSize = requestedSize.coerceAtMost(maxSingleWidth).coerceAtLeast(80.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!leftHandedMode) {
                // Standard: D-Pad on Left, Action Buttons on Right
                DPadController(
                    size = controllerSize,
                    onLeft = onMoveLeft,
                    onRight = onMoveRight,
                    onDown = onSoftDrop,
                    onUp = onHardDrop,
                    skin = skin
                )

                ActionButtonsCluster(
                    size = controllerSize,
                    onRotateRight = onRotateClockwise,
                    onRotateLeft = onRotateCounterClockwise,
                    onHardDrop = onHardDrop,
                    onHold = onHoldPiece,
                    buttonLayout = userSettings?.actionButtonLayout ?: ActionButtonLayout.GRID_2X2,
                    button1Action = userSettings?.button1Action ?: ActionButtonType.HOLD,
                    button2Action = userSettings?.button2Action ?: ActionButtonType.HARD_DROP,
                    button3Action = userSettings?.button3Action ?: ActionButtonType.ROTATE_LEFT,
                    button4Action = userSettings?.button4Action ?: ActionButtonType.ROTATE_RIGHT,
                    skin = skin
                )
            } else {
                // Left-Handed: Action Buttons on Left, D-Pad on Right
                ActionButtonsCluster(
                    size = controllerSize,
                    onRotateRight = onRotateClockwise,
                    onRotateLeft = onRotateCounterClockwise,
                    onHardDrop = onHardDrop,
                    onHold = onHoldPiece,
                    buttonLayout = userSettings?.actionButtonLayout ?: ActionButtonLayout.GRID_2X2,
                    button1Action = userSettings?.button1Action ?: ActionButtonType.HOLD,
                    button2Action = userSettings?.button2Action ?: ActionButtonType.HARD_DROP,
                    button3Action = userSettings?.button3Action ?: ActionButtonType.ROTATE_LEFT,
                    button4Action = userSettings?.button4Action ?: ActionButtonType.ROTATE_RIGHT,
                    skin = skin
                )

                DPadController(
                    size = controllerSize,
                    onLeft = onMoveLeft,
                    onRight = onMoveRight,
                    onDown = onSoftDrop,
                    onUp = onHardDrop,
                    skin = skin
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DPadController(
    size: Dp = 140.dp,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onDown: () -> Unit,
    onUp: () -> Unit,
    skin: ConsoleSkin,
    modifier: Modifier = Modifier
) {
    val segmentSize = (size * 0.28f).coerceIn(24.dp, 46.dp)
    val pivotSize = (size * 0.28f).coerceIn(24.dp, 46.dp)
    val iconSize = (segmentSize * 0.52f).coerceIn(12.dp, 22.dp)

    Box(
        modifier = modifier
            .size(size)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Base D-Pad Cross Background Plate
        Box(
            modifier = Modifier
                .size(size * 0.92f)
                .clip(CircleShape)
                .background(skin.bezelColor.copy(alpha = 0.5f))
        )

        // Cross D-Pad Shape
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // UP BUTTON
            DPadSegmentButton(
                icon = Icons.Default.ArrowUpward,
                label = "UP",
                skin = skin,
                segmentSize = segmentSize,
                iconSize = iconSize,
                onClick = onUp,
                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // LEFT BUTTON
                DPadSegmentButton(
                    icon = Icons.Default.ArrowBack,
                    label = "LEFT",
                    skin = skin,
                    segmentSize = segmentSize,
                    iconSize = iconSize,
                    onClick = onLeft,
                    shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)
                )

                // CENTER D-PAD PIVOT
                Box(
                    modifier = Modifier
                        .size(pivotSize)
                        .background(skin.dpadColor)
                        .border(1.dp, Color.Black.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .size(pivotSize * 0.38f)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                            .align(Alignment.Center)
                    )
                }

                // RIGHT BUTTON
                DPadSegmentButton(
                    icon = Icons.Default.ArrowForward,
                    label = "RIGHT",
                    skin = skin,
                    segmentSize = segmentSize,
                    iconSize = iconSize,
                    onClick = onRight,
                    shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                )
            }

            // DOWN BUTTON (SOFT DROP)
            DPadSegmentButton(
                icon = Icons.Default.KeyboardArrowDown,
                label = "DOWN",
                skin = skin,
                segmentSize = segmentSize,
                iconSize = iconSize,
                onClick = onDown,
                shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
            )
        }
    }
}

@Composable
private fun DPadSegmentButton(
    icon: ImageVector,
    label: String,
    skin: ConsoleSkin,
    segmentSize: Dp,
    iconSize: Dp,
    onClick: () -> Unit,
    shape: RoundedCornerShape
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1.0f, label = "dpad_scale")

    Box(
        modifier = Modifier
            .size(segmentSize)
            .scale(scale)
            .clip(shape)
            .background(
                if (isPressed) skin.dpadColor.copy(alpha = 0.7f)
                else skin.dpadColor
            )
            .border(1.dp, Color.White.copy(alpha = 0.15f), shape)
            .clickable {
                isPressed = true
                onClick()
                isPressed = false
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun ActionButtonsCluster(
    size: Dp = 140.dp,
    onRotateRight: () -> Unit,
    onRotateLeft: () -> Unit,
    onHardDrop: () -> Unit,
    onHold: () -> Unit,
    buttonLayout: ActionButtonLayout = ActionButtonLayout.GRID_2X2,
    button1Action: ActionButtonType = ActionButtonType.HOLD,
    button2Action: ActionButtonType = ActionButtonType.HARD_DROP,
    button3Action: ActionButtonType = ActionButtonType.ROTATE_LEFT,
    button4Action: ActionButtonType = ActionButtonType.ROTATE_RIGHT,
    skin: ConsoleSkin,
    modifier: Modifier = Modifier
) {
    val buttonSize = when (buttonLayout) {
        ActionButtonLayout.GRID_2X2 -> (size * 0.38f).coerceIn(28.dp, 50.dp)
        ActionButtonLayout.DIAMOND -> (size * 0.30f).coerceIn(24.dp, 44.dp)
        ActionButtonLayout.LINE_ROW -> (size * 0.22f).coerceIn(20.dp, 36.dp)
    }
    val iconSize = (buttonSize * 0.48f).coerceIn(10.dp, 20.dp)
    val textSize = (size.value * 0.05f).coerceIn(5.5f, 9.5f).sp

    @Composable
    fun RenderSingleActionButton(actionType: ActionButtonType) {
        val icon = when (actionType) {
            ActionButtonType.HOLD -> Icons.Default.PanTool
            ActionButtonType.HARD_DROP -> Icons.Default.KeyboardDoubleArrowDown
            ActionButtonType.ROTATE_LEFT -> Icons.Default.RotateLeft
            ActionButtonType.ROTATE_RIGHT -> Icons.Default.RotateRight
        }
        val label = actionType.shortLabel
        val color = when (actionType) {
            ActionButtonType.HOLD -> skin.actionButtonColorHold
            ActionButtonType.HARD_DROP -> skin.actionButtonColorDrop
            ActionButtonType.ROTATE_LEFT -> skin.actionButtonColorRotateLeft
            ActionButtonType.ROTATE_RIGHT -> skin.actionButtonColorRotateRight
        }
        val onClick = when (actionType) {
            ActionButtonType.HOLD -> onHold
            ActionButtonType.HARD_DROP -> onHardDrop
            ActionButtonType.ROTATE_LEFT -> onRotateLeft
            ActionButtonType.ROTATE_RIGHT -> onRotateRight
        }

        UniformRoundActionButton(
            icon = icon,
            subLabel = label,
            color = color,
            textColor = skin.actionButtonTextColor,
            buttonSize = buttonSize,
            iconSize = iconSize,
            textSize = textSize,
            onClick = onClick
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        when (buttonLayout) {
            ActionButtonLayout.GRID_2X2 -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RenderSingleActionButton(button1Action)
                        RenderSingleActionButton(button2Action)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RenderSingleActionButton(button3Action)
                        RenderSingleActionButton(button4Action)
                    }
                }
            }
            ActionButtonLayout.DIAMOND -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    RenderSingleActionButton(button1Action)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RenderSingleActionButton(button4Action)
                        RenderSingleActionButton(button2Action)
                    }
                    RenderSingleActionButton(button3Action)
                }
            }
            ActionButtonLayout.LINE_ROW -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RenderSingleActionButton(button1Action)
                    RenderSingleActionButton(button2Action)
                    RenderSingleActionButton(button3Action)
                    RenderSingleActionButton(button4Action)
                }
            }
        }
    }
}

@Composable
private fun UniformRoundActionButton(
    icon: ImageVector,
    subLabel: String,
    color: Color,
    textColor: Color,
    onClick: () -> Unit,
    buttonSize: Dp = 44.dp,
    iconSize: Dp = 20.dp,
    textSize: TextUnit = 8.sp
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.9f else 1.0f, label = "btn_scale")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(buttonSize)
                .scale(scale)
                .shadow(if (isPressed) 1.dp else 2.dp, CircleShape)
                .clip(CircleShape)
                .background(if (isPressed) color.copy(alpha = 0.8f) else color)
                .border(1.dp, Color.Black.copy(alpha = 0.25f), CircleShape)
                .clickable {
                    isPressed = true
                    onClick()
                    isPressed = false
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = subLabel,
                tint = textColor,
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = subLabel,
            color = color,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
    }
}
