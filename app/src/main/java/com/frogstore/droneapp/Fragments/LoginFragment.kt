package com.frogstore.droneapp.Fragments

// Required imports for Android components, ViewModel, navigation, and coroutines
import android.content.Intent
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import com.frogstore.droneapp.AccountManager
import com.frogstore.droneapp.R
import com.frogstore.droneapp.SignUpResult
import com.frogstore.droneapp.LoginViewModel
import kotlinx.coroutines.launch
import com.frogstore.droneapp.LoginAction
import com.frogstore.droneapp.SideMenuNavBarActivity
import com.frogstore.droneapp.UserSessionManager

// This Fragment handles user login by taking their credentials and verifying them
class LoginFragment : Fragment() {

    // AccountManager handles sign-up or login functionality, allowing interaction with authentication services
    private lateinit var accountManager: AccountManager

    // ViewModel is shared across multiple fragments; it manages UI-related data in a lifecycle-conscious way
    private val loginViewModel: LoginViewModel by activityViewModels()

    // This method inflates the login screen and sets up event listeners
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the login fragment from XML
        val layout = inflater.inflate(R.layout.fragment_login, container, false)

        // Initialize UI elements: button and text fields for user input (email and password)
        val btnLogin = layout.findViewById<Button>(R.id.btnLogin)
        val emailField = layout.findViewById<EditText>(R.id.txtLoginEmail)
        val passwordField = layout.findViewById<EditText>(R.id.txtLoginPassword)

        val fingerprint = layout.findViewById<TextView>(R.id.lblLoginFingerprint)

        fingerprint.setOnClickListener{
            Toast.makeText(requireContext(), "Fingerprint feature coming soon!", Toast.LENGTH_SHORT).show()
        }
        // AccountManager is used to manage user accounts (e.g., sign-up, login)
        // It is initialized with the context of the current activity
        accountManager = AccountManager(requireActivity())

        // Set a click listener for the login button
        btnLogin.setOnClickListener {
            // Get the user's input for the email (username) and password
            val username = emailField.text.toString()
            val password = passwordField.text.toString()

            // Input validation: ensure both email and password fields are filled
            if (username.isEmpty() || password.isEmpty()) {
                // If either field is empty, show a message to the user
                Toast.makeText(requireContext(), "Please enter both email and password.", Toast.LENGTH_SHORT).show()
                // Return early to avoid attempting to login with missing data
                return@setOnClickListener
            }
            if (!isValidEmail(username)) {
                Toast.makeText(requireContext(), "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Use Kotlin Coroutines to handle asynchronous tasks like logging in
            // `lifecycleScope.launch` starts a coroutine tied to the lifecycle of the fragment
            lifecycleScope.launch {
                // Call the signUp method of the AccountManager with the provided username and password
                val result = accountManager.signUp(username, password)

                // Handle the result of the login/sign-up process
                handleSignUpResult(result)
            }
        }

        // Return the inflated layout as the view for this fragment
        return layout
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // This method processes the result of the sign-up/login process
    private fun handleSignUpResult(result: SignUpResult) {
        // Handle different outcomes of the sign-up process (Success, Failure, Cancelled)
        when (result) {
            // If sign-up is successful
            is SignUpResult.Success -> {
                // Notify the ViewModel that the user has signed up successfully
                loginViewModel.onAction(LoginAction.OnSignUp(result))


                // Show a toast message to inform the user that login was successful
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()

                // Initialize UserSessionManager
                val userSessionManager = UserSessionManager(requireContext())
                // Save the user session (you might need to extract email from result)
                userSessionManager.saveUserSession(result.username, result.username)

                // Navigate to the next screen (home screen or main app screen) after successful login
                // Create an Intent to start the SideMenuNavBarActivity (the main application screen)
                val intent = Intent(activity, SideMenuNavBarActivity::class.java)
                // Start the new activity
                startActivity(intent)

                // Finish the current activity to prevent the user from returning to the login screen
                requireActivity().finish()
            }
            // If sign-up failed (invalid credentials, server issue, etc.)
            SignUpResult.Failure -> {
                // Inform the user that the login process failed via a toast message
                Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
            }
            // If sign-up was cancelled by the user (e.g., back pressed during the process)
            SignUpResult.Cancelled -> {
                // Notify the user that the login was cancelled
                Toast.makeText(requireContext(), "Login cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
