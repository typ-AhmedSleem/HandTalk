package com.typ.handtalk.utils

import android.graphics.Point
import com.typ.handtalk.core.algorithms.sequencer.GestureSequence
import com.typ.handtalk.core.models.hands.LeftHand
import com.typ.handtalk.core.models.hands.RightHand
import com.typ.handtalk.core.models.signs.HandSign
import com.typ.handtalk.core.resolvers.models.FrameResult

fun sequenceOfGestures(vararg results: FrameResult): GestureSequence {
    return GestureSequence(false, results.toMutableList())
}

fun frameResult(rhs: HandSign?, lhs: HandSign? = null): FrameResult {
    return FrameResult(LeftHand(lhs, emptyList()), RightHand(rhs, emptyList()))
}

fun rhResult(rhs: HandSign?): FrameResult {
    return frameResult(rhs)
}

fun handSign(label: String): HandSign {
    return HandSign(label)
}

fun emptyPoint() = Point(0,0)

fun point(x: Int, y: Int) = Point(x, y)