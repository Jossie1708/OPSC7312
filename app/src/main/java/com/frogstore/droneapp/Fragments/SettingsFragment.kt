package com.frogstore.droneapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.frogstore.droneapp.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_settings, container, false)

        val btnDetails = layout.findViewById<Button>(R.id.btnAccountDetails)
        val btnPass = layout.findViewById<Button>(R.id.btnSetPassword)
        val btnFingerprint = layout.findViewById<Button>(R.id.btnSetFingerprint)

        btnDetails.setOnClickListener {
            // Navigate to AccountDetailsFragment
            findNavController().navigate(R.id.action_nav_settings_to_accountDetailsFragment)
        }

        btnPass.setOnClickListener {
            // Navigate to UpdatePasswordFragment
            findNavController().navigate(R.id.action_nav_settings_to_updatePasswordFragment)
        }

        btnFingerprint.setOnClickListener {
            // Navigate to SetFingerprintFragment
            findNavController().navigate(R.id.action_nav_settings_to_setFingerprintFragment)
        }

        // Return the inflated layout
        return layout
    }
}
