package com.typ.handtalk.core.algorithms.words

import com.typ.handtalk.core.algorithms.sequencer.GestureSequence
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.repository.Words

object WordSuggester {

    @JvmStatic
    fun suggestWords(sequence: GestureSequence): MutableList<Word> {
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

}