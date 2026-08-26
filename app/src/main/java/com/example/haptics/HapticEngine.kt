package com.example.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.data.HapticIntensity
import com.example.game.HapticEffectEvent

class HapticEngine(private val context: Context) {
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var intensity: HapticIntensity = HapticIntensity.MEDIUM

    fun trigger(event: HapticEffectEvent, composeHaptic: HapticFeedback? = null) {
        if (intensity == HapticIntensity.OFF) return

        val multiplier = when (intensity) {
            HapticIntensity.OFF -> 0.0f
            HapticIntensity.SOFT -> 0.5f
            HapticIntensity.MEDIUM -> 1.0f
            HapticIntensity.STRONG -> 1.5f
        }

        if (vibrator != null && vibrator?.hasVibrator() == true) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = when (event) {
                        HapticEffectEvent.MOVE, HapticEffectEvent.TICK -> VibrationEffect.createOneShot(
                            (10 * multiplier).toLong().coerceAtLeast(1L),
                            (55 * multiplier).toInt().coerceIn(1, 255)
                        )
                        HapticEffectEvent.ROTATE -> VibrationEffect.createWaveform(
                            longArrayOf(0, (14 * multiplier).toLong(), 18, (18 * multiplier).toLong()),
                            intArrayOf(0, (90 * multiplier).toInt().coerceIn(1, 255), 0, (140 * multiplier).toInt().coerceIn(1, 255)),
                            -1
                        )
                        HapticEffectEvent.HOLD -> VibrationEffect.createWaveform(
                            longArrayOf(0, (20 * multiplier).toLong(), 25, (15 * multiplier).toLong()),
                            intArrayOf(0, (120 * multiplier).toInt().coerceIn(1, 255), 0, (70 * multiplier).toInt().coerceIn(1, 255)),
                            -1
                        )
                        HapticEffectEvent.SOFT_DROP -> VibrationEffect.createOneShot(
                            (8 * multiplier).toLong().coerceAtLeast(1L),
                            (40 * multiplier).toInt().coerceIn(1, 255)
                        )
                        HapticEffectEvent.DROP_LOCK -> VibrationEffect.createOneShot(
                            (22 * multiplier).toLong().coerceAtLeast(1L),
                            (140 * multiplier).toInt().coerceIn(1, 255)
                        )
                        HapticEffectEvent.HARD_DROP -> VibrationEffect.createWaveform(
                            longArrayOf(0, (35 * multiplier).toLong(), 15, (20 * multiplier).toLong()),
                            intArrayOf(0, (220 * multiplier).toInt().coerceIn(1, 255), 0, (110 * multiplier).toInt().coerceIn(1, 255)),
                            -1
                        )
                        HapticEffectEvent.LINE_CLEAR_SINGLE, HapticEffectEvent.LINE_CLEAR -> VibrationEffect.createWaveform(
                            longArrayOf(0, (25 * multiplier).toLong(), 30, (35 * multiplier).toLong()),
                            intArrayOf(0, (140 * multiplier).toInt().coerceIn(1, 255), 0, (190 * multiplier).toInt().coerceIn(1, 255)),
                            -1
                        )
                        HapticEffectEvent.LINE_CLEAR_DOUBLE -> VibrationEffect.createWaveform(
                            longArrayOf(0, (20 * multiplier).toLong(), 25, (30 * multiplier).toLong(), 25, (40 * multiplier).toLong()),
                            intArrayOf(0, (120 * multiplier).toInt().coerceIn(1, 255), 0, (170 * multiplier).toInt().coerceIn(1, 255), 0, (220 * multiplier).toInt().coerceIn(1, 255)),
                            -1
                        )
                        HapticEffectEvent.LINE_CLEAR_TRIPLE -> VibrationEffect.createWaveform(
                            longArrayOf(0, (25 * multiplier).toLong(), 20, (35 * multiplier).toLong(), 20, (50 * multiplier).toLong()),
                            intArrayOf(0, (150 * multiplier).toInt().coerceIn(1, 255), 0, (200 * multiplier).toInt().coerceIn(1, 255), 0, (255 * multiplier).toInt().coerceIn(1, 255)),
                            -1
                        )
                        HapticEffectEvent.TETRIS_BURST -> VibrationEffect.createWaveform(
                            longArrayOf(0, (40 * multiplier).toLong(), 30, (50 * multiplier).toLong(), 30, (60 * multiplier).toLong(), 40, (90 * multiplier).toLong()),
                            intArrayOf(0, (200 * multiplier).toInt().coerceIn(1, 255), 0, (230 * multiplier).toInt().coerceIn(1, 255), 0, (250 * multiplier).toInt().coerceIn(1, 255), 0, (255 * multiplier).toInt().coerceIn(1, 255)),
                            -1
                        )
                        HapticEffectEvent.LEVEL_UP -> VibrationEffect.createWaveform(
                            longArrayOf(0, (30 * multiplier).toLong(), 30, (40 * multiplier).toLong(), 30, (60 * multiplier).toLong()),
                            intArrayOf(0, (130 * multiplier).toInt().coerceIn(1, 255), 0, (180 * multiplier).toInt().coerceIn(1, 255), 0, (240 * multiplier).toInt().coerceIn(1, 255)),
                            -1
                        )
                        HapticEffectEvent.GAME_OVER -> VibrationEffect.createWaveform(
                            longArrayOf(0, (70 * multiplier).toLong(), 40, (100 * multiplier).toLong(), 50, (140 * multiplier).toLong()),
                            intArrayOf(0, (240 * multiplier).toInt().coerceIn(1, 255), 0, (160 * multiplier).toInt().coerceIn(1, 255), 0, (80 * multiplier).toInt().coerceIn(1, 255)),
                            -1
                        )
                        HapticEffectEvent.VICTORY -> VibrationEffect.createWaveform(
                            longArrayOf(0, (40 * multiplier).toLong(), 30, (40 * multiplier).toLong(), 30, (80 * multiplier).toLong(), 40, (120 * multiplier).toLong()),
                            intArrayOf(0, (180 * multiplier).toInt().coerceIn(1, 255), 0, (200 * multiplier).toInt().coerceIn(1, 255), 0, (230 * multiplier).toInt().coerceIn(1, 255), 0, (255 * multiplier).toInt().coerceIn(1, 255)),
                            -1
                        )
                        HapticEffectEvent.BUTTON_CLICK -> VibrationEffect.createOneShot(
                            (10 * multiplier).toLong().coerceAtLeast(1L),
                            (70 * multiplier).toInt().coerceIn(1, 255)
                        )
                    }
                    vibrator?.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate((20 * multiplier).toLong())
                }
            } catch (e: Exception) {
                fallbackComposeHaptic(event, composeHaptic)
            }
        } else {
            fallbackComposeHaptic(event, composeHaptic)
        }
    }

    private fun fallbackComposeHaptic(event: HapticEffectEvent, composeHaptic: HapticFeedback?) {
        composeHaptic?.let {
            when (event) {
                HapticEffectEvent.MOVE, HapticEffectEvent.TICK, HapticEffectEvent.SOFT_DROP, HapticEffectEvent.BUTTON_CLICK ->
                    it.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                HapticEffectEvent.ROTATE, HapticEffectEvent.HOLD, HapticEffectEvent.DROP_LOCK ->
                    it.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                HapticEffectEvent.HARD_DROP, HapticEffectEvent.LINE_CLEAR, HapticEffectEvent.LINE_CLEAR_SINGLE,
                HapticEffectEvent.LINE_CLEAR_DOUBLE, HapticEffectEvent.LINE_CLEAR_TRIPLE, HapticEffectEvent.TETRIS_BURST,
                HapticEffectEvent.LEVEL_UP, HapticEffectEvent.GAME_OVER, HapticEffectEvent.VICTORY ->
                    it.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }
}
