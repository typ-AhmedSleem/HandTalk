package com.typ.handtalk.ui.a2s

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.databinding.ActivityA2sTranslatorBinding

class Arabic2SignTranslatorActivity : AppCompatActivity() {

    private val prompt: String
        get() = (binding.tilA2sPrompt.editText?.text ?: "").toString()

    private lateinit var binding: ActivityA2sTranslatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityA2sTranslatorBinding.inflate(layoutInflater)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.fabTranslateA2s.setOnClickListener {
            if (prompt.isEmpty()) return@setOnClickListener
            val translation = translate()
        }
    }

    /**
     * Translate the prompt to sign language
     * and result a map of with key is the word
     * and value is (array or A2SPlayableImage) or (A2SPlayableVideo).
     */
    private fun translate() {
        // * todo: translate the prompt to sign language
    }

}
