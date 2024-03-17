package elfak.mosis.cityexplorer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var textView: TextView

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, HomeFragment::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editTextEmail = findViewById<TextInputEditText>(R.id.email)
        editTextPassword = findViewById<TextInputEditText>(R.id.password)
        buttonLogin = findViewById<Button>(R.id.login_button)
        auth = FirebaseAuth.getInstance()
        textView = findViewById<TextView>(R.id.registerNow)
        textView.setOnClickListener {
            val intent = Intent(applicationContext, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonLogin.setOnClickListener{
            val email: String
            val password: String
            email = editTextEmail.text.toString()
            password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(email)){
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(password)){
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Login successfull", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, HomeFragment::class.java)
                        startActivity(intent)
                        finish()

                    } else {

                        Toast.makeText(
                            this,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

        }
    }
}