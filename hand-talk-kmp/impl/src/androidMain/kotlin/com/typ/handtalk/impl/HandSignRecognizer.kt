package com.typ.handtalk.impl

import android.os.SystemClock
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.framework.image.BitmapImageBuilder
import androidx.camera.core.ImageProxy
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.typ.handtalk.domain.models.FrameResult
import com.typ.handtalk.domain.models.HandTalkEvent
import com.typ.handtalk.domain.models.RecognizerError
import com.typ.handtalk.impl.resolvers.FrameResultResolver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import android.content.Context

actual class HandSignRecognizer(
    val context: Context,
    private val delegate: Delegate = Delegate.CPU,
    val minHandDetectionConfidence: Float = 0.5f,
    val minHandTrackingConfidence: Float = 0.5f,
    val minHandPresenceConfidence: Float = 0.5f,
) {
    var isInitialized: Boolean = false
        private set

    val closed: Boolean get() = gestureRecognizer == null
    val running: Boolean get() = gestureRecognizer != null
    var currentDelegate: Delegate = delegate

    private var gestureRecognizer: GestureRecognizer? = null

    private val _frameState = MutableStateFlow<FrameResult?>(null)
    actual val frameState: StateFlow<FrameResult?> = _frameState.asStateFlow()

    private val _handtalkEvents = MutableSharedFlow<HandTalkEvent>(extraBufferCapacity = 64)
    actual val handtalkEvents: SharedFlow<HandTalkEvent> = _handtalkEvents.asSharedFlow()

    private val _errors = MutableSharedFlow<RecognizerError>(extraBufferCapacity = 8)
    actual val errors: SharedFlow<RecognizerError> = _errors.asSharedFlow()

    private var lastFrameResult: FrameResult? = null

    /** Allows the [HandTalk] facade to emit events (e.g., [HandTalkEvent.StopRecognizer]). */
    fun emitEvent(event: HandTalkEvent) {
        _handtalkEvents.tryEmit(event)
    }

    actual fun setup() {
        val baseOptionsBuilder = BaseOptions.builder()
            .setDelegate(delegate)
            .setModelAssetPath("model/handtalk-model-v1.task")

        try {
            val optionsBuilder = GestureRecognizer.GestureRecognizerOptions.builder()
                .setBaseOptions(baseOptionsBuilder.build())
                .setMinHandDetectionConfidence(minHandDetectionConfidence)
                .setMinTrackingConfidence(minHandTrackingConfidence)
                .setMinHandPresenceConfidence(minHandPresenceConfidence)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener(this::onResult)
                .setErrorListener(this::onError)

            gestureRecognizer = GestureRecognizer.createFromOptions(context, optionsBuilder.build())
            isInitialized = true
        } catch (e: Exception) {
            _errors.tryEmit(RecognizerError.ModelLoadError(e.message))
        }
    }

    fun clearGestureRecognizer() = release()

    fun recognize(imageProxy: ImageProxy) {
        val mpImage = BitmapImageBuilder(imageProxy.toBitmap()).build()
        gestureRecognizer?.recognizeAsync(mpImage, SystemClock.uptimeMillis())
        imageProxy.close()
    }

    private fun onResult(result: GestureRecognizerResult, image: MPImage) {
        val frameResult = FrameResultResolver.resolve(result)
        _frameState.value = frameResult

        // Logic for emitting HandTalkEvents
        processEvents(frameResult)

        lastFrameResult = frameResult
    }

    private fun processEvents(newFrame: FrameResult) {
        val prevFrame = lastFrameResult

        if (prevFrame == null) {
            if (!newFrame.isRightNullOrNone() || !newFrame.isLeftNullOrNone()) {
                _handtalkEvents.tryEmit(HandTalkEvent.SignRecognized(newFrame.lhs, newFrame.rhs))
            }
            return
        }

        // Logic for SignChanged, HandsDisappeared, etc.
        val handsVisible = !newFrame.isRightNullOrNone() || !newFrame.isLeftNullOrNone()
        val prevHandsVisible = !prevFrame.isRightNullOrNone() || !prevFrame.isLeftNullOrNone()

        if (!handsVisible && prevHandsVisible) {
            _handtalkEvents.tryEmit(HandTalkEvent.HandsDisappeared)
        } else if (handsVisible && !prevHandsVisible) {
            _handtalkEvents.tryEmit(HandTalkEvent.SignRecognized(newFrame.lhs, newFrame.rhs))
        } else if (handsVisible) {
            // Check for sign changes with timeout logic if needed, 
            // but for lightweight we focus on simple changes
            if (newFrame.rhs != prevFrame.rhs || newFrame.lhs != prevFrame.lhs) {
                // Could emit SignChanged if we track specifically which hand changed
                // For now, emit a new SignRecognized as a catch-all or specific SignChanged if it's one hand
                val prevRHS = prevFrame.rhs
                val newRHS = newFrame.rhs
                if (newRHS != prevRHS && prevRHS != null && newRHS != null) {
                    _handtalkEvents.tryEmit(HandTalkEvent.SignChanged(prevRHS, newRHS))
                } else {
                    _handtalkEvents.tryEmit(HandTalkEvent.SignRecognized(newFrame.lhs, newFrame.rhs))
                }
            }
        }
    }

    private fun onError(error: RuntimeException) {
        _errors.tryEmit(RecognizerError.UnknownError(error.message))
    }

    actual fun release() {
        gestureRecognizer?.close()
        gestureRecognizer = null
        isInitialized = false
    }
}
