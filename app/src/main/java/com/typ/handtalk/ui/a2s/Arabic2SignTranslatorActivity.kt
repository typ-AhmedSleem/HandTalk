package com.typ.handtalk.ui.a2s

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.core.a2s.Arabic2SignTranslator
import com.typ.handtalk.databinding.ActivityA2sTranslatorBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Arabic2SignTranslatorActivity : AppCompatActivity() {

    private val prompt: String
        get() = (binding.tilA2sPrompt.editText?.text ?: "").toString()

    private lateinit var binding: ActivityA2sTranslatorBinding
    private lateinit var translator: Arabic2SignTranslator

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        translator = Arabic2SignTranslator()
        binding = ActivityA2sTranslatorBinding.inflate(layoutInflater).apply {
            setContentView(root)
            toolbar.setNavigationOnClickListener { finish() }
            btnTranslateA2s.setOnClickListener {
                if (prompt.isEmpty()) return@setOnClickListener
                val translation = translator.translate(prompt)
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
    }

    companion object {
        const val DELAY_TIME = 1000L
        const val TAG = "actA2S_TRANSLATOR"
    }

}
