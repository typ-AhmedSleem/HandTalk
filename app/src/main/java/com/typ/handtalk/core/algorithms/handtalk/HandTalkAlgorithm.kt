package com.typ.handtalk.core.algorithms.handtalk

import android.content.Context
import android.graphics.Point
import android.util.Log
import androidx.camera.core.ImageProxy
import com.typ.handtalk.core.algorithms.identifier.WordIdentifier
import com.typ.handtalk.core.algorithms.motion.HandMotionInfo
import com.typ.handtalk.core.algorithms.motion.HandMovementTracker
import com.typ.handtalk.core.algorithms.motion.HandMovementTrackerCallback
import com.typ.handtalk.core.algorithms.recognizer.GestureRecognizerConfig
import com.typ.handtalk.core.algorithms.recognizer.HandSignRecognizer
import com.typ.handtalk.core.algorithms.recognizer.RecognizerError
import com.typ.handtalk.core.algorithms.recognizer.interfaces.HandSignRecognizerCallback
import com.typ.handtalk.core.algorithms.sequencer.GestureSequence
import com.typ.handtalk.core.algorithms.sequencer.GestureSequencer
import com.typ.handtalk.core.algorithms.sequencer.GestureSequencerCallback
import com.typ.handtalk.core.models.ImageShape
import com.typ.handtalk.core.resolvers.models.FrameResult

class HandTalkAlgorithm(
    val context: Context,
    private val callback: HandTalkAlgorithmCallback
) : HandSignRecognizerCallback, GestureSequencerCallback, HandMovementTrackerCallback {

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
        if (!trackerInitialized) handTracker = HandMovementTracker(this)
    }

    fun recognizeHandGestures(imageProxy: ImageProxy) {
        recognizer.recognizeSignsInFrame(imageProxy)
    }

    // REGION: HandSignRecognizerCallback

    override fun onRecognizerReady() {
    }

    override fun onHandAppeared(frame: FrameResult, inputShape: ImageShape) {
        handTracker.beginTracking(frame, inputShape)
    }

    override fun onRecognizeHands(frameResult: FrameResult, inputShape: ImageShape) {
        callback.onIdentifyGesture(frameResult)
        callback.onReadyToDrawLandmarks(frameResult, inputShape)
//        handTracker.feedFrame(frameResult, inputShape)
    }

    override fun onHandSignChanged(oldResult: FrameResult, newResult: FrameResult) {
        // Feed frame to the sequencer
        onSequenceFed(newResult)
    }

    override fun onSameSignRecognized(result: FrameResult, inputShape: ImageShape) {
        // * Check if the time difference btw current frame and starting frame is more than the allowed timeout
        handTracker.feedFrame(result, inputShape)
    }

    override fun onHandsDisappear() {
        // Obtain current sequence
        with(sequencer.obtainResult()) {
            if (isValid && onSequenceCompleted(this)) {
                sequencer.createNewRun()
                onSequenceStarted()
            }
        }
        handTracker.stopTracking()
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

    // REGION: HandMovementTrackerCallback
    override fun onBeginHandTracking() {
        Log.i(TAG, "onBeginHandTracking")
    }

    override fun onHandMoving(position: Point) {
        Log.i(TAG, "onHandMoving: Currently at $position")
    }

    override fun onStopHandTracking(info: HandMotionInfo?) {
        if (info != null) {
            Log.i(TAG, "onStopHandTracking: Detected gesture => $info")
        }
    }

    // END: HandMovementTrackerCallback

    companion object {
        const val TAG = "HandTalkAlgo"
    }

}