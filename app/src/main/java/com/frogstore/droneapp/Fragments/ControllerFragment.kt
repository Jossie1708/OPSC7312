package com.frogstore.droneapp.Fragments

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.frogstore.droneapp.R
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.coroutineContext
import java.io.OutputStreamWriter
import java.net.Socket
import androidx.fragment.app.activityViewModels
import com.frogstore.droneapp.SharedViewModel


class ControllerFragment : Fragment() {
    private lateinit var textureView: TextureView
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var statusTextView: TextView
    private var streamJob: Job? = null
    private var x = 0.0f
    private var y = 0.0f
    private var z = 0.0f
    private var isIncreasing = false
    private var isDecreasing = false
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_controller, container, false)

        // Set up control buttons
        layout.findViewById<ImageButton>(R.id.button_left).setOnClickListener { x -= 0.1f; sendThrottleValues() }
        layout.findViewById<ImageButton>(R.id.button_right).setOnClickListener { x += 0.1f; sendThrottleValues() }
        layout.findViewById<ImageButton>(R.id.button_forward).setOnClickListener { y += 0.1f; sendThrottleValues() }
        layout.findViewById<ImageButton>(R.id.button_back).setOnClickListener { y -= 0.1f; sendThrottleValues() }
        layout.findViewById<ImageButton>(R.id.button_up).setOnClickListener { z += 0.1f; sendThrottleValues() }
        layout.findViewById<ImageButton>(R.id.button_down).setOnClickListener { z -= 0.1f; sendThrottleValues() }
// Example: Trigger check when `statusTextView` text changes
        statusTextView.text = "Connected"
//        checkConnectionAndUpdateLog()
//
//        textureView = layout.findViewById(R.id.textureView)
//        statusTextView = layout.findViewById(R.id.statusTextView)
//
//        textureView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//
//        streamVideo("192.168.101.112", 80)
//        val rotateButton: ImageButton = layout.findViewById(R.id.btnConFullScreen)
//        rotateButton.setOnClickListener {
//            toggleOrientation(rotateButton)
//
//        }

        return layout
    }

//    private fun checkConnectionAndUpdateLog() {
//        if (statusTextView.text == "Connected") {
//            SharedViewModel.("Connected")
//        }
//    }

    private fun sendThrottleValues() {
        // Calculate throttle values for each motor based on x, y, and z
        val throttleValues = mapToThrottle(x, y, z)

        // Log the throttle values
        Log.d("ThrottleValues", "Throttle Motor 1: ${throttleValues[0]}, " +
                "Throttle Motor 2: ${throttleValues[1]}, " +
                "Throttle Motor 3: ${throttleValues[2]}, " +
                "Throttle Motor 4: ${throttleValues[3]}")

//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val socket = Socket("192.168.1.100", 80)  // Replace with Arduino's IP and port
//                val writer = OutputStreamWriter(socket.getOutputStream())
//                val json = """
//                {
//                    "motor1": ${throttleValues[0]},
//                    "motor2": ${throttleValues[1]},
//                    "motor3": ${throttleValues[2]},
//                    "motor4": ${throttleValues[3]}
//                }
//            """.trimIndent()
//                writer.write(json)
//                writer.flush()
//                writer.close()
//                socket.close()
//            } catch (e: Exception) {
//                Log.e("ControllerFragment", "Error sending throttle values", e)
//            }
//        }
    }

    private fun mapToThrottle(x: Float, y: Float, z: Float): IntArray {
        val baseThrottle = (z * 500 + 1500).toInt()
        val rollInfluence = (x * 200).toInt()
        val pitchInfluence = (y * 200).toInt()

        val throttleMotor1 = baseThrottle + rollInfluence - pitchInfluence
        val throttleMotor2 = baseThrottle - rollInfluence - pitchInfluence
        val throttleMotor3 = baseThrottle + rollInfluence + pitchInfluence
        val throttleMotor4 = baseThrottle - rollInfluence + pitchInfluence

        return intArrayOf(
            throttleMotor1.coerceIn(1000, 2000),
            throttleMotor2.coerceIn(1000, 2000),
            throttleMotor3.coerceIn(1000, 2000),
            throttleMotor4.coerceIn(1000, 2000)
        )
    }


    private fun toggleOrientation(rotateButton: ImageButton) {
        val activity = activity
        if (activity != null) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                rotateButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_exit_fullscreen))
            } else {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                rotateButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_full_screen))
            }
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        streamJob?.cancel() // Cancel streaming if the view is destroyed
//    }
//
//    // Adjusted streamVideo function
//    private fun streamVideo(ip: String, port: Int) {
//        streamJob = CoroutineScope(Dispatchers.IO).launch {
//            val url = "http://$ip:$port/"
//            try {
//                while (isActive) {  // Direct use of isActive within coroutine scope
//                    val connection = URL(url).openConnection() as HttpURLConnection
//                    connection.doInput = true
//                    connection.connect()
//
//                    withContext(Dispatchers.Main) {
//                        statusTextView.text = "Connected"
//                    }
//
//                    val inputStream = connection.inputStream
//                    processStream(inputStream)
//
//                    connection.disconnect()
//                    delay(100)
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    statusTextView.text = "Error: ${e.message}"
//                }
//                Log.e("ControllerFragment", "Error streaming video", e)
//            }
//        }
//    }
//
//    // Adjusted processStream function
//    private suspend fun processStream(inputStream: InputStream) {
//        val buffer = ByteArray(4096)
//        val byteArrayOutputStream = ByteArrayOutputStream()
//
//        while (coroutineContext.isActive) {  // Use coroutineContext.isActive
//            try {
//                val bytesRead = inputStream.read(buffer)
//                if (bytesRead <= 0) break
//
//                byteArrayOutputStream.write(buffer, 0, bytesRead)
//                val byteArray = byteArrayOutputStream.toByteArray()
//                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//
//                if (bitmap != null) {
//                    withContext(Dispatchers.Main) {
//                        displayFrame(bitmap)
//                    }
//                }
//                byteArrayOutputStream.reset()
//            } catch (e: Exception) {
//                Log.e("ControllerFragment", "Error processing stream", e)
//                withContext(Dispatchers.Main) {
//                    statusTextView.text = "Stream error: ${e.message}"
//                }
//                break
//            }
//        }
//        inputStream.close()
//    }
//
//
//
//    private fun displayFrame(bitmap: Bitmap) {
//        val canvas = textureView.lockCanvas() ?: return
//        try {
//            canvas.drawColor(0, PorterDuff.Mode.CLEAR)
//            val scaleX = textureView.width.toFloat() / bitmap.width
//            val scaleY = textureView.height.toFloat() / bitmap.height
//            val scale = maxOf(scaleX, scaleY)
//            val newWidth = (bitmap.width * scale).toInt()
//            val newHeight = (bitmap.height * scale).toInt()
//            val left = (textureView.width - newWidth) / 2f
//            val top = (textureView.height - newHeight) / 2f
//
//            canvas.drawBitmap(bitmap, null, RectF(left, top, left + newWidth, top + newHeight), null)
//        } finally {
//            textureView.unlockCanvasAndPost(canvas)
//        }
//    }
}
