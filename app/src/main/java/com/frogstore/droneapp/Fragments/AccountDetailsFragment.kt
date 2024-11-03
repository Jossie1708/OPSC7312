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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.frogstore.droneapp.R
import com.frogstore.droneapp.UserDetails.LoginViewModel
import com.frogstore.droneapp.UserDetails.UserSessionManager
import com.frogstore.droneapp.databinding.ActivitySideMenuNavBarBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.json.JSONException

class AccountDetailsFragment : Fragment() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var editTextNewUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonUpdateUsername: Button
    private lateinit var binding: ActivitySideMenuNavBarBinding
    private lateinit var name: TextView
    private lateinit var email: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_account_details, container, false)

        // Initialize the UI components
        editTextNewUsername = layout.findViewById(R.id.txtAccUsername)
        buttonUpdateUsername = layout.findViewById(R.id.btnUpdateAccountDetails)
        editTextEmail = layout.findViewById(R.id.txtAccEmail)

        val loginViewModel = LoginViewModel(requireActivity().application)

        // Retrieve the user session
        val auth = Firebase.auth
            //val loginState = loginViewModel.getUserSession()
        val email = auth.currentUser?.email // Get the logged-in user's email

        // Load the user's email into the editText and disable it
        editTextEmail.setText(email)
        editTextEmail.isEnabled = false

        // Initialize the RequestQueue for making network requests
        requestQueue = Volley.newRequestQueue(requireContext())

        // Inflate the binding for the side menu navigation bar
        binding = ActivitySideMenuNavBarBinding.inflate(inflater)

        buttonUpdateUsername.setOnClickListener {
            val newUsername = editTextNewUsername.text.toString()

            if (newUsername.isNotEmpty()) {
                updateUsername(email, newUsername)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid username", Toast.LENGTH_SHORT).show()
            }
        }

        return layout
    }

    private fun updateUsername(email: String?, newUsername: String) {
        val url = "https://frogtrackapi2-bjaufahwavexambv.eastasia-01.azurewebsites.net/updateUsername?email=$email&newUsername=$newUsername"

        val params = HashMap<String, String>()
        params["email"] = email ?: ""
        params["newUsername"] = newUsername

        val stringRequest = object : StringRequest(
            Method.PUT, url,
            { response ->
                Log.d("API Response", response) // Log the response
                try {
                    val username = newUsername
                    Toast.makeText(requireContext(), "Username updated to $username", Toast.LENGTH_SHORT).show()

                    // Update the user's session with the new username
                    val loginViewModel = LoginViewModel(requireActivity().application)
                    email?.let { loginViewModel.login(username, it) }
                    // Restart the activity to apply changes
                    requireActivity().recreate()

                } catch (e: JSONException) {
                    Log.e("JSON Error", "Invalid response format: $response", e)
                    Toast.makeText(requireContext(), "Invalid response format: $response", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("API Error", "Error updating username", error)
                Toast.makeText(requireContext(), "Error updating username: ${error.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded" // Adjust the content type as needed
                return headers
            }
        }

        requestQueue.add(stringRequest)
    }

}