package com.typ.handtalk.core.algorithms.sequencer

import android.util.Log
import com.typ.handtalk.core.algorithms.AbstractAlgorithm.AlgorithmState
import com.typ.handtalk.core.algorithms.words.WordSuggester
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.resolvers.models.FrameResult

class GestureSequencer {

    // * Runtime
    private var currentSequence = GestureSequence()
    private var lastSuggestedWords = mutableListOf<Word>()

    val state: AlgorithmState
        get() {
            return if (sequenceLength > 0) AlgorithmState.RUNNING else AlgorithmState.NEW_RUN
        }
    val sequenceLength: Int
        get() = currentSequence.length

    private fun createNewSequence(): GestureSequence {
        return GestureSequence()
    }

    fun createNewRun() {
        if (state == AlgorithmState.NEW_RUN) return
        currentSequence = createNewSequence()
        lastSuggestedWords = mutableListOf()
    }

    fun feed(payload: FrameResult): Word? {
        // Append the result to the current sequence
        currentSequence.appendFrameResult(payload)
        Log.i(TAG, "feed: Fed result ${payload.rhsLabel} to algorithm.")
        val possibleWords = WordSuggester.suggestWords(currentSequence)
        // Log possible words
        if (possibleWords.isNotEmpty()) {
            Log.i(
                TAG, "Possible words: ${
                    possibleWords.joinToString(
                        prefix = "(\n",
                        separator = "\n",
                        postfix = "\n)"
                    ) {
                        "\t" + it.arabicText
                    }
                }"
            )

            // Save the last suggested words
            lastSuggestedWords = mutableListOf()
            lastSuggestedWords.addAll(possibleWords)

            // Check if at least one word has same sequence of signs
            val completedWords = possibleWords.filter {
                it.signs.containsWithSameLength(currentSequence)
            }
            if (completedWords.isNotEmpty()) {
                Log.i(TAG, "Found complete word for curr=${currentSequence}, last=$lastSuggestedWords")
                createNewRun()
                return completedWords.first()
            } else {
                Log.i(TAG, "No complete word for curr=${currentSequence}, last=$lastSuggestedWords")
                return null
            }
        } else {
            createNewRun()
            Log.i(TAG, "No possible words for seq: curr=${currentSequence.suggestedWords.contentToString()}, last=$lastSuggestedWords")
            return null
        }
    }

    fun cancelCurrentRun() {
        currentSequence = GestureSequence()
        lastSuggestedWords = mutableListOf()
    }

    fun obtainResult(): GestureSequence {
        return GestureSequence(currentSequence.signs, lastSuggestedWords.toTypedArray())
    }

    companion object {
        const val TAG = "GestureSequencer"
    }

}