package together.devs.playtogether

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import together.devs.playtogether.firebase.UserManager
import together.devs.playtogether.model.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val butReg: Button = findViewById(R.id.btnRegister)
        val log: TextView = findViewById(R.id.loginTvBTN)

        val emailEditText: EditText = findViewById(R.id.emailET)
        val passwordEditText: EditText = findViewById(R.id.passwordET)
        val userNameEditText: EditText = findViewById(R.id.nameET)

        auth = Firebase.auth
        userManager = UserManager(Firebase.database)

        butReg.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val userName = userNameEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && userName.isNotEmpty()) {
                createAccount(email, password, userName)
            } else {
                Toast.makeText(this, "Please ensure all fields are filled correctly", Toast.LENGTH_SHORT).show()
            }
        }

        log.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(email: String, password: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    user?.let {
                        val userObj = User(userName = userName, available = true)
                        userManager.saveUser(it.uid, userObj)
                    }
                    updateUI(user)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed- ${task.exception?.message}",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    private fun reload() {
    }
}
