package com.frogstore.droneapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.frogstore.droneapp.Adapters.NotificationsAdapter
import com.frogstore.droneapp.databinding.ActivitySideMenuNavBarBinding
import java.io.File

class SideMenuNavBarActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySideMenuNavBarBinding
    private lateinit var popupWindow: PopupWindow
    private lateinit var notifications: ArrayList<String>
    private lateinit var imageList: ArrayList<String>

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.toolsbarColor)
            window.statusBarColor = ContextCompat.getColor(this, R.color.toolsbarColor)
        }

        binding = ActivitySideMenuNavBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarSideMenuNavBar.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_side_menu_nav_bar)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_location, R.id.nav_controller, R.id.nav_settings),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Initialize notifications and image lists
        notifications = arrayListOf("Notification 1", "Notification 2", "Notification 3", "Notification 4", "Notification 5", "Notification 6")
        imageList = arrayListOf() // Initialize this based on your needs
        loadImages() // Load images into imageList

        // Setup PopupWindow
        setupPopupWindow()

        // Setup notification icon click listener
        val notificationIcon: ImageButton = findViewById(R.id.notificationIcon)
        notificationIcon.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            } else {
                popupWindow.showAsDropDown(notificationIcon) // Show below the icon
            }
        }

        // Update the UI based on notifications
        updateNotificationUI()
    }

    private fun setupPopupWindow() {
        // Inflate the dropdown layout
        val popupView = LayoutInflater.from(this).inflate(R.layout.dropdown_notification, null)
        val recyclerView: RecyclerView = popupView.findViewById(R.id.recyclerViewNotifications)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NotificationsAdapter(this, notifications, imageList) { notification ->
            Toast.makeText(this, "Clicked: $notification", Toast.LENGTH_SHORT).show()
        }

        // Setup clear button
        val clearButton: ImageButton = popupView.findViewById(R.id.clearNotificationsButton)
        clearButton.setOnClickListener {
            clearNotifications()
            popupWindow.dismiss() // Optionally dismiss the popup
        }

        // Create PopupWindow
        popupWindow = PopupWindow(popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true)

        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(getDrawable(android.R.color.white))
    }

    private fun loadImages() {
        // Load images from your storage logic here and populate imageList
        val storageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolume = storageManager.storageVolumes.firstOrNull()
        val folderPath = storageVolume?.directory?.path + "/DCIM/pic" // Specify your folder path here
        val folder = File(folderPath)

        if (folder.exists() && folder.isDirectory) {
            val imageFiles = folder.listFiles { file ->
                file.isFile && (file.name.endsWith(".jpeg", true) || file.name.endsWith(".jpg", true) || file.name.endsWith(".png", true))
            }?.map { it.absolutePath } ?: emptyList()

            imageList.addAll(imageFiles) // Populate your imageList
        } else {
            Toast.makeText(this, "Folder not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNotificationUI() {
        val recyclerView: RecyclerView = popupWindow.contentView.findViewById(R.id.recyclerViewNotifications)
        val noNotificationsMessage: TextView = popupWindow.contentView.findViewById(R.id.noNotificationsMessage)

        if (notifications.isEmpty()) {
            recyclerView.visibility = View.GONE
            noNotificationsMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noNotificationsMessage.visibility = View.GONE
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    fun clearNotifications() {
        notifications.clear()
        updateNotificationUI() // Update UI after clearing notifications
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.side_menu_nav_bar, menu)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Permissions granted
                } else {
                    // Permissions denied
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_side_menu_nav_bar)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
