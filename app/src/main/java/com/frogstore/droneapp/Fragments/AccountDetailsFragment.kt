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
import com.frogstore.droneapp.UserSessionManager
import com.frogstore.droneapp.databinding.ActivitySideMenuNavBarBinding
import org.json.JSONException

class AccountDetailsFragment : Fragment() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var editTextNewUsername: EditText
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

        // Initialize the RequestQueue for making network requests
        requestQueue = Volley.newRequestQueue(requireContext())

        // Inflate the binding for the side menu navigation bar
        binding = ActivitySideMenuNavBarBinding.inflate(inflater)

        buttonUpdateUsername.setOnClickListener {
            val newUsername = editTextNewUsername.text.toString()
            val userSessionManager = UserSessionManager(requireContext())
            val email = userSessionManager.getUserSession()?.loggedInUser // Get the logged-in user's email

            if (email != null && newUsername.isNotEmpty()) {
                updateUsername(email, newUsername)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid username", Toast.LENGTH_SHORT).show()
            }
        }

        return layout
    }

    private fun updateHeader(newUsername: String) {
        // Initialize UserSessionManager
        val userSessionManager = UserSessionManager(requireContext())

        // Retrieve the user session
        val loginState = userSessionManager.getUserSession()

        // Initialize the TextViews from the header layout
        val navView = binding.navView // Assuming navView is your NavigationView
        val headerView = navView.getHeaderView(0) // Get the first header view
        name = headerView.findViewById(R.id.txtLoginUsername)
        email = headerView.findViewById(R.id.txtLoginEmail)

        // Check if user session is available
        loginState?.let {
            email.text = it.loggedInUser // Assuming this is the user's email
            name.text = newUsername // Display the updated username
        } ?: run {
            // Handle the case where the user is not signed in
            name.text = getString(R.string.sign_in_name) // Default name
            email.text = getString(R.string.sign_in_email) // Default email
        }
    }

    private fun updateUsername(email: String, newUsername: String) {
        val url = "https://frogtrackapi2-bjaufahwavexambv.eastasia-01.azurewebsites.net/updateUsername?email=$email&newUsername=$newUsername"

        val params = HashMap<String, String>()
        params["email"] = email
        params["newUsername"] = newUsername

        val stringRequest = object : StringRequest(
            Method.PUT, url,
            { response ->
                Log.d("API Response", response) // Log the response
                try {
                    val username = response.replace("\"", "") // Remove double quotes
                    Toast.makeText(requireContext(), "Username updated to $username", Toast.LENGTH_SHORT).show()

                    updateHeader(newUsername)
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
