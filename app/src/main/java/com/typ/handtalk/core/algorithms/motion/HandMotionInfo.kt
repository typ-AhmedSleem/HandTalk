package com.typ.handtalk.core.algorithms.motion

import android.graphics.Point
import com.typ.handtalk.core.enums.MovingDirection
import com.typ.handtalk.utils.emptyPoint

data class HandMotionInfo(
    val distance: Point = emptyPoint(),
    val direction: MovingDirection = MovingDirection.UNKNOWN
) {

    override fun toString(): String {
        return "HandMotionInfo(distance=$distance, direction=$direction)"
    }
}