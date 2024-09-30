package com.frogstore.droneapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.frogstore.droneapp.UserDetails.AccountManager
import com.frogstore.droneapp.R
import com.frogstore.droneapp.UserDetails.SignUpResult
import com.frogstore.droneapp.UserDetails.LoginViewModel
import kotlinx.coroutines.launch
import com.frogstore.droneapp.UserDetails.LoginAction

// RegisterFragment is a fragment responsible for handling the user registration process.
class RegisterFragment : Fragment() {
    // Reference to AccountManager, which handles account operations such as sign-up.
    private lateinit var accountManager: AccountManager

    // Shared ViewModel to manage the login-related data across activities and fragments.
    private val loginViewModel: LoginViewModel by activityViewModels()

    // This method inflates the layout and sets up event listeners for user actions in the fragment.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment and store it in 'layout'.
        val layout = inflater.inflate(R.layout.fragment_register, container, false)

        // Find views by their IDs to access the UI elements in the layout.
        val btnRegister = layout.findViewById<Button>(R.id.btnRegister)
        val usernameField = layout.findViewById<EditText>(R.id.txtRegUsername)
        val passwordField = layout.findViewById<EditText>(R.id.txtRegPassword)
        val confirmPasswordField = layout.findViewById<EditText>(R.id.txtRegConfirm)
        val emailField = layout.findViewById<EditText>(R.id.txtRegEmail)

        // Initialize the AccountManager with the current Activity's context.
        accountManager = AccountManager(requireActivity())

        // Set up register button click listener
        btnRegister.setOnClickListener {
            // Capture the text entered in the username, password, confirm password, and email fields.
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()
            val email = emailField.text.toString()

            // Check if any of the fields (username, password, confirm password, email) are empty.
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
                // If any field is empty, show a toast message to notify the user.
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the entered password matches the confirmed password.
            if (password == confirmPassword) {
                // If the passwords match, launch a coroutine to handle the sign-up process asynchronously
                lifecycleScope.launch {
                    // Use the accountManager to perform the sign-up operation with the provided username and password.
                    val result = accountManager.signUp(username, password)
                    // Handle the result of the sign-up process.
                    handleSignUpResult(result)
                }
            } else {
                // If the passwords do not match, show a toast message to inform the user.
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
        // Return the inflated layout as the root view for this fragment.
        return layout
    }

    // This function handles the result of the sign-up operation.
    private fun handleSignUpResult(result: SignUpResult) {
        when (result) {
            // If the sign-up is successful:
            is SignUpResult.Success -> {
                // Notify the shared ViewModel that the sign-up was successful.
                loginViewModel.onAction(LoginAction.OnSignUp(result))
                // Display a success message to the user.
                Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to the next screen or fragment after successful registration.
            }
            // If the sign-up fails:
            SignUpResult.Failure -> {
                // Display an error message indicating that the registration failed.
                Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
            }
            // If the sign-up process is cancelled by the user or system:
            SignUpResult.Cancelled -> {
                // Display a message indicating that the registration was cancelled.
                Toast.makeText(requireContext(), "Registration cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
