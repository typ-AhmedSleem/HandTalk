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

class HandSignRecognizer(
    val context: Context,
    var currentDelegate: Delegate = Delegate.CPU,
    var minHandDetectionConfidence: Float = DEFAULT_HAND_DETECTION_CONFIDENCE,
    var minHandTrackingConfidence: Float = DEFAULT_HAND_TRACKING_CONFIDENCE,
    var minHandPresenceConfidence: Float = DEFAULT_HAND_PRESENCE_CONFIDENCE,
    val listener: GestureRecognizerListener? = null,
) {

    private var gestureRecognizer: GestureRecognizer? = null
    val closed: Boolean
        get() = gestureRecognizer == null

    val running: Boolean
        get() = !closed

    init {
        setupGestureRecognizer()
        Log.i(TAG, "HandSignRecognizer is now fully initialized.")
    }

    fun clearGestureRecognizer() {
        gestureRecognizer?.close()
        gestureRecognizer = null
    }

    // Initialize the gesture recognizer using current settings on the
    // thread that is using it. CPU can be used with recognizers
    // that are created on the main thread and used on a background thread, but
    // the GPU delegate needs to be used on the thread that initialized the recognizer
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
            listener?.onRecognizerError(RecognizerError.OtherError(e.message))
            Log.e(TAG, "MP Task Vision failed to load the task with error: " + e.message)
        } catch (e: RuntimeException) {
            listener?.onRecognizerError(RecognizerError.GPUError(e.message))
            Log.e(TAG, "MP Task Vision failed to load the task with error: " + e.message)
        }
    }

    // Convert the ImageProxy to MP Image and feed it to GestureRecognizer.
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

    // Run hand gesture recognition using MediaPipe Gesture Recognition API
    fun recognizeSignsInFrame(imageProxy: ImageProxy) {
        val (frameTime, mpImage) = preprocessCameraFrame(imageProxy)
        gestureRecognizer?.recognizeAsync(mpImage, frameTime)
    }

    // Return the recognition result to the GestureRecognizerHelper's caller
    private fun returnLivestreamResult(result: GestureRecognizerResult, input: MPImage) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()
        listener?.onRecognizerResult(
            ResultBundle(
                result,
                inferenceTime,
                input.height,
                input.width
            )
        )
    }

    private fun returnLivestreamError(error: RuntimeException) {
        listener?.onRecognizerError(RecognizerError.UnknownError(error.message))
    }

    companion object {
        val TAG = "HandSignRecognizer ${this.hashCode()}"
        private const val MP_RECOGNIZER_TASK = "model/gesture_recognizer.task"

        const val NUM_HANDS = 2
        const val DEFAULT_HAND_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F
    }

}
