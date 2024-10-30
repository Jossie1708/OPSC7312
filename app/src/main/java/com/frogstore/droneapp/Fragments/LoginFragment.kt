package com.frogstore.droneapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.frogstore.droneapp.AccountManager
import com.frogstore.droneapp.R
import com.frogstore.droneapp.SignUpResult
import com.frogstore.droneapp.LoginViewModel
import kotlinx.coroutines.launch
import com.frogstore.droneapp.LoginAction
import com.frogstore.droneapp.SideMenuNavBarActivity
import com.frogstore.droneapp.UserSessionManager

class LoginFragment : androidx.fragment.app.Fragment() {

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

        val fingerprint = layout.findViewById<TextView>(R.id.lblLoginFingerprint)
        val lblForgotPassword = layout.findViewById<TextView>(R.id.lblForgotPassword)

        lblForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Feature coming soon in part 3!", Toast.LENGTH_SHORT).show()
        }

        fingerprint.setOnClickListener{
            Toast.makeText(requireContext(), "Fingerprint feature coming soon!", Toast.LENGTH_SHORT).show()
        }
        accountManager = AccountManager(requireActivity())

        btnLogin.setOnClickListener {
            val username = emailField.text.toString()
            val password = passwordField.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidEmail(username)) {
                Toast.makeText(requireContext(), "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = accountManager.signUp(username, password)
                handleSignUpResult(result)
            }
        }
        return layout
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun handleSignUpResult(result: SignUpResult) {
        when (result) {
            is SignUpResult.Success -> {
                loginViewModel.onAction(LoginAction.OnSignUp(result))


                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()

                val userSessionManager = UserSessionManager(requireContext())
                userSessionManager.saveUserSession(result.username, result.username)

                val intent = Intent(activity, SideMenuNavBarActivity::class.java)
                startActivity(intent)

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
