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

        val isWordAppended = sentenceBuilder.appendWord(word)
        Log.w(TAG, "onSequenceCompleted: isWordAppended= $isWordAppended")

        Log.i(TAG, "onSequenceCompleted: Current sentence= ${sentenceBuilder.currentSentence}, expected= ${sentenceBuilder.expectedSentence}")
        // * Check if the sentence matches the expected sentence
        val isSentenceCompleted = sentenceBuilder.isSentenceCompleted
        if (isSentenceCompleted) {
            // * Notify callback
            sentenceBuilder.expectedSentence?.let { callback.onTranslateFullSentence(it.clone()) }
            sentenceBuilder.reset()
        } else callback.onIdentifyNewWord(word)

        return true
        /*
        * Validate the sequence
        if (!sequence.isValid) {
        Log.d(TAG, "onSequenceCompleted: Invalid sequence (Empty suggested words).")
        return true
        }
        val suggestedWords = sequence.suggestedWords
        Log.d(TAG, "onSequenceCompleted: Suggested words= ${suggestedWords.contentToString()}")
        // * Get the current sentence
        var currentSentence = sentenceBuilder.currentSentence
        Log.d(TAG, "onSequenceCompleted: CurrentSentence= $currentSentence")
        var expectedSentence = sentenceBuilder.expectedSentence
        Log.d(TAG, "onSequenceCompleted: ExpectedSentence= $expectedSentence")
        // * Select the most suitable word out of this sequence then append it to builder
        val suitableWord = WordSelector.selectMostSuitableWord(currentSentence, expectedSentence, suggestedWords)
        val completed = sentenceBuilder.appendWord(suitableWord)
        currentSentence = sentenceBuilder.currentSentence
        Log.d(TAG, "onSequenceCompleted: CurrentSentence= $currentSentence")
        expectedSentence = sentenceBuilder.expectedSentence
        Log.d(TAG, "onSequenceCompleted: ExpectedSentence= $expectedSentence")
        // * Check if the sentence is completed
        if (completed) {
        // Notify callback
        Log.d(TAG, "onSequenceCompleted: Sentence completed. cur=$currentSentence | exp=$expectedSentence")
        sentenceBuilder.expectedSentence?.let { callback.onTranslateFullSentence(it) }
        sentenceBuilder.reset()
        } else {
        Log.d(TAG, "onSequenceCompleted: Sentence not completed. length= ${sentenceBuilder.currentSentence}")
        }
        return true
        */
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