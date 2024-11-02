package com.frogstore.droneapp.Activities

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.frogstore.droneapp.Fragments.LoginFragment
import com.frogstore.droneapp.Fragments.RegisterFragment
import com.frogstore.droneapp.R
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private val loginFragment = LoginFragment()
    private val registerFragment = RegisterFragment()


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Load theme before super.onCreate
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        if (isDarkTheme) {
            setTheme(R.style.Theme_DroneApp_Dark)
        } else {
            setTheme(R.style.Theme_DroneApp)
        }

        setContentView(R.layout.activity_main)

        // Set system UI colors based on the theme
        updateSystemUiColors(isDarkTheme)

        // Handle window insets to avoid overlapping system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set initial fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frMain, loginFragment)
                .commit()
        }

        // Set up TabLayout and listener
        val tabBar = findViewById<TabLayout>(R.id.tabLayout)
        tabBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> replaceFrag(loginFragment)
                    1 -> replaceFrag(registerFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Optional: Handle tab unselected if needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Optional: Handle tab reselected if needed
            }
        })


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
        //window.statusBarColor = statusBarColor
        window.navigationBarColor = colorPrimary
    }

    private fun replaceFrag(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frMain, fragment)
            transaction.commit()
        }
    }
}
