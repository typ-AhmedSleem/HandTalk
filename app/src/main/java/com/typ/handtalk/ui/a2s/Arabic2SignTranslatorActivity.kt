package com.typ.handtalk.ui.a2s

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.core.a2s.A2STranslationHistoryRecord
import com.typ.handtalk.core.a2s.A2STranslationsHistory
import com.typ.handtalk.core.a2s.Arabic2SignTranslator
import com.typ.handtalk.databinding.ActivityA2sTranslatorBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Arabic2SignTranslatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityA2sTranslatorBinding

    // * Runtime
    private lateinit var translator: Arabic2SignTranslator
    private val prompt: String
        get() = (binding.tilA2sPrompt.editText?.text ?: "").toString()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // * Initialize translator instance
        translator = Arabic2SignTranslator()
        // * Initialize UI
        supportActionBar?.hide()
        binding = ActivityA2sTranslatorBinding.inflate(layoutInflater).apply {
            setContentView(root)
            toolbar.setNavigationOnClickListener { finish() }
            btnTranslateA2s.setOnClickListener {
                if (prompt.isEmpty()) return@setOnClickListener
                // * Translate the prompt
                val translation = translator.translate(prompt)
                // * Quit if the translation contains only nulls
                if (translation.values.all { it == null }) {
                    Log.i(TAG, "Translation has no playables.")
                    return@setOnClickListener
                }
                // * Save the translation
                A2STranslationsHistory.saveTranslation(
                    this@Arabic2SignTranslatorActivity,
                    A2STranslationHistoryRecord(sentence = prompt)
                )
                // * Display the translation
                GlobalScope.launch(Dispatchers.IO) {
                    translation.values.forEach { playable ->
                        if (playable != null) {
                            withContext(Dispatchers.Main) {
                                binding.a2sTranslationPlayerView.display(playable)
                            }
                            delay(DELAY_TIME)
                        }
                    }
                }
            }
        }
        // * Get passed prompt from intent (if any)
        intent.getStringExtra(EXTRA_PROMPT)?.let { sentence ->
            binding.tilA2sPrompt.editText?.setText(sentence)
        }
    }

    companion object {
        const val DELAY_TIME = 1000L
        const val TAG = "actA2S_TRANSLATOR"
        const val EXTRA_PROMPT = "EXTRA_PROMPT"
    }

}
