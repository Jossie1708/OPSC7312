package com.frogstore.droneapp.Fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.collection.emptyLongSet
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.frogstore.droneapp.BiometricPromptManager
import com.frogstore.droneapp.UserDetails.AccountManager
import com.frogstore.droneapp.R
import com.frogstore.droneapp.UserDetails.SignUpResult
import com.frogstore.droneapp.UserDetails.LoginViewModel
import kotlinx.coroutines.launch
import com.frogstore.droneapp.SideMenuNavBarActivity
import com.frogstore.droneapp.UserDetails.LoginAction.*
import com.frogstore.droneapp.UserDetails.UserSessionManager
import com.frogstore.droneapp.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope


class LoginFragment : Fragment() {

    private lateinit var accountManager: AccountManager
    private lateinit var requestQueue: RequestQueue
    private lateinit var biometricPromptManager: BiometricPromptManager
    private lateinit var auth: FirebaseAuth



    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_login, container, false)
        auth = Firebase.auth

        val googleSignInClient = GoogleSignInClient(requireContext())
        biometricPromptManager = BiometricPromptManager(requireActivity())
        val btnLogin = layout.findViewById<Button>(R.id.btnLogin)
        val btnGoogleSignIn = layout.findViewById<Button>(R.id.btnGoogleSignIn)
        val emailField = layout.findViewById<EditText>(R.id.txtLoginEmail)
        val passwordField = layout.findViewById<EditText>(R.id.txtLoginPassword)
        val currentUser = auth.currentUser

        if (currentUser != null)
        {
            val intent = Intent(activity, SideMenuNavBarActivity::class.java)
            startActivity(intent)
        }


        //Google SSO button signin
        btnGoogleSignIn.setOnClickListener {
            lifecycleScope.launch {
                // Attempt Google sign-in
                googleSignInClient.signIn()

                if (googleSignInClient.isSingedIn())
                {


                    //auth biometrics
                    biometricPromptManager.showBiometricPrompt(
                        title = "Biometric Authentication",
                        description = "Please authenticate to proceed"
                    )
                    // Collect biometric results
                    biometricPromptManager.promptResults.collect { result ->
                        when (result) {
                            is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                                // Navigate on successful biometric authentication
                                val intent = Intent(activity, SideMenuNavBarActivity::class.java)
                                startActivity(intent)
                            }
                            is BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                                // Handle authentication failure (e.g., show a message)
                                Toast.makeText(activity, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                            is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                                // Handle authentication error (e.g., show a message)
                                Toast.makeText(activity, "Authentication error. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                            is BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                                // Handle case where hardware is unavailable
                                Toast.makeText(activity, "Biometric hardware unavailable.", Toast.LENGTH_SHORT).show()
                            }
                            is BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                                // Handle case where the feature is not available
                                Toast.makeText(activity, "Biometric feature not available.", Toast.LENGTH_SHORT).show()
                            }
                            is BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                                // Handle case where no biometric data is enrolled
                                Toast.makeText(activity, "No biometric data enrolled.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                else
                {
                }

            }
        }

        requestQueue = Volley.newRequestQueue(requireContext())

        accountManager = AccountManager(requireActivity())

        btnLogin.setOnClickListener {
            val email = emailField.text.toString()
            var password = passwordField.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                // Show error message for empty email
                Toast.makeText(
                    requireContext(),
                    "Email and password cannot be empty.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else{
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->

                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val intent = Intent(activity, SideMenuNavBarActivity::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                requireContext(),
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }
        return layout
    }
}
