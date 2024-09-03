package com.frogstore.droneapp

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private val HomeFrag =UpdatePasswordFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE)
        {
            window.statusBarColor = ContextCompat.getColor(this, R.color.green)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.green)
        }

        replaceFrag(HomeFrag)
        val bottomBar = findViewById<BottomNavigationView>(R.id.navBarHome)
        bottomBar.selectedItemId = R.id.ic_home
        bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.ic_home -> replaceFrag(HomeFrag)
            }
            true

        }
    }
    private fun replaceFrag(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frHome, fragment)
            transaction.commit()
        }
    }
}