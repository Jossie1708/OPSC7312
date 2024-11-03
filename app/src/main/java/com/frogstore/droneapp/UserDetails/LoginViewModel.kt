package com.frogstore.droneapp.UserDetails

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class LoginViewModel(application: Application) : AndroidViewModel(application)
{

    private val context = application

    val userSessionManager = UserSessionManager(context)

    fun login(username: String, email: String)
    {
        if (userSessionManager.login(username, email))
        {
            userSessionManager.saveUserSession(username, email)
        }
        else
        {
            // Login failed, handle error
        }
    }

    fun getUserSession(): LoginState?
    {
        return userSessionManager.getUserSession()
    }

    fun isUserLoggedIn(): Boolean
    {
        return userSessionManager.isUserLoggedIn()
    }

    var state by mutableStateOf(LoginState())
        private set

    fun onAction(action: LoginAction)
    {
        when (action)
        {
            is LoginAction.OnpasswordChange -> {
                state = state.copy(password = action.password)
            }

            is LoginAction.OnSignUp -> {
                when (action.result)
                {
                    SignUpResult.Cancelled -> {
                        state = state.copy(
                            errorMessage = "Sign up was cancelled"
                        )
                    }
                    is SignUpResult.Failure -> {
                        state = state.copy(
                            errorMessage = "Sign up failed"
                        )
                    }
                    is SignUpResult.Success -> {

                        state = state.copy(loggedInUser = action.result.username)

                        storeUserInSSO(action.result.username, action.result.email)
                    }
                }
            }

            LoginAction.OnToggleIsRegister -> {
                state = state.copy(isRegister = !state.isRegister)
            }

            is LoginAction.OnUsernameChange -> {
                state = state.copy(username = action.username)
            }
        }
    }

    private fun storeUserInSSO(username: String, email: String)
    {
        val userSessionManager = UserSessionManager(context)
        userSessionManager.login(username, email)
    }
}
