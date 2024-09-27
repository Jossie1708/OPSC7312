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
import com.frogstore.droneapp.AccountManager
import com.frogstore.droneapp.R
import com.frogstore.droneapp.SignUpResult
import com.frogstore.droneapp.LoginViewModel
import kotlinx.coroutines.launch
import com.frogstore.droneapp.LoginAction


class RegisterFragment : Fragment() {

    private lateinit var accountManager: AccountManager
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_register, container, false)

        val btnRegister = layout.findViewById<Button>(R.id.btnRegister)
        val usernameField = layout.findViewById<EditText>(R.id.txtRegUsername)
        val passwordField = layout.findViewById<EditText>(R.id.txtRegPassword)
        val confirmPasswordField = layout.findViewById<EditText>(R.id.txtRegConfirm)

        // Initialize AccountManager with Activity
        accountManager = AccountManager(requireActivity())

        // Set up register button click listener
        btnRegister.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            // Check if passwords match
            if (password == confirmPassword) {
                // Launch a coroutine to handle registration
                lifecycleScope.launch {
                    val result = accountManager.signUp(username, password)
                    handleSignUpResult(result)
                }
            } else {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }

        return layout
    }

    // Handle the result of sign-up
    private fun handleSignUpResult(result: SignUpResult) {
        when (result) {
            is SignUpResult.Success -> {
                loginViewModel.onAction(LoginAction.OnSignUp(result))
                Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()
                // Navigate to the next fragment
            }
            SignUpResult.Failure -> {
                Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
            }
            SignUpResult.Cancelled -> {
                Toast.makeText(requireContext(), "Registration cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
