package com.frogstore.droneapp.Fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.frogstore.droneapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ControllerFragment : Fragment() {
    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var statusTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_controller, container, false)

        surfaceView = layout.findViewById(R.id.surfaceView)
        surfaceHolder = surfaceView.holder
        statusTextView = layout.findViewById(R.id.statusTextView)

        // Replace with the actual IP address of your ESP32-CAM
        streamVideo("192.168.101.113", 80)

        // Return the inflated layout
        return layout
    }

    private fun streamVideo(ip: String, port: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "http://$ip:$port/"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                // Update UI to show connected status
                withContext(Dispatchers.Main) {
                    statusTextView.text = "Connected"
                }

                val inputStream: InputStream = connection.inputStream
                val buffer = ByteArray(1024 * 20) // Buffer to store incoming bytes
                var bytesRead: Int

                while (true) {
                    bytesRead = inputStream.read(buffer) // Read the incoming stream
                    if (bytesRead > 0) {
                        val bitmap = BitmapFactory.decodeByteArray(buffer, 0, bytesRead)
                        if (bitmap != null) {
                            withContext(Dispatchers.Main) {
                                val canvas = surfaceHolder.lockCanvas()
                                if (canvas != null) {
                                    canvas.drawColor(
                                        0,
                                        android.graphics.PorterDuff.Mode.CLEAR
                                    ) // Clear canvas

                                    // Calculate scaling
                                    val surfaceWidth = surfaceView.width
                                    val surfaceHeight = surfaceView.height
                                    val bitmapWidth = bitmap.width
                                    val bitmapHeight = bitmap.height

                                    val scaleX = surfaceWidth.toFloat() / bitmapWidth
                                    val scaleY = surfaceHeight.toFloat() / bitmapHeight
                                    val scale = Math.min(scaleX, scaleY)

                                    // Calculate the new width and height
                                    val newWidth = (bitmapWidth * scale).toInt()
                                    val newHeight = (bitmapHeight * scale).toInt()

                                    // Center the bitmap on the canvas
                                    val x = (surfaceWidth - newWidth) / 2
                                    val y = (surfaceHeight - newHeight) / 2

                                    canvas.drawBitmap(bitmap, x.toFloat(), y.toFloat(), null)
                                    surfaceHolder.unlockCanvasAndPost(canvas)
                                }
                            }
                        }
                    } else {
                        // If bytesRead is 0 or negative, it means the stream is closed
                        break
                    }
                    delay(100) // Manage frame rate if necessary
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    statusTextView.text = "Connection Failed: ${e.message}"
                }
            }
        }
    }

}