package com.typ.handtalk.core.algorithms.sequencer

import android.util.Log
import com.typ.handtalk.core.algorithms.AbstractAlgorithm
import com.typ.handtalk.core.resolvers.models.FrameResult

class GestureSequencerAlgorithm : AbstractAlgorithm<FrameResult, GestureSequence>() {

    // * Flags
    val isIdle: Boolean
        get() = state == AlgorithmState.IDLE
    val isRunning: Boolean
        get() = state == AlgorithmState.RUNNING
    val payloadFedCount: Int
        get() = results.size

    // * Runtime
    private var state: AlgorithmState = AlgorithmState.IDLE
    private val results = mutableListOf<FrameResult>()

    override fun createNewRun() {
        if (isRunning) cancelCurrentRun()
        state = AlgorithmState.IDLE
        Log.i(TAG, "createNewRun: Created a new run.")
    }

    override fun feed(payload: FrameResult) {
        // Update state if not yet updated
        if (!isRunning) state = AlgorithmState.RUNNING
        results.add(payload)
        Log.i(TAG, "feed: Fed result ${payload.rhsLabel} to algorithm.")
    }

    override fun cancelCurrentRun() {
        results.clear()
        state = AlgorithmState.IDLE
        Log.i(TAG, "cancelCurrentRun: Cancelled current run.")
    }

    override fun obtainResult(thenCreateNewRun: Boolean): GestureSequence {
        val sequence = GestureSequence(results.toTypedArray())
        if (thenCreateNewRun) createNewRun()
        return sequence
    }

    companion object {
        const val TAG = "GestureSequencerAlgorithm"
    }

}