package com.frogstore.droneapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        viewPager = findViewById(R.id.viewPager)

        val imagePaths = intent.getStringArrayListExtra("imagePaths") ?: arrayListOf()
        val position = intent.getIntExtra("position", 0)

        // Set up the adapter with a simple onClick that could close the viewer
        imageAdapter = ImageAdapter(this, imagePaths) { clickedPosition ->
            // Handle image click if necessary, for example, finish the activity
            finish()
        }

        viewPager.adapter = imageAdapter
        viewPager.setCurrentItem(position, false) // Set the current item without animation
    }
}
