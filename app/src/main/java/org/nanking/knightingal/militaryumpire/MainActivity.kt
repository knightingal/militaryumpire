package org.nanking.knightingal.militaryumpire

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.internal.utils.ImageUtil
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.nanking.knightingal.militaryumpire.bean.Chequer
import org.nanking.knightingal.militaryumpire.bean.OcrResponse
import org.nanking.knightingal.militaryumpire.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var player: String? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        player = intent.getStringExtra("player")

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                @SuppressLint("RestrictedApi")
                @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val imageBytes = ImageUtil.jpegImageToJpegByteArray(image)
                    image.close()

                    val rectWidth = viewBinding.capCover.coverWidth;
                    val capBitmap: Bitmap =
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    Log.d("size", "image size: ${capBitmap.width} * ${capBitmap.height}")
                    Log.d("size", "preview size: ${viewBinding.viewFinder.width} * ${viewBinding.viewFinder.height}")
                    val capRectWidth = capBitmap.width / (viewBinding.viewFinder.height / rectWidth)
                    val miniBitmap: Bitmap = Bitmap.createBitmap(
                        capBitmap,
                        capBitmap.width / 2 - capRectWidth / 2,
                        capBitmap.height / 2 - capRectWidth / 2,
                        capRectWidth,
                        capRectWidth)

                    val out = ByteArrayOutputStream()
                    val success = miniBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    if (!success) {
                        throw Exception("Encode bitmap failed.")
                    }
                    miniBitmap.recycle()
                    val miniByteArray = out.toByteArray()

                    Log.d("PIC", "image length ${miniByteArray.size}")
                    Thread(kotlinx.coroutines.Runnable {
                        val client = OkHttpClient()
                        val binaryType: MediaType = "image/jpg".toMediaType()
                        val body = miniByteArray.toRequestBody(binaryType)
                        val request = Request.Builder().url("http://192.168.2.12:8000")
                            .post(body).build()
                        val response = client.newCall(request).execute()
                        val code = response.code
                        val respBody = response.body!!.string()
                        Log.d("PIC", "upload pic resp $code")
                        Log.d("PIC", "upload pic resp $respBody")
                        val ocrRespList = Json.decodeFromString<List<OcrResponse>>(respBody)
                        if (ocrRespList.size == 1) {
                            val ocrResponse = ocrRespList[0]
                            try {
                                val chequer = Chequer.valueOf(ocrResponse.text)
                                val intent = Intent()
                                intent.putExtra("ocr", chequer.name)
                                intent.putExtra("player", player)
                                val coverWidth = viewBinding.capCover.coverWidth
                                intent.putExtra("coverWidth", coverWidth)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            } catch (_: IllegalArgumentException) {
                            }
                        }
                    }).start()
                }
            }
        )
    }

    private fun captureVideo() {}

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            val viewFinder = viewBinding.viewFinder
            val size = Size(viewFinder.width, viewFinder.height)
            imageCapture = ImageCapture.Builder()
//                .setTargetResolution(size)
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
            startSchedulePicture()

        }, ContextCompat.getMainExecutor(this))
    }

    private fun startSchedulePicture() {
        Thread {
            while(true) {
                Thread.sleep(500)
                takePhoto()
            }
        }
//            .start()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("Main", "onBackPressed")
        val coverWidth = viewBinding.capCover.coverWidth
        Log.d("Main", "coverWidth=${coverWidth}")
        val intent = Intent()
        intent.putExtra("coverWidth", coverWidth)
        setResult(Activity.RESULT_OK, intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}