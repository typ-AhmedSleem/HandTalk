/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.typ.handtalk.core.recognizer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.typ.handtalk.core.algorithms.sequencer.GestureSequencer
import com.typ.handtalk.core.algorithms.sequencer.GestureSequencerCallback
import com.typ.handtalk.core.recognizer.interfaces.HandRecognizerInternalCallback
import com.typ.handtalk.core.recognizer.interfaces.HandSignRecognizerCallback
import com.typ.handtalk.core.resolvers.FrameResultResolver
import com.typ.handtalk.core.resolvers.models.FrameResult

class HandSignRecognizer(
    val context: Context,
    var currentDelegate: Delegate = Delegate.CPU,
    var minHandDetectionConfidence: Float = DEFAULT_HAND_DETECTION_CONFIDENCE,
    var minHandTrackingConfidence: Float = DEFAULT_HAND_TRACKING_CONFIDENCE,
    var minHandPresenceConfidence: Float = DEFAULT_HAND_PRESENCE_CONFIDENCE,
    val recognizerCallback: HandSignRecognizerCallback,
    val sequencerCallback: GestureSequencerCallback,
) : HandRecognizerInternalCallback {

    private var gestureRecognizer: GestureRecognizer? = null
    val closed: Boolean
        get() = gestureRecognizer == null

    val running: Boolean
        get() = !closed

    // Recognizer runtime
    private var prevResult: FrameResult? = null

    // Algorithms
    private val sequencer = GestureSequencer()

    init {
        setupGestureRecognizer()
        logi("HandSignRecognizer is now fully initialized.")
    }

    fun clearGestureRecognizer() {
        gestureRecognizer?.close()
        gestureRecognizer = null
    }

    /**
    Initialize the gesture recognizer using current settings on the
    thread that is using it. CPU can be used with recognizers
    that are created on the main thread and used on a background thread, but
    the GPU delegate needs to be used on the thread that initialized the recognizer
     */
    fun setupGestureRecognizer() {
        // Set general recognition options, including number of used threads
        val baseOptionBuilder = BaseOptions.builder()
        // Use the specified hardware for running the model. Default to CPU
        baseOptionBuilder.setDelegate(currentDelegate)
        baseOptionBuilder.setModelAssetPath(MP_RECOGNIZER_TASK)

        try {
            val baseOptions = baseOptionBuilder.build()
            val optionsBuilder =
                GestureRecognizer.GestureRecognizerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setNumHands(NUM_HANDS)
                    .setMinHandDetectionConfidence(minHandDetectionConfidence)
                    .setMinTrackingConfidence(minHandTrackingConfidence)
                    .setMinHandPresenceConfidence(minHandPresenceConfidence)
                    .setRunningMode(RunningMode.LIVE_STREAM)
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)

            val options = optionsBuilder.build()
            gestureRecognizer = GestureRecognizer.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            recognizerCallback.onRecognizerError(RecognizerError.OtherError(e.message))
            Log.e(TAG, "MP Task Vision failed to load the task with error: " + e.message)
        } catch (e: RuntimeException) {
            recognizerCallback.onRecognizerError(RecognizerError.GPUError(e.message))
            Log.e(TAG, "MP Task Vision failed to load the task with error: " + e.message)
        }
    }

    /** Convert the ImageProxy to MP Image and feed it to GestureRecognizer. */
    private fun preprocessCameraFrame(imageProxy: ImageProxy): Pair<Long, MPImage> {
        val frameTime = SystemClock.uptimeMillis()

        // Copy out RGB bits from the frame to a bitmap buffer
        val bitmapBuffer = Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        val matrix = Matrix().apply {
            // Rotate the frame received from the camera to be in the same direction as it'll be shown
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            // Flip image since we only support front camera
            postScale(-1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat())
        }

        // Rotate bitmap to match what our model expects
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            matrix,
            true
        )

        // Convert the input Bitmap object to an MPImage object to run inference
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        return frameTime to mpImage
    }

    /** Run hand gesture recognition using MediaPipe Gesture Recognition API */
    fun recognizeSignsInFrame(imageProxy: ImageProxy) {
        val (frameTime, mpImage) = preprocessCameraFrame(imageProxy)
        gestureRecognizer?.recognizeAsync(mpImage, frameTime)
    }

    private fun returnLivestreamError(error: RuntimeException) {
        recognizerCallback.onRecognizerError(RecognizerError.UnknownError(error.message))
    }

    /** Return the recognition result to the GestureRecognizerHelper's caller */
    private fun returnLivestreamResult(rawResult: GestureRecognizerResult, input: MPImage) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - rawResult.timestampMs()

        val newResult = FrameResultResolver.resolve(rawResult)

        if (prevResult == null) {
            // No previous result
            prevResult = newResult
            return
        }
        // Found a previous result
        prevResult?.let prev@{ prev ->
            if (newResult.isRhsNone) return@prev
            // Check if newResult is same as lastResult
            if (newResult == prev) {
                // * Fire onSameSignRecognized
                this.onSameSignRecognized(newResult)
                return@prev
            }
            // Check if RHS has changed
            if (newResult.isRhsNull) {
                // * Fire onReachNoResultTimeout
                val timeout = newResult.timestamp - prev.timestamp
                val timeoutReached = timeout >= HAND_SIGN_CHANGE_TIMEOUT
                val disappeared = !prev.isRhsNull
                if (timeoutReached && disappeared) {
                    // Timeout has been exceeded
                    logi("onHandDisappeared: Right hand has disappeared.")
                    this.onHandDisappeared()
                    return@prev
                }
            } else {
                // * Handle the right hand
                newResult.rightHand?.sign?.let rhs@{ rhs ->
                    // Return immediately if RHS hasn't changed
                    if (rhs == prev.rightHand?.sign) return@prev
                    // Sign has actually changed. Check the timeout...
                    if (newResult.timestamp - prev.timestamp < HAND_SIGN_CHANGE_TIMEOUT) {
                        // Timeout hasn't yet been exceeded
                        logi("onHandSignChanged: Timeout hasn't yet been exceeded. Timeout is ${newResult.timestamp - prev.timestamp}")
                        return@prev
                    }
                }
                // * Fire onHandSignChanged
                this.onHandSignChanged(prev, newResult)
            }
            // * Update runtime
            prevResult = newResult
        }

        // * Fire the listener
        recognizerCallback.onRecognizeHands(newResult, input.height to input.width)
    }

    override fun onHandDisappeared() {
        prevResult = null
        // Obtain current sequence
        with(sequencer.obtainResult()) {
            if (isValid && sequencerCallback.onSequenceCompleted(this)) {
                sequencer.createNewRun()
                sequencerCallback.onSequenceStarted()
            }
        }
        // Notify callback about disappeared hands
        recognizerCallback.onHandsDisappear()
    }

    override fun onHandSignChanged(oldResult: FrameResult, newResult: FrameResult) {
        logi("RHS has changed: ${oldResult.rhsLabel} -> ${newResult.rhsLabel}. Took ${newResult.timestamp - oldResult.timestamp} ms to change.\n")
        // Feed frame to the sequencer
        sequencer.feed(newResult)
        // Notify callback
        sequencerCallback.onSequenceFed(newResult)
    }

    override fun onSameSignRecognized(result: FrameResult) {
        // todo: Feed frame to the MotionEstimation algorithm
        // todo: Check the motion estimation algorithm if the hand has moved the distance threshold
    }

    companion object {
        val TAG = "HandSignRecognizer-${this.hashCode()}"
        private const val MP_RECOGNIZER_TASK = "model/gesture_recognizer.task"

        const val NUM_HANDS = 2
        const val DEFAULT_HAND_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F

        const val HAND_SIGN_CHANGE_TIMEOUT = 50 // in millis
        const val EMPTY_HAND_SIGN_CHANGE_TIMEOUT = 1500 // in millis

        @JvmStatic
        fun logi(msg: Any) {
            Log.i(TAG, "$msg")
        }
    }

}
