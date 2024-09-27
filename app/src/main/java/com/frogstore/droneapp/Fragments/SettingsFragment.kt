package com.frogstore.droneapp.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.frogstore.droneapp.R
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var switchDarkTheme: Switch
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var radioGroupLanguages: RadioGroup

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
        radioGroupLanguages = layout.findViewById(R.id.radioGroupLanguages)

        // Set the initial state of the radio buttons based on saved preference
        val savedLanguage = sharedPreferences.getString("appLanguage", "en")
        if (savedLanguage == "en") {
            radioGroupLanguages.check(R.id.rbtnEnglish)
        } else {
            radioGroupLanguages.check(R.id.rbtnAfrikaans)
        }

        // Set up listeners for language selection
        radioGroupLanguages.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbtnEnglish -> setLocale("en")
                R.id.rbtnAfrikaans -> setLocale("af")
            }
        }

        // Set the switch state based on saved preference
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        switchDarkTheme.isChecked = isDarkTheme

        // Switch listener to change theme
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("isDarkTheme", isChecked).apply()
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

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the language preference
        sharedPreferences.edit().putString("appLanguage", languageCode).apply()

        // Restart the activity to apply changes
        requireActivity().recreate()
   }
}
