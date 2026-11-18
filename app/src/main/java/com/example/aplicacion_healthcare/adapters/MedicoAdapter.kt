package com.example.aplicacion_healthcare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.aplicacion_healthcare.R
import com.example.aplicacion_healthcare.models.Medico

// Adapter que muestra cada médico con item_medico.xml.
// Lo usan la lista de médicos y "Atendido recientemente" de Inicio.

class MedicoAdapter(
    private val context: Context,
    private var medicos: List<Medico>
) : BaseAdapter() {

    // Guarda las vistas de una fila para no buscarlas cada vez.
    private class FilaMedico(vista: View) {
        val ivFoto: ImageView = vista.findViewById(R.id.ivFoto)
        val tvNombre: TextView = vista.findViewById(R.id.tvNombre)
        val tvEspecialidad: TextView = vista.findViewById(R.id.tvEspecialidad)
    }

    override fun getCount(): Int = medicos.size

    override fun getItem(posicion: Int): Medico = medicos[posicion]

    override fun getItemId(posicion: Int): Long = medicos[posicion].id.toLong()

    override fun getView(
        posicion: Int,
        vistaReciclada: View?,
        padre: ViewGroup
    ): View {

        val vista: View
        val fila: FilaMedico

        if (vistaReciclada == null) {
            vista = LayoutInflater.from(context)
                .inflate(R.layout.item_medico, padre, false)
            fila = FilaMedico(vista)
            vista.tag = fila
        } else {
            vista = vistaReciclada
            fila = vista.tag as FilaMedico
        }

        val medico = medicos[posicion]

        fila.tvNombre.text = medico.nombre
        fila.tvEspecialidad.text = medico.especialidad

        // La foto se busca en drawable por su nombre (ej. "doc_elena").
        val idFoto = context.resources.getIdentifier(
            medico.foto,
            "drawable",
            context.packageName
        )
        fila.ivFoto.setImageResource(
            if (idFoto != 0) idFoto else R.drawable.ic_medicos
        )

        return vista
    }

    // Cambia la lista mostrada (al buscar o filtrar) sin crear otro adapter.
    fun actualizar(nuevaLista: List<Medico>) {
        medicos = nuevaLista
        notifyDataSetChanged()
    }
}
