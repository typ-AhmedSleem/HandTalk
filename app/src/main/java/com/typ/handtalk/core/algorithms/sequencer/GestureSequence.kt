package com.typ.handtalk.core.algorithms.sequencer

import android.util.Log
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.resolvers.models.FrameResult

/**
 * Holds array of FrameResult instances
 * that will be fed later to WordIdentifierAlgorithm
 * to identify words from repository that has
 * signs
 */
class GestureSequence(
    val signs: MutableList<FrameResult> = mutableListOf(),
    val suggestedWords: Array<Word> = emptyArray(),
) {
    val length: Int
        get() = signs.size

    val isValid: Boolean
        get() = suggestedWords.isNotEmpty()

    val signsToString: String
        get() = signs.joinToString(prefix = "seq[", postfix = "]") { it.rhsLabel.toString() }

    fun appendFrameResult(frameResult: FrameResult) {
        signs.add(frameResult)
    }

    override fun toString(): String {
        return "GestureSequence(isValid=$isValid, length=$length, signs: '${
            signs.joinToString(
                prefix = "signs: '",
                postfix = "')",
            ) { it.rhsLabel.toString() }
        }, ${
            suggestedWords.joinToString(
                prefix = "suggestedWords: '",
                postfix = "')",
            ) { it.toString() }
        }"
    }

    fun containsWithSameLength(sequence: GestureSequence): Boolean {
        Log.d("GestureSequence", "containsWithSameLength: curr=${this.signsToString} & seq=${sequence.signsToString}")
        // Check whether current sequence is not shorter than the given one
        if (sequence.length != this.length) return false
        // Check whether current sequence is contained in the given one
        return sequence.signs == this.signs
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