package com.frogstore.droneapp

data class LoginState(
    val loggedInUser: String? = null,
    val username: String = "user",
    val password: String = "pass",
    val errorMessage: String? = null,
    val isRegister: Boolean = false
)
