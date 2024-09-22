package com.frogstore.droneapp

import android.content.Intent
import android.os.Bundle
import android.os.storage.StorageManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frogstore.droneapp.Adapters.NotificationsAdapter
import java.io.File

class NotificationsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCustomAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notifications)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

       // val itemAdapter = NotificationsAdapter(this, getItemsList(), getImageList())

       // recyclerView.adapter = itemAdapter
        loadImages()

    }

    private fun getItemsList():ArrayList<String>
    {
        val list = ArrayList<String>()
        for (i in 1..6)
        {
            list.add("Items  $i")
        }
        return list
    }


    private fun loadImages() {
        val storageManager = this.getSystemService(StorageManager::class.java)
        val storageVolume = storageManager?.storageVolumes?.get(0)
        val folderPath = storageVolume?.directory?.path + "/DCIM/pic" // Specify your folder path here
        val folder = File(folderPath)

        if (folder.exists() && folder.isDirectory) {
            // Load all image files from the folder
            val imageFiles = folder.listFiles { file ->
                file?.isFile == true && (file.name.endsWith(".jpeg", true) || file.name.endsWith(".jpg", true) || file.name.endsWith(".png", true))
            }?.map { it.absolutePath } ?: emptyList()

            val notifications = getItemsList() // Get notifications

            if (imageFiles.isNotEmpty()) {
                // Initialize the NotificationsAdapter with both lists
                recyclerView.adapter = NotificationsAdapter(this, notifications, imageFiles)

            } else {
                Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Folder not found", Toast.LENGTH_SHORT).show()
        }
    }
}