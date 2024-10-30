package com.frogstore.droneapp.UserDetails

// Sealed interface `SignUpResult` represents the possible outcomes of a sign-up or login process.
// This sealed interface ensures that all possible outcomes are known and handled explicitly in the code.
sealed interface SignUpResult {

    data class Success(val username: String, val email: String) : SignUpResult
    object Failure: SignUpResult
    object Cancelled : SignUpResult
}
