package com.typ.handtalk.core.resolvers.models

import com.typ.handtalk.core.models.hands.Hand
import com.typ.handtalk.core.models.hands.LeftHand
import com.typ.handtalk.core.models.hands.RightHand
import com.typ.handtalk.core.models.signs.HandSign

data class FrameResult(
    val leftHand: LeftHand? = null,
    val rightHand: RightHand? = null,
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
        rhsLabel == "none" || rhsLabel == ""
    }

    val isRhsNull by lazy {
        rhsLabel == null
    }

    val isLhsNone by lazy {
        lhsLabel == "none" || rhsLabel == ""
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
        val hasLHS = !isLeftNullOrNone()
        val sameRHS = hasSameRightSignAs(other.rhs)
        if (hasLHS) {
            return hasSameLeftSignAs(other.lhs) && sameRHS
        }
        return sameRHS
    }

    fun hasSameRightSignAs(prevRHS: HandSign?): Boolean {
        if (rightHand == null || prevRHS == null) return false
        return rightHand.hasSameSignAs(prevRHS)
    }

    fun hasSameLeftSignAs(prevSign: HandSign?): Boolean {
        if (leftHand == null || prevSign == null) return false
        return leftHand.hasSameSignAs(prevSign)
    }

    fun isRightNullOrNone(): Boolean {
        return isRhsNull || isRhsNone
    }

    fun isLeftNullOrNone(): Boolean {
        return lhsLabel == null || lhsLabel == "None"
    }

    fun areAllNullsOrNones(): Boolean {
        return isRightNullOrNone() && isLeftNullOrNone()
    }

    fun forEachHand(action: (Hand) -> Unit) {
        leftHand?.let(action)
        rightHand?.let(action)
    }

}
