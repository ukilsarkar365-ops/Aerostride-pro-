package com.example.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class VoiceCoach(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false
    var isEnabled: Boolean = true

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.setSpeechRate(1.05f)
            tts?.setPitch(1.0f)
            isInitialized = true
        } else {
            Log.e("VoiceCoach", "TTS initialization failed: $status")
        }
    }

    fun speak(text: String, isHighPriority: Boolean = false) {
        if (!isEnabled || !isInitialized || tts == null) return
        val queueMode = if (isHighPriority) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(text, queueMode, null, "AEROSTRIDE_${System.currentTimeMillis()}")
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("VoiceCoach", "Error shutting down TTS", e)
        }
    }
}
