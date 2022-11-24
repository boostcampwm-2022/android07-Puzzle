package com.juniori.puzzle.ui.addvideo.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.ActivityCameraBinding
import com.juniori.puzzle.ui.addvideo.AddVideoBottomSheet
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
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
        grantResults:
            IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (checkCameraPermissions()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "권한이 없오리",
                    Toast.LENGTH_SHORT
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
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
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

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        binding.buttonCameraCapture.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            progressJob?.cancel()
            binding.progressCamera.setProgressCompat(0,false)
            binding.progressCamera.isVisible = false
            curRecording.stop()
            recording = null
            return
        }

        binding.progressCamera.isVisible = true
        progressJob = lifecycleScope.launch(Dispatchers.IO) {
            for(i in 1..20){
                delay(1000)
                withContext(Dispatchers.Main){
                    binding.progressCamera.setProgressCompat(i*100/20, true)
                }
            }
            recording?.stop()
            recording = null
        }

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREA) // todo 이름
            .format(System.currentTimeMillis())

        val fileOutputOptions = FileOutputOptions
            .Builder(File(cacheDir, "$name.mp4"))
            .build()

        recording = videoCapture.output
            .prepareRecording(this, fileOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(
                        this@CameraActivity,
                        Manifest.permission.RECORD_AUDIO
                    ) ==
                    PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.buttonCameraCapture.apply {
                            text = getString(R.string.camera_capture_stop)
                            isEnabled = true
                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "비디오가 저장되었습니다 :  " +
                                "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()

                            val intent = Intent().apply {
                                putExtra(AddVideoBottomSheet.VIDEO_NAME_KEY, name)
                            }
                            setResult(RESULT_OK, intent)
                            finish()
                        } else {
                            recording?.close()
                            recording = null
                        }
                        binding.buttonCameraCapture.apply {
                            text = getString(R.string.camera_capture_start)
                            isEnabled = true
                        }
                    }
                }
            }
    }

    companion object {
        private const val TAG = "CameraActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}
