package com.typ.handtalk.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.typ.handtalk.MainViewModel
import com.typ.handtalk.core.PermissionHelper
import com.typ.handtalk.core.recognizer.GestureRecognizerListener
import com.typ.handtalk.core.recognizer.HandSignRecognizer
import com.typ.handtalk.core.recognizer.RecognizerError
import com.typ.handtalk.core.recognizer.ResultBundle
import com.typ.handtalk.core.resolvers.FrameResultResolver
import com.typ.handtalk.core.resolvers.models.FrameResult
import com.typ.handtalk.databinding.ActivitySignToTextTranslatorBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LiveSignTranslatorActivity : AppCompatActivity(), GestureRecognizerListener {

    companion object {
        private const val TAG = "SignLiveTranslator"
    }

    private lateinit var binding: ActivitySignToTextTranslatorBinding
    private lateinit var recognizer: HandSignRecognizer
    private val viewModel: MainViewModel by viewModels()

    // * Camera runtime
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService

    private fun requestRequiredPermissions() {
        PermissionHelper.requestPermissionLauncher(this) { isGranted ->
            assert(PermissionHelper.hasPermissions(this)) { "Required permissions aren't all satisfied." }
            Toast.makeText(this, "Permissions are granted.", Toast.LENGTH_SHORT).show()
        }
    }

    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()
                // Build and bind the camera use cases
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer = ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            // The analyzer can then be assigned to the instance
            .also {
                Log.i(TAG, "Setting analyzer. Recognizer initialized = ${this::recognizer.isInitialized}")
                it.setAnalyzer(backgroundExecutor, recognizer::recognizeSignsInFrame)
            }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // Bind camera to lifecycle
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup binding
        binding = ActivitySignToTextTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // * Check required permissions
        if (!PermissionHelper.hasPermissions(this)) {
            this.requestRequiredPermissions()
        }
        // Setup background executor instance
        backgroundExecutor = Executors.newSingleThreadExecutor()

        // Setup GestureRecognizer instance
        recognizer = HandSignRecognizer(
            context = this,
            minHandDetectionConfidence = viewModel.currentMinHandDetectionConfidence,
            minHandTrackingConfidence = viewModel.currentMinHandTrackingConfidence,
            minHandPresenceConfidence = viewModel.currentMinHandPresenceConfidence,
            currentDelegate = viewModel.currentDelegate,
            listener = this
        )

        if (recognizer.closed) {
            backgroundExecutor.execute(recognizer::setupGestureRecognizer)
        }

        // Setup Camera instance
        binding.viewFinder.post {
            setupCamera()
        }

        // Listeners
        binding.fabHandTalkNormalUi.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionHelper.hasPermissions(this)) {
            this.requestRequiredPermissions()
        }
        // Start the recognizer again when users come back to foreground.
        backgroundExecutor.execute {
            if (recognizer.closed) recognizer.setupGestureRecognizer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::recognizer.isInitialized) {
            viewModel.setMinHandDetectionConfidence(recognizer.minHandDetectionConfidence)
            viewModel.setMinHandTrackingConfidence(recognizer.minHandTrackingConfidence)
            viewModel.setMinHandPresenceConfidence(recognizer.minHandPresenceConfidence)
            viewModel.setDelegate(recognizer.currentDelegate)
            // Close the HandSignRecognizer instance and release resources
            backgroundExecutor.execute {
                recognizer.clearGestureRecognizer()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Shut down the background executor
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = binding.viewFinder.display.rotation
    }

    private var lastResult: FrameResult? = null
    override fun onRecognizerResult(resultBundle: ResultBundle) {
        runOnUiThread {
            // Show result of recognized gesture
            val rawResult = resultBundle.rawResult
            val frameResult = FrameResultResolver.resolve(rawResult)

            if (frameResult != lastResult) {
                // * Different results. Do checks first
                lastResult?.let {
                    Log.i(
                        TAG,
                        """FrameResult changed
                        LEFT: ${it.leftHandSign?.label} -> ${frameResult.leftHandSign?.label}
                        RIGHT: ${it.rightHandSign?.label} -> ${frameResult.rightHandSign?.label}
                        """.trimIndent()
                    )
                }
                lastResult = frameResult
            } else {
                // * Same frame. Check for movement using OpticalFlow algorithm
            }

            // Pass necessary information to OverlayView for drawing on the canvas
            binding.overlay.setResults(
                rawResult,
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM
            )

            // Force a redraw
            binding.overlay.invalidate()
        }
    }

    override fun onRecognizerError(error: RecognizerError) {
        runOnUiThread {
            when (error) {
                is RecognizerError.GPUError -> {
                    Log.e(TAG, "onRecognizerError::GPUError => $error")
                    viewModel.setDelegate(Delegate.CPU)
                    recognizer.currentDelegate = viewModel.currentDelegate
                    if (recognizer.running) {
                        recognizer.clearGestureRecognizer()
                        recognizer.setupGestureRecognizer()
                    }
                }

                is RecognizerError.OtherError -> {
                    Log.e(TAG, "onRecognizerError::OtherError => $error")
                }

                else -> {
                    Log.e(TAG, "onRecognizerError::UnknownError => $error")
                }
            }
        }
    }

}