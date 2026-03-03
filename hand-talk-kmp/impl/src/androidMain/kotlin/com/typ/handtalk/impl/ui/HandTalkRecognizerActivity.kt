package com.typ.handtalk.impl.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.typ.handtalk.domain.models.HandTalkEvent
import com.typ.handtalk.impl.HandSignRecognizer
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A dedicated activity bridge that handles CameraX setup and feeds frames to the [HandSignRecognizer].
 * It has no UI — it is intended to be used as a background processing layer for Compose Multiplatform.
 *
 * The recognizer is injected via Koin and the activity reactively finishes itself when it
 * collects a [HandTalkEvent.StopRecognizer] event from the shared flow.
 */
class HandTalkRecognizerActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HandTalkRecognizerAct"
    }

    /** Injected via Koin — same singleton instance used everywhere. */
    private val recognizer: HandSignRecognizer by inject()

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var backgroundExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundExecutor = Executors.newSingleThreadExecutor()

        // Setup the recognizer if it hasn't been set up yet
        if (!recognizer.isInitialized) {
            backgroundExecutor.execute { recognizer.setup() }
        }

        setupCamera()
        observeStopEvent()
    }

    /**
     * Collect [HandTalkEvent.StopRecognizer] from the shared flow.
     * When received, the activity finishes itself — no static references needed.
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

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
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
            cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalyzer)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundExecutor.shutdown()
    }
}