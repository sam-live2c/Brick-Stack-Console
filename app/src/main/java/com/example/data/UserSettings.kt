package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class HapticIntensity {
    OFF, SOFT, MEDIUM, STRONG
}

enum class ActionButtonLayout(val label: String) {
    GRID_2X2("2x2 Grid"),
    DIAMOND("Diamond (3 Buttons)"),
    LINE_ROW("Linear Row")
}

enum class ActionButtonType(val label: String, val shortLabel: String) {
    ROTATE_RIGHT("Rotate CW", "ROTATE R"),
    ROTATE_LEFT("Rotate CCW", "ROTATE L"),
    HARD_DROP("Hard Drop", "DROP"),
    HOLD("Hold Piece", "HOLD");

    companion object {
        fun fromName(name: String, default: ActionButtonType): ActionButtonType {
            return try {
                valueOf(name)
            } catch (e: Exception) {
                default
            }
        }
    }
}

enum class SpeedOption(val label: String, val multiplier: Float, val scoreModifierLabel: String) {
    RELAXED("0.5x Relaxed", 0.5f, "-50% Score"),
    EASY("0.75x Easy", 0.75f, "-25% Score"),
    NORMAL("1.0x Normal", 1.0f, "100% Score"),
    FAST("1.25x Fast", 1.25f, "+25% Score"),
    INSANE("1.5x Insane", 1.5f, "+50% Score"),
    ULTRA("2.0x Ultra", 2.0f, "+100% Score");

    companion object {
        fun fromMultiplier(valMult: Float): SpeedOption {
            return values().firstOrNull { kotlin.math.abs(it.multiplier - valMult) < 0.05f } ?: NORMAL
        }
    }
}

data class UserSettings(
    val themeIndex: Int = 0,
    val hapticIntensity: HapticIntensity = HapticIntensity.MEDIUM,
    val soundEnabled: Boolean = true,
    val ghostPieceEnabled: Boolean = true,
    val gestureControlEnabled: Boolean = true,
    val virtualButtonsEnabled: Boolean = true,
    val leftHandedMode: Boolean = false,
    val buttonScale: Float = 1.0f,
    val controllerVerticalOffset: Int = 0,
    val dpadVerticalOffset: Int = 0,
    val actionButtonsVerticalOffset: Int = 0,
    val controllerHorizontalSpacing: Int = 0,
    val actionButtonsSpacing: Int = 0,
    val actionButtonLayout: ActionButtonLayout = ActionButtonLayout.GRID_2X2,
    val button1Action: ActionButtonType = ActionButtonType.HOLD,
    val button2Action: ActionButtonType = ActionButtonType.HARD_DROP,
    val button3Action: ActionButtonType = ActionButtonType.ROTATE_LEFT,
    val button4Action: ActionButtonType = ActionButtonType.ROTATE_RIGHT,
    val dasMs: Long = 160L,
    val arrMs: Long = 40L,
    val startLevel: Int = 1,
    val speedMultiplier: Float = 1.0f,
    val maxUnlockedLevel: Int = 1,
    // Granular Button Visibility Toggles
    val showDpadUp: Boolean = true,
    val showDpadDown: Boolean = true,
    val showDpadLeft: Boolean = true,
    val showDpadRight: Boolean = true,
    val showActionButton1: Boolean = true,
    val showActionButton2: Boolean = true,
    val showActionButton3: Boolean = true,
    val showActionButton4: Boolean = true,
    val showSystemPause: Boolean = true,
    val showSystemReset: Boolean = true,
    val showSystemSound: Boolean = true,
    val showSystemOption: Boolean = true
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("brick_console_prefs", Context.MODE_PRIVATE)

    fun loadSettings(): UserSettings {
        return UserSettings(
            themeIndex = prefs.getInt("themeIndex", 0),
            hapticIntensity = try {
                HapticIntensity.valueOf(prefs.getString("hapticIntensity", HapticIntensity.MEDIUM.name) ?: HapticIntensity.MEDIUM.name)
            } catch (e: Exception) { HapticIntensity.MEDIUM },
            soundEnabled = prefs.getBoolean("soundEnabled", true),
            ghostPieceEnabled = prefs.getBoolean("ghostPieceEnabled", true),
            gestureControlEnabled = prefs.getBoolean("gestureControlEnabled", true),
            virtualButtonsEnabled = prefs.getBoolean("virtualButtonsEnabled", true),
            leftHandedMode = prefs.getBoolean("leftHandedMode", false),
            buttonScale = prefs.getFloat("buttonScale", 1.0f),
            controllerVerticalOffset = prefs.getInt("controllerVerticalOffset", 0),
            dpadVerticalOffset = prefs.getInt("dpadVerticalOffset", prefs.getInt("controllerVerticalOffset", 0)),
            actionButtonsVerticalOffset = prefs.getInt("actionButtonsVerticalOffset", prefs.getInt("controllerVerticalOffset", 0)),
            controllerHorizontalSpacing = prefs.getInt("controllerHorizontalSpacing", 0),
            actionButtonsSpacing = prefs.getInt("actionButtonsSpacing", 0),
            actionButtonLayout = try {
                ActionButtonLayout.valueOf(prefs.getString("actionButtonLayout", ActionButtonLayout.GRID_2X2.name) ?: ActionButtonLayout.GRID_2X2.name)
            } catch (e: Exception) { ActionButtonLayout.GRID_2X2 },
            button1Action = ActionButtonType.fromName(prefs.getString("button1Action", ActionButtonType.HOLD.name) ?: "", ActionButtonType.HOLD),
            button2Action = ActionButtonType.fromName(prefs.getString("button2Action", ActionButtonType.HARD_DROP.name) ?: "", ActionButtonType.HARD_DROP),
            button3Action = ActionButtonType.fromName(prefs.getString("button3Action", ActionButtonType.ROTATE_LEFT.name) ?: "", ActionButtonType.ROTATE_LEFT),
            button4Action = ActionButtonType.fromName(prefs.getString("button4Action", ActionButtonType.ROTATE_RIGHT.name) ?: "", ActionButtonType.ROTATE_RIGHT),
            dasMs = prefs.getLong("dasMs", 160L),
            arrMs = prefs.getLong("arrMs", 40L),
            startLevel = prefs.getInt("startLevel", 1),
            speedMultiplier = prefs.getFloat("speedMultiplier", 1.0f),
            maxUnlockedLevel = prefs.getInt("maxUnlockedLevel", 1),
            showDpadUp = prefs.getBoolean("showDpadUp", true),
            showDpadDown = prefs.getBoolean("showDpadDown", true),
            showDpadLeft = prefs.getBoolean("showDpadLeft", true),
            showDpadRight = prefs.getBoolean("showDpadRight", true),
            showActionButton1 = prefs.getBoolean("showActionButton1", true),
            showActionButton2 = prefs.getBoolean("showActionButton2", true),
            showActionButton3 = prefs.getBoolean("showActionButton3", true),
            showActionButton4 = prefs.getBoolean("showActionButton4", true),
            showSystemPause = prefs.getBoolean("showSystemPause", true),
            showSystemReset = prefs.getBoolean("showSystemReset", true),
            showSystemSound = prefs.getBoolean("showSystemSound", true),
            showSystemOption = prefs.getBoolean("showSystemOption", true)
        )
    }

    fun saveSettings(settings: UserSettings) {
        prefs.edit()
            .putInt("themeIndex", settings.themeIndex)
            .putString("hapticIntensity", settings.hapticIntensity.name)
            .putBoolean("soundEnabled", settings.soundEnabled)
            .putBoolean("ghostPieceEnabled", settings.ghostPieceEnabled)
            .putBoolean("gestureControlEnabled", settings.gestureControlEnabled)
            .putBoolean("virtualButtonsEnabled", settings.virtualButtonsEnabled)
            .putBoolean("leftHandedMode", settings.leftHandedMode)
            .putFloat("buttonScale", settings.buttonScale)
            .putInt("controllerVerticalOffset", settings.controllerVerticalOffset)
            .putInt("dpadVerticalOffset", settings.dpadVerticalOffset)
            .putInt("actionButtonsVerticalOffset", settings.actionButtonsVerticalOffset)
            .putInt("controllerHorizontalSpacing", settings.controllerHorizontalSpacing)
            .putInt("actionButtonsSpacing", settings.actionButtonsSpacing)
            .putString("actionButtonLayout", settings.actionButtonLayout.name)
            .putString("button1Action", settings.button1Action.name)
            .putString("button2Action", settings.button2Action.name)
            .putString("button3Action", settings.button3Action.name)
            .putString("button4Action", settings.button4Action.name)
            .putLong("dasMs", settings.dasMs)
            .putLong("arrMs", settings.arrMs)
            .putInt("startLevel", settings.startLevel)
            .putFloat("speedMultiplier", settings.speedMultiplier)
            .putInt("maxUnlockedLevel", settings.maxUnlockedLevel)
            .putBoolean("showDpadUp", settings.showDpadUp)
            .putBoolean("showDpadDown", settings.showDpadDown)
            .putBoolean("showDpadLeft", settings.showDpadLeft)
            .putBoolean("showDpadRight", settings.showDpadRight)
            .putBoolean("showActionButton1", settings.showActionButton1)
            .putBoolean("showActionButton2", settings.showActionButton2)
            .putBoolean("showActionButton3", settings.showActionButton3)
            .putBoolean("showActionButton4", settings.showActionButton4)
            .putBoolean("showSystemPause", settings.showSystemPause)
            .putBoolean("showSystemReset", settings.showSystemReset)
            .putBoolean("showSystemSound", settings.showSystemSound)
            .putBoolean("showSystemOption", settings.showSystemOption)
            .apply()
    }
}
