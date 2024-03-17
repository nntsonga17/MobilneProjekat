package elfak.mosis.cityexplorer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonReg: Button
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
        setContentView(R.layout.activity_registration)
        editTextEmail = findViewById<TextInputEditText>(R.id.email)
        editTextPassword = findViewById<TextInputEditText>(R.id.password)
        buttonReg = findViewById<Button>(R.id.register_button)
        auth = FirebaseAuth.getInstance()
        textView = findViewById<TextView>(R.id.loginNow)
        textView.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonReg.setOnClickListener{
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

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Account created",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
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