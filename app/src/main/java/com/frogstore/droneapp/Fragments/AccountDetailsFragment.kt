package com.frogstore.droneapp.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.frogstore.droneapp.R
import com.frogstore.droneapp.databinding.ActivitySideMenuNavBarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

class AccountDetailsFragment : Fragment() {

    private lateinit var editTextNewUsername: EditText
    private lateinit var btnImage: ImageButton
    private lateinit var editTextEmail: EditText
    private lateinit var buttonUpdateUsername: Button
    private lateinit var imageViewProfile: ImageView
    private lateinit var binding: ActivitySideMenuNavBarBinding
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_account_details, container, false)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        editTextNewUsername = layout.findViewById(R.id.txtAccUsername)
        buttonUpdateUsername = layout.findViewById(R.id.btnUpdateAccountDetails)
        editTextEmail = layout.findViewById(R.id.txtAccEmail)
        btnImage = layout.findViewById(R.id.btnEditImage)
        imageViewProfile = layout.findViewById(R.id.imgAccProfile)

        // Load the user's email into the editText and disable it
        val email = auth.currentUser?.email
        editTextEmail.setText(email)
        editTextEmail.isEnabled = false

        buttonUpdateUsername.setOnClickListener {
            val newUsername = editTextNewUsername.text.toString()
            if (newUsername.isNotEmpty()) {
                updateUsername(newUsername)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid username", Toast.LENGTH_SHORT).show()
            }
        }

        btnImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        return layout
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri? = data.data

            uri?.let {
                imageViewProfile.setImageURI(it)
                uploadImageToFirebase(it)
            }
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val user = auth.currentUser
        if (user != null) { // Ensure the user is not null
            val storageRef = FirebaseStorage.getInstance().reference.child("profilePictures/${user.uid}")

            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Create a profile update request with the new photo URL
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(downloadUrl)
                        .build()

                    user.updateProfile(profileUpdates).addOnCompleteListener { task -> // Ensure `user` is not null here
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                         //   Toast.makeText(requireContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener { exception ->
              //  Toast.makeText(requireContext(), "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("User Error", "No user is currently signed in.")
            Toast.makeText(requireContext(), "No user is currently signed in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateHeader() {
        // Update header views accordingly
        val navView = binding.navView // Assuming navView is your NavigationView
        val headerView = navView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.txtLoginUsername)

        val user = auth.currentUser
        if (user != null) {
            nameTextView.text = user.displayName
        } else {
            nameTextView.text = getString(R.string.sign_in_name)
        }
    }

    private fun updateUsername(newUsername: String) {
        val user = auth.currentUser
        user?.let {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build()

            it.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Profile Update", "User profile updated.")
                    Toast.makeText(requireContext(), "Username updated successfully", Toast.LENGTH_SHORT).show()
                    updateHeader()
                } else {
                    Log.e("Profile Update Error", "Failed to update profile: ${task.exception?.message}")
                    Toast.makeText(requireContext(), "Error updating username: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Log.e("User Error", "No user is currently signed in.")
            Toast.makeText(requireContext(), "No user is currently signed in.", Toast.LENGTH_SHORT).show()
        }
    }
}
