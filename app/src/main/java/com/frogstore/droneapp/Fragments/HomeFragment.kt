package com.frogstore.droneapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.frogstore.droneapp.NotificationsActivity
import com.frogstore.droneapp.R

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_home, container, false)

        val btn = layout.findViewById<Button>(R.id.btnTestingNotifications)

        btn.setOnClickListener {
            // Create an Intent to start the NotificationsActivity
           val intent = Intent(requireContext(), NotificationsActivity::class.java)
            startActivity(intent) // Start the activity
        }
        // Return the inflated layout
        return layout


    }

}