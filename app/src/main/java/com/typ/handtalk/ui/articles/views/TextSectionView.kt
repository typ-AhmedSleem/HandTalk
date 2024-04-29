package com.typ.handtalk.ui.articles.views

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.textview.MaterialTextView
import com.typ.handtalk.R
import com.typ.handtalk.articles.models.Section

@SuppressLint("ViewConstructor")
class TextSectionView(
    context: Context,
    section: Section.TextSection
) : LinearLayout(context) {

    init {
        inflate(context, R.layout.layout_text_section, this)
        findViewById<MaterialTextView>(R.id.tv_question).apply {
            text = section.question.trim()
            visibility = if (TextUtils.isEmpty(section.question)) View.GONE else View.VISIBLE
        }
        findViewById<MaterialTextView>(R.id.tv_answer).apply {
            text = section.answer.trim()
            visibility = if (TextUtils.isEmpty(section.answer)) View.GONE else View.VISIBLE
        }
    }

}