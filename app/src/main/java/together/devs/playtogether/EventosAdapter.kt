package together.devs.playtogether

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class EventosAdapter(context: Context, resource: Int, objects: List<String>) : ArrayAdapter<String>(context, resource, objects) {
    private val EVENTOS_NAME_INDEX = 1 // Índice del nombre del país en el cursor

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.eventos_adapter, parent, false)
        val tvEventoName = view.findViewById<TextView>(R.id.nombre) // ID del TextView en el diseño
        val eventoName = getItem(position)

        tvEventoName.text = eventoName

        return view
    }
}