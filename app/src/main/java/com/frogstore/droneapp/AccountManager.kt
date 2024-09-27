package com.frogstore.droneapp

import android.app.Activity
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException

class AccountManager (
    private val activity : Activity
) {
    private val credentialManager = CredentialManager.create(activity)

    suspend fun signUp(username: String, password: String): SignUpResult {
        return try{
            credentialManager.createCredential(
                context = activity,
                request = CreatePasswordRequest(
                    id = username,
                    password = password
                )
            )
            SignUpResult.Success(username)
        }catch(e: CreateCredentialCancellationException) {
            e.printStackTrace()
            SignUpResult.Cancelled
        }catch(e: CreateCredentialException) {
            e.printStackTrace()
            SignUpResult.Failure
        }
    }
}
