package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import together.devs.playtogether.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private fun goToFriends() {
        startActivity(Intent(baseContext, FriendsListActivity::class.java))
    }

    private fun goToSettings() {
        startActivity(Intent(baseContext, SettingsActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.friendsButton.setOnClickListener { goToFriends() }
        binding.settingsButton.setOnClickListener { goToSettings() }
    }
}