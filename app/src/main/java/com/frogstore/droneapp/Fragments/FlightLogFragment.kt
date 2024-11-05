package com.frogstore.droneapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frogstore.droneapp.Adapters.FlightLogAdapter
import com.frogstore.droneapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FlightLogFragment : Fragment() {
    private lateinit var flightLogAdapter: FlightLogAdapter
    private val sampleFlightLogs = listOf(
        "Flight on 2024-11-02 at Port Elizabeth",
        "Flight on 2024-11-02 at Port Elizabeth",
        "Flight on 2024-11-02 at Port Elizabeth",
        "Flight on 2024-11-02 at Port Elizabeth",
        "Flight on 2024-11-02 at Port Elizabeth",
        "Flight on 2024-11-02 at Port Elizabeth",
        "Flight on 2024-11-03 at Port Elizabeth"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_flight_log, container, false)

        // Set up the RecyclerView
        val recyclerView = layout.findViewById<RecyclerView>(R.id.recyclerview_flight_logs)
        flightLogAdapter = FlightLogAdapter(requireContext(), sampleFlightLogs)
        recyclerView.adapter = flightLogAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up the FloatingActionButton
        val btnMap = layout.findViewById<FloatingActionButton>(R.id.fabLocationMap)
        btnMap.setOnClickListener {
            findNavController().navigate(R.id.action_nav_logs_to_map)
        }

        return layout
    }
}
