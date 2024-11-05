package com.frogstore.droneapp.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat.recreate
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

        // Initialize SharedPreferences and UI components
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        switchDarkTheme = layout.findViewById(R.id.switchTheme)
        radioGroupLanguages = layout.findViewById(R.id.radioGroupLanguages)

        // Set up initial state for switches and radio buttons
        val savedLanguage = sharedPreferences.getString("appLanguage", "en") ?: "en"
        val currentLanguage = Locale.getDefault().language
        if (savedLanguage != currentLanguage) {
            // Only call setLocale if the language has changed
            setLocale(savedLanguage)
        }

        if (savedLanguage == "en") {
            radioGroupLanguages.check(R.id.rbtnEnglish)
        } else {
            radioGroupLanguages.check(R.id.rbtnAfrikaans)
        }

        // Set up listeners for language selection
        radioGroupLanguages.setOnCheckedChangeListener { _, checkedId ->
            val selectedLanguage = when (checkedId) {
                R.id.rbtnEnglish -> "en"
                R.id.rbtnAfrikaans -> "af"
                else -> savedLanguage
            }

            // Only call setLocale if the selected language is different
            if (selectedLanguage != savedLanguage) {
                setLocale(selectedLanguage)
            }
        }

        // Dark theme switch setup
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        switchDarkTheme.isChecked = isDarkTheme
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("isDarkTheme", isChecked).apply()
            requireActivity().recreate()
        }

        // Button navigation setup
        val btnDetails = layout.findViewById<ImageButton>(R.id.btnAccountDetails)
        val btnPass = layout.findViewById<ImageButton>(R.id.btnSetPassword)
        btnDetails.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_accountDetailsFragment)
        }
        btnPass.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_updatePasswordFragment)
        }

        return layout
    }

    fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the chosen language in SharedPreferences
        sharedPreferences.edit().putString("appLanguage", languageCode).apply()

        // Restart the activity to apply the language change
        requireActivity().recreate()
    }
}