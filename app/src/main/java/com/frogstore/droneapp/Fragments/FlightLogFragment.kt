package com.frogstore.droneapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.frogstore.droneapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FlightLogFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_flight_log, container, false)
        val btnMap = layout.findViewById<FloatingActionButton>(R.id.fabLocationMap)
        btnMap.setOnClickListener {
            findNavController().navigate(R.id.action_nav_logs_to_map)
        }

        return layout
    }


}