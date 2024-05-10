package com.typ.handtalk.ui.s2a

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
import com.typ.handtalk.MainViewModel
import com.typ.handtalk.R
import com.typ.handtalk.core.algorithms.handtalk.HandTalkAlgorithm
import com.typ.handtalk.core.algorithms.handtalk.HandTalkAlgorithmCallback
import com.typ.handtalk.core.algorithms.recognizer.RecognizerError
import com.typ.handtalk.core.errors.HandTalkError
import com.typ.handtalk.core.models.ImageShape
import com.typ.handtalk.core.models.Sentence
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.perms.PermissionHelper
import com.typ.handtalk.core.resolvers.models.FrameResult
import com.typ.handtalk.core.tts.TextSpeaker
import com.typ.handtalk.databinding.ActivitySignToTextTranslatorBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LiveSignTranslatorActivity : AppCompatActivity(), HandTalkAlgorithmCallback {

    companion object {
        private const val TAG = "SignLiveTranslator"
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var speaker: TextSpeaker
    private lateinit var algoHandTalk: HandTalkAlgorithm
    private lateinit var binding: ActivitySignToTextTranslatorBinding

    // * Camera runtime
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService

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
                it.setAnalyzer(backgroundExecutor, algoHandTalk::recognizeHandGestures)
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
        ensurePermissionsGranted()
        // Setup background executor instance
        backgroundExecutor = Executors.newSingleThreadExecutor()
        // * Setup HandTalkAlgorithm instance
        algoHandTalk = HandTalkAlgorithm(this, this)

        // * Initialize the TextSpeaker
        speaker = TextSpeaker().apply {
            this.initializeEngine(this@LiveSignTranslatorActivity) {
                speaker.speak("مرحباً بكم في HandTalk")
            }
        }

        // * Initialize HandTalk recognizer
        if (!algoHandTalk.recognizerInitialized) {
            backgroundExecutor.execute(algoHandTalk::setupGestureRecognizer)
        }

        // Setup Camera instance
        binding.viewFinder.post {
            setupCamera()
        }

    }

    private fun ensurePermissionsGranted() {
        if (!PermissionHelper.requiredPermissionsGranted(this)) {
            Toast.makeText(this, getString(R.string.camera_perm_required), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        ensurePermissionsGranted()
        // Start the recognizer again when users come back to foreground.
        backgroundExecutor.execute {
            if (algoHandTalk.recognizer.closed) algoHandTalk.setupGestureRecognizer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::algoHandTalk.isInitialized) {
            viewModel.setDelegate(algoHandTalk.recognizer.currentDelegate)
            viewModel.setMinHandDetectionConfidence(algoHandTalk.recognizer.minHandDetectionConfidence)
            viewModel.setMinHandTrackingConfidence(algoHandTalk.recognizer.minHandTrackingConfidence)
            viewModel.setMinHandPresenceConfidence(algoHandTalk.recognizer.minHandPresenceConfidence)
            // Close the HandSignRecognizer instance and release resources
            backgroundExecutor.execute {
                algoHandTalk.recognizer.clearGestureRecognizer()
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

    // REGION: HandTalkAlgorithmCallback

    override fun onHandsDisappear() {
        runOnUiThread {
            binding.tvRightGesture.text = null
            binding.tvLeftGesture.text = null
            binding.tvInterpretedText.text = null
            binding.overlay.clear()
        }
    }

    override fun onIdentifyGesture(frameResult: FrameResult) {
        runOnUiThread {
            if (!frameResult.isRhsNone) {
                binding.tvRightGesture.text = frameResult.rhsLabel
            }
            if (!frameResult.isLhsNone) {
                binding.tvLeftGesture.text = frameResult.lhsLabel
            }
        }
    }

    override fun onIdentifyNewWord(word: Word) {
        runOnUiThread {
            val sentence = binding.tvInterpretedText.text.toString() + " " + word.arabicText
            binding.tvInterpretedText.text = sentence
            // * Try to speak the word
            speaker.speak(word.arabicText)
        }
    }

    override fun onTranslateFullSentence(sentence: Sentence) {
        runOnUiThread {
            // * Display the translation
            startActivity(Intent(this, DisplayTranslationActivity::class.java).apply {
                putExtra(DisplayTranslationActivity.EXTRA_TRANSLATION, sentence.arabic)
            })
        }
    }

    override fun onReadyToDrawLandmarks(frameResult: FrameResult, inputShape: ImageShape) {
        runOnUiThread {
            binding.overlay.drawLandmarks(
                frameResult,
                inputShape.height,
                inputShape.width,
            )
        }
    }

    override fun onErrorOccurred(error: HandTalkError) {
        runOnUiThread {
            when (error) {
                is RecognizerError.GPUError -> {
                    Log.e(TAG, "onRecognizerError::GPUError => $error")
                    viewModel.setDelegate(Delegate.CPU)
                    algoHandTalk.recognizer.currentDelegate = viewModel.currentDelegate
                    if (algoHandTalk.recognizer.running) {
                        algoHandTalk.recognizer.clearGestureRecognizer()
                        algoHandTalk.setupGestureRecognizer()
                    }
                }

                is RecognizerError.OtherError -> {
                    Log.e(TAG, "onRecognizerError::OtherError => $error")
                    errorHasOccurred()
                }

                else -> {
                    Log.e(TAG, "onRecognizerError::UnknownError => $error")
                    errorHasOccurred()
                }
            }
        }
    }

    // END: HandTalkAlgorithmCallback

    private fun errorHasOccurred() {
        Toast.makeText(this, "Error has occurred. Restart the app", Toast.LENGTH_SHORT).show()
        finish()
    }

}