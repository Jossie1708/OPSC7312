package com.frogstore.droneapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.view.LayoutInflater
import android.view.Menu
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frogstore.droneapp.Adapters.NotificationsAdapter
import com.frogstore.droneapp.databinding.ActivitySideMenuNavBarBinding
import java.io.File

class SideMenuNavBarActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySideMenuNavBarBinding
    private lateinit var popupWindow: PopupWindow
    private lateinit var notifications: ArrayList<String>
    private lateinit var imageList: ArrayList<String>

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
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_location, R.id.nav_controller, R.id.nav_settings), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val toolbarTitle: TextView = binding.appBarSideMenuNavBar.toolbar.findViewById(R.id.toolbarTitle)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val title = destination.label
            if (title != null) {
                toolbarTitle.text = title
            } else {
                toolbarTitle.text = ""
            }
        }


        // Initialize notifications and image lists
        notifications = arrayListOf("Notification 1", "Notification 2", "Notification 3")
        imageList = arrayListOf()
        loadImages()
        setupPopupWindow()

        // Setup notification icon click listener
        val notificationIcon: ImageButton = findViewById(R.id.notificationIcon)
        notificationIcon.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            } else {
                popupWindow.showAsDropDown(notificationIcon)
            }
        }
    }

    private fun setupPopupWindow() {
        val popupView = LayoutInflater.from(this).inflate(R.layout.dropdown_notification, null)
        val recyclerView: RecyclerView = popupView.findViewById(R.id.recyclerViewNotifications)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NotificationsAdapter(this, notifications, imageList) { notification ->
            Toast.makeText(this, "Clicked: $notification", Toast.LENGTH_SHORT).show()
        }

        val clearButton: ImageButton = popupView.findViewById(R.id.clearNotificationsButton)
        clearButton.setOnClickListener {
            clearNotifications()
            popupWindow.dismiss()
        }

        popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(getDrawable(android.R.color.white))
    }

    private fun loadImages() {
        val storageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolume = storageManager.storageVolumes.firstOrNull()
        val folderPath = storageVolume?.directory?.path + "/DCIM/pic"
        val folder = File(folderPath)

        if (folder.exists() && folder.isDirectory) {
            val imageFiles = folder.listFiles { file ->
                file.isFile && (file.name.endsWith(".jpeg", true) || file.name.endsWith(".jpg", true) || file.name.endsWith(".png", true))
            }?.map { it.absolutePath } ?: emptyList()

            imageList.addAll(imageFiles)
        } else {
            Toast.makeText(this, "Folder not found", Toast.LENGTH_SHORT).show()
        }
    }

    fun clearNotifications() {
        notifications.clear()
        popupWindow.contentView.findViewById<RecyclerView>(R.id.recyclerViewNotifications).adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.side_menu_nav_bar, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_side_menu_nav_bar)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
