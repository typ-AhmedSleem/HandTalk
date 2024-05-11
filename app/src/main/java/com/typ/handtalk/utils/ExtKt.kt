package com.typ.handtalk.utils

import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.typ.handtalk.core.models.ImageShape
import com.typ.handtalk.core.models.Sentence

fun NormalizedLandmark.toPoint(): PointF {
    return PointF(this.x(), this.y())
}

fun NormalizedLandmark.toScaledPoint(wsf: Int, hsf: Int): Point {
    return Point((this.x() * wsf).toInt(), (this.y() * hsf).toInt())
}

fun MPImage.shape() = ImageShape(width, height)

fun Sentence.asSuggestion(expected: Sentence): SpannableString {
    val expArabic = expected.arabic.trim()
    val curArabic = this.arabic.trim()
    val styledSentence = SpannableString(expArabic)

    val firstMatchIdx = expArabic.indexOf(curArabic)
    if (firstMatchIdx == 0) {
        // Found match
        styledSentence.setSpan(
            ForegroundColorSpan(Color.GREEN),
            firstMatchIdx,
            curArabic.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    } else {
        // No match
        styledSentence.setSpan(
            ForegroundColorSpan(Color.WHITE),
            0,
            expArabic.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return styledSentence
}