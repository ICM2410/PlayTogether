package together.devs.playtogether.info

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.json.JSONObject
import together.devs.playtogether.R
import java.io.InputStream

class TeamInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_equipo)
        val json = loadJSONFromAsset("equipos.json")
        InfoPais(json)
    }
    fun loadJSONFromAsset(fileName: String): String {
        val inputStream: InputStream = assets.open(fileName)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }
    fun InfoPais(jsonString: String) {

        val pais = intent.getStringExtra("equipoElegido")
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("equipos")

        val textViewName: TextView = findViewById(R.id.nombre2)
        val textViewInt: TextView = findViewById(R.id.nombreInter2)
        val textViewSigla: TextView = findViewById(R.id.siglas2)
        val textViewCapital: TextView = findViewById(R.id.capital2)

        val countryDataMap = mutableMapOf<String, List<String>>()

        for (i in 0 until jsonArray.length()) {

            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val countryName = jsonObject.getString("nombre_equipo")
            val internName = jsonObject.getString("miembros")
            val sigla = jsonObject.getString("capitan")
            val capital = jsonObject.getString("deporte")
            countryDataMap[countryName] = listOf(internName, sigla, capital)
        }

        val key: String? = pais
        val tail = countryDataMap[key]

        if (tail != null) {
            val internName = tail[0]
            val sigla = tail[1]
            val capital = tail[2]

            textViewName.text = "Nombre del equipo: $pais"
            textViewInt.text = "No miembros: $internName"
            textViewSigla.text = "Capitan: $sigla"
            textViewCapital.text = "Deporte: $capital"
        }
    }
}