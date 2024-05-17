package together.devs.playtogether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import org.json.JSONObject
import together.devs.playtogether.adapters.EventosAdapter
import together.devs.playtogether.info.EventInfo
import java.io.IOException
import java.io.InputStream

class Eventos : AppCompatActivity() {
    private lateinit var eventosListView: ListView
    private var mEventosAdapter: EventosAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)
        val json = JSONObject(loadJSONFromAsset())
        val eventosJSONArray = json.getJSONArray("eventos")
        val eventonombre = ArrayList<String>()
        eventosListView = findViewById(R.id.eventosListView)

        for (i in 0 until eventosJSONArray.length()) {
            val jsonObject = eventosJSONArray.getJSONObject(i)
            val nombreEvento = jsonObject.getString("nombre_evento")
            eventonombre.add(nombreEvento)
        }

        mEventosAdapter = EventosAdapter(this, R.layout.eventos_adapter, eventonombre)
        eventosListView.adapter = mEventosAdapter


        eventosListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val eventoNombre = parent.getItemAtPosition(position) as String
                val intent = Intent(this, EventInfo::class.java)
                intent.putExtra("eventoElegido", eventoNombre)
                startActivity(intent)
            }
    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val istream: InputStream = assets.open("eventos.json")
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


