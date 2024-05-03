package com.typ.handtalk.core.resolvers.models

import com.typ.handtalk.core.models.hands.Hand
import com.typ.handtalk.core.models.signs.HandSign
import com.typ.handtalk.core.models.signs.MovingSign
import com.typ.handtalk.core.repository.Signs

data class FrameResult(
    val leftHand: Hand? = null,
    val rightHand: Hand? = null,
    val timestamp: Long = System.currentTimeMillis(),
) {

    val rhs by lazy {
        rightHand?.sign
    }

    val lhs by lazy {
        leftHand?.sign
    }

    val rhsLabel by lazy {
        rhs?.label
    }

    val lhsLabel by lazy {
        lhs?.label
    }

    val isRhsNone by lazy {
        rhsLabel == "None"
    }

    val isRhsNull by lazy {
        rhsLabel == null
    }

    fun isSeparator(): Boolean {
        if (rightHand == null) return false
        if ((rightHand is MovingSign).not()) return false
        return false
//        return ((leftHand?.label ?: "None") == "None") && ((rightHand as MovingSign).isSeparator())
    }

    override fun toString(): String {
        return "FrameResult(timestamp=$timestamp, leftHand=$leftHand, rightHand=$rightHand)"
    }

    override fun hashCode(): Int {
        var result = leftHand?.hashCode() ?: 0
        result = 31 * result + (rightHand?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FrameResult) return false

//        return lhs == other.lhs && rhs == other.rhs
        return hasSameRightSignAs(other.rhs)
    }

    fun hasSameRightSignAs(prevRHS: HandSign?): Boolean {
        if (rightHand == null || prevRHS == null) return false
        return rightHand.hasSameSignAs(prevRHS)
    }

    fun hasSameLeftSignAs(prevSign: Hand?): Boolean {
        if (leftHand == null || prevSign == null) return false
        return leftHand.hasSameSignAs(prevSign.sign)
    }

    fun isRightNullOrNone(): Boolean {
        return rhsLabel == null || isRhsNone
    }

    fun isLeftNullOrNone(): Boolean {
        return lhsLabel == null || lhsLabel == "None"
    }

    fun areAllNullsOrNones(): Boolean {
        return isRightNullOrNone() && isLeftNullOrNone()
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
