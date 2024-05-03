package com.typ.handtalk.core.algorithms

abstract class AbstractAlgorithm<FT, OT> {

    abstract fun createNewRun()

    abstract fun feed(payload: FT)

    abstract fun cancelCurrentRun()

    abstract fun obtainResult(): OT

    enum class AlgorithmState {
        NEW_RUN,
        RUNNING
    }

}

