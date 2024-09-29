package com.frogstore.droneapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.frogstore.droneapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    // Weather API variables
    private val CITY: String = "Port Elizabeth"
    private val API: String = "f26bb4e87fb52fbdc876bd89a0ecdc44" // Use API key


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_home, container, false)



        // Execute the weather task
        fetchWeatherData(layout)

        return layout
    }

    private fun fetchWeatherData(layout : View) {
        // Showing the ProgressBar, Making the main design GONE
        layout.findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
        layout.findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
        layout.findViewById<TextView>(R.id.errorText).visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            val response = fetchWeather()
            withContext(Dispatchers.Main) {
                handleResponse(response, layout)
            }
        }
    }

    private fun fetchWeather(): String? {
        return try {
            URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    private fun handleResponse(result: String?, layout: View) {
        if (result != null) {
            try {
                // Extracting JSON returns from the API
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt * 1000)
                )
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                // Populating extracted data into our views
                layout.findViewById<TextView>(R.id.address).text = address
                layout.findViewById<TextView>(R.id.updated_at).text = updatedAtText
                layout.findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                layout.findViewById<TextView>(R.id.temp).text = temp
                layout.findViewById<TextView>(R.id.temp_min).text = tempMin
                layout.findViewById<TextView>(R.id.temp_max).text = tempMax
                layout.findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunrise * 1000)
                )
                layout.findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunset * 1000)
                )
                layout.findViewById<TextView>(R.id.wind).text = windSpeed
                layout.findViewById<TextView>(R.id.pressure).text = pressure
                layout.findViewById<TextView>(R.id.humidity).text = humidity

                // Hide ProgressBar and show main container
                layout.findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                layout.findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
            } catch (e: Exception) {
                // Handle JSON parsing errors
            }
        } else {
            // Handle null response (e.g., show error message)
            layout.findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
        }
    }


}
