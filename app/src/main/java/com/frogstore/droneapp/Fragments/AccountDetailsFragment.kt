package com.frogstore.droneapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.frogstore.droneapp.R
import com.frogstore.droneapp.UserDetails.UserSessionManager
import com.frogstore.droneapp.databinding.FragmentAccountDetailsBinding

class AccountDetailsFragment : Fragment() {
    private lateinit var binding: FragmentAccountDetailsBinding
    private lateinit var name: TextView
    private lateinit var email: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAccountDetailsBinding.inflate(inflater, container, false)
        // Return the inflated layout
        updateHeader()
        return binding.root
    }

    private fun updateHeader() {
        // Initialize UserSessionManager
        val userSessionManager = UserSessionManager(requireContext())

        // Retrieve the user session
        val loginState = userSessionManager.getUserSession()

        // Initialize the TextViews from the header layout
        name = binding.txtAccUsername
        email = binding.txtAccEmail

        // Check if user session is available
        loginState?.let {
            // Set the name and email from the user session
            name.text = it.loggedInUser // Assuming this is the username
            email.text = it.email//it.email // Assuming this is the user's email
        } ?: run {
            // Handle the case where the user is not signed in
            name.text = getString(R.string.sign_in_name) // Default name
            email.text = getString(R.string.sign_in_email) // Default email
        }
    }
}