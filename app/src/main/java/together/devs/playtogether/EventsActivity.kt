package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import together.devs.playtogether.databinding.ActivityEventsBinding

class EventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventsBinding

    private fun goToCreateEvent() {
        startActivity(Intent(baseContext, NewEventActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mockEvents =
            arrayOf(
                "Torneo Futbol 5 - Parque Nacional",
                "JaveMicro - Javeriana",
                "Disco Volador - Parque el Country",
                "Cestas y cestas - Parque el virrey"
            )

        val adapter = ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, mockEvents)
        binding.eventsList.adapter = adapter

        binding.newTeam.setOnClickListener { goToCreateEvent() }
    }
}