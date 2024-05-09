package com.typ.handtalk.core.algorithms.words

import android.util.Log
import com.typ.handtalk.core.algorithms.sequencer.GestureSequence
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.repository.Words

object WordSuggester {

    @JvmStatic
    fun suggestWords(sequence: GestureSequence): MutableList<Word> {
        Log.i(TAG, "suggestWords: Suggesting for sequence: ${sequence.signs.joinToString(prefix = "[", postfix = "]") { it.rhsLabel.toString() }}")
        val possibleWords = mutableListOf<Word>()
        for (word in Words.WORDS) {
            if (word.signs.length >= sequence.length) {
                if (word.signs.containsWithSameOrder(sequence)) {
                    possibleWords.add(word)
                }
            }
        }
        return possibleWords
    }

    const val TAG = "WordSuggester"

}