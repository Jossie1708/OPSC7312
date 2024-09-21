package com.frogstore.droneapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private val homeFrag = HomeFragment()
    private val galleryFrag = GalleryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val bottomBar = findViewById<BottomNavigationView>(R.id.navBarHome)
        bottomBar.selectedItemId = R.id.ic_home
        bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.ic_home -> replaceFrag(homeFrag)
                R.id.ic_gallery -> replaceFrag(galleryFrag)
            }
            true
        }
    }



    private fun replaceFrag(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frHome, fragment)
        transaction.commit()
    }
}
