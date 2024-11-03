package com.frogstore.droneapp.Fragments

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


class LoginFragment : Fragment() {

    private lateinit var accountManager: AccountManager
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var requestQueue: RequestQueue
    private lateinit var googleSignInClient: GoogleSignInClient  // Declare GoogleSignInClient
    private lateinit var biometricPromptManager: BiometricPromptManager



    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_login, container, false)

        val googleSignInClient = GoogleSignInClient(requireContext())
        biometricPromptManager = BiometricPromptManager(requireActivity())
        observeBiometricPromptResults()
        val btnLogin = layout.findViewById<Button>(R.id.btnLogin)
        val btnGoogleSignIn = layout.findViewById<Button>(R.id.btnGoogleSignIn)
        val emailField = layout.findViewById<EditText>(R.id.txtLoginEmail)
        val passwordField = layout.findViewById<EditText>(R.id.txtLoginPassword)
        val lblForgotPassword = layout.findViewById<TextView>(R.id.lblForgotPassword)

        //Google SSO button signin
        btnGoogleSignIn.setOnClickListener {
            lifecycleScope.launch {
                // Attempt Google sign-in
                googleSignInClient.signIn()

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
        }

        //EMAIL PASSWORD SIGNIN

        //implement today
        lblForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Feature coming soon in part 3!", Toast.LENGTH_SHORT).show()
        }

        requestQueue = Volley.newRequestQueue(requireContext())

        accountManager = AccountManager(requireActivity())

        btnLogin.setOnClickListener {
            val email = emailField.text.toString()
            var username: String = ""

            getUsername(email)
            {
                result -> username = result

                validateUser (email, passwordField.text.toString())
                {
                    isValidUser  ->
                    if (email.isNotBlank() && passwordField.text.toString().isNotBlank() && username.isNotBlank())
                    {
                        if (isValidUser )
                        {
                            lifecycleScope.launch {
                                val result = accountManager.signUp(username, passwordField.text.toString(), email)

                                handleSignUpResult(result)
                            }
                        }
                        else
                        {
                            Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else
                    {
                        Toast.makeText(requireContext(), "Please enter both email, username and password.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return layout
    }

    private fun validateUser(email: String, password: String, callback: (Boolean) -> Unit)
    {
        val url = "https://frogtrackapi2-bjaufahwavexambv.eastasia-01.azurewebsites.net/validateUser?email=$email&password=$password"

        val stringRequest = object : StringRequest(
            Method.GET, url,
            { response ->
                val isValidUser = response.toBoolean()
               // Toast.makeText(requireContext(), "User validation successful", Toast.LENGTH_SHORT).show()
                callback(isValidUser)
            },
            { error ->
              //  Toast.makeText(requireContext(), "Error validating user: ${error.message}", Toast.LENGTH_SHORT).show()
                callback(false)
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun getUsername(email: String, callback: (String) -> Unit)
    {
        val url = "https://frogtrackapi2-bjaufahwavexambv.eastasia-01.azurewebsites.net/getUsername?email=$email"

        val stringRequest = object : StringRequest(
            Method.GET, url,
            { response ->
                val username = response.replace("\"", "") // Remove double quotes
                callback(username)
            },
            { error ->
               // Toast.makeText(requireContext(), "Error retrieving username: ${error.message}", Toast.LENGTH_SHORT).show()
                callback("")
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun handleSignUpResult(result: SignUpResult)
    {
        when (result)
        {
            is SignUpResult.Success ->{
                loginViewModel.onAction(OnSignUp(result))

                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()

                val userSessionManager = UserSessionManager(requireContext())
                userSessionManager.saveUserSession(result.username, result.email)

                val intent = Intent(activity, SideMenuNavBarActivity::class.java)
                startActivity(intent)

                requireActivity().finish()
            }
            is SignUpResult.Failure -> {
                Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
            }
            else -> {
                SignUpResult.Cancelled
                Toast.makeText(requireContext(), "Login cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun observeBiometricPromptResults() {
        // Observe the prompt results in the fragment's lifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            biometricPromptManager.promptResults.collect { result ->
                when (result) {
                    is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                        // Handle successful authentication
                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, SideMenuNavBarActivity::class.java)
                        startActivity(intent)
                    }
                    is BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                        // Handle failed authentication
                        Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()

                    }
                    is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                        // Handle authentication error, show error message
                        Toast.makeText(requireContext(), "Login error", Toast.LENGTH_SHORT).show()

                    }
                    is BiometricPromptManager.BiometricResult.HardwareUnavailable,
                    is BiometricPromptManager.BiometricResult.FeatureUnavailable,
                    is BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                        // Handle cases where biometric is unavailable or not set up
                    }
                }
            }
        }
    }
}
