package com.typ.handtalk.ui.articles.views

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import com.google.android.material.textview.MaterialTextView
import com.typ.handtalk.R

@SuppressLint("ViewConstructor")
class TitleSectionView(
    context: Context,
    title: String
) : LinearLayout(context) {

    init {
        inflate(context, R.layout.layout_title_section, this)
        findViewById<MaterialTextView>(R.id.tv_title).apply {
            text = title.trim()
        }
    }

}