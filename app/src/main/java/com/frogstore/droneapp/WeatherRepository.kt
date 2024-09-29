package com.frogstore.droneapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherRepository {

    private val CITY = "Port Elizabeth"
    private val API = "f26bb4e87fb52fbdc876bd89a0ecdc44"

    suspend fun fetchWeather(): WeatherData? {
        return try {
            val response = withContext(Dispatchers.IO) {
                URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(Charsets.UTF_8)
            }
            parseWeatherData(response)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseWeatherData(response: String): WeatherData? {
        return try {
            val jsonObj = JSONObject(response)
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val wind = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

            val updatedAt: Long = jsonObj.getLong("dt")
            val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt * 1000))
            val temp = main.getString("temp") + "°C"
            val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
            val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity")

            val sunrise: Long = sys.getLong("sunrise")
            val sunset: Long = sys.getLong("sunset")
            val windSpeed = wind.getString("speed")
            val weatherDescription = weather.getString("description").capitalize()
            val address = jsonObj.getString("name") + ", " + sys.getString("country")

            WeatherData(
                address = address,
                updatedAt = updatedAtText,
                temperature = temp,
                minTemperature = tempMin,
                maxTemperature = tempMax,
                pressure = pressure,
                humidity = humidity,
                sunrise = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000)),
                sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000)),
                windSpeed = windSpeed,
                weatherDescription = weatherDescription
            )
        } catch (e: Exception) {
            null
        }
    }
}
