package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ActionButtonLayout
import com.example.data.ActionButtonType
import com.example.data.HapticIntensity
import com.example.data.UserSettings
import com.example.ui.console.ActionButtonsCluster
import com.example.ui.console.ConsoleSkin
import com.example.ui.console.PhysicalControllersRow
import com.example.ui.console.SystemPillButtonsRow
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    currentSettings: UserSettings,
    skin: ConsoleSkin,
    onSaveSettings: (UserSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var settings by remember(currentSettings) { mutableStateOf(currentSettings) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0D111A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header (Fixed against scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D111A))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Game",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "CONSOLE SETTINGS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            }

            // Middle Content (Scrollable)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                    // 1. GAME SPEED MULTIPLIER & START LEVEL
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GAME SPEED & START LEVEL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Falling Speed Multiplier:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val currentSpeedOpt = com.example.data.SpeedOption.fromMultiplier(settings.speedMultiplier)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        com.example.data.SpeedOption.values().toList().chunked(3).forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                chunk.forEach { opt ->
                                    val isSelected = currentSpeedOpt == opt
                                    OptionCapsule(
                                        text = opt.label,
                                        subText = opt.scoreModifierLabel,
                                        isSelected = isSelected,
                                        onClick = {
                                            val newS = settings.copy(speedMultiplier = opt.multiplier)
                                            settings = newS
                                            onSaveSettings(newS)
                                        },
                                        modifier = Modifier.weight(1f),
                                        height = 36.dp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Starting Level: LEVEL ${settings.startLevel}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Slider(
                        value = settings.startLevel.toFloat(),
                        onValueChange = {
                            val newS = settings.copy(startLevel = it.roundToInt())
                            settings = newS
                            onSaveSettings(newS)
                        },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val presets = listOf(
                            "EASY" to 1,
                            "NORMAL" to 3,
                            "HARD" to 6,
                            "INSANE" to 9
                        )
                        presets.forEach { (label, lvl) ->
                            val isSelected = settings.startLevel == lvl
                            OptionCapsule(
                                text = label,
                                isSelected = isSelected,
                                onClick = {
                                    val newS = settings.copy(startLevel = lvl)
                                    settings = newS
                                    onSaveSettings(newS)
                                },
                                modifier = Modifier.weight(1f),
                                height = 32.dp
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // 2. CONSOLE SKIN SELECTOR
                    Text(
                        text = "CONSOLES & THEMES",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ConsoleSkin.ALL_SKINS.forEach { skin ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (settings.themeIndex == skin.id) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable {
                                        val newS = settings.copy(themeIndex = skin.id)
                                        settings = newS
                                        onSaveSettings(newS)
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(skin.bodyColor)
                                        .border(1.dp, Color.Black.copy(alpha = 0.3f), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = skin.name,
                                    fontWeight = if (settings.themeIndex == skin.id) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (settings.themeIndex == skin.id) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("SELECTED", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // 3. HAPTIC FEEDBACK SETTING
                    Text(
                        text = "HAPTIC FEEDBACK INTENSITY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HapticIntensity.values().forEach { intensity ->
                            val isSelected = settings.hapticIntensity == intensity
                            OptionCapsule(
                                text = intensity.name,
                                isSelected = isSelected,
                                onClick = {
                                    val newS = settings.copy(hapticIntensity = intensity)
                                    settings = newS
                                    onSaveSettings(newS)
                                },
                                modifier = Modifier.weight(1f),
                                height = 32.dp
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // 4. TOUCH CONTROLS & LAYOUT
                    Text(
                        text = "TOUCH CONTROLS & VIRTUAL BUTTONS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    SettingSwitchRow(
                        title = "Virtual Hardware Buttons",
                        subtitle = "Show D-Pad and circular buttons on console",
                        checked = settings.virtualButtonsEnabled,
                        onCheckedChange = {
                            val newS = settings.copy(virtualButtonsEnabled = it)
                            settings = newS
                            onSaveSettings(newS)
                        }
                    )

                    if (settings.virtualButtonsEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // A. BUTTON SIZE / SCALE CONTROL
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Button Size (Scale: ${(settings.buttonScale * 100).roundToInt()}%)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Adjust overall size of D-Pad and action buttons",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Slider(
                                value = settings.buttonScale,
                                onValueChange = { scale ->
                                    val newS = settings.copy(buttonScale = (scale * 20).roundToInt() / 20f)
                                    settings = newS
                                    onSaveSettings(newS)
                                },
                                valueRange = 0.7f..1.4f,
                                steps = 13,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("85%" to 0.85f, "100%" to 1.0f, "115%" to 1.15f, "130%" to 1.30f).forEach { (label, scaleVal) ->
                                    val isSelected = kotlin.math.abs(settings.buttonScale - scaleVal) < 0.04f
                                    OptionCapsule(
                                        text = label,
                                        isSelected = isSelected,
                                        onClick = {
                                            val newS = settings.copy(buttonScale = scaleVal)
                                            settings = newS
                                            onSaveSettings(newS)
                                        },
                                        modifier = Modifier.weight(1f),
                                        height = 30.dp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // B. CONTROLLER POSITION CONTROL (ORIENTATION & VERTICAL OFFSET)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Controller Position & Layout",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Customize D-Pad side and vertical positioning on console",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Hand orientation switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Hand Mode", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    listOf("Left D-Pad" to false, "Right D-Pad" to true).forEach { (label, isRight) ->
                                        val isSelected = settings.leftHandedMode == isRight
                                        OptionCapsule(
                                            text = label,
                                            isSelected = isSelected,
                                            onClick = {
                                                val newS = settings.copy(leftHandedMode = isRight)
                                                settings = newS
                                                onSaveSettings(newS)
                                            },
                                            modifier = Modifier.weight(1f),
                                            height = 32.dp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Vertical Offset slider
                            Text(
                                text = "Controller Height / Vertical Offset (${if (settings.controllerVerticalOffset >= 0) "+${settings.controllerVerticalOffset}" else settings.controllerVerticalOffset} dp)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Adjust controller height below options row (cannot overlap PAUSE/RESET buttons)",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Slider(
                                value = settings.controllerVerticalOffset.coerceIn(-4, 20).toFloat(),
                                onValueChange = { offsetVal ->
                                    val newS = settings.copy(controllerVerticalOffset = offsetVal.roundToInt())
                                    settings = newS
                                    onSaveSettings(newS)
                                },
                                valueRange = -4f..20f,
                                steps = 11,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Top (0)" to 0, "Mid (+6)" to 6, "Low (+12)" to 12, "Bottom (+18)" to 18).forEach { (label, offsetVal) ->
                                    val isSelected = settings.controllerVerticalOffset == offsetVal
                                    OptionCapsule(
                                        text = label,
                                        isSelected = isSelected,
                                        onClick = {
                                            val newS = settings.copy(controllerVerticalOffset = offsetVal)
                                            settings = newS
                                            onSaveSettings(newS)
                                        },
                                        modifier = Modifier.weight(1f),
                                        height = 30.dp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // C. 4 CONTROL BUTTONS POSITION & MAPPING CONTROL
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "4 Control Buttons Position & Layout",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Customize position and action assignments for all 4 control buttons",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // 1. Layout Shape Pattern
                            Text("Layout Shape", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ActionButtonLayout.values().forEach { layout ->
                                    val isSelected = settings.actionButtonLayout == layout
                                    OptionCapsule(
                                        text = layout.label,
                                        isSelected = isSelected,
                                        onClick = {
                                            val newS = settings.copy(actionButtonLayout = layout)
                                            settings = newS
                                            onSaveSettings(newS)
                                        },
                                        modifier = Modifier.weight(1f),
                                        height = 32.dp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 2. Position Presets
                            Text("Quick Position Presets", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val presets = listOf(
                                    "Default" to Quadruple(ActionButtonType.HOLD, ActionButtonType.HARD_DROP, ActionButtonType.ROTATE_LEFT, ActionButtonType.ROTATE_RIGHT),
                                    "Classic" to Quadruple(ActionButtonType.ROTATE_RIGHT, ActionButtonType.ROTATE_LEFT, ActionButtonType.HARD_DROP, ActionButtonType.HOLD),
                                    "Rotate Top" to Quadruple(ActionButtonType.ROTATE_LEFT, ActionButtonType.ROTATE_RIGHT, ActionButtonType.HOLD, ActionButtonType.HARD_DROP),
                                    "Drop Top" to Quadruple(ActionButtonType.HARD_DROP, ActionButtonType.HOLD, ActionButtonType.ROTATE_LEFT, ActionButtonType.ROTATE_RIGHT)
                                )
                                presets.forEach { (label, quad) ->
                                    val isMatch = settings.button1Action == quad.first &&
                                            settings.button2Action == quad.second &&
                                            settings.button3Action == quad.third &&
                                            settings.button4Action == quad.fourth
                                    OptionCapsule(
                                        text = label,
                                        isSelected = isMatch,
                                        onClick = {
                                            val newS = settings.copy(
                                                button1Action = quad.first,
                                                button2Action = quad.second,
                                                button3Action = quad.third,
                                                button4Action = quad.fourth
                                            )
                                            settings = newS
                                            onSaveSettings(newS)
                                        },
                                        modifier = Modifier.weight(1f),
                                        height = 32.dp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 3. Custom Position Remapping
                            Text("Custom Button Positions (Remap Slots)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))

                            val slotLabels = when (settings.actionButtonLayout) {
                                ActionButtonLayout.GRID_2X2 -> listOf("Top-Left (Slot 1)", "Top-Right (Slot 2)", "Bottom-Left (Slot 3)", "Bottom-Right (Slot 4)")
                                ActionButtonLayout.DIAMOND -> listOf("Top (Slot 1)", "Right (Slot 2)", "Bottom (Slot 3)", "Left (Slot 4)")
                                ActionButtonLayout.LINE_ROW -> listOf("Pos 1 (Left)", "Pos 2 (Mid-Left)", "Pos 3 (Mid-Right)", "Pos 4 (Right)")
                            }

                            val currentActions = listOf(settings.button1Action, settings.button2Action, settings.button3Action, settings.button4Action)

                            slotLabels.forEachIndexed { index, slotName ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(slotName, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.1f))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        ActionButtonType.values().forEach { action ->
                                            val isSelected = currentActions[index] == action
                                            OptionCapsule(
                                                text = action.shortLabel,
                                                isSelected = isSelected,
                                                onClick = {
                                                    val list = mutableListOf(settings.button1Action, settings.button2Action, settings.button3Action, settings.button4Action)
                                                    val oldAction = list[index]
                                                    if (oldAction != action) {
                                                        val dupIdx = list.indexOf(action)
                                                        if (dupIdx != -1) {
                                                            list[dupIdx] = oldAction
                                                        }
                                                        list[index] = action
                                                        val newS = settings.copy(
                                                            button1Action = list[0],
                                                            button2Action = list[1],
                                                            button3Action = list[2],
                                                            button4Action = list[3]
                                                        )
                                                        settings = newS
                                                        onSaveSettings(newS)
                                                    }
                                                },
                                                modifier = Modifier.width(42.dp),
                                                height = 28.dp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 4. Live Mini Preview & Interactive Button Toggling
                            val toggleButtonVisibility: (String) -> Unit = { key ->
                                val newS = when (key) {
                                    "showDpadUp" -> settings.copy(showDpadUp = !settings.showDpadUp)
                                    "showDpadDown" -> settings.copy(showDpadDown = !settings.showDpadDown)
                                    "showDpadLeft" -> settings.copy(showDpadLeft = !settings.showDpadLeft)
                                    "showDpadRight" -> settings.copy(showDpadRight = !settings.showDpadRight)
                                    "showActionButton1" -> settings.copy(showActionButton1 = !settings.showActionButton1)
                                    "showActionButton2" -> settings.copy(showActionButton2 = !settings.showActionButton2)
                                    "showActionButton3" -> settings.copy(showActionButton3 = !settings.showActionButton3)
                                    "showActionButton4" -> settings.copy(showActionButton4 = !settings.showActionButton4)
                                    "showSystemPause" -> settings.copy(showSystemPause = !settings.showSystemPause)
                                    "showSystemReset" -> settings.copy(showSystemReset = !settings.showSystemReset)
                                    "showSystemSound" -> settings.copy(showSystemSound = !settings.showSystemSound)
                                    "showSystemOption" -> settings.copy(showSystemOption = !settings.showSystemOption)
                                    else -> settings
                                }
                                settings = newS
                                onSaveSettings(newS)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Live Layout Preview", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                Text("(Tap any button to show / remove)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ConsoleSkin.getById(settings.themeIndex).bezelColor)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SystemPillButtonsRow(
                                        isPaused = false,
                                        soundEnabled = true,
                                        onTogglePause = {},
                                        onReset = {},
                                        onToggleSound = {},
                                        onOpenSettings = {},
                                        onOpenHighScores = {},
                                        skin = ConsoleSkin.getById(settings.themeIndex),
                                        userSettings = settings,
                                        onToggleKey = toggleButtonVisibility
                                    )
                                    PhysicalControllersRow(
                                        onMoveLeft = {},
                                        onMoveRight = {},
                                        onSoftDrop = {},
                                        onHardDrop = {},
                                        onRotateClockwise = {},
                                        onRotateCounterClockwise = {},
                                        onHoldPiece = {},
                                        leftHandedMode = settings.leftHandedMode,
                                        buttonScale = settings.buttonScale,
                                        verticalOffset = 0,
                                        userSettings = settings,
                                        onToggleKey = toggleButtonVisibility,
                                        skin = ConsoleSkin.getById(settings.themeIndex)
                                    )
                                }
                            }
                        }
                    }

                    SettingSwitchRow(
                        title = "Screen Touch Gestures",
                        subtitle = "Drag & tap directly on LCD screen",
                        checked = settings.gestureControlEnabled,
                        onCheckedChange = {
                            val newS = settings.copy(gestureControlEnabled = it)
                            settings = newS
                            onSaveSettings(newS)
                        }
                    )

                    SettingSwitchRow(
                        title = "Ghost Piece Projection",
                        subtitle = "Show landing outline",
                        checked = settings.ghostPieceEnabled,
                        onCheckedChange = {
                            val newS = settings.copy(ghostPieceEnabled = it)
                            settings = newS
                            onSaveSettings(newS)
                        }
                    )

                    SettingSwitchRow(
                        title = "Sound Effects",
                        subtitle = "Retro beep tones",
                        checked = settings.soundEnabled,
                        onCheckedChange = {
                            val newS = settings.copy(soundEnabled = it)
                            settings = newS
                            onSaveSettings(newS)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val defaultSettings = UserSettings()
                            settings = defaultSettings
                            onSaveSettings(defaultSettings)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC62828),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reset to Default Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

@Composable
fun SettingsDialog(
    currentSettings: UserSettings,
    onSaveSettings: (UserSettings) -> Unit,
    onDismiss: () -> Unit
) {
    SettingsScreen(
        currentSettings = currentSettings,
        skin = ConsoleSkin.getById(currentSettings.themeIndex),
        onSaveSettings = onSaveSettings,
        onDismiss = onDismiss
    )
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun OptionCapsule(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 32.dp,
    subText: String? = null
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .height(height)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (subText != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1
                )
                Text(
                    text = subText,
                    fontSize = 8.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        } else {
            Text(
                text = text,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
