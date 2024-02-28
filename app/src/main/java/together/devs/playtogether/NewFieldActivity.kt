package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import together.devs.playtogether.databinding.ActivityNewFieldBinding

class NewFieldActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewFieldBinding

    private fun goToFields() {
        startActivity(Intent(baseContext, FieldInfoActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewFieldBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveField.setOnClickListener { goToFields() }
    }
}