package com.frogstore.droneapp.UserDetails

import android.app.Activity
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import android.os.Bundle

class AccountManager(private val activity: Activity) {

    private val credentialManager = CredentialManager.create(activity)

    suspend fun signUp(username: String, password: String, email: String): SignUpResult
    {
        return try
        {
            val request = CreatePasswordRequest(username, password)

            credentialManager.createCredential(context = activity, request = request)

            SignUpResult.Success(username, email)

        }

        catch (e: CreateCredentialCancellationException)
        {
            e.printStackTrace()
            SignUpResult.Cancelled
        }

        catch (e: CreateCredentialException)
        {
            e.printStackTrace()
            SignUpResult.Failure
        }
    }
}