package com.frogstore.droneapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.storage.StorageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frogstore.droneapp.Activities.ImageViewerActivity
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

        recyclerView = layout.findViewById(R.id.recyclerViewGallery)
        layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = layoutManager

        loadImagesFromFolder()

        return layout
    }

    @SuppressLint("NewApi")
    private fun loadImagesFromFolder() {
        val storageManager = requireContext().getSystemService(StorageManager::class.java)
        val storageVolume = storageManager?.storageVolumes?.get(0)
        val folderPath =
            storageVolume?.directory?.path + "/DCIM/100PINT/Pins" // Specify your folder path here
        val folder = File(folderPath)

        if (folder.exists() && folder.isDirectory) {
            val imageFiles = folder.listFiles { file ->
                file?.isFile == true && (file.name.endsWith(".jpeg", true) ||
                        file.name.endsWith(".jpg", true) || file.name.endsWith(".png", true))
            }?.map { it.absolutePath } ?: emptyList()

            if (imageFiles.isNotEmpty()) {
                recyclerViewCustomAdapter = ImageAdapter(requireContext(), imageFiles, { position ->
                    val intent = Intent(requireContext(), ImageViewerActivity::class.java).apply {
                        putStringArrayListExtra("imagePaths", ArrayList(imageFiles))
                        putExtra("position", position)
                    }
                    startActivity(intent)
                }, { imagePath -> sendImage(imagePath) }) // Call sendImage on share button click

                recyclerView.adapter = recyclerViewCustomAdapter
            } else {
                Toast.makeText(requireContext(), "No images found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Folder not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendImage(imagePath: String) {
        val file = File(imagePath)
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)

        // Creating a new intent
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri) // Use the content URI
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant temporary read permission
        }

        // Checking whether WhatsApp is installed or not
        if (intent.resolveActivity(requireContext().packageManager) == null) {
            Toast.makeText(requireContext(), "Please install WhatsApp first.", Toast.LENGTH_SHORT).show()
            return
        }

        // Starting WhatsApp
        startActivity(intent)
    }

}

