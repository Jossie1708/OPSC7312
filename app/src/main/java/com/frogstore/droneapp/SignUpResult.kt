package com.frogstore.droneapp

// Sealed interface `SignUpResult` represents the possible outcomes of a sign-up or login process.
// This sealed interface ensures that all possible outcomes are known and handled explicitly in the code.
sealed interface SignUpResult {

    // `Success` is a data class that represents a successful login or sign-up.
    // It holds a single piece of data: `username`, which is the username of the user who successfully logged in or signed up.
    // The use of a data class allows storing relevant information about the success result (like the username).
    data class Success(val username: String): SignUpResult

    // `Cancelled` is a data object that indicates the user has cancelled the sign-up or login process.
    // Being a `data object` means this represents a singleton, and no additional data is needed.
    // It will always represent the same instance, making it memory-efficient for cases where no extra information is required.
    data object Cancelled: SignUpResult

    // `Failure` is another data object representing a failed sign-up or login attempt (e.g., due to invalid credentials or a network issue).
    // Similar to `Cancelled`, this is a singleton because no additional data is needed to describe the failure.
    data object Failure: SignUpResult
}
