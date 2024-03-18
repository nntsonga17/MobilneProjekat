package elfak.mosis.cityexplorer

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class RegistrationFragment : Fragment() {
    private lateinit var editTextUserName: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonReg: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var textView: TextView
    private lateinit var editTextFirstName: TextInputEditText
    private lateinit var editTextLastName: TextInputEditText
    private lateinit var editTextPhoneNumber: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        editTextUserName = view.findViewById(R.id.username)
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
            val username = editTextUserName.text.toString()
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
