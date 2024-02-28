package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import together.devs.playtogether.databinding.ActivityTeamsBinding

class TeamsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamsBinding

    private fun goToNewTeam() {
        startActivity(Intent(baseContext, NewTeamActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mockTeams = arrayOf(
            "FutMates - Futbol",
            "Facultad Ingeniería - Ultimate",
            "La banda del patio - Basquetbol"
        )

        binding.teamsList.adapter =
            ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, mockTeams)

        binding.newTeam.setOnClickListener { goToNewTeam() }
    }
}