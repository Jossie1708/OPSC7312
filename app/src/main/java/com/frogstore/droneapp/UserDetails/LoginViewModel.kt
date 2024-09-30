package com.frogstore.droneapp.UserDetails

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// The `LoginViewModel` class extends the `ViewModel` class, meaning it manages UI-related data in a lifecycle-conscious way.
// This class stores the state for the login process and handles actions that affect the UI state, such as input changes and sign-up results.
class LoginViewModel(private val context: Context) : ViewModel() {

    val userSessionManager = UserSessionManager(context)
    fun login(username: String, email: String) {
        if (userSessionManager.login(username, email)) {
            // Login successful, save user session
            userSessionManager.saveUserSession(username, email)
        } else {
            // Login failed, handle error
        }
    }

    fun getUserSession(): LoginState? {
        return userSessionManager.getUserSession()
    }

    fun isUserLoggedIn(): Boolean {
        return userSessionManager.isUserLoggedIn()
    }

    // `state` holds the current login state, which is of type `LoginState`.
    // `mutableStateOf` creates a state variable that Compose can track for changes.
    // The `by` keyword delegates access to this state through Compose's property delegate (`getValue`, `setValue`).
    var state by mutableStateOf(LoginState())
        private set // `private set` ensures that the state can only be modified internally within the `LoginViewModel`.

    // `onAction` is a function that processes different user actions and updates the state accordingly.
    // It takes a parameter `action` of type `LoginAction`, which represents the various actions the user can perform (e.g., changing the username, signing up, etc.).
    fun onAction(action: LoginAction) {
        // `when` expression is used to handle different types of actions.
        when (action) {
            // Handle the action for password change.
            is LoginAction.OnpasswordChange -> {
                // When the password changes, create a new copy of the current `state` with the updated password.
                state = state.copy(password = action.password)
            }

            // Handle the action for sign-up result.
            is LoginAction.OnSignUp -> {
                // The result of the sign-up process is checked with another `when` expression.
                when (action.result) {
                    // If sign-up was cancelled, update the state with an appropriate error message.
                    SignUpResult.Cancelled -> {
                        state = state.copy(
                            errorMessage = "Sign up was cancelled" // Set an error message for the cancelled sign-up.
                        )
                    }
                    // If sign-up failed, update the state with a failure error message.
                    SignUpResult.Failure -> {
                        state = state.copy(
                            errorMessage = "Sign up failed" // Set an error message for the failed sign-up.
                        )
                    }
                    // If sign-up was successful, update the state with the logged-in user's username.
                    is SignUpResult.Success -> {
                        state = state.copy(
                            loggedInUser = action.result.username // Store the username of the successfully logged-in user.
                        )
                    }
                }
            }

            // Handle the action for toggling between registration and login modes.
            LoginAction.OnToggleIsRegister -> {
                // Toggle the `isRegister` boolean value in the state, switching between the login and registration modes.
                state = state.copy(isRegister = !state.isRegister)
            }

            // Handle the action for username change.
            is LoginAction.OnUsernameChange -> {
                // When the username changes, create a new copy of the current `state` with the updated username.
                state = state.copy(username = action.username)
            }
        }
    }
}
