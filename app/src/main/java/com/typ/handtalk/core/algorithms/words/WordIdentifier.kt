package com.typ.handtalk.core.algorithms.words

import android.util.Log
import com.typ.handtalk.core.algorithms.sequencer.GestureSequence
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.repository.Words

object WordIdentifier {

    const val TAG = "WordIdentifier"

    @JvmStatic
    fun identifyWord(sequence: GestureSequence): Word? {
        // Double check if the sequence is valid
        if (!sequence.isValid) return null
        // Search words repository for matching sequence
        for (word in Words.WORDS) {
            val foundWord = word.signs.containsWithSameOrder(sequence)
            Log.i(TAG, "Checking (${sequence.signs.joinToString { it.rhsLabel.toString() }}) against (${word.signs.signs.joinToString { it.rhsLabel.toString() }}). Result= $foundWord")

            if (foundWord) return word
        }

        return null
    }

}