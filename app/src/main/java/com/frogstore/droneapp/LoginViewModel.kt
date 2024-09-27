package com.frogstore.droneapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {
    var state by mutableStateOf(LoginState())
    private set

    fun onAction(action: LoginAction){
        when(action){
            is LoginAction.OnpasswordChange -> {
                state = state.copy(password = action.password)
            }
            is LoginAction.OnSignUp -> {
                when(action.result) {
                    SignUpResult.Cancelled -> {
                        state = state.copy(
                            errorMessage = "Sign up was cancelled"
                        )
                    }
                    SignUpResult.Failure -> {
                        state = state.copy(
                            errorMessage = "Sign up was failed"
                        )
                    }
                    is SignUpResult.Success -> {
                            state = state.copy(
                                loggedInUser = action.result.username
                            )
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
}