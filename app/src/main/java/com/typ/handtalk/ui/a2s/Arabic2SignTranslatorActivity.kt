package com.typ.handtalk.ui.a2s

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
    private var lastPrompt: String? = null
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
                if (prompt != lastPrompt) {
                    lastPrompt = prompt
                    A2STranslationsHistory.saveTranslation(
                        this@Arabic2SignTranslatorActivity,
                        A2STranslationHistoryRecord(sentence = prompt)
                    )
                }
                // * Display the stylized prompt on its own Textview
                binding.tvA2sTranslationSentence.text = stylizePrompt(prompt, translation)
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
            lastPrompt = sentence
            binding.tilA2sPrompt.editText?.setText(sentence)
            binding.btnTranslateA2s.performClick()
        }
    }

    private fun stylizePrompt(prompt: String, values: Map<String, Any?>): SpannableString {
        val styledPrompt = SpannableString(prompt)
        val words = prompt.split(SPACE)
        for (word in words) {
            if (values.containsKey(word)) {
                val value = values[word]
                val color = if (value != null) Color.GREEN else Color.RED
                styledPrompt.setSpan(
                    ForegroundColorSpan(color),
                    prompt.indexOf(word),
                    prompt.indexOf(word) + word.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        return styledPrompt
    }


    companion object {
        const val SPACE = ' '
        const val DELAY_TIME = 1000L
        const val TAG = "actA2S_TRANSLATOR"
        const val EXTRA_PROMPT = "EXTRA_PROMPT"
    }

}
