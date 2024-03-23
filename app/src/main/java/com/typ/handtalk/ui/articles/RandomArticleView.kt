package com.typ.handtalk.ui.articles

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.typ.handtalk.R

class RandomArticleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        // Pick a random article

        // Init view
        initializeView()
    }

    private fun initializeView() {
        inflate(context, R.layout.view_random_article, this)
    }

}