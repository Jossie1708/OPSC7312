package com.frogstore.droneapp


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.storage.StorageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GalleryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    private lateinit var recyclerViewCustomAdapter: ImageAdapter
    private lateinit var layoutManager: GridLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_gallery, container, false)


        recyclerView = layout.findViewById(R.id.recyclerView)
        layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = layoutManager

        loadImagesFromFolder()


        return layout
    }

    private fun loadImagesFromFolder() {
        val storageManager = requireContext().getSystemService(StorageManager::class.java)
        val storageVolume = storageManager?.storageVolumes?.get(0)
        val folderPath = storageVolume?.directory?.path + "/DCIM/100PINT/Pins" // Specify your folder path here
        val folder = File(folderPath)

        if (folder.exists() && folder.isDirectory) {
            // Load all image files from the folder
            val imageFiles = folder.listFiles { file ->
                file?.isFile == true && (file.name.endsWith(".jpeg", true) || file.name.endsWith(".jpg", true) || file.name.endsWith(".png", true))
            }?.map { it.absolutePath } ?: emptyList()

            if (imageFiles.isNotEmpty()) {
                // Initialize the ImageAdapter with a click listener to open the ImageViewerActivity
                recyclerViewCustomAdapter = ImageAdapter(requireContext(), imageFiles) { position ->
                    val intent = Intent(requireContext(), ImageViewerActivity::class.java).apply {
                        putStringArrayListExtra("imagePaths", ArrayList(imageFiles))
                        putExtra("position", position)
                    }
                    startActivity(intent)
                }
                recyclerView.adapter = recyclerViewCustomAdapter
            } else {
                Toast.makeText(requireContext(), "No images found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Folder not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                loadImagesFromFolder() // Load images if permission granted
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }




}
