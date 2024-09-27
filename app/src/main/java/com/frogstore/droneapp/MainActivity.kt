package com.frogstore.droneapp

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.frogstore.droneapp.Fragments.FingerprintFragment
import com.frogstore.droneapp.Fragments.LoginFragment
import com.frogstore.droneapp.Fragments.RegisterFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private val loginFragment = LoginFragment()
    private val registerFragment = RegisterFragment()
    private val fingerprintFragment = FingerprintFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.greenAccent)
        }

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

    private fun replaceFrag(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frMain, fragment)
            transaction.commit()
        }
    }
}
