package com.typ.handtalk.core.algorithms.words

import android.util.Log
import com.typ.handtalk.core.models.Sentence
import com.typ.handtalk.core.models.Word

object WordSelector {

    /**
     * Selects the most suitable word for given sentence context.
     *
     * NOTE: param 'words' must be non empty and checked before calling this method
     */
    @JvmStatic
    fun selectMostSuitableWord(sentence: Sentence, expectedSentence: Sentence?, words: Array<Word>): Word {
        // * Return random word if sentence is empty to be the first word in it
        if (sentence.words.isEmpty()) {
            Log.d(TAG, "Sentence has no words. Returning first word...")
            return words.last()
        }
        // * Get the most suitable word according to expected sentence
        return ((expectedSentence?.let {
            for (word in words) {
                if (it.words.contains(word)) {
                    Log.d(TAG, "Found most suitable word: $word")
                    return word
                }
            }
            words.random()
        } ?: words.random()))
    }

    const val TAG = "WordSelector"

}