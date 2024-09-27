package com.frogstore.droneapp

interface LoginAction {
    data class OnSignUp(val result: SignUpResult): LoginAction
    data class OnUsernameChange(val username: String): LoginAction
    data class OnpasswordChange(val password: String): LoginAction
    data object OnToggleIsRegister: LoginAction
}