package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import together.devs.playtogether.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private fun goToHomePage() {
        startActivity(Intent(baseContext, HomeActivity::class.java))
    }

    private fun gotToSignUp() {
        startActivity(Intent(baseContext, SignUpActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { goToHomePage() }
        binding.registerButton.setOnClickListener { gotToSignUp() }
    }
}

//This is a github push test in ubuntu