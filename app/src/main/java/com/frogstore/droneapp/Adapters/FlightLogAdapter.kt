package com.frogstore.droneapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.frogstore.droneapp.R

class FlightLogAdapter(
    private val context: Context,
    private var flightLogList: List<String>
) : RecyclerView.Adapter<FlightLogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_flight_logs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flightLog = flightLogList[position]
        // Split the sample data into date and location
        val logParts = flightLog.split(" at ")
        holder.itemFlightLogDate.text = logParts[0] // Set the date
        holder.itemFlightLogLocation.text = logParts.getOrNull(1) ?: "Unknown Location" // Set the location
    }

    override fun getItemCount(): Int {
        return flightLogList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemFlightLogDate: TextView = view.findViewById(R.id.txtItem_flight_log_date)
        val itemFlightLogLocation: TextView = view.findViewById(R.id.txtItem_flight_log_location)
    }
}
