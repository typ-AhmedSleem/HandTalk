package com.typ.handtalk.core.resolvers.models

import com.typ.handtalk.core.models.HandSign

data class FrameResult(
    val leftHandSign: HandSign? = null,
    val rightHandSign: HandSign? = null
) {

    override fun toString(): String {
        return "FrameResult(leftHandSign=$leftHandSign, rightHandSign=$rightHandSign)"
    }
}
