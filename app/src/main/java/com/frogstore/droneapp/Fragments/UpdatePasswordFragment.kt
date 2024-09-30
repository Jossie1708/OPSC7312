package com.frogstore.droneapp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.frogstore.droneapp.R
import com.frogstore.droneapp.UserSessionManager
import org.json.JSONException

class UpdatePasswordFragment : Fragment() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var editTextOldPassword: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var buttonUpdatePassword: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_update_password, container, false)

        // Initialize UI components
        editTextOldPassword = layout.findViewById(R.id.txtOldPassword)
        editTextNewPassword = layout.findViewById(R.id.txtNewPassword)
        buttonUpdatePassword = layout.findViewById(R.id.btnUpdatePassword)

        // Initialize the RequestQueue for making network requests
        requestQueue = Volley.newRequestQueue(requireContext())

        buttonUpdatePassword.setOnClickListener {
            val oldPassword = editTextOldPassword.text.toString()
            val newPassword = editTextNewPassword.text.toString()
            val userSessionManager = UserSessionManager(requireContext())
            val email = userSessionManager.getUserSession()?.loggedInUser // Get the logged-in user's email

            if (email != null && oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                updatePassword(email, oldPassword, newPassword)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return layout
    }

    private fun updatePassword(email: String, oldPassword: String, newPassword: String) {
        val url = "https://frogtrackapi2-bjaufahwavexambv.eastasia-01.azurewebsites.net/updatePassword?email=$email&oldPass=$oldPassword&newPass=$newPassword"

        val params = HashMap<String, String>()
        params["email"] = email
        params["oldPass"] = oldPassword
        params["newPass"] = newPassword

        val stringRequest = object : StringRequest(
            Method.PUT, url,
            { response ->
                Log.d("API Response", response) // Log the response
                try {
                    // Assuming response indicates success, adjust as necessary
                    Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    Log.e("JSON Error", "Invalid response format: $response", e)
                    Toast.makeText(requireContext(), "Invalid response format: $response", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("API Error", "Error updating password", error)
                Toast.makeText(requireContext(), "Error updating password: ${error.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
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
