package com.typ.handtalk.core.resolvers.models

import com.typ.handtalk.core.models.HandSign
import com.typ.handtalk.core.models.MovingSign
import com.typ.handtalk.core.repository.Signs

data class FrameResult(
    val leftHandSign: HandSign? = null,
    val rightHandSign: HandSign? = null
) {

    fun isSeparator(): Boolean {
        if (rightHandSign == null) return false
        if ((rightHandSign is MovingSign).not()) return false
        return ((leftHandSign?.label ?: "None") == "None") &&
                ((rightHandSign as MovingSign).isSeparator())
    }

    override fun toString(): String {
        return "FrameResult(leftHandSign=$leftHandSign, rightHandSign=$rightHandSign)"
    }

    override fun hashCode(): Int {
        var result = leftHandSign?.hashCode() ?: 0
        result = 31 * result + (rightHandSign?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FrameResult) return false

        if (leftHandSign?.label != other.leftHandSign?.label) return false
        if (rightHandSign?.label != other.rightHandSign?.label) return false
        return true
    }
}

private fun MovingSign.isSeparator(): Boolean {
    val separator = Signs.Separator()

    if (this.label != separator.label) return false
    if (this.score < separator.score) return false
    if (this.direction != separator.direction) return false
    if (this.distance < separator.distance) return false

    return true // Considered a separator.
}
