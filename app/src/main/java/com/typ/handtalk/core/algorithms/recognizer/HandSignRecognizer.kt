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
package com.typ.handtalk.core.algorithms.recognizer

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
import com.typ.handtalk.core.algorithms.recognizer.interfaces.HandRecognizerInternalCallback
import com.typ.handtalk.core.algorithms.recognizer.interfaces.HandSignRecognizerCallback
import com.typ.handtalk.core.models.ImageShape
import com.typ.handtalk.core.resolvers.FrameResultResolver
import com.typ.handtalk.core.resolvers.models.FrameResult
import com.typ.handtalk.utils.shape

class HandSignRecognizer(
    val context: Context,
    var currentDelegate: Delegate = Delegate.CPU,
    var minHandDetectionConfidence: Float = DEFAULT_HAND_DETECTION_CONFIDENCE,
    var minHandTrackingConfidence: Float = DEFAULT_HAND_TRACKING_CONFIDENCE,
    var minHandPresenceConfidence: Float = DEFAULT_HAND_PRESENCE_CONFIDENCE,
    private val callback: HandSignRecognizerCallback,
) : HandRecognizerInternalCallback {

    private var gestureRecognizer: GestureRecognizer? = null
    val closed: Boolean
        get() = gestureRecognizer == null

    val running: Boolean
        get() = !closed

    val recognizingSameSignForAWhile: Boolean
        get() {
            return sameSignCurrFrameTimestamp - sameSignStartFrameTimestamp >= SAME_SIGN_RECOGNIZE_TIMEOUT
        }

    val timestamps: Triple<Long, Long, Long>
        get() {
            return Triple(sameSignStartFrameTimestamp, sameSignCurrFrameTimestamp, sameSignCurrFrameTimestamp - sameSignStartFrameTimestamp)
        }

    // Recognizer runtime
    private var currentFrame: FrameResult? = null
    private var sameSignStartFrameTimestamp: Long = 0L
    private var sameSignCurrFrameTimestamp: Long = 0L

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
            callback.onRecognizerError(RecognizerError.OtherError(e.message))
            Log.e(TAG, "MP Task Vision failed to load the task with error: " + e.message)
        } catch (e: RuntimeException) {
            callback.onRecognizerError(RecognizerError.GPUError(e.message))
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
        callback.onRecognizerError(RecognizerError.UnknownError(error.message))
    }

    /** Return the recognition result to the GestureRecognizerHelper's caller */
    private fun returnLivestreamResult(rawResult: GestureRecognizerResult, input: MPImage) {

        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - rawResult.timestampMs()

        val newFrame = FrameResultResolver.resolve(rawResult)

        if (currentFrame == null) {
            // No previous result
            currentFrame = newFrame
            // * Notify
            if (!newFrame.isRightNullOrNone()) {
                this.onHandAppeared(newFrame, input.shape())
            }
            return
        }
        // Found a previous result
        currentFrame?.let prev@{ prev ->
            if (newFrame.isRhsNone) return@prev
            // Check if newFrame is same as lastResult
            if (newFrame == prev) {
                // * Fire onSameSignRecognized
                this.onSameSignRecognized(newFrame, input.shape())
                return@prev
            }
            // Check if RHS has changed
            if (newFrame.isRhsNull) {
                // * Fire onReachNoResultTimeout
                val timeout = newFrame.timestamp - prev.timestamp
                val timeoutReached = timeout >= HAND_DISAPPEAR_TIMEOUT
                val disappeared = !prev.isRhsNull
                if (timeoutReached && disappeared) {
                    // Timeout has been exceeded
                    logi("onHandDisappeared: Right hand has disappeared.")
                    this.onHandDisappeared()
                    return@prev
                }
            } else {
                // * Handle the right hand
                newFrame.rightHand?.sign?.let rhs@{ rhs ->
                    // Return immediately if RHS hasn't changed
                    if (rhs == prev.rightHand?.sign) return@prev
                    // Sign has actually changed. Check the timeout...
                    if (newFrame.timestamp - prev.timestamp < HAND_SIGN_CHANGE_TIMEOUT) {
                        // Timeout hasn't yet been exceeded
                        logi("onHandSignChanged: Timeout hasn't yet been exceeded. Timeout is ${newFrame.timestamp - prev.timestamp}")
                        return@prev
                    }
                }
                // * Fire onHandSignChanged
                this.onHandSignChanged(prev, newFrame)
            }
            // * Update runtime
            currentFrame = newFrame
        }

        // * Notify the global callback
        callback.onRecognizeHands(newFrame, input.shape())
    }

    override fun onHandAppeared(frame: FrameResult, inputShape: ImageShape) {
        sameSignStartFrameTimestamp = 0L
        callback.onHandAppeared(frame, inputShape)
    }

    override fun onHandDisappeared() {
        if (currentFrame != null) {
            currentFrame = null
            sameSignStartFrameTimestamp = 0L
            // Notify callback about disappeared hands
            callback.onHandsDisappear()
        }
    }

    override fun onHandSignChanged(oldResult: FrameResult, newResult: FrameResult) {
        sameSignStartFrameTimestamp = 0L
        logi("RHS has changed: ${oldResult.rhsLabel} -> ${newResult.rhsLabel}. Took ${newResult.timestamp - oldResult.timestamp} ms to change.\n")
        // Notify callback
        callback.onHandSignChanged(oldResult, newResult)
    }

    override fun onSameSignRecognized(result: FrameResult, inputShape: ImageShape) {
        if (sameSignStartFrameTimestamp == 0L) {
            sameSignStartFrameTimestamp = result.timestamp
//            Log.i(TAG, "Started tracking a sign: ${result.rhsLabel} at ${result.timestamp}")
        }
        sameSignCurrFrameTimestamp = result.timestamp
        callback.onSameSignRecognized(result, inputShape)
    }

    companion object {
        val TAG = "HandSignRecognizer-${this.hashCode()}"
        private const val MP_RECOGNIZER_TASK = "model/handtalk-model-v1.task"

        const val NUM_HANDS = 2
        const val DEFAULT_HAND_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F

        const val HAND_SIGN_CHANGE_TIMEOUT = 250 // in millis
        const val HAND_DISAPPEAR_TIMEOUT = 25 // in millis
        const val SAME_SIGN_RECOGNIZE_TIMEOUT = 2500 // in millis
        const val WORD_RECOGNITION_TIMEOUT = 3000 // in millis

        @JvmStatic
        fun logi(msg: Any) {
            Log.i(TAG, "$msg")
        }
    }

}
