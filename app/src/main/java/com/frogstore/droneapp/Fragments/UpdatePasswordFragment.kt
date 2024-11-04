import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.frogstore.droneapp.R
import com.frogstore.droneapp.UserDetails.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider

class UpdatePasswordFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
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

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        buttonUpdatePassword.setOnClickListener {
            val oldPassword = editTextOldPassword.text.toString()
            val newPassword = editTextNewPassword.text.toString()
            val user = auth.currentUser

            if (user != null && oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                // Re-authenticate the user
                val credential: AuthCredential = EmailAuthProvider.getCredential(user.email!!, oldPassword)

                user.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User re-authenticated, now update the password
                        user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Error updating password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Re-authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return layout
    }
}
