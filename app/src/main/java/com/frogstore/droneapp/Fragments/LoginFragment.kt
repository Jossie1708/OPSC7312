package com.frogstore.droneapp.Fragments

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.frogstore.droneapp.AccountManager
import com.frogstore.droneapp.R
import com.frogstore.droneapp.SignUpResult
import com.frogstore.droneapp.LoginViewModel
import kotlinx.coroutines.launch
import com.frogstore.droneapp.LoginAction
import com.frogstore.droneapp.SideMenuNavBarActivity

class LoginFragment : Fragment() {

    private lateinit var accountManager: AccountManager
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_login, container, false)

        val btnLogin = layout.findViewById<Button>(R.id.btnLogin)
        val emailField = layout.findViewById<EditText>(R.id.txtLoginEmail)
        val passwordField = layout.findViewById<EditText>(R.id.txtLoginPassword)

        // Initialize AccountManager with Activity
        accountManager = AccountManager(requireActivity())

        // Set up login button click listener
        btnLogin.setOnClickListener {
            val username = emailField.text.toString()
            val password = passwordField.text.toString()

            // Launch a coroutine to handle login
            lifecycleScope.launch {
                val result = accountManager.signUp(username, password)
                handleSignUpResult(result)
            }
        }

        return layout
    }

    // Handle the result of sign-up
    private fun handleSignUpResult(result: SignUpResult) {
        when (result) {
            is SignUpResult.Success -> {
                loginViewModel.onAction(LoginAction.OnSignUp(result))
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                // Navigate to the home screen or the next fragment
                // Create an Intent to start HomeActivity
                val intent = Intent(activity, SideMenuNavBarActivity::class.java)
                startActivity(intent)
                // Finish the current activity
                requireActivity().finish()
            }
            SignUpResult.Failure -> {
                Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
            }
            SignUpResult.Cancelled -> {
                Toast.makeText(requireContext(), "Login cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
