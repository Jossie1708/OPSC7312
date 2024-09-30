package com.frogstore.droneapp.UserDetails

import android.app.Activity
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException

// The `AccountManager` class is responsible for managing user account operations,
// particularly sign-up functionality using the Android Credential Manager API.
class AccountManager (
    // The `activity` parameter is the context of the Activity that will be used
    // to perform credential management operations. It is required for creating the
    // CredentialManager instance.
    private val activity : Activity
) {
    // `credentialManager` is an instance of `CredentialManager`,
    // which is used to interact with the system's credential management features.
    // It is initialized using the provided activity context.
    private val credentialManager = CredentialManager.create(activity)

    // The `signUp` function is a suspend function that handles the sign-up process.
    // It takes a username and password as parameters and returns a `SignUpResult`.
    suspend fun signUp(username: String, password: String): SignUpResult {
        return try {
            // Create a new credential using the provided username and password.
            // This will attempt to store the user's credentials securely using the
            // Android Credential Manager.
            credentialManager.createCredential(
                context = activity, // The context from the activity.
                request = CreatePasswordRequest(
                    id = username, // The username to be used as the credential ID.
                    password = password // The password associated with the username.
                )
            )
            // If the credential creation is successful, return a success result with the username.
            SignUpResult.Success(username)
        } catch (e: CreateCredentialCancellationException) {
            // This exception is thrown if the credential creation process is cancelled by the user.
            // Print the stack trace for debugging purposes.
            e.printStackTrace()
            // Return a cancelled result, indicating that the sign-up was not completed.
            SignUpResult.Cancelled
        } catch (e: CreateCredentialException) {
            // This exception is thrown for any other errors that occur during credential creation.
            // Print the stack trace for debugging purposes.
            e.printStackTrace()
            // Return a failure result, indicating that the sign-up attempt failed.
            SignUpResult.Failure
        }
    }
}
