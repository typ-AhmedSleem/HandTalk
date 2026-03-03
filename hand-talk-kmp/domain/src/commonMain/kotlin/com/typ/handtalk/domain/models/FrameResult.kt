package com.typ.handtalk.domain.models

data class FrameResult(
    val leftHand: LeftHand? = null,
    val rightHand: RightHand? = null,
    val timestamp: Long
) {
    val rhs: HandSign? by lazy { rightHand?.sign }
    val lhs: HandSign? by lazy { leftHand?.sign }
    val rhsLabel: String? by lazy { rhs?.label }
    val lhsLabel: String? by lazy { lhs?.label }

    val isRhsNone: Boolean by lazy { rhsLabel == "none" || rhsLabel == "" }
    val isRhsNull: Boolean by lazy { rhsLabel == null }
    val isLhsNone: Boolean by lazy { lhsLabel == "none" || lhsLabel == "" }

    fun isRightNullOrNone(): Boolean = isRhsNull || isRhsNone
    fun isLeftNullOrNone(): Boolean = lhsLabel == null || lhsLabel == "None" || lhsLabel == "none" || lhsLabel == ""
}
