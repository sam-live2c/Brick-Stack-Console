package com.example.audio

import android.media.AudioManager
import android.media.ToneGenerator
import com.example.game.SoundEffectEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioSynthesizer {
    private var toneGenerator: ToneGenerator? = null
    var enabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (e: Exception) {
            toneGenerator = null
        }
    }

    fun playSound(event: SoundEffectEvent) {
        if (!enabled || toneGenerator == null) return

        CoroutineScope(Dispatchers.Default).launch {
            try {
                when (event) {
                    SoundEffectEvent.MOVE -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_1, 25)
                    }
                    SoundEffectEvent.ROTATE -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_3, 35)
                    }
                    SoundEffectEvent.LOCK -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_7, 45)
                    }
                    SoundEffectEvent.DROP -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 50)
                    }
                    SoundEffectEvent.LINE_CLEAR -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_A, 100)
                    }
                    SoundEffectEvent.TETRIS_CLEAR -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 180)
                    }
                    SoundEffectEvent.LEVEL_UP -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_S, 200)
                    }
                    SoundEffectEvent.GAME_OVER -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_P, 300)
                    }
                    SoundEffectEvent.BUTTON_CLICK -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_2, 30)
                    }
                }
            } catch (e: Exception) {
                // Ignore audio errors gracefully
            }
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
