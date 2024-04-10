package com.typ.handtalk.core.algorithms.sequencer

import android.util.Log
import com.typ.handtalk.core.algorithms.AbstractAlgorithm
import com.typ.handtalk.core.resolvers.models.FrameResult
import com.typ.handtalk.sequenceOfGestures

class GestureSequencerAlgorithm : AbstractAlgorithm<FrameResult, GestureSequence>() {

    // * Flags
    val isIdle: Boolean
        get() = state == AlgorithmState.IDLE
    val isRunning: Boolean
        get() = state == AlgorithmState.RUNNING
    val sequenceLength: Int
        get() = currentSequence.length

    // * Runtime
    private var state: AlgorithmState = AlgorithmState.IDLE
    private var currentSequence = GestureSequence()
    private val suggestedWords = mutableListOf<String>()

    private fun createNewSequence(): GestureSequence {
        return GestureSequence()
    }

    override fun createNewRun() {
        if (isRunning) cancelCurrentRun()
        currentSequence = createNewSequence()
        Log.i(TAG, "createNewRun: Created a new run.")
    }

    override fun feed(payload: FrameResult) {
        // Update state if not yet updated
        if (!isRunning) state = AlgorithmState.RUNNING
        currentSequence.appendFrameResult(payload)
        Log.i(TAG, "feed: Fed result ${payload.rhsLabel} to algorithm.")
    }

    override fun cancelCurrentRun() {
//        currentSequence.clear()
        currentSequence = sequenceOfGestures()
        state = AlgorithmState.IDLE
        Log.i(TAG, "cancelCurrentRun: Cancelled current run.")
    }

    override fun obtainResult(thenCreateNewRun: Boolean): GestureSequence {
        val sequence = GestureSequence(currentSequence.isValid, currentSequence.signs)
        if (thenCreateNewRun) createNewRun()
        return sequence
    }

    companion object {
        const val TAG = "GestureSequencerAlgorithm"
    }

}