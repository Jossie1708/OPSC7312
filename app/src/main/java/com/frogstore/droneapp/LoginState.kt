package com.frogstore.droneapp

// The `LoginState` data class represents the current state of the login screen.
// It stores the data needed for login functionality such as username, password, and error messages. 
data class LoginState(
    val loggedInUser: String? = null,

    val username: String = "user",

    val password: String = "pass",

    val email: String = "email",

    val errorMessage: String? = null,

    val isRegister: Boolean = false
)
