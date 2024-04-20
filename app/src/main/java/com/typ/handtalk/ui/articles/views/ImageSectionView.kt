package com.typ.handtalk.ui.articles.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.constraintlayout.utils.widget.ImageFilterView
import java.io.IOException

@SuppressLint("ViewConstructor")
class ImageSectionView constructor(
    context: Context,
    src: String
) : ImageFilterView(context) {

    init {
        roundPercent = 0.15f
        adjustViewBounds = true
        scaleType = ScaleType.CENTER_CROP
        try {
            setImageDrawable(Drawable.createFromStream(context.assets.open(src), null))
        } catch (e: IOException) {
            e.printStackTrace()
            visibility = View.GONE
        }
    }

}