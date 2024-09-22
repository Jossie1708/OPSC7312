package com.frogstore.droneapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.frogstore.droneapp.HomeActivity
import com.frogstore.droneapp.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_login, container, false)

        val lblUseFingerprint = layout.findViewById<TextView>(R.id.lblLoginFingerprint)
        val btnLogin = layout.findViewById<Button>(R.id.btnLogin)


        btnLogin.setOnClickListener{
            // Create an Intent to start HomeActivity
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)

        }

        lblUseFingerprint.setOnClickListener{
            Toast.makeText(requireContext(), "Fingerprint fragment pops up thank you!", Toast.LENGTH_SHORT).show()
        }
        // Return the inflated layout
        return layout


    }

}