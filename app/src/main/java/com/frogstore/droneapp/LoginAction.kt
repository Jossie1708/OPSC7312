package com.frogstore.droneapp

// `LoginAction` is an interface that represents different actions related to the login process.
// These actions can be user-triggered events like changing the username, password, or toggling the registration mode.
interface LoginAction {

    data class OnSignUp(val result: SignUpResult): LoginAction

    data class OnUsernameChange(val username: String): LoginAction

    data class OnpasswordChange(val password: String): LoginAction

    data object OnToggleIsRegister: LoginAction
}
