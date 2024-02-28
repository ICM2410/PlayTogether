package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import together.devs.playtogether.databinding.ActivityFieldInfoBinding

class FieldInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFieldInfoBinding

    private fun goToBooking() {
        startActivity(Intent(baseContext, BookingActivity::class.java))
    }

    private fun goToNewField() {
        startActivity(Intent(baseContext, NewFieldActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFieldInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bookField.setOnClickListener { goToBooking() }
        binding.addField.setOnClickListener { goToNewField() }
    }
}