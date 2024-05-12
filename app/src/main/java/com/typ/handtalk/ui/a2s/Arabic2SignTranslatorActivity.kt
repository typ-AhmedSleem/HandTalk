package com.typ.handtalk.ui.a2s

import android.Manifest
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.typ.handtalk.R
import com.typ.handtalk.core.a2s.A2STranslationHistoryRecord
import com.typ.handtalk.core.a2s.A2STranslationsHistory
import com.typ.handtalk.core.a2s.Arabic2SignTranslator
import com.typ.handtalk.core.perms.PermissionHelper
import com.typ.handtalk.core.stt.SpeechToTextEngine
import com.typ.handtalk.databinding.ActivityA2sTranslatorBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Arabic2SignTranslatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityA2sTranslatorBinding
    private lateinit var stt: SpeechToTextEngine
    private lateinit var permLauncher: ActivityResultLauncher<Array<String>>

    // * Runtime
    private var lastPrompt: String? = null
    private lateinit var translator: Arabic2SignTranslator
    private val prompt: String
        get() = (binding.tilA2sPrompt.editText?.text ?: "").toString()

    private var currentlyActiveJob: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // * Initialize translator instance
        translator = Arabic2SignTranslator()
        // * Initialize translator instance
        stt = SpeechToTextEngine(this) {
            binding.tilA2sPrompt.editText?.setText(it)
        }
        // * Initialize UI
        supportActionBar?.hide()
        binding = ActivityA2sTranslatorBinding.inflate(layoutInflater).apply {
            setContentView(root)
            toolbar.setNavigationOnClickListener { finish() }
            btnTranslateA2s.setOnClickListener {
                if (binding.a2sTranslationPlayerView.playing) {
                    // * Cancel the current translation player and reset UI
                    currentlyActiveJob?.cancel()
                    binding.a2sTranslationPlayerView.reset()
                    binding.tilA2sPrompt.isEnabled = true
                    binding.tvA2sTranslationError.setText(R.string.sign_language_will_be_displayed_here)
                    changeButtonState(R.string.translate, R.color.colorPrimary)
                    return@setOnClickListener
                }
                // * * Perform translation
                if (prompt.isEmpty()) {
                    toast(R.string.empty_translation_prompt)
                    return@setOnClickListener
                }
                binding.tilA2sPrompt.isEnabled = false
                binding.tvA2sTranslationError.text = null
                changeButtonState(R.string.stop_translating, R.color.colorErrorContainer)
                // * Translate the prompt
                val translation = translator.translate(prompt)
                // * Display the stylized prompt on its own Textview
                binding.tvA2sTranslationSentence.visibility = VISIBLE
                binding.tvA2sTranslationSentence.text = stylizePrompt(prompt, translation)
                if (translation.values.any { it == null }) {
//                    toast(R.string.translation_has_missing_words)
                    binding.tvA2sTranslationError.setText(R.string.translation_has_missing_words)
                    binding.tilA2sPrompt.isEnabled = true
                    changeButtonState(R.string.translate, R.color.colorPrimary)
                }
                // Quit if the translation contains only nulls
//                if (translation.values.all { it == null }) {
//                    Log.i(TAG, "Translation has no playables.")
//                    return@setOnClickListener
//                }
                // * Save the translation
                if (prompt != lastPrompt) {
                    lastPrompt = prompt
                    A2STranslationsHistory.saveTranslation(
                        this@Arabic2SignTranslatorActivity,
                        A2STranslationHistoryRecord(sentence = prompt)
                    )
                }
                // * Display the translation
                currentlyActiveJob = GlobalScope.launch(Dispatchers.Main) {
                    // Create counter for each playable
                    val iterator = translation.values.iterator()
                    if (!iterator.hasNext()) return@launch
                    binding.a2sTranslationPlayerView.display(iterator.next() ?: return@launch) {
                        if (iterator.hasNext()) {
                            return@display iterator.next()
                        } else {
                            binding.a2sTranslationPlayerView.reset()
                            binding.tilA2sPrompt.isEnabled = true
                            binding.tvA2sTranslationError.setText(R.string.sign_language_will_be_displayed_here)
                            changeButtonState(R.string.translate, R.color.colorPrimary)
                        }
                        return@display null
                    }
                }
            }
            tilA2sPrompt.setEndIconOnClickListener {
                listenToUser()
            }
        }
        // * Get passed prompt from intent (if any)
        intent.getStringExtra(EXTRA_PROMPT)?.let { sentence ->
            lastPrompt = sentence
            binding.tilA2sPrompt.editText?.setText(sentence)
            binding.btnTranslateA2s.performClick()
        }
        // * Initialize microphone permission launcher
        permLauncher = PermissionHelper.requestPermissionLauncher(this) {
            val granted = it.all { result -> result.value }
            if (granted) listenToUser()
            else toast(R.string.mic_permission_denied_or_cancelled)
        }
    }

    private fun listenToUser() {
        if (PermissionHelper.arePermissionsGranted(this, arrayOf(Manifest.permission.RECORD_AUDIO))) {
            stt.listenToUser()
            toast(R.string.listening)
            return
        }
        permLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
    }

    private fun changeButtonState(text: Int, bgColor: Int) {
        binding.btnTranslateA2s.apply {
            setText(text)
            setBackgroundColor(ResourcesCompat.getColor(resources, bgColor, null))
        }
    }


    private val colorRed: Int by lazy {
        ResourcesCompat.getColor(resources, R.color.colorErrorContainer, null)
    }

    private val colorGreen: Int by lazy {
        ResourcesCompat.getColor(resources, R.color.colorSuccessContainer, null)
    }

    private fun stylizePrompt(prompt: String, values: Map<String, Any?>): SpannableString {
        val styledPrompt = SpannableString(prompt)
        // Full sentence at once
        if (values[prompt] != null) {
            styledPrompt.setSpan(
                ForegroundColorSpan(colorGreen),
                prompt.indexOf(prompt),
                prompt.indexOf(prompt) + prompt.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return styledPrompt
        }
        // Words-split sentence
        val words = prompt.split(SPACE)
        for (word in words) {
            if (values.containsKey(word)) {
                val value = values[word]
                val color = if (value != null) colorGreen else colorRed
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

    private fun toast(resId: Int) {
        Toast.makeText(this@Arabic2SignTranslatorActivity, resId, Toast.LENGTH_SHORT).show()
    }


    companion object {
        const val SPACE = ' '
        const val PLAYABLE_SWITCH_DEFAULT_DELAY_TIME = 1000L
        const val TAG = "actA2S_TRANSLATOR"
        const val EXTRA_PROMPT = "EXTRA_PROMPT"
    }

}
