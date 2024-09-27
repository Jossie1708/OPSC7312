package com.frogstore.droneapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.frogstore.droneapp.MainActivity2
import com.frogstore.droneapp.R
import com.frogstore.droneapp.SideMenuNavBarActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class HomeFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var signOutBtn: Button
    private lateinit var WeatherApi: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        name = view.findViewById(R.id.name)
        email = view.findViewById(R.id.email)
        signOutBtn = view.findViewById(R.id.signout)
        WeatherApi = view.findViewById(R.id.WeatherApi)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Get the signed-in account information
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(requireActivity())
        account?.let {
            val personName = it.displayName
            val personEmail = it.email
            name.text = personName
            email.text = personEmail
        }
        WeatherApi.setOnClickListener {
            val intent = Intent(activity, MainActivity2::class.java)
            // Start the new activity
            startActivity(intent)
        }

        // Set the sign-out button click listener
        signOutBtn.setOnClickListener {
            signOut()
        }

        return view
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener { task: Task<Void> ->
            // Handle sign-out complete
            if (task.isSuccessful) {
                val intent = Intent(activity, LoginFragment::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
}
