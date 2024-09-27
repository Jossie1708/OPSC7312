package com.frogstore.droneapp.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.frogstore.droneapp.R

class SettingsFragment : Fragment() {

    private lateinit var switchDarkTheme: Switch
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_settings, container, false)

        val btnDetails = layout.findViewById<Button>(R.id.btnAccountDetails)
        val btnPass = layout.findViewById<Button>(R.id.btnSetPassword)
        val btnFingerprint = layout.findViewById<Button>(R.id.btnSetFingerprint)

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        switchDarkTheme = layout.findViewById(R.id.switch1)

        // Set the switch state based on saved preference
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        switchDarkTheme.isChecked = isDarkTheme

        // Switch listener to change theme
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("isDarkTheme", isChecked).apply()
            // Restart the activity to apply the new theme
            requireActivity().recreate()
        }



        btnDetails.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_accountDetailsFragment)
        }

        btnPass.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_updatePasswordFragment)
        }

        btnFingerprint.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_setFingerprintFragment)
        }

        // Return the inflated layout
        return layout
    }


}
