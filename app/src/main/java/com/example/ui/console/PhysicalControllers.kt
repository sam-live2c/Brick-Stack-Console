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
import androidx.compose.foundation.layout.defaultMinSize
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
    userSettings: UserSettings? = null,
    onToggleKey: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val buttonWidth = (maxWidth / 4.4f).coerceIn(36.dp, 52.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val showPause = userSettings?.showSystemPause != false
            if (onToggleKey != null || showPause) {
                SystemSmallButton(
                    label = if (isPaused) "PLAY" else "PAUSE",
                    icon = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    skin = skin,
                    buttonWidth = buttonWidth,
                    onClick = if (onToggleKey != null) { { onToggleKey("showSystemPause") } } else onTogglePause,
                    alpha = if (showPause) 1.0f else 0.25f
                )
            }
            val showReset = userSettings?.showSystemReset != false
            if (onToggleKey != null || showReset) {
                SystemSmallButton(
                    label = "RESTART",
                    icon = Icons.Default.Refresh,
                    skin = skin,
                    buttonWidth = buttonWidth,
                    onClick = if (onToggleKey != null) { { onToggleKey("showSystemReset") } } else onReset,
                    alpha = if (showReset) 1.0f else 0.25f
                )
            }
            val showSound = userSettings?.showSystemSound != false
            if (onToggleKey != null || showSound) {
                SystemSmallButton(
                    label = if (soundEnabled) "SOUND ON" else "MUTED",
                    icon = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    skin = skin,
                    buttonWidth = buttonWidth,
                    onClick = if (onToggleKey != null) { { onToggleKey("showSystemSound") } } else onToggleSound,
                    alpha = if (showSound) 1.0f else 0.25f
                )
            }
            val showOption = userSettings?.showSystemOption != false
            if (onToggleKey != null || showOption) {
                SystemSmallButton(
                    label = "OPTION",
                    icon = Icons.Default.Settings,
                    skin = skin,
                    buttonWidth = buttonWidth,
                    onClick = if (onToggleKey != null) { { onToggleKey("showSystemOption") } } else onOpenSettings,
                    alpha = if (showOption) 1.0f else 0.25f
                )
            }
        }
    }
}

@Composable
private fun SystemSmallButton(
    label: String,
    icon: ImageVector,
    skin: ConsoleSkin,
    buttonWidth: Dp,
    onClick: () -> Unit,
    alpha: Float = 1.0f
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(1.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .size(width = buttonWidth, height = 22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            skin.systemButtonColor.copy(alpha = alpha),
                            skin.systemButtonColor.copy(alpha = 0.7f * alpha)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White.copy(alpha = alpha),
                modifier = Modifier.size(13.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = skin.brandTextColor.copy(alpha = alpha),
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
    onToggleKey: ((String) -> Unit)? = null,
    skin: ConsoleSkin,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-5).dp + verticalOffset.dp.coerceIn((-24).dp, 20.dp))
    ) {
        val availableWidth = maxWidth
        val maxSingleWidth = (availableWidth - 8.dp) / 2.05f
        val requestedSize = 126.dp * buttonScale
        val controllerSize = requestedSize.coerceAtMost(maxSingleWidth).coerceAtLeast(76.dp)

        val dpadVOffset = (userSettings?.dpadVerticalOffset ?: verticalOffset).coerceIn(-16, 16).dp
        val actionVOffset = (userSettings?.actionButtonsVerticalOffset ?: verticalOffset).coerceIn(-16, 16).dp
        val hSpacing = (userSettings?.controllerHorizontalSpacing ?: 0).coerceIn(-16, 16)
        val buttonLayout = userSettings?.actionButtonLayout ?: ActionButtonLayout.GRID_2X2
        val isLinearRow = (buttonLayout == ActionButtonLayout.LINE_ROW)
        val horizontalOuterPadding = if (isLinearRow) {
            2.dp
        } else {
            (10.dp - (hSpacing.dp * 0.45f)).coerceIn(0.dp, 26.dp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalOuterPadding),
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
                    userSettings = userSettings,
                    onToggleKey = onToggleKey,
                    skin = skin,
                    modifier = Modifier.offset(y = dpadVOffset)
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
                    userSettings = userSettings,
                    onToggleKey = onToggleKey,
                    skin = skin,
                    modifier = Modifier.offset(y = actionVOffset)
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
                    userSettings = userSettings,
                    onToggleKey = onToggleKey,
                    skin = skin,
                    modifier = Modifier.offset(y = actionVOffset)
                )

                DPadController(
                    size = controllerSize,
                    onLeft = onMoveLeft,
                    onRight = onMoveRight,
                    onDown = onSoftDrop,
                    onUp = onHardDrop,
                    userSettings = userSettings,
                    onToggleKey = onToggleKey,
                    skin = skin,
                    modifier = Modifier.offset(y = dpadVOffset)
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
    userSettings: UserSettings? = null,
    onToggleKey: ((String) -> Unit)? = null,
    skin: ConsoleSkin,
    modifier: Modifier = Modifier
) {
    val segmentSize = (size * 0.28f).coerceIn(24.dp, 46.dp)
    val pivotSize = (size * 0.28f).coerceIn(24.dp, 46.dp)
    val iconSize = (segmentSize * 0.52f).coerceIn(12.dp, 22.dp)

    val showUp = userSettings?.showDpadUp ?: true
    val showDown = userSettings?.showDpadDown ?: true
    val showLeft = userSettings?.showDpadLeft ?: true
    val showRight = userSettings?.showDpadRight ?: true

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
            if (onToggleKey != null || showUp) {
                DPadSegmentButton(
                    icon = Icons.Default.ArrowUpward,
                    label = "UP",
                    skin = skin,
                    segmentSize = segmentSize,
                    iconSize = iconSize,
                    onClick = if (onToggleKey != null) { { onToggleKey("showDpadUp") } } else onUp,
                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                    alpha = if (showUp) 1.0f else 0.25f
                )
            } else {
                Spacer(modifier = Modifier.size(segmentSize))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // LEFT BUTTON
                if (onToggleKey != null || showLeft) {
                    DPadSegmentButton(
                        icon = Icons.Default.ArrowBack,
                        label = "LEFT",
                        skin = skin,
                        segmentSize = segmentSize,
                        iconSize = iconSize,
                        onClick = if (onToggleKey != null) { { onToggleKey("showDpadLeft") } } else onLeft,
                        shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp),
                        alpha = if (showLeft) 1.0f else 0.25f
                    )
                } else {
                    Spacer(modifier = Modifier.size(segmentSize))
                }

                // CENTER D-PAD PIVOT
                Box(
                    modifier = Modifier
                        .size(pivotSize)
                        .background(skin.dpadColor)
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
                if (onToggleKey != null || showRight) {
                    DPadSegmentButton(
                        icon = Icons.Default.ArrowForward,
                        label = "RIGHT",
                        skin = skin,
                        segmentSize = segmentSize,
                        iconSize = iconSize,
                        onClick = if (onToggleKey != null) { { onToggleKey("showDpadRight") } } else onRight,
                        shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp),
                        alpha = if (showRight) 1.0f else 0.25f
                    )
                } else {
                    Spacer(modifier = Modifier.size(segmentSize))
                }
            }

            // DOWN BUTTON (SOFT DROP)
            if (onToggleKey != null || showDown) {
                DPadSegmentButton(
                    icon = Icons.Default.KeyboardArrowDown,
                    label = "DOWN",
                    skin = skin,
                    segmentSize = segmentSize,
                    iconSize = iconSize,
                    onClick = if (onToggleKey != null) { { onToggleKey("showDpadDown") } } else onDown,
                    shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp),
                    alpha = if (showDown) 1.0f else 0.25f
                )
            } else {
                Spacer(modifier = Modifier.size(segmentSize))
            }
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
    shape: RoundedCornerShape,
    alpha: Float = 1.0f
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1.0f, label = "dpad_scale")

    Box(
        modifier = Modifier
            .size(segmentSize)
            .scale(scale)
            .clip(shape)
            .background(
                if (isPressed) skin.dpadColor.copy(alpha = 0.7f * alpha)
                else skin.dpadColor.copy(alpha = alpha)
            )
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
            tint = Color.White.copy(alpha = 0.85f * alpha),
            modifier = Modifier.size(iconSize)
        )
    }
}

private data class ActionButtonItem(
    val slotIndex: Int,
    val actionType: ActionButtonType,
    val isVisible: Boolean,
    val keyName: String
)

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
    userSettings: UserSettings? = null,
    onToggleKey: ((String) -> Unit)? = null,
    skin: ConsoleSkin,
    modifier: Modifier = Modifier
) {
    val show1 = userSettings?.showActionButton1 ?: true
    val show2 = userSettings?.showActionButton2 ?: true
    val show3 = userSettings?.showActionButton3 ?: true
    val show4 = userSettings?.showActionButton4 ?: true

    val allItems = listOf(
        ActionButtonItem(1, button1Action, show1, "showActionButton1"),
        ActionButtonItem(2, button2Action, show2, "showActionButton2"),
        ActionButtonItem(3, button3Action, show3, "showActionButton3"),
        ActionButtonItem(4, button4Action, show4, "showActionButton4")
    )

    // Filter to active items, but in Live Preview mode (onToggleKey != null) keep all items visible/dimmed so user can tap to unhide!
    val activeItems = if (onToggleKey != null) allItems else allItems.filter { it.isVisible }

    val isLinearRow = (buttonLayout == ActionButtonLayout.LINE_ROW)
    val buttonSize = if (isLinearRow && activeItems.size >= 4) {
        (size * 0.22f).coerceIn(20.dp, 36.dp)
    } else if (isLinearRow && activeItems.size == 3) {
        (size * 0.24f).coerceIn(22.dp, 38.dp)
    } else {
        (size * 0.28f).coerceIn(24.dp, 44.dp)
    }
    val iconSize = (buttonSize * 0.48f).coerceIn(10.dp, 20.dp)
    val textSize = (buttonSize.value * 0.22f).coerceIn(5.5f, 9.5f).sp
    val btnSpacingExtra = (userSettings?.actionButtonsSpacing ?: 0).dp

    @Composable
    fun RenderItem(item: ActionButtonItem) {
        val icon = when (item.actionType) {
            ActionButtonType.HOLD -> Icons.Default.PanTool
            ActionButtonType.HARD_DROP -> Icons.Default.KeyboardDoubleArrowDown
            ActionButtonType.ROTATE_LEFT -> Icons.Default.RotateLeft
            ActionButtonType.ROTATE_RIGHT -> Icons.Default.RotateRight
        }
        val label = item.actionType.shortLabel
        val color = when (item.actionType) {
            ActionButtonType.HOLD -> skin.actionButtonColorHold
            ActionButtonType.HARD_DROP -> skin.actionButtonColorDrop
            ActionButtonType.ROTATE_LEFT -> skin.actionButtonColorRotateLeft
            ActionButtonType.ROTATE_RIGHT -> skin.actionButtonColorRotateRight
        }
        val onClick = if (onToggleKey != null) {
            { onToggleKey(item.keyName) }
        } else {
            when (item.actionType) {
                ActionButtonType.HOLD -> onHold
                ActionButtonType.HARD_DROP -> onHardDrop
                ActionButtonType.ROTATE_LEFT -> onRotateLeft
                ActionButtonType.ROTATE_RIGHT -> onRotateRight
            }
        }

        UniformRoundActionButton(
            icon = icon,
            subLabel = label,
            color = color,
            textColor = skin.actionButtonTextColor,
            buttonSize = buttonSize,
            iconSize = iconSize,
            textSize = textSize,
            onClick = onClick,
            alpha = if (item.isVisible) 1.0f else 0.25f
        )
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = size, minHeight = size)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        when (activeItems.size) {
            0 -> {
                // All buttons disabled
                Text(
                    text = "NO BTNS",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = skin.brandTextColor.copy(alpha = 0.35f)
                )
            }
            1 -> {
                // 1 Button: Centered perfectly
                RenderItem(activeItems[0])
            }
            2 -> {
                // 2 Buttons: Compact centered pair
                Row(
                    horizontalArrangement = Arrangement.spacedBy((8.dp + btnSpacingExtra).coerceAtLeast(1.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RenderItem(activeItems[0])
                    RenderItem(activeItems[1])
                }
            }
            3 -> {
                // 3 Buttons: Dynamic reflow according to layout preference
                if (buttonLayout == ActionButtonLayout.LINE_ROW) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((6.dp + btnSpacingExtra).coerceAtLeast(1.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RenderItem(activeItems[0])
                        RenderItem(activeItems[1])
                        RenderItem(activeItems[2])
                    }
                } else if (buttonLayout == ActionButtonLayout.DIAMOND) {
                    // Pyramid layout (1 top, 2 bottom)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy((4.dp + btnSpacingExtra * 0.7f).coerceAtLeast(1.dp))
                    ) {
                        RenderItem(activeItems[0])
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((8.dp + btnSpacingExtra).coerceAtLeast(1.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RenderItem(activeItems[1])
                            RenderItem(activeItems[2])
                        }
                    }
                } else {
                    // GRID_2X2 for 3 buttons (2 top, 1 bottom centered)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy((4.dp + btnSpacingExtra * 0.7f).coerceAtLeast(1.dp))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((8.dp + btnSpacingExtra).coerceAtLeast(1.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RenderItem(activeItems[0])
                            RenderItem(activeItems[1])
                        }
                        RenderItem(activeItems[2])
                    }
                }
            }
            4 -> {
                // 4 Buttons: Full layout
                if (buttonLayout == ActionButtonLayout.LINE_ROW) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((4.dp + btnSpacingExtra).coerceAtLeast(1.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RenderItem(activeItems[0])
                        RenderItem(activeItems[1])
                        RenderItem(activeItems[2])
                        RenderItem(activeItems[3])
                    }
                } else if (buttonLayout == ActionButtonLayout.DIAMOND) {
                    // Diamond: 1 top, 2 middle, 1 bottom
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy((2.dp + btnSpacingExtra * 0.7f).coerceAtLeast(1.dp))
                    ) {
                        RenderItem(activeItems[0])
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((10.dp + btnSpacingExtra).coerceAtLeast(1.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RenderItem(activeItems[2])
                            RenderItem(activeItems[1])
                        }
                        RenderItem(activeItems[3])
                    }
                } else {
                    // GRID_2X2: 2 top, 2 bottom
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy((4.dp + btnSpacingExtra * 0.7f).coerceAtLeast(1.dp))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((8.dp + btnSpacingExtra).coerceAtLeast(1.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RenderItem(activeItems[0])
                            RenderItem(activeItems[1])
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((8.dp + btnSpacingExtra).coerceAtLeast(1.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RenderItem(activeItems[2])
                            RenderItem(activeItems[3])
                        }
                    }
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
    textSize: TextUnit = 8.sp,
    alpha: Float = 1.0f
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.9f else 1.0f, label = "btn_scale")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(buttonSize)
                .scale(scale)
                .shadow(if (alpha < 0.5f) 0.dp else (if (isPressed) 1.dp else 2.dp), CircleShape)
                .clip(CircleShape)
                .background(if (isPressed) color.copy(alpha = 0.8f * alpha) else color.copy(alpha = alpha))
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
                tint = textColor.copy(alpha = alpha),
                modifier = Modifier.size(iconSize)
            )
        }
        if (subLabel.isNotEmpty()) {
            Text(
                text = subLabel,
                color = textColor.copy(alpha = 0.8f * alpha),
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
        }
    }
}
