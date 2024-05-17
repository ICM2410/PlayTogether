package together.devs.playtogether.info

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.json.JSONObject
import together.devs.playtogether.R
import java.io.InputStream

class EventInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_evento)
        val json = loadJSONFromAsset("eventos.json")
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

        val pais = intent.getStringExtra("eventoElegido")
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("eventos")

        val textViewName: TextView = findViewById(R.id.nombre1)
        val textViewInt: TextView = findViewById(R.id.nombreInter1)
        val textViewSigla: TextView = findViewById(R.id.siglas1)
        val textViewCapital: TextView = findViewById(R.id.capital1)

        val countryDataMap = mutableMapOf<String, List<String>>()

        for (i in 0 until jsonArray.length()) {

            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val countryName = jsonObject.getString("nombre_evento")
            val internName = jsonObject.getString("fecha")
            val sigla = jsonObject.getString("organizador")
            val capital = jsonObject.getString("hora")
            countryDataMap[countryName] = listOf(internName, sigla, capital)
        }

        val key: String? = pais
        val tail = countryDataMap[key]

        if (tail != null) {
            val internName = tail[0]
            val sigla = tail[1]
            val capital = tail[2]

            textViewName.text = "El nombre evento: $pais"
            textViewInt.text = "Fecha: $internName"
            textViewSigla.text = "Organizador: $sigla"
            textViewCapital.text = "Hora:$capital"
        }
    }
}