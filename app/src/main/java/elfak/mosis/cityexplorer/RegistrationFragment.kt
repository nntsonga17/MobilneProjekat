package elfak.mosis.cityexplorer

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import elfak.mosis.cityexplorer.R.layout.fragment_registration
import elfak.mosis.cityexplorer.data.UserData
import elfak.mosis.cityexplorer.databinding.FragmentRegistrationBinding
import elfak.mosis.cityexplorer.model.UserViewModel


class RegistrationFragment : Fragment() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    lateinit var editTextUsername: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonReg: Button
    lateinit var auth: FirebaseAuth
    lateinit var textView: TextView
    lateinit var editTextFirstName: EditText
    lateinit var editTextLastName: EditText
    lateinit var editTextPhoneNumber: EditText
    lateinit var progress: ProgressBar
    lateinit var database: DatabaseReference
    lateinit var imgUrl: String
    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var user: UserData
    private val sharedViewModel: UserViewModel by activityViewModels()

    private lateinit var openCameraButton: Button
    private lateinit var imageView: ImageView
    private lateinit var pictureReg: ProgressBar

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(fragment_registration, container, false)
        editTextUsername = view.findViewById(R.id.username)
        editTextPassword = view.findViewById(R.id.password)
        buttonReg = view.findViewById(R.id.register_button)
        textView = view.findViewById(R.id.loginNow)
        editTextPhoneNumber = view.findViewById(R.id.phoneNumber)
        editTextFirstName = view.findViewById(R.id.firstName)
        editTextLastName = view.findViewById(R.id.lastName)



        auth = FirebaseAuth.getInstance()

        textView.setOnClickListener {
            findNavController().navigate(R.id.action_RegistrationFragment_to_LoginFragment)
        }

        buttonReg.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            val phoneNumber = editTextPhoneNumber.text.toString()
            val firstName = editTextFirstName.text.toString()
            val lastName = editTextLastName.text.toString()


            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Ako je kreiranje korisnika uspešno, možemo automatski navigirati korisnika na LoginFragment
                        findNavController().navigate(R.id.action_RegistrationFragment_to_LoginFragment)

                        // Takođe možete prikazati odgovarajuću poruku korisniku ako je potrebno
                        // Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()
                    } else {
                        // Ako kreiranje korisnika nije uspelo, prikaži odgovarajuću poruku o grešci korisniku
                        Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return view
    }
}
