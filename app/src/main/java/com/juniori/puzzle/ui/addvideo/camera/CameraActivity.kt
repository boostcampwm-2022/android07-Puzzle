package com.juniori.puzzle.ui.addvideo.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.ActivityCameraBinding
import com.juniori.puzzle.ui.addvideo.AddVideoBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraExecutor: ExecutorService
    private var progressJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkCameraPermissions()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.buttonCameraCapture.setOnClickListener { captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (checkCameraPermissions()) {
                startCamera()
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.camera_no_permission,
                    Snackbar.LENGTH_SHORT
                ).show()
                onBackPressed()
            }
        }

        binding.buttonCameraCapture.setOnClickListener {
            captureVideo()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkCameraPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(baseContext, permission) ==
                PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewCamera.surfaceProvider)
                }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    videoCapture
                )
            } catch (e: Exception) {
                Log.e(TAG, "바인딩 실패", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("MissingPermission")
    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        binding.buttonCameraCapture.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            progressJob?.cancel()
            binding.progressCamera.setProgressCompat(0, false)
            binding.progressCamera.isVisible = false
            curRecording.stop()
            recording = null
            return
        }

        binding.progressCamera.isVisible = true
        progressJob = lifecycleScope.launch(Dispatchers.IO) {
            for (i in 1..20) {
                delay(1000)
                withContext(Dispatchers.Main) {
                    binding.progressCamera.setProgressCompat(i * 100 / 20, true)
                }
            }
            recording?.stop()
            recording = null
        }

        val file = File(cacheDir, "${System.currentTimeMillis()}.mp4")
        val fileOutputOptions = FileOutputOptions
            .Builder(file)
            .build()

        recording = videoCapture.output
            .prepareRecording(this, fileOutputOptions)
            .apply {
                withAudioEnabled()
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.buttonCameraCapture.apply {
                            setBackgroundResource(R.drawable.camera_button_recording)
                            isEnabled = true
                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            Snackbar.make(
                                binding.root,
                                R.string.camera_video_saved,
                                Snackbar.LENGTH_SHORT
                            ).show()
                            setVideoNameInActivityResult(file.path)
                            finish()
                        } else {
                            recording?.close()
                            recording = null
                        }
                        binding.buttonCameraCapture.apply {
                            setBackgroundResource(R.drawable.camera_button)
                            isEnabled = true
                        }
                    }
                }
            }
    }

    private fun setVideoNameInActivityResult(videoName: String) {
        Intent().also { intent ->
            intent.putExtra(AddVideoBottomSheet.VIDEO_NAME_KEY, videoName)
            setResult(RESULT_OK, intent)
        }
    }

    companion object {
        private const val TAG = "CameraActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).toTypedArray()
    }
}
