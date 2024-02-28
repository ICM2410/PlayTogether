package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import together.devs.playtogether.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private fun goToProfile() {
        startActivity(Intent(baseContext, ProfileActivity::class.java))
    }

    private fun goToEvents() {
        startActivity(Intent(baseContext, EventsActivity::class.java))
    }

    private fun goToFields() {
        startActivity(Intent(baseContext, FieldInfoActivity::class.java))
    }

    private fun goToTeams() {
        startActivity(Intent(baseContext, TeamsActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profileButton.setOnClickListener { goToProfile() }
        binding.goToEvents.setOnClickListener { goToEvents() }
        binding.goToFields.setOnClickListener { goToFields() }
        binding.goToTeams.setOnClickListener { goToTeams() }
    }
}