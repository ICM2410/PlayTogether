package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import org.json.JSONObject
import together.devs.playtogether.adapters.EquiposAdapter
import together.devs.playtogether.info.TeamInfo
import java.io.IOException
import java.io.InputStream

class Team : AppCompatActivity() {
    private lateinit var equiposListView: ListView
    private var mEquiposAdapter: EquiposAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipos)
        val json = JSONObject(loadJSONFromAsset())
        val equiposJSONArray = json.getJSONArray("equipos")
        val equiponombre = ArrayList<String>()
        equiposListView = findViewById(R.id.listaEquipos)

        for (i in 0 until equiposJSONArray.length()) {
            val jsonObject = equiposJSONArray.getJSONObject(i)
            val nombreEquipo = jsonObject.getString("nombre_equipo")
            equiponombre.add(nombreEquipo)
        }

        mEquiposAdapter = EquiposAdapter(this, R.layout.eventos_adapter, equiponombre)
        equiposListView.adapter = mEquiposAdapter


        equiposListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val equipoNombre = parent.getItemAtPosition(position) as String
                val intent = Intent(this, TeamInfo::class.java)
                intent.putExtra("equipoElegido", equipoNombre)
                startActivity(intent)
            }
    }
    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val istream: InputStream = assets.open("equipos.json")
            val size: Int = istream.available()
            val buffer = ByteArray(size)
            istream.read(buffer)
            istream.close()
            json = buffer.toString(Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}