package com.frogstore.droneapp

// Sealed interface `SignUpResult` represents the possible outcomes of a sign-up or login process.
// This sealed interface ensures that all possible outcomes are known and handled explicitly in the code.
sealed interface SignUpResult {

    data class Success(val username: String): SignUpResult

    data object Cancelled: SignUpResult

    data object Failure: SignUpResult
}
