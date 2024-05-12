package com.typ.handtalk.ui.s2a

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.R
import com.typ.handtalk.core.tts.TextSpeaker
import com.typ.handtalk.databinding.ActivityDisplayTranslationBinding
import com.typ.handtalk.ui.a2s.Arabic2SignTranslatorActivity
import kotlinx.coroutines.DelicateCoroutinesApi

class DisplayTranslationActivity : AppCompatActivity() {

    private lateinit var speaker: TextSpeaker

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val translation = intent.getStringExtra("translation")
        speaker = TextSpeaker().apply {
            initializeEngine(this@DisplayTranslationActivity) {
                // Speak the sentence once engine is ready
                speak(translation)
            }
        }
        with(ActivityDisplayTranslationBinding.inflate(layoutInflater)) {
            setContentView(root)
            tvTranslation.text = translation

            // Listeners
            fabDismiss.setOnClickListener {
                finish()
            }
            btnCopy.setOnClickListener {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(getString(R.string.app_name), translation)
                clipboard.setPrimaryClip(clip)
            }
            btnSpeak.setOnClickListener {
                speaker.speak(translation)
            }
            btnReply.setOnClickListener {
                startActivity(Intent(this@DisplayTranslationActivity, Arabic2SignTranslatorActivity::class.java))
            }

        }
    }

    companion object {
        const val EXTRA_TRANSLATION = "translation"
    }

}