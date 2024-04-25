package com.typ.handtalk.ui.a2s

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.core.a2s.Arabic2SignTranslator
import com.typ.handtalk.core.a2s.playables.A2SignPlayableImage
import com.typ.handtalk.core.a2s.playables.A2SignPlayableVideo
import com.typ.handtalk.databinding.ActivityA2sTranslatorBinding

class Arabic2SignTranslatorActivity : AppCompatActivity() {

    private val prompt: String
        get() = (binding.tilA2sPrompt.editText?.text ?: "").toString()

    private lateinit var binding: ActivityA2sTranslatorBinding
    private lateinit var translator: Arabic2SignTranslator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityA2sTranslatorBinding.inflate(layoutInflater)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.fabTranslateA2s.setOnClickListener {
            if (prompt.isEmpty()) return@setOnClickListener
            val translation = translator.translate(prompt)
            translation.values.forEach { playable ->
                if (playable == null) return@forEach
                when (playable) {
                    is A2SignPlayableImage -> {
                        // TODO: Show image in the A2SPlayableView
                    }

                    is A2SignPlayableVideo -> {
                        // TODO: Show video in the A2SPlayableView
                    }
                }
            }
        }
    }
}
