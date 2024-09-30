package com.frogstore.droneapp.WeatherData

data class WeatherData(
    val address: String,
    val updatedAt: String,
    val temperature: String,
    val minTemperature: String,
    val maxTemperature: String,
    val pressure: String,
    val humidity: String,
    val sunrise: String,
    val sunset: String,
    val windSpeed: String,
    val weatherDescription: String
)
