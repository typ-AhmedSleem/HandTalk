package com.typ.handtalk.ui.a2s

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.core.a2s.A2STranslationHistoryRecord
import com.typ.handtalk.core.a2s.A2STranslationsHistory
import com.typ.handtalk.databinding.ActivityArabic2signTranslatorBinding
import com.typ.handtalk.ui.a2s.views.A2STranslationHistoryView

class Arabic2SignTranslatorActivity : AppCompatActivity() {

    companion object {
        const val TAG = "activityArabic2SignTranslator"
    }

    private lateinit var binding: ActivityArabic2signTranslatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArabic2signTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Setup views
        binding.toolbar.setNavigationOnClickListener {
            finishAfterTransition()
        }

        binding.thvA2sTranslationHistory.apply {
            layoutChangeCallback = { state ->
                if (state == A2STranslationHistoryView.LayoutState.EMPTY) binding.fabA2sNewTranslation.hide()
                else binding.fabA2sNewTranslation.show()
            }
            btnEmptyLayoutPlus.setOnClickListener(requestTranslationClickListener)
        }

        binding.fabA2sNewTranslation.setOnClickListener(requestTranslationClickListener)

        binding.fabA2sNewTranslation.setOnLongClickListener {
            binding.thvA2sTranslationHistory.clearHistory()
            true
        }
    }

    private val requestTranslationClickListener: (View) -> Unit by lazy {
        {
            requestNewTranslation()
        }
    }

    private fun requestNewTranslation() {
        // todo: Start a new translation
        val translation = A2STranslationHistoryRecord((1..100).random().toString())
        A2STranslationsHistory.saveTranslation(this, translation)
        binding.thvA2sTranslationHistory.addRecords(translation)
    }
}