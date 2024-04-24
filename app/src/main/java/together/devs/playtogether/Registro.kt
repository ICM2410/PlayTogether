package together.devs.playtogether

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class Registro : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val butReg: Button = findViewById(R.id.btnRegister)
        val log: TextView = findViewById(R.id.loginTvBTN)

        val emailEditText: EditText = findViewById(R.id.emailET)
        val passwordEditText: EditText = findViewById(R.id.PasswordET)

        auth = Firebase.auth

        butReg.setOnClickListener {
            val email = emailEditText.text.toString();
            val password = passwordEditText.text.toString();

            if (email.isNotEmpty() && password.isNotEmpty()) {
                createAccount(email, password)
            } else {
                Toast.makeText(this, "Por favor revisa que todos los campos estén correctos", Toast.LENGTH_SHORT).show()
            }
        }
        log.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed- ${task.exception?.message}",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Registration successful, you can redirect the user to the home screen or perform other actions
            val intent = Intent(this, MainActivity::class.java) //Main es Login
            startActivity(intent)
        } else {
            val intent = Intent(this, Registro::class.java) //Main es Login
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    private fun reload() {
    }
}