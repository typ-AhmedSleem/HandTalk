package com.typ.handtalk.core.algorithms.handtalk

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageProxy
import com.typ.handtalk.core.algorithms.identifier.WordIdentifier
import com.typ.handtalk.core.algorithms.motion.HandMovementTracker
import com.typ.handtalk.core.algorithms.sequencer.GestureSequence
import com.typ.handtalk.core.algorithms.sequencer.GestureSequencer
import com.typ.handtalk.core.algorithms.sequencer.GestureSequencerCallback
import com.typ.handtalk.core.algorithms.recognizer.GestureRecognizerConfig
import com.typ.handtalk.core.algorithms.recognizer.HandSignRecognizer
import com.typ.handtalk.core.algorithms.recognizer.RecognizerError
import com.typ.handtalk.core.algorithms.recognizer.interfaces.HandSignRecognizerCallback
import com.typ.handtalk.core.resolvers.models.FrameResult
import com.typ.handtalk.utils.Height
import com.typ.handtalk.utils.Width

class HandTalkAlgorithm(
    val context: Context,
    private val callback: HandTalkAlgorithmCallback
) : HandSignRecognizerCallback, GestureSequencerCallback {

    // * Algorithms
    lateinit var recognizer: HandSignRecognizer
        private set
    lateinit var sequencer: GestureSequencer
        private set
    lateinit var handTracker: HandMovementTracker
        private set

    // * Flags
    val recognizerInitialized: Boolean
        get() = ::recognizer.isInitialized

    val sequencerInitialized: Boolean
        get() = ::sequencer.isInitialized
    val trackerInitialized: Boolean
        get() = ::handTracker.isInitialized

    init {
        setupGestureRecognizer()
        setupSequencer()
        setupHandTracker()
    }

    fun setupGestureRecognizer(recognizerConfig: GestureRecognizerConfig = GestureRecognizerConfig()) {
        if (!recognizerInitialized) {
            recognizer = HandSignRecognizer(
                context = context,
                minHandDetectionConfidence = recognizerConfig.minHandDetectionConfidence,
                minHandTrackingConfidence = recognizerConfig.minHandTrackingConfidence,
                minHandPresenceConfidence = recognizerConfig.minHandPresenceConfidence,
                currentDelegate = recognizerConfig.delegate,
                callback = this
            )
        }
        recognizer.setupGestureRecognizer()
    }

    private fun setupSequencer() {
        if (!sequencerInitialized) sequencer = GestureSequencer()
    }

    private fun setupHandTracker() {
        if (!trackerInitialized) handTracker = HandMovementTracker()
    }

    fun recognizeHandGestures(imageProxy: ImageProxy) {
        recognizer.recognizeSignsInFrame(imageProxy)
    }

    // REGION: HandSignRecognizerCallback

    override fun onRecognizerReady() {
    }

    override fun onRecognizeHands(frameResult: FrameResult, inputShape: Pair<Width, Height>) {
        callback.onIdentifyGesture(frameResult)
        callback.onReadyToDrawLandmarks(frameResult, inputShape)
//        handTracker.feedFrame(frameResult, inputShape.first, inputShape.second)
    }

    override fun onHandSignChanged(oldResult: FrameResult, newResult: FrameResult) {
        // Feed frame to the sequencer
        onSequenceFed(newResult)
    }

    override fun onSameSignRecognized(result: FrameResult, inputShape: Pair<Width, Height>) {
        // * Feed frame to the HandTracker algorithm
        handTracker.feedFrame(result, inputShape.first, inputShape.second)
    }

    override fun onHandsDisappear() {
        // Obtain current sequence
        with(sequencer.obtainResult()) {
            if (isValid && onSequenceCompleted(this)) {
                sequencer.createNewRun()
                onSequenceStarted()
            }
        }
        handTracker.cancelCurrentRun()
        callback.onHandsDisappear()
    }

    override fun onRecognizerError(error: RecognizerError) {
        callback.onErrorOccurred(error)
    }

    // END: HandSignRecognizerCallback

    // REGION: GestureSequencerCallback

    override fun onSequenceFed(frame: FrameResult) {
        // todo: We have much work here to do
        sequencer.feed(frame)
    }

    override fun onSequenceCompleted(sequence: GestureSequence): Boolean {
        // * Identify the word through WordIdentifier algorithm
        WordIdentifier.identifyWord(sequence)?.let {
            callback.onIdentifyNewWord(it)
            Log.i(TAG, "onSequenceCompleted: $sequence")
        }
        return true
    }

    override fun onSequenceStarted() {
    }

    override fun onSequenceCancelled() {
    }

    // END: GestureSequencerCallback

    companion object {
        const val TAG = "HandTalkAlgo"
    }

}