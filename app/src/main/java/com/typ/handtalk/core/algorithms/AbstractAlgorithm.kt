package com.typ.handtalk.core.algorithms

abstract class AbstractAlgorithm<FT, OT> {

    abstract fun createNewRun()

    abstract fun feed(payload: FT)

    abstract fun cancelCurrentRun()

    abstract fun obtainResult(thenCreateNewRun: Boolean = true): OT

    enum class AlgorithmState {
        IDLE,
        RUNNING
    }

}

