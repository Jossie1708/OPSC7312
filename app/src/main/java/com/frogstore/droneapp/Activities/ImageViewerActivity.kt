package com.frogstore.droneapp.Activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewpager2.widget.ViewPager2
import com.frogstore.droneapp.Adapters.ImageViewerAdapter
import com.frogstore.droneapp.R
import java.io.File

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var imageAdapter: ImageViewerAdapter
    private lateinit var imagePaths: List<String>

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.toolsbarColor)
            window.statusBarColor = ContextCompat.getColor(this, R.color.toolsbarColor)
        }
        viewPager = findViewById(R.id.viewPager)

        imagePaths = intent.getStringArrayListExtra("imagePaths") ?: arrayListOf()
        val position = intent.getIntExtra("position", 0)

        imageAdapter = ImageViewerAdapter(this, imagePaths)
        viewPager.adapter = imageAdapter
        viewPager.setCurrentItem(position, false) // Set the current item without animation

        // Set up the share button click listener
        findViewById<Button>(R.id.btnShare).setOnClickListener {
            shareImage(position)
        }
    }

    private fun shareImage(position: Int) {
        val imagePath = imagePaths[position]
        val file = File(imagePath)
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (intent.resolveActivity(packageManager) == null) {
            Toast.makeText(this, "Please install a compatible app to share images.", Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(Intent.createChooser(intent, "Share Image"))
    }
}
