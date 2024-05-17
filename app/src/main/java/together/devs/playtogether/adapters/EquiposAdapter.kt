package together.devs.playtogether.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import together.devs.playtogether.R

class EquiposAdapter(context: Context, resource: Int, objects: List<String>) : ArrayAdapter<String>(context, resource, objects) {
    private val EQUIPOS_NAME_INDEX = 1 // Índice del nombre del país en el cursor

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.equipos_adapter, parent, false)
        val tvEquipoName = view.findViewById<TextView>(R.id.nombre) // ID del TextView en el diseño
        val equipoName = getItem(position)

        tvEquipoName.text = equipoName

        return view
    }
}