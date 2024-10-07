package com.frogstore.droneapp.UserDetails

// The `LoginState` data class represents the current state of the login screen.
// It stores the data needed for login functionality such as username, password, and error messages.
data class LoginState(
    // `loggedInUser` holds the username of the currently logged-in user.
    // It is nullable (`String?`) because the user may not be logged in, in which case it will be null.
    val loggedInUser: String? = null,

    // `username` stores the input username for login or registration.
    // It has a default value of "user", which may represent a pre-filled placeholder or testing default.
    val username: String = "user",

    // `password` stores the input password for login or registration.
    // It has a default value of "pass", again potentially for testing purposes.
    val password: String = "pass",

    val email: String = "",

    // `errorMessage` holds any error message that needs to be displayed in case of login failure, signup failure, or other issues.
    // It is nullable (`String?`) because there may be times when no error message is needed, in which case it will be null.
    val errorMessage: String? = null,

    // `isRegister` is a boolean flag that indicates whether the user is currently on the registration screen (`true`)
    // or the login screen (`false`). It helps the app determine which UI mode is active (login or registration).
    val isRegister: Boolean = false
)
