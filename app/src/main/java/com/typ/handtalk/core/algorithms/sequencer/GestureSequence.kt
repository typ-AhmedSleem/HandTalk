package com.typ.handtalk.core.algorithms.sequencer

import com.typ.handtalk.core.resolvers.models.FrameResult

/**
 * Holds array of FrameResult instances
 * that will be fed later to WordIdentifierAlgorithm
 * to identify words from repository that has
 * signs
 */
class GestureSequence(
    val signs: Array<FrameResult>
)