package together.devs.playtogether.info

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import together.devs.playtogether.R
import together.devs.playtogether.Reserva
import java.io.IOException
import java.io.InputStream
import together.devs.playtogether.adapters.CanchasAdapter

class CourtInfo : AppCompatActivity() {
    private lateinit var canchasListView: ListView
    private var mCanchasAdapter: CanchasAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_canchas)
        val json = JSONObject(loadJSONFromAsset())
        val canchasJSONArray = json.getJSONArray("canchas")
        val canchanombre = ArrayList<String>()
        canchasListView = findViewById(R.id.listaCanchas)

        for (i in 0 until canchasJSONArray.length()) {
            val jsonObject = canchasJSONArray.getJSONObject(i)
            val nombreCancha = jsonObject.getString("nombre_cancha")
            canchanombre.add(nombreCancha)
        }

        mCanchasAdapter = CanchasAdapter(this, R.layout.canchas_adapter, canchanombre)
        canchasListView.adapter = mCanchasAdapter


        canchasListView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val canchaNombre = parent.getItemAtPosition(position) as String
            val intent = Intent(this, Reserva::class.java)
            intent.putExtra("canchaElegida", canchaNombre)
            startActivity(intent)
        }

    }
    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val istream: InputStream = assets.open("canchas.json")
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