package com.typ.handtalk.core.algorithms.sequencer

import com.typ.handtalk.core.resolvers.models.FrameResult

/**
 * Holds array of FrameResult instances
 * that will be fed later to WordIdentifierAlgorithm
 * to identify words from repository that has
 * signs
 */
class GestureSequence(
    val isValid: Boolean = false,
    val signs: MutableList<FrameResult> = mutableListOf()
) {
    val length: Int
        get() = signs.size

    fun appendFrameResult(frameResult: FrameResult) {
        signs.add(frameResult)
    }

    fun clear() {
        signs.clear()
    }

    override fun toString(): String {
        return "GestureSequence(isValid=$isValid, length=$length, signs:${
            signs.joinToString(
                prefix = "GestureSequence(isValid=$isValid, length=$length, signs:\n",
                postfix = "\n)",
                separator = "\n"
            ) { it.rhsLabel.toString() }
        }"
    }


}