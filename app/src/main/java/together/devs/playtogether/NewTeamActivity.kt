package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import together.devs.playtogether.databinding.ActivityNewTeamBinding

class NewTeamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewTeamBinding

    private fun goToTeams() {
        startActivity(Intent(baseContext, TeamsActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mockMembers = arrayOf(
            "Nicolás Montañez",
            "Juan Muñoz",
            "Nicolás Cerón",
            "Martín Cerón",
            "Daniel Castellanos"
        )

        binding.membersList.adapter =
            ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, mockMembers)

        binding.createTeam.setOnClickListener { goToTeams() }
    }
}