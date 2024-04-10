package com.typ.handtalk

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