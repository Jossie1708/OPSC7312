package com.frogstore.droneapp

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException


class GoogleSignInClient(private val context: Context, )
{

    private val tag = "GoogleAuthClient: "
    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()

    //checks if user is signed into the firebase database
    fun isSignedIn(): Boolean
    {
        if (firebaseAuth.currentUser != null)
        {
            println(tag + "already signed in")
            return true
        }
        return false
    }

    suspend fun signIn(): Boolean {
        if (isSignedIn()) {
            return true
        }

        //result holds the user data during the sign-in process
        try
        {
            val result = buildCredentialRequest() //gets the user data here
            return handleSignIn(result) //Handles the Sign-in procedure using the user credentials saved in result
        }

        catch (e: Exception)
        {
            e.printStackTrace()
            if (e is CancellationException) throw e

            println(tag + "signIn error: ${e.message}")
            Log.e(tag, "signIn error", e) // Add this line to log the exception
            return false
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Boolean
    {
        val credential = result.credential //extracts the result credential data

        //this if statement looks to see if the credential holds google ID credentials for authentication
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)
        {
            try
            {
                //Converts the credential data into a GoogleIdTokenCredential object. This object contains information about the user’s Google ID token.
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                //prints user data for error handling
                println(tag + "name: ${tokenCredential.displayName}")
                println(tag + "email: ${tokenCredential.id}")
                println(tag + "image: ${tokenCredential.profilePictureUri}")

                //firebase sign-in
                val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                return authResult.user != null


            }

            catch (e: GoogleIdTokenParsingException)
            {
                println(tag + "GoogleIdTokenParsingException: ${e.message}")
                return false
            }

        }
        else
        {
            println(tag + "credential is not GoogleIdTokenCredential")
            return false
        }
    }

    //method gets the users Google Data
    private suspend fun buildCredentialRequest(): GetCredentialResponse
    {
        val request = GetCredentialRequest.Builder().addCredentialOption(GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
            .setServerClientId("266453904291-0313i7jjt13ndgcb3937o3uau3eca7gv.apps.googleusercontent.com").setAutoSelectEnabled(false).build()).build()

        return credentialManager.getCredential(request = request, context = context)
    }

    suspend fun signOut()
    {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        firebaseAuth.signOut()
    }

}