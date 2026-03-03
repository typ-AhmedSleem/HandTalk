package com.typ.handtalk.impl.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.typ.handtalk.domain.models.HandTalkEvent
import com.typ.handtalk.impl.HandSignRecognizer
import com.typ.handtalk.impl.R
import com.typ.handtalk.impl.ui.overlay.OverlayView
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Activity that displays the camera preview with a hand landmark overlay.
 * Feeds frames to the [HandSignRecognizer] and reactively finishes when
 * [HandTalkEvent.StopRecognizer] is collected.
 */
class HandTalkRecognizerActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HandTalkRecognizerAct"
    }

    private val recognizer: HandSignRecognizer by inject()

    private lateinit var viewFinder: PreviewView
    private lateinit var overlay: OverlayView

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var preview: Preview? = null
    private lateinit var backgroundExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handtalk_recognizer)

        viewFinder = findViewById(R.id.view_finder)
        overlay = findViewById(R.id.overlay)

        backgroundExecutor = Executors.newSingleThreadExecutor()

        if (!recognizer.isInitialized) {
            backgroundExecutor.execute { recognizer.setup() }
        }

        viewFinder.post { setupCamera() }

        observeStopEvent()
        observeFrameState()
    }

    /**
     * Reactively finish this activity when [HandTalkEvent.StopRecognizer] is collected.
     */
    private fun observeStopEvent() {
        lifecycleScope.launch {
            recognizer.handtalkEvents.collect { event ->
                if (event is HandTalkEvent.StopRecognizer) {
                    finish()
                }
            }
        }
    }

    /**
     * Update the landmark overlay whenever a new [FrameResult] arrives.
     */
    private fun observeFrameState() {
        lifecycleScope.launch {
            recognizer.frameState.collect { frameResult ->
                if (frameResult != null) {
                    overlay.drawLandmarks(
                        frameResult,
                        imageHeight = viewFinder.height,
                        imageWidth = viewFinder.width
                    )
                } else {
                    overlay.clear()
                }
            }
        }
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: return
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        // Camera preview
        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(viewFinder.display.rotation)
            .build()

        // Image analysis for frame processing
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(viewFinder.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also {
                it.setAnalyzer(backgroundExecutor) { imageProxy ->
                    recognizer.recognize(imageProxy)
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundExecutor.shutdown()
    }
}