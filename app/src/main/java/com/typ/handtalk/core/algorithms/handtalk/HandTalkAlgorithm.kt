package com.typ.handtalk.core.algorithms.handtalk

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageProxy
import com.typ.handtalk.core.algorithms.identifier.WordIdentifier
import com.typ.handtalk.core.algorithms.sequencer.GestureSequence
import com.typ.handtalk.core.algorithms.sequencer.GestureSequencer
import com.typ.handtalk.core.algorithms.sequencer.GestureSequencerCallback
import com.typ.handtalk.core.recognizer.GestureRecognizerConfig
import com.typ.handtalk.core.recognizer.HandSignRecognizer
import com.typ.handtalk.core.recognizer.RecognizerError
import com.typ.handtalk.core.recognizer.interfaces.HandSignRecognizerCallback
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

    // * Flags
    val recognizerInitialized: Boolean
        get() = ::recognizer.isInitialized

    val sequencerInitialized: Boolean
        get() = ::sequencer.isInitialized

    init {
        // * Setup gesture recognizer
        setupGestureRecognizer()
        setupSequencer()
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

    fun recognizeHandGestures(imageProxy: ImageProxy) {
        recognizer.recognizeSignsInFrame(imageProxy)
    }

    // REGION: HandSignRecognizerCallback

    override fun onRecognizerReady() {
    }

    override fun onRecognizeHands(frameResult: FrameResult, inputShape: Pair<Width, Height>) {
        callback.onIdentifyGesture(frameResult)
        callback.onReadyToDrawLandmarks(frameResult, inputShape)
    }

    override fun onHandSignChanged(oldResult: FrameResult, newResult: FrameResult) {
        // Feed frame to the sequencer
        onSequenceFed(newResult)
    }

    override fun onSameSignRecognized(result: FrameResult) {
        // todo: Feed frame to the MotionEstimation algorithm
        // todo: Check the motion estimation algorithm if the hand has moved the distance threshold
    }

    override fun onHandsDisappear() {
        // Obtain current sequence
        with(sequencer.obtainResult()) {
            if (isValid && onSequenceCompleted(this)) {
                sequencer.createNewRun()
                onSequenceStarted()
            }
        }
        callback.onHandsDisappear()
    }

    override fun onRecognizerError(error: RecognizerError) {
        callback.onErrorOccurred(error)
    }

    // END: HandSignRecognizerCallback

    // REGION: GestureSequencerCallback

    override fun onSequenceFed(frame: FrameResult) {
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