package com.frogstore.droneapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.storage.StorageManager
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.frogstore.droneapp.Adapters.NotificationsAdapter
import com.frogstore.droneapp.databinding.ActivitySideMenuNavBarBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.json.JSONException
import java.io.File
import java.nio.charset.Charset
import org.json.JSONObject

class SideMenuNavBarActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var name: TextView
    private lateinit var email: TextView

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySideMenuNavBarBinding
    private lateinit var popupWindow: PopupWindow
    private lateinit var notifications: ArrayList<String>
    private lateinit var imageList: ArrayList<String>

    private lateinit var requestQueue: RequestQueue

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        if (isDarkTheme) {
            setTheme(R.style.Theme_DroneApp_Dark)
        } else {
            setTheme(R.style.Theme_DroneApp)
        }
        super.onCreate(savedInstanceState)

        // Set system UI colors based on the theme
        updateSystemUiColors(isDarkTheme)

        // Initialize the RequestQueue for making network requests
        requestQueue = Volley.newRequestQueue(this)

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

        val toolbarTitle: TextView = binding.appBarSideMenuNavBar.toolbar.findViewById(R.id.toolbarTitle)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val title = destination.label
            if (title != null) {
                toolbarTitle.text = title
            } else {
                toolbarTitle.text = ""
            }
        }

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

        updateHeader()
    }
    private fun getUsername() {
        val url = "https://frogtrackapi2-bjaufahwavexambv.eastasia-01.azurewebsites.net/getUsername?email=${email.text}"

        val stringRequest = object : StringRequest(
            Method.GET, url,
            { response ->
                Log.d("API Response", response) // Log the response
                try {
                    // Remove quotes if the response is a simple string
                    val username = response.replace("\"", "") // Remove double quotes
                    name.text = username
                    Toast.makeText(this, "Username retrieved successfully", Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    Toast.makeText(this, "Invalid response format: $response", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error retrieving username: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        requestQueue.add(stringRequest)
    }



    private fun updateHeader() {
        // Initialize UserSessionManager
        val userSessionManager = UserSessionManager(this)

        // Retrieve the user session
        val loginState = userSessionManager.getUserSession()

        // Initialize the TextViews from the header layout
        val navView = binding.navView // Assuming navView is your NavigationView
        val headerView = navView.getHeaderView(0) // Get the first header view
        name = headerView.findViewById(R.id.txtLoginUsername)
        email = headerView.findViewById(R.id.txtLoginEmail)

        // Check if user session is available
        loginState?.let {
            // Set the email from the user session and call getUsername to retrieve the username
            email.text = it.loggedInUser // Assuming this is the user's email
            getUsername() // Fetch and display the username
        } ?: run {
            // Handle the case where the user is not signed in
            name.text = getString(R.string.sign_in_name) // Default name
            email.text = getString(R.string.sign_in_email) // Default email
        }
    }


    private fun updateSystemUiColors(isDarkTheme: Boolean) {
        val colorPrimary = if (isDarkTheme) {
            ContextCompat.getColor(this, R.color.darkGreenAccent) // Dark theme color
        } else {
            ContextCompat.getColor(this, R.color.darkGreen) // Light theme color
        }

        val statusBarColor = if (isDarkTheme) {
            ContextCompat.getColor(this, R.color.darkGreenAccent)
        } else {
            ContextCompat.getColor(this, R.color.darkGreen)
        }

        // Update system UI colors
        window.statusBarColor = statusBarColor
        window.navigationBarColor = colorPrimary
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
        val notificationsTitle: TextView = popupWindow.contentView.findViewById(R.id.notificationsTitle)
        val lineBreak: View = popupWindow.contentView.findViewById(R.id.lineBreak)
        val clearButton: ImageButton = popupWindow.contentView.findViewById(R.id.clearNotificationsButton)

        if (notifications.isEmpty()) {
            recyclerView.visibility = View.GONE
            noNotificationsMessage.visibility = View.VISIBLE
            notificationsTitle.visibility = View.GONE
            lineBreak.visibility = View.GONE
            clearButton.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            noNotificationsMessage.visibility = View.GONE
            notificationsTitle.visibility = View.VISIBLE
            lineBreak.visibility = View.VISIBLE
            clearButton.visibility = View.VISIBLE
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
