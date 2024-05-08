package com.typ.handtalk.core.algorithms.sequencer

import android.util.Log
import com.typ.handtalk.core.resolvers.models.FrameResult

/**
 * Holds array of FrameResult instances
 * that will be fed later to WordIdentifierAlgorithm
 * to identify words from repository that has
 * signs
 */
class GestureSequence(
    val isValid: Boolean = true,
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
        return signs.joinToString(
            prefix = "GestureSequence(isValid=$isValid, length=$length, signs: '",
            postfix = "')",
        ) { it.rhsLabel.toString() }
    }

    fun containsWithSameLength(sequence: GestureSequence): Boolean {
//        if (sequence.length == 1 && this.length == 1) {
//            return sequence.signs.containsAll(this.signs)
//        }
        // Check whether current sequence is not shorter than the given one
        if (sequence.length != this.length) return false
        // Check whether current sequence is contained in the given one
        return sequence.signs.containsAll(this.signs)
    }

    fun containsWithSameOrder(seq: GestureSequence): Boolean {
        val originalSeq = this.signs.joinToString(",") { it.rhsLabel.toString() }
        val subSeq = seq.signs.joinToString(",") { it.rhsLabel.toString() }
        Log.d("GestureSequence", "originalSeq: '$originalSeq', subSeq: '$subSeq', contains: ${originalSeq.indexOf(subSeq) == 0}")

        if (!originalSeq.contains(subSeq)) return false

        val startIndex = originalSeq.indexOf(subSeq)
        if (startIndex != 0) return false

        for (i in 1 until seq.signs.size) {
            if (seq.signs[i] != this.signs[i]) return false
        }

        return true
    }

}