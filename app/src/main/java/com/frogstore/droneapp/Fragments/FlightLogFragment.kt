package com.frogstore.droneapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frogstore.droneapp.Adapters.FlightLogAdapter
import com.frogstore.droneapp.R
import com.frogstore.droneapp.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate


class FlightLogFragment : Fragment() {
    private lateinit var flightLogAdapter: FlightLogAdapter
    private val viewModel: SharedViewModel by activityViewModels()
    private val LOCATION = "Port Elizabeth"
    private val sampleFlightLogs = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_flight_log, container, false)

        // Observe the connection status from the ViewModel
        viewModel.connectionStatus.observe(viewLifecycleOwner) { status ->
            if (status == "Connected") {
                addFlightLog()  // Add log asynchronously when connected
            }
        }
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
    private fun addFlightLog() {
        val currentDate = LocalDate.now()
        val newLog = "Flight on $currentDate at $LOCATION"
        sampleFlightLogs.add(newLog)
        Toast.makeText(requireContext(), "Flight log added: $newLog", Toast.LENGTH_SHORT).show()
    }
}
