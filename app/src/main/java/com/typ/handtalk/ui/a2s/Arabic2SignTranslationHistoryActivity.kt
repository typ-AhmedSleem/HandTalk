package com.typ.handtalk.ui.a2s

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.databinding.ActivityA2sTranslationHistoryBinding
import com.typ.handtalk.ui.a2s.views.A2STranslationHistoryView

class Arabic2SignTranslationHistoryActivity : AppCompatActivity() {

    companion object {
        const val TAG = "actA2STranslationHistory"
    }

    private lateinit var binding: ActivityA2sTranslationHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityA2sTranslationHistoryBinding.inflate(layoutInflater)
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
        // Start Arabic2SignTranslatorActivity
        startActivity(Intent(this, Arabic2SignTranslatorActivity::class.java))
    }
}