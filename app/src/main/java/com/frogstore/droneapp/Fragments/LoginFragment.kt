package com.frogstore.droneapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_login, container, false)

        googleSignInClient = GoogleSignInClient(requireContext())

        val btnLogin = layout.findViewById<Button>(R.id.btnLogin)
        val btnGoogleSignIn = layout.findViewById<Button>(R.id.btnGoogleSignIn)  // Google sign-in button
        val emailField = layout.findViewById<EditText>(R.id.txtLoginEmail)
        val passwordField = layout.findViewById<EditText>(R.id.txtLoginPassword)
        val fingerprint = layout.findViewById<TextView>(R.id.lblLoginFingerprint)
        val lblForgotPassword = layout.findViewById<TextView>(R.id.lblForgotPassword)

        btnGoogleSignIn.setOnClickListener {
            lifecycleScope.launch {
                try {

                    //if signed in run if statement
                    if (googleSignInClient.isSignedIn()) {
                        Toast.makeText(context, "Already signed in with Google", Toast.LENGTH_SHORT).show()
                    }

                    //if not singed in run else statement and call the signIn method from GoogleSignInClient class
                    else {

                        val signInResult = googleSignInClient.signIn()

                        //If Sign in is successfull display toast and navigate
                        if (signInResult) {
                            Toast.makeText(context, "Google sign-in successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, SideMenuNavBarActivity::class.java)
                            startActivity(intent)
                        }

                        //If sign in was unsuccessful display Google sign-in failed toast
                        else {
                            Toast.makeText(context, "Google sign-in failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                //If there was an error throw exception
                catch (e: Exception) {
                    Log.e("LoginFragment", "Google sign-in error: ${e.message}")
                }
            }
        }

        //implement today
        lblForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Feature coming soon in part 3!", Toast.LENGTH_SHORT).show()
        }


        requestQueue = Volley.newRequestQueue(requireContext())


        //implement today
        fingerprint.setOnClickListener{
            Toast.makeText(requireContext(), "Fingerprint feature coming soon!", Toast.LENGTH_SHORT).show()
        }


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
            SignUpResult.Cancelled -> {
                Toast.makeText(requireContext(), "Login cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
