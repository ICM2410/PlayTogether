package together.devs.playtogether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.json.JSONObject
import java.io.InputStream

class Reserva : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)
        val json = loadJSONFromAsset("canchas.json")
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

        val pais = intent.getStringExtra("canchaElegida")
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("canchas")

        val textViewName: TextView = findViewById(R.id.nombre)
        val textViewInt: TextView = findViewById(R.id.nombreInter)
        val textViewSigla: TextView = findViewById(R.id.siglas)
        val textViewCapital: TextView = findViewById(R.id.capital)

        val countryDataMap = mutableMapOf<String, List<String>>()

        for (i in 0 until jsonArray.length()) {

            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val countryName = jsonObject.getString("nombre_cancha")
            val internName = jsonObject.getString("lugar")
            val sigla = jsonObject.getString("dueno")
            val capital = jsonObject.getString("deporte")
            countryDataMap[countryName] = listOf(internName, sigla, capital)
        }

        val key: String? = pais
        val tail = countryDataMap[key]

        if (tail != null) {
            val internName = tail[0]
            val sigla = tail[1]
            val capital = tail[2]

            textViewName.text = "Nombre cancha: $pais"
            textViewInt.text = "Ubicacion: $internName"
            textViewSigla.text = "Dueno: $sigla"
            textViewCapital.text = "Deporte: $capital"
        }
    }
}