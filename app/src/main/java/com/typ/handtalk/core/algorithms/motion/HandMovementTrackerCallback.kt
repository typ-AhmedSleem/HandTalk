package com.typ.handtalk.core.algorithms.motion

import android.graphics.Point

interface HandMovementTrackerCallback {

    fun onBeginHandTracking()

    fun onHandMoving(position: Point)

    fun onStopHandTracking(info: HandMotionInfo?)

}