package com.frogstore.droneapp

import android.os.Bundle
import android.os.storage.StorageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frogstore.droneapp.Adapters.NotificationsAdapter
import java.io.File

class NotificationsFragmentFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_notification, container, false)

        recyclerView = layout.findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        loadImages()



        return layout
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
        val storageManager = requireContext().getSystemService(StorageManager::class.java)
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
                recyclerView.adapter = NotificationsAdapter(requireContext(), notifications, imageFiles)

            } else {
                Toast.makeText(requireContext(), "No images found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Folder not found", Toast.LENGTH_SHORT).show()
        }
    }

}