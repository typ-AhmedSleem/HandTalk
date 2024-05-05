package com.typ.handtalk.utils

import android.graphics.Point
import android.graphics.PointF
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

fun NormalizedLandmark.toPoint(): PointF {
    return PointF(this.x(), this.y())
}

fun NormalizedLandmark.toScaledPoint(wsf: Int, hsf: Int): Point {
    return Point((this.x() * wsf).toInt(), (this.y() * hsf).toInt())
}