package com.frogstore.droneapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.frogstore.droneapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val weatherViewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe weather data
        weatherViewModel.weatherData.observe(viewLifecycleOwner, Observer { weatherData ->
            if (weatherData != null) {
                updateUI(weatherData)
            }
        })
    }

    private fun updateUI(weatherData: WeatherData) {
        binding.address.text = weatherData.address
        binding.lblDate.text = weatherData.updatedAt
        binding.lblTemp.text = weatherData.temperature
        binding.lblTempLow.text = weatherData.minTemperature
        binding.lblTempHigh.text = weatherData.maxTemperature
        binding.lblHumididtyPercentage.text = weatherData.humidity
        binding.lblWindSpeedKm.text = weatherData.windSpeed
        //binding.lblRainPercentage.text = weatherData.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
