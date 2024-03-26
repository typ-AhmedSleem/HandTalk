package com.typ.handtalk.ui.a2s

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import com.typ.handtalk.R
import com.typ.handtalk.core.a2s.A2STranslationHistoryRecord
import com.typ.handtalk.databinding.ActivityArabic2signTranslatorBinding
import com.typ.handtalk.databinding.ItemA2sTranslationHistoryRecordBinding
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class Arabic2SignTranslatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArabic2signTranslatorBinding
    private lateinit var historyAdapter: ListAdapter<A2STranslationHistoryRecord, RecyclerViewHolder<A2STranslationHistoryRecord>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArabic2signTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Setup adapter
        historyAdapter = adapterOf {
            register(
                layoutResource = R.layout.item_a2s_translation_history_record,
                viewHolder = Arabic2SignTranslatorActivity::VH,
                onBindViewHolder = { vh, _, record ->
                    vh.binding.root.text = record.sentence
                    vh.binding.root.setOnClickListener {
                        // todo: Start A2STranslatorActivity with the record passed
                        Toast.makeText(this@Arabic2SignTranslatorActivity, record.sentence, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        
        // Setup history rv
        binding.rvArabic2signTranslationHistory.apply {
            adapter = historyAdapter
        }
    }

    private class VH(view: View) : RecyclerViewHolder<A2STranslationHistoryRecord>(view) {
        val binding = ItemA2sTranslationHistoryRecordBinding.bind(view)
    }

}