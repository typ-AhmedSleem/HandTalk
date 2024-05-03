package com.typ.handtalk.core.a2s.playables

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.typ.handtalk.ui.a2s.Arabic2SignTranslatorActivity
import java.io.IOException

class A2SignPlayableImage(
    filename: String,
    delay: Long = Arabic2SignTranslatorActivity.PLAYABLE_SWITCH_DEFAULT_DELAY_TIME
) : A2SignPlayable(filename, delay) {

    override val filePath: String
        get() = "A2S/Images/${filename}"

    fun loadInto(iv: ImageView) {
        try {
            iv.setImageDrawable(Drawable.createFromStream(iv.context.assets.open(filePath), null))
            iv.visibility = View.VISIBLE
            Log.i("A2SignPlayableImage", "loaded: $filePath")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("A2SignPlayableImage", "Error loading image at '$filePath'. Reason: $e")
            iv.visibility = View.INVISIBLE
        }
    }

    override fun toString(): String {
        return "A2SignPlayableImage('$filePath')"
    }


}