package com.typ.handtalk.core.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Class that utilizes TTS (TextToSpeech) apis
 * to speak out sentences we obtain from
 * Sign language to Text translation
 */
class TextSpeaker(context: Context) {

    private var status: Status = Status.NOT_READY
    private val engine = TextToSpeech(context) { ttsStatus ->
        status = when (ttsStatus) {
            TextToSpeech.SUCCESS -> {
                initializeEngine()
                Status.READY
            }

            else -> Status.FAILED
        }
    }

    /**
     * Setup tts instance with desired language and voice.
     *
     * !NOTE: This engine only speaks US english now but it
     * !should speak Arabic as primary language.
     */
    private fun initializeEngine() {
        // Set language
        val english = Locale("eng_USA_default")
        Log.i(TAG, "Language ${english.language} support is ${engine.isLanguageAvailable(english)}")
        Log.i(TAG, "List of supported languages:\n${engine.availableLanguages}")
        if (engine.isLanguageAvailable(english) == TextToSpeech.LANG_AVAILABLE) {
            engine.setLanguage(english)
            Log.d(TAG, "Language is set to: ${english.language}.")
        } else {
            Log.w(TAG, "Language isn't available.")
        }
        // Set voice
        val voices = engine.voices
        if (voices.isEmpty()) {
            Log.w(TAG, "No voices available for this language.")
        } else {
            val targetVoice = voices.find { it.name == "en-US-default" }
            Log.d(TAG, "Discovering voices\n")
            voices.forEach {
                Log.d(TAG, "\t${it.name},${it.locale} ,${it.quality}")
            }
            Log.d(TAG, "Voice is set to: $targetVoice .")
            engine.voice = targetVoice
        }
    }

    fun speak(sentence: String) {
        if (status == Status.READY) {
            // Speak out sentence
            engine.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            // Can't speak text because of error or failed to init
            Log.w(TAG, "Can't speak due to error or failed to initialize engine.")
        }
    }

    enum class Status {
        NOT_READY,
        READY,
        FAILED
    }

    companion object {
        const val TAG = "TextSpeaker"
    }

}