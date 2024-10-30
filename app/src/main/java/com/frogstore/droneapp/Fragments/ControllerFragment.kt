package com.frogstore.droneapp.Fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.frogstore.droneapp.R
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.coroutineContext

class ControllerFragment : Fragment() {
    private lateinit var textureView: TextureView
    private lateinit var statusTextView: TextView
    private var streamJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_controller, container, false)
//
//        textureView = layout.findViewById(R.id.textureView)
//        statusTextView = layout.findViewById(R.id.statusTextView)
//
//        textureView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//
//        streamVideo("192.168.101.112", 80)

        return layout
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
