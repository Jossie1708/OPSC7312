package com.frogstore.droneapp.Fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.frogstore.droneapp.UserDetails.AccountManager
import com.frogstore.droneapp.R
import com.frogstore.droneapp.SideMenuNavBarActivity
import com.frogstore.droneapp.UserDetails.SignUpResult
import com.frogstore.droneapp.UserDetails.LoginViewModel
import kotlinx.coroutines.launch
import com.frogstore.droneapp.UserDetails.LoginAction
import com.frogstore.droneapp.UserDetails.LoginViewModelFactory
import com.frogstore.droneapp.UserDetails.UserSessionManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.lang.reflect.Method
import java.nio.charset.Charset


class RegisterFragment : Fragment() {
    private lateinit var accountManager: AccountManager
    private lateinit var auth: FirebaseAuth
    private val loginViewModel: LoginViewModel by activityViewModels { LoginViewModelFactory(requireContext()) }
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_register, container, false)
        val btnRegister = layout.findViewById<Button>(R.id.btnRegister)
        val usernameField = layout.findViewById<EditText>(R.id.txtRegUsername)
        val passwordField = layout.findViewById<EditText>(R.id.txtRegPassword)
        val emailField = layout.findViewById<EditText>(R.id.txtRegEmail)
        val confirmPasswordField = layout.findViewById<EditText>(R.id.txtRegConfirm)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize the AccountManager with the current Activity's context.
        accountManager = AccountManager(requireActivity())

        // Set up register button click listener
        btnRegister.setOnClickListener {
            // Capture the text entered in the username, password, confirm password, and email fields.
             username = usernameField.text.toString()
             password = passwordField.text.toString()
             val confirmPassword = confirmPasswordField.text.toString()
             email = emailField.text.toString()


            // Check if any of the fields (username, password, confirm password, email) are empty.
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty())
            {
                // If any field is empty, show a toast message to notify the user.
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Check if the entered password matches the confirmed password.
            if (password == confirmPassword)
            {
                lifecycleScope.launch {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->


                            if (task.isSuccessful)
                            {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = auth.currentUser
                                val intent = Intent(activity, SideMenuNavBarActivity::class.java)
                                startActivity(intent)
                            }


                            else
                            {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT,).show()
                            }
                        }
                }
            }


            else
            {
                // If the passwords do not match, show a toast message to inform the user.
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
        return layout
    }
}
