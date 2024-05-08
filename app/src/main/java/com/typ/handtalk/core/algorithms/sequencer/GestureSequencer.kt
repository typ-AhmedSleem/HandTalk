package com.typ.handtalk.core.algorithms.sequencer

import android.util.Log
import com.typ.handtalk.core.algorithms.AbstractAlgorithm
import com.typ.handtalk.core.algorithms.words.WordSuggester
import com.typ.handtalk.core.resolvers.models.FrameResult

class GestureSequencer : AbstractAlgorithm<FrameResult, GestureSequence>() {

    // * Runtime
    private var currentSequence = GestureSequence()
    private val suggestedWords = mutableListOf<String>()

    val state: AlgorithmState
        get() {
            return if (sequenceLength > 0) AlgorithmState.RUNNING else AlgorithmState.NEW_RUN
        }
    val sequenceLength: Int
        get() = currentSequence.length

    private fun createNewSequence(): GestureSequence {
        return GestureSequence()
    }

    override fun createNewRun() {
        if (state == AlgorithmState.NEW_RUN) return
        currentSequence = createNewSequence()
    }

    override fun feed(payload: FrameResult) {
        // Append the result to the current sequence
        currentSequence.appendFrameResult(payload)
        Log.i(TAG, "feed: Fed result ${payload.rhsLabel} to algorithm.")
        val possibleWords = WordSuggester.suggestWords(currentSequence)
        // Log possible words
        if (possibleWords.isNotEmpty()) {
            Log.i(
                TAG, "Possible words: ${
                    possibleWords.joinToString(
                        prefix = "Possible words(\n",
                        separator = "\n",
                        postfix = "\n)"
                    ) {
                        "\t" + it.arabicText
                    }
                }"
            )
        } else {
            createNewRun()
            Log.i(TAG, "No possible words for seq: $currentSequence")
        }
    }

    override fun cancelCurrentRun() {
        currentSequence = GestureSequence()
    }

    override fun obtainResult(): GestureSequence {
        return GestureSequence(sequenceLength > 0, currentSequence.signs)
    }

    companion object {
        const val TAG = "GestureSequencerAlgorithm"
    }

}