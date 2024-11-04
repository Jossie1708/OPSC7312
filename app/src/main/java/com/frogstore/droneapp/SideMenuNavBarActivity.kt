package com.frogstore.droneapp

import NotificationItem
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.frogstore.droneapp.Activities.MainActivity
import com.frogstore.droneapp.Adapters.NotificationsAdapter
import com.frogstore.droneapp.UserDetails.LoginViewModel
import com.frogstore.droneapp.databinding.ActivitySideMenuNavBarBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File

class SideMenuNavBarActivity : AppCompatActivity() {
    private lateinit var name: TextView
    private lateinit var email: TextView

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySideMenuNavBarBinding
    private lateinit var popupWindow: PopupWindow
    private lateinit var notifications: ArrayList<NotificationItem>

    private lateinit var requestQueue: RequestQueue
    private lateinit var auth: FirebaseAuth

    private lateinit var notificationApplication: NotificationApplication
    private val tag = "sideNavBar: "

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        if (isDarkTheme) {
            setTheme(R.style.Theme_DroneApp_Dark)
        } else {
            setTheme(R.style.Theme_DroneApp)
        }

        super.onCreate(savedInstanceState)
        requestPermissions()
        askNotificationPermission()

        // Initialize notifications list
        notifications = arrayListOf() // Start with an empty list

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance() // Initialize here

        // Load notifications from SharedPreferences
        loadNotifications()

        // Setup PopupWindow after loading notifications
        setupPopupWindow()

        // Setup RecyclerView with the adapter
        setupRecyclerView()

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
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_flight_logs, R.id.nav_controller, R.id.nav_settings),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        notificationApplication = application as NotificationApplication
        notifications = notificationApplication.notifications

        val toolbarTitle: TextView = binding.appBarSideMenuNavBar.toolbar.findViewById(R.id.toolbarTitle)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val title = destination.label
            if (title != null) {
                toolbarTitle.text = title
            } else {
                toolbarTitle.text = ""
            }
        }

        // Update the UI based on notifications
        updateNotificationUI()
        updateHeader()

        subscribeToWeatherUpdates()
        retrieveFCMToken() // Call the function to retrieve the token

        // Setup notification icon click listener
        val notificationIcon: ImageButton = findViewById(R.id.notificationIcon)
        notificationIcon.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            } else {
                popupWindow.showAsDropDown(notificationIcon) // Show below the icon
            }
        }


//        // Set up the NavigationView
//        val googleSignInClient = GoogleSignInClient(this)
//        val navigationView: NavigationView = findViewById(R.id.nav_view)
//        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
//            when (item.itemId) {
//                R.id.nav_logout -> {
//                    lifecycleScope.launch {
//                        googleSignInClient.signOut()
//                        println(tag+"succcess")
//
//                        // Navigate to MainActivity
//                        val intent = Intent(this@SideMenuNavBarActivity, MainActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(intent)
//                        finish() // Close the current activity
//                    }
//                    true
//                }
//                else ->{
//                    println(tag+"false")
//                    false
//                    }
//            }
//        }
    }

    private fun updateHeader() {
        // Initialize LoginViewModel
        val loginViewModel = LoginViewModel(application)

        // Retrieve the user session
        val loginState = loginViewModel.getUserSession()

        // Initialize the TextViews from the header layout
        val navView = binding.navView // Assuming navView is your NavigationView
        val headerView = navView.getHeaderView(0) // Get the first header view
        name = headerView.findViewById(R.id.txtLoginUsername)
        email = headerView.findViewById(R.id.txtLoginEmail)

        // Check if user session is available
        loginState?.let {
            email.text = auth.currentUser?.email
            name.text = auth.currentUser?.displayName
        } ?: run {
            name.text = getString(R.string.sign_in_name) // Default name
            email.text = getString(R.string.sign_in_email) // Default email
        }
    }

    private fun updateSystemUiColors(isDarkTheme: Boolean) {
        val colorPrimary = if (isDarkTheme) {
            ContextCompat.getColor(this, R.color.darkGreenAccent) // Dark theme color
        } else {
            ContextCompat.getColor(this, R.color.greenAccent) // Light theme color
        }

        val statusBarColor = if (isDarkTheme) {
            ContextCompat.getColor(this, R.color.darkGreenAccent)
        } else {
            ContextCompat.getColor(this, R.color.greenAccent)
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

        recyclerView.adapter = NotificationsAdapter(this, notifications) { notification ->
            // Handle item click
            Toast.makeText(this, "Clicked: ${notification.title}", Toast.LENGTH_SHORT).show()
        }

        // Setup clear button
        val clearButton: ImageButton = popupView.findViewById(R.id.clearNotificationsButton)
        clearButton.setOnClickListener {
            clearNotifications()
            popupWindow.dismiss() //Optionally dismiss the popup
        }

        // Create PopupWindow
        popupWindow = PopupWindow(popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true)

        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(getDrawable(android.R.color.white))
    }

    fun addNotification(notification: NotificationItem) {
        notifications.add(notification)
        val adapter = popupWindow.contentView.findViewById<RecyclerView>(R.id.recyclerViewNotifications).adapter as NotificationsAdapter
        adapter.updateNotifications(notifications)
        saveNotification(notification)
        updateNotificationUI()
    }

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val notificationItem = intent?.getParcelableExtra<NotificationItem>("notification")
            notificationItem?.let {
                addNotification(it) // Call your method to add the notification
            }
        }
    }

    private fun loadNotifications() {
        val sharedPreferences = getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val notificationsJson = sharedPreferences.getString("notification_list", "[]")

        // Debug log
        Log.d("LoadNotifications", "Loaded JSON: $notificationsJson")

        // Convert the JSON back to a list of NotificationItem
        val gson = Gson()
        val type = object : TypeToken<MutableList<NotificationItem>>() {}.type
        notifications = gson.fromJson(notificationsJson, type) ?: arrayListOf() // Ensure it's not null
    }


    fun saveNotification(notification: NotificationItem) {
        val sharedPreferences = getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val notificationsJson = sharedPreferences.getString("notification_list", "[]")

        // Convert the existing notifications from JSON to a mutable list
        val gson = Gson()
        val type = object : TypeToken<MutableList<NotificationItem>>() {}.type
        val notificationsList: MutableList<NotificationItem> = gson.fromJson(notificationsJson, type) ?: mutableListOf()

        // Add new notification and save back
        notificationsList.add(notification)
        val json = gson.toJson(notificationsList)
        editor.putString("notification_list", json)
        editor.apply()

        // Debug log
        Log.d("SaveNotification", "Saved JSON: $json")
    }


    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = popupWindow.contentView.findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = NotificationsAdapter(this, notifications) { notification ->
            // Handle item click
            Toast.makeText(this, "Clicked: ${notification.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            notificationReceiver, IntentFilter("NEW_NOTIFICATION")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_CODE_PERMISSIONS)
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
                if (grantResults.isNotEmpty()) {
                    var locationPermissionGranted = false
                    var readMediaImagesPermissionGranted = false

                    for (i in grantResults.indices) {
                        if (permissions[i] == Manifest.permission.ACCESS_FINE_LOCATION) {
                            locationPermissionGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED
                        } else if (permissions[i] == Manifest.permission.READ_MEDIA_IMAGES) {
                            readMediaImagesPermissionGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED
                        }
                    }
                    if (!readMediaImagesPermissionGranted) {
                        Toast.makeText(this, "Permission denied. Please grant permission to access images.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_side_menu_nav_bar)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun retrieveFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and show the token
            Log.d("FCM", "FCM Token: $token")
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Handle permission granted
        } else {
            Toast.makeText(this, "Frog-copter will not send you notifications.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun subscribeToWeatherUpdates() {
        FirebaseMessaging.getInstance().subscribeToTopic("weather_updates")
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) "Subscribed to weather updates" else "Subscription failed"
                Log.d("FCM", msg)
            }
    }
}

