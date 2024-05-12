package com.typ.handtalk.core.algorithms.handtalk

import android.content.Context
import android.graphics.Point
import android.util.Log
import androidx.camera.core.ImageProxy
import com.typ.handtalk.core.algorithms.motion.HandMotionInfo
import com.typ.handtalk.core.algorithms.motion.HandMovementTrackerCallback
import com.typ.handtalk.core.algorithms.recognizer.GestureRecognizerConfig
import com.typ.handtalk.core.algorithms.recognizer.HandSignRecognizer
import com.typ.handtalk.core.algorithms.recognizer.RecognizerError
import com.typ.handtalk.core.algorithms.recognizer.interfaces.HandSignRecognizerCallback
import com.typ.handtalk.core.algorithms.sequencer.GestureSequencer
import com.typ.handtalk.core.algorithms.sequencer.GestureSequencerCallback
import com.typ.handtalk.core.algorithms.words.SentenceBuilder
import com.typ.handtalk.core.algorithms.words.enums.SentenceFlag
import com.typ.handtalk.core.models.ImageShape
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.resolvers.models.FrameResult

class HandTalkAlgorithm(
    val context: Context,
    private val callback: HandTalkAlgorithmCallback
) : HandSignRecognizerCallback, GestureSequencerCallback, HandMovementTrackerCallback {

    // * Algorithms
    lateinit var recognizer: HandSignRecognizer
        private set
    private lateinit var sequencer: GestureSequencer
//    private lateinit var handTracker: HandMovementTracker

    // * Flags
    val recognizerInitialized: Boolean
        get() = ::recognizer.isInitialized

    private val sequencerInitialized: Boolean
        get() = ::sequencer.isInitialized
//    private val trackerInitialized: Boolean
//        get() = ::handTracker.isInitialized

    private val sentenceBuilder = SentenceBuilder()

    init {
        setupGestureRecognizer()
        setupSequencer()
//        setupHandTracker()
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
//        if (!trackerInitialized) handTracker = HandMovementTracker(this)
    }

    fun recognizeHandGestures(imageProxy: ImageProxy) {
        recognizer.recognizeSignsInFrame(imageProxy)
    }

    // REGION: HandSignRecognizerCallback

    override fun onRecognizerReady() {
    }

    override fun onHandAppeared(frame: FrameResult, inputShape: ImageShape) {

//        handTracker.beginTracking(frame, inputShape)
    }

    override fun onRecognizeHands(frameResult: FrameResult, inputShape: ImageShape) {
        // * Feed frame to the tracker
//        handTracker.feedFrame(frameResult, inputShape)
        // * Notify callback
        callback.onReadyToDrawLandmarks(frameResult, inputShape)
    }

    override fun onHandSignChanged(oldResult: FrameResult, newResult: FrameResult) {
        // Feed frame to the sequencer
        onSequenceFed(newResult)
        callback.onIdentifyGesture(newResult)
    }

    override fun onSameSignRecognized(result: FrameResult, inputShape: ImageShape) {
        // * Check if the same sign is recognized for a while
//        if (recognizer.recognizingSameSignForAWhile) {
//            // Check if hand has travelled distance than the threshold
//            if (handTracker.currentResult != null) {
//                // * Finish the current sequence
//                finishCurrentSequence()
//            }
//            return
//        }
        // * Feed frame to the tracker
//        handTracker.feedFrame(result, inputShape)
    }

    override fun onHandsDisappear() {
        Log.d(TAG, "onHandsDisappear: Right hand disappeared.")
        Log.i(TAG, "onHandsDisappear: $sentenceBuilder")
        // Obtain current sequence
//        with(sequencer.obtainResult()) {
//            Log.d(TAG, "finishCurrentSequence: $this")
//            if (onSequenceCompleted(this)) {
//                sequencer.createNewRun()
//                onSequenceStarted()
//            }
//        }
        sequencer.createNewRun()
        sentenceBuilder.reset()
        // Notify callback
        callback.onHandsDisappear()
    }

    override fun onRecognizerError(error: RecognizerError) {
        callback.onErrorOccurred(error)
    }

    // END: HandSignRecognizerCallback

    // REGION: GestureSequencerCallback

    override fun onSequenceFed(frame: FrameResult) {
        Log.d(TAG, "------------------------------ Feeding a new result to the sequence ------------------------------------")
        val completedWord = sequencer.feed(frame)
        completedWord?.let { onWordIdentified(it) }
    }

    override fun onWordIdentified(word: Word): Boolean {
        Log.i(TAG, "onSequenceCompleted: Received seq: ${word.signs.signsToString}. word= $word")
        Log.d(TAG, "onSequenceCompleted: Completed word identified. word= $word.")

        val flag = sentenceBuilder.appendWord(word)
        Log.v(TAG, "onSequenceCompleted: flag= $flag")

        when (flag) {
            SentenceFlag.SINGLE_WORD -> {
                callback.onIdentifyNewWord(word)
                sequencer.createNewRun()
                sentenceBuilder.reset()
            }

            SentenceFlag.WORD_ACCEPTED -> {
                callback.onIdentifySentenceWord(word)
                sentenceBuilder.expectedSentence?.let { callback.onSuggestSentence(sentenceBuilder.currentSentence, it) }
            }

            SentenceFlag.WORD_REJECTED -> {
//                callback.onHandsDisappear()
                sequencer.createNewRun()
                sentenceBuilder.reset()
            }

            SentenceFlag.SENTENCE_COMPLETED -> {
                sentenceBuilder.expectedSentence?.let { callback.onTranslateFullSentence(it) }
                sequencer.createNewRun()
                sentenceBuilder.reset()
            }

            SentenceFlag.WORD_MISMATCH -> {
                Log.w(TAG, "onSequenceCompleted: Word mismatch detected. word= $word.")
            }

            SentenceFlag.REPEATING_WORD -> {
                Log.w(TAG, "onSequenceCompleted: Word is repeating. word= $word.")
            }
        }

//
//        val flag = sentenceBuilder.appendWord(word)
//        Log.w(TAG, "onSequenceCompleted: flag= $flag")
//
//        Log.i(TAG, "onSequenceCompleted: $sentenceBuilder")
//        // * Check if the sentence matches the expected sentence
//        val isSentenceCompleted = sentenceBuilder.isSentenceCompleted
//        if (isSentenceCompleted) {
//            // * Notify callback
//            sentenceBuilder.expectedSentence?.let { callback.onTranslateFullSentence(it.clone()) }
//            sentenceBuilder.reset()
//        } else {
//            sentenceBuilder.expectedSentence?.let {
//                callback.onIdentifySentenceWord(word)
//                callback.onSuggestSentence(sentenceBuilder.currentSentence, it)
//            } ?: callback.onIdentifyNewWord(word)
//        }

        return false
    }

    override fun onSequenceStarted() {
    }

    override fun onSequenceCancelled() {
    }

    // END: GestureSequencerCallback

    // REGION: HandMovementTrackerCallback
    override fun onBeginHandTracking() {
        Log.d(TAG, "onBeginHandTracking")
    }

    override fun onHandMoving(position: Point) {
//        Log.d(TAG, "onHandMoving: Hand is currently at $position")
    }

    override fun onStopHandTracking(info: HandMotionInfo?) {
        if (info != null) Log.d(TAG, "onStopHandTracking: Detected gesture => $info")
        else Log.d(TAG, "onStopHandTracking: No gesture identified.")
    }

    // END: HandMovementTrackerCallback

    companion object {
        const val TAG = "HandTalkAlgo"
    }

}