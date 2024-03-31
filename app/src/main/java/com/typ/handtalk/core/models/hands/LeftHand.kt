package com.typ.handtalk.core.models.hands

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.typ.handtalk.core.models.signs.HandSign
import com.typ.handtalk.core.resolvers.FrameResultResolver

class LeftHand(sign: HandSign?, landmarks: List<List<NormalizedLandmark>>) : Hand(
    idx = FrameResultResolver.LEFT_HAND_INDEX,
    sign = sign,
    landmarks = landmarks,
) {
    override fun toString(): String {
        return "LH($sign)"
    }
}
