package com.typ.handtalk.core.algorithms.sequencer

import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.resolvers.models.FrameResult

interface GestureSequencerCallback {

    fun onSequenceStarted()

    /**
     * Called when the sequencer has fed with a new FrameResult.
     *
     * @param frame the new FrameResult.
     */
    fun onSequenceFed(frame: FrameResult)

    fun onSequenceCancelled()

    /**
     * Called when recognizer notified callback about detecting separator
     * which indicates that sequence is completed.
     *
     * @param sequence the completed sequence.
     *
     * @return boolean indicating whether sequencer should create a new sequence or not.
     */
    fun onWordIdentified(word: Word): Boolean

}