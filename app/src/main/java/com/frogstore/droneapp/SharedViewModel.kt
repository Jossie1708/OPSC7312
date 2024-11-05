package com.frogstore.droneapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _connectionStatus = MutableLiveData<String>()
    val connectionStatus: LiveData<String> get() = _connectionStatus

    fun updateConnectionStatus(status: String) {
        _connectionStatus.value = status
    }
}
