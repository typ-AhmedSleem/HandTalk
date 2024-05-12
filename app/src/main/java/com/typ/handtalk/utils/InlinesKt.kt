package com.typ.handtalk.utils

import android.graphics.Point
import com.typ.handtalk.core.algorithms.motion.HandMotionInfo
import com.typ.handtalk.core.algorithms.sequencer.GestureSequence
import com.typ.handtalk.core.enums.MovingDirection
import com.typ.handtalk.core.models.ImageShape
import com.typ.handtalk.core.models.hands.LeftHand
import com.typ.handtalk.core.models.hands.RightHand
import com.typ.handtalk.core.models.signs.HandSign
import com.typ.handtalk.core.resolvers.models.FrameResult

fun sequenceOfGestures(vararg results: FrameResult): GestureSequence {
    return GestureSequence(results.toMutableList())
}

fun frameResult(rhs: HandSign?, lhs: HandSign? = null): FrameResult {
    return FrameResult(LeftHand(lhs, emptyList()), RightHand(rhs, emptyList()))
}

fun rhResult(rhs: HandSign?): FrameResult {
    return frameResult(rhs)
}

fun rhResult(rhsLabel: String?): FrameResult {
    return rhsLabel?.let { frameResult(handSign(it)) } ?: frameResult(null)
}

fun handSign(label: String): HandSign {
    return HandSign(label)
}

fun emptyPoint() = Point(0, 0)

fun point(x: Int, y: Int) = Point(x, y)

fun emptyImageShape() = ImageShape(0, 0)

fun motionInfo(distance: Point, direction: MovingDirection) = HandMotionInfo(distance, direction)