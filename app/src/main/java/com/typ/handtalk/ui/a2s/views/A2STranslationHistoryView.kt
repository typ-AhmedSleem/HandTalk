package com.typ.handtalk.ui.a2s.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.typ.handtalk.R
import com.typ.handtalk.core.a2s.A2STranslationHistoryRecord
import com.typ.handtalk.core.a2s.A2STranslationsHistory
import com.typ.handtalk.databinding.ItemA2sTranslationHistoryRecordBinding
import com.typ.handtalk.databinding.LayoutA2sEmptyHistoryBinding
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class A2STranslationHistoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    // Views
    private val rvHistory: RecyclerView
    private val emptyLayoutBinding: LayoutA2sEmptyHistoryBinding
    private val emptyLayout: ViewGroup
        get() = emptyLayoutBinding.root
    val btnEmptyLayoutPlus: MaterialButton
        get() = emptyLayoutBinding.btnA2sNewTranslation

    // Runtime
    private val records = mutableListOf<A2STranslationHistoryRecord>()
    var state = LayoutState.EMPTY
        private set

    // Callbacks
    var layoutChangeCallback: ((LayoutState) -> Unit)? = null
        set(value) {
            field = value
            field?.invoke(state)
        }

    init {
        rvHistory = createRecyclerView().also {
            addView(it, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        emptyLayoutBinding = bindEmptyLayout().also {
            addView(it.root, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        // Populate view with saved records
        records.clear()
        records.addAll(A2STranslationsHistory.getAllRecords(context))
        getAdapter().submitList(records)
        switchLayout()
    }

    private fun createRecyclerView(): RecyclerView {
        return RecyclerView(context).apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = createAdapter()
        }
    }

    private fun bindEmptyLayout(): LayoutA2sEmptyHistoryBinding {
        return LayoutA2sEmptyHistoryBinding.bind(inflate(context, R.layout.layout_a2s_empty_history, null))
    }

    private fun createAdapter(): ListAdapter<A2STranslationHistoryRecord, RecyclerViewHolder<A2STranslationHistoryRecord>> {
        return adapterOf {
            register(
                layoutResource = R.layout.item_a2s_translation_history_record,
                viewHolder = ::VH,
                onBindViewHolder = { vh, _, record ->
                    vh.binding.root.text = record.sentence
                    vh.binding.root.setOnClickListener {
                        // todo: Start A2STranslatorActivity with the record passed
                        Toast.makeText(context, record.sentence, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getAdapter(): ListAdapter<A2STranslationHistoryRecord, RecyclerViewHolder<A2STranslationHistoryRecord>> {
        return rvHistory.adapter as ListAdapter<A2STranslationHistoryRecord, RecyclerViewHolder<A2STranslationHistoryRecord>>
    }

    fun addRecords(vararg records: A2STranslationHistoryRecord) {
        // Add records to records list
        if (records.isEmpty()) return
        // Submit list
        getAdapter().submitList(this.records + records)
        this.records.addAll(0, records.toList())
        if (records.isNotEmpty() && state == LayoutState.EMPTY) showHistory()
        else if (records.isEmpty() && state == LayoutState.RECYCLER) showEmpty()
    }

    fun clearHistory() {
        A2STranslationsHistory.clearHistory(context)
        records.clear()
        getAdapter().submitList(emptyList())
        showEmpty()
    }

    private fun showEmpty() {
        emptyLayout.visibility = VISIBLE
        rvHistory.visibility = GONE
        state = LayoutState.EMPTY
        layoutChangeCallback?.invoke(LayoutState.EMPTY)
    }

    private fun showHistory() {
        emptyLayout.visibility = GONE
        rvHistory.visibility = VISIBLE
        state = LayoutState.RECYCLER
        layoutChangeCallback?.invoke(LayoutState.RECYCLER)
    }

    private fun switchLayout() {
        if (records.isEmpty()) showEmpty()
        else showHistory()
    }

    private class VH(view: View) : RecyclerViewHolder<A2STranslationHistoryRecord>(view) {
        val binding = ItemA2sTranslationHistoryRecordBinding.bind(view)
    }

    enum class LayoutState {
        RECYCLER,
        EMPTY,
    }

}