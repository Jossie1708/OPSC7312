package com.frogstore.droneapp

// `LoginAction` is an interface that represents different actions related to the login process.
// These actions can be user-triggered events like changing the username, password, or toggling the registration mode.
interface LoginAction {

    // Action triggered when the sign-up process is completed.
    // This action is dispatched when the user tries to sign up, and it passes a `SignUpResult`.
    // `result` represents the outcome of the sign-up process: success, failure, or cancellation.
    data class OnSignUp(val result: SignUpResult): LoginAction

    // Action triggered when the user changes the username.
    // `username` holds the updated username entered by the user.
    data class OnUsernameChange(val username: String): LoginAction

    // Action triggered when the user changes the password.
    // `password` holds the updated password entered by the user.
    data class OnpasswordChange(val password: String): LoginAction

    // Action triggered when the user toggles the registration mode (i.e., switches between login and registration).
    // This is a simple action that does not carry any data but flips the `isRegister` state in the `LoginState`.
    data object OnToggleIsRegister: LoginAction
}
