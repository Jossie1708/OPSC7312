package com.frogstore.droneapp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.frogstore.droneapp.R
import com.frogstore.droneapp.UserDetails.LoginViewModel
import com.frogstore.droneapp.databinding.ActivitySideMenuNavBarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AccountDetailsFragment : Fragment() {

    private lateinit var editTextNewUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonUpdateUsername: Button
    private lateinit var binding: ActivitySideMenuNavBarBinding
    private lateinit var auth: FirebaseAuth // FirebaseAuth instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_account_details, container, false)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize the UI components
        editTextNewUsername = layout.findViewById(R.id.txtAccUsername)
        buttonUpdateUsername = layout.findViewById(R.id.btnUpdateAccountDetails)
        editTextEmail = layout.findViewById(R.id.txtAccEmail)

        // Load the user's email into the editText and disable it
        val email = auth.currentUser?.email // Get the logged-in user's email
        editTextEmail.setText(email)
        editTextEmail.isEnabled = false

        buttonUpdateUsername.setOnClickListener {
            val newUsername = editTextNewUsername.text.toString()

            if (newUsername.isNotEmpty()) {
                updateUsername(newUsername) // Pass the new username directly
            } else {
                Toast.makeText(requireContext(), "Please enter a valid username", Toast.LENGTH_SHORT).show()
            }
        }

        return layout
    }

    private fun updateHeader() {
        // Retrieve the user session and update the header views accordingly
        val navView = binding.navView // Assuming navView is your NavigationView
        val headerView = navView.getHeaderView(0) // Get the first header view
        val nameTextView = headerView.findViewById<TextView>(R.id.txtLoginUsername)

        val user = auth.currentUser
        if (user != null) {
            nameTextView.text = user.displayName // Update with the new display name
        } else {
            // Handle the case where the user is not signed in
            nameTextView.text = getString(R.string.sign_in_name) // Default name
        }
    }

    private fun updateUsername(newUsername: String) {
        // Update Firebase Authentication display name
        val user = auth.currentUser
        user?.let {
            // Create a UserProfileChangeRequest
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build()

            it.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Profile Update", "User profile updated.")
                        Toast.makeText(requireContext(), "Username updated successfully", Toast.LENGTH_SHORT).show()
                        updateHeader() // Update header with new username
                    } else {
                        Log.e("Profile Update Error", "Failed to update profile: ${task.exception?.message}")
                        Toast.makeText(requireContext(), "Error updating username: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } ?: run {
            Log.e("User Error", "No user is currently signed in.")
            Toast.makeText(requireContext(), "No user is currently signed in.", Toast.LENGTH_SHORT).show()
        }
    }
}
