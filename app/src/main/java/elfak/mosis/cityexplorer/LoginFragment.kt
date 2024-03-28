package elfak.mosis.cityexplorer

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var editTextUserName: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var textView: TextView
    lateinit var progress: ProgressBar
    private  val sharedViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        editTextUserName = view.findViewById(R.id.username)
        editTextPassword = view.findViewById(R.id.password)
        buttonLogin = view.findViewById(R.id.login_button)
        textView = view.findViewById(R.id.registerNow)
        progress = view.findViewById(R.id.progressBar2)

        auth = FirebaseAuth.getInstance()

        textView.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegistrationFragment)
        }

        buttonLogin.setOnClickListener {
            progress.visibility=View.VISIBLE
            val username = editTextUserName.text.toString()
            val password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                makeText(requireContext(), "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        progress.visibility=View.GONE
                        sharedViewModel.name = username
                        // Ako je prijava uspešna, prikaži odgovarajuću poruku korisniku
                        findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)

                        // Nakon uspešne prijave, možete navigirati korisnika na sledeći ekran ili uraditi nešto drugo
                        // Na primer:
                        // findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
                    } else {
                        // Ako prijava nije uspela, prikaži odgovarajuću poruku o grešci korisniku
                        Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return view
    }
}

