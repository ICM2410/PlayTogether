package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CrearEquipo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_equipo)
        val amigos:Button=findViewById(R.id.invitarAmigosButton)
        amigos.setOnClickListener {
            intent= Intent(this,ContactListActivity::class.java)
            startActivity(intent)
        }
    }
}