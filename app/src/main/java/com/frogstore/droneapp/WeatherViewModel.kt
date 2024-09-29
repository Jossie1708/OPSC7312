package com.frogstore.droneapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherData?>()
    val weatherData: LiveData<WeatherData?> get() = _weatherData

    private val repository = WeatherRepository()

    fun fetchWeatherData() {
        viewModelScope.launch {
            val data = repository.fetchWeather()
            _weatherData.postValue(data)
        }
    }
}
