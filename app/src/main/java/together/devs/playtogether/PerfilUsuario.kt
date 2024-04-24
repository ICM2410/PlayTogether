package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PerfilUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)
        val but:Button=findViewById(R.id.agregarCancha)
        val but2:Button=findViewById(R.id.amigosButton)
        val but3:Button=findViewById(R.id.crearEvento)
        val but4:Button=findViewById(R.id.crearEquipo)
        val but5:Button=findViewById(R.id.verHistorial)
        val signOut:Button=findViewById(R.id.editarPerfilButton)

        but2.setOnClickListener {
            intent= Intent(this,ListaAmigos::class.java)
            startActivity(intent)
        }
        but3.setOnClickListener {
            intent= Intent(this,CreacionEvento::class.java)
            startActivity(intent)
        }
        but.setOnClickListener {
            intent= Intent(this,AgregarCancha::class.java)
            startActivity(intent)
        }
        but4.setOnClickListener {
            intent= Intent(this,CrearEquipo::class.java)
            startActivity(intent)
        }
        but5.setOnClickListener {
            intent= Intent(this,InfoCanchas::class.java)
            startActivity(intent)
        }
        signOut.setOnClickListener {
            Firebase.auth.signOut()
            intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}