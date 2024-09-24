package com.frogstore.droneapp

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.frogstore.droneapp.Adapters.ImageViewerAdapter

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var imageAdapter: ImageViewerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.toolsbarColor)
            window.statusBarColor = ContextCompat.getColor(this, R.color.toolsbarColor)
        }
        viewPager = findViewById(R.id.viewPager)

        val imagePaths = intent.getStringArrayListExtra("imagePaths") ?: arrayListOf()
        val position = intent.getIntExtra("position", 0)

        // Use the new ImageViewerAdapter for ViewPager2
        imageAdapter = ImageViewerAdapter(this, imagePaths)

        viewPager.adapter = imageAdapter
        viewPager.setCurrentItem(position, false) // Set the current item without animation
    }
}

