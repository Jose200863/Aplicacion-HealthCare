package com.example.aplicacion_healthcare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.aplicacion_healthcare.R
import com.example.aplicacion_healthcare.models.Cita

// Adapter que muestra cada cita con item_cita.xml.
// Lo usa MisCitasActivity para las pestañas Próximas y Anteriores.

class CitasAdapter(
    private val context: Context,
    private var citas: List<Cita>
) : BaseAdapter() {

    // ViewHolder: guarda las vistas de una fila para no buscarlas en cada scroll.
    private class FilaCita(vista: View) {
        val tvMedicoNombre: TextView = vista.findViewById(R.id.tvMedicoNombre)
        val tvEspecialidad: TextView = vista.findViewById(R.id.tvEspecialidad)
        val tvFechaHora: TextView = vista.findViewById(R.id.tvFechaHora)
        val tvMotivo: TextView = vista.findViewById(R.id.tvMotivo)
        val tvModalidad: TextView = vista.findViewById(R.id.tvModalidad)
    }

    override fun getCount(): Int = citas.size

    override fun getItem(posicion: Int): Cita = citas[posicion]

    override fun getItemId(posicion: Int): Long = citas[posicion].id.toLong()

    override fun getView(
        posicion: Int,
        vistaReciclada: View?,
        padre: ViewGroup
    ): View {

        val vista: View
        val fila: FilaCita

        if (vistaReciclada == null) {
            vista = LayoutInflater.from(context)
                .inflate(R.layout.item_cita, padre, false)
            fila = FilaCita(vista)
            vista.tag = fila
        } else {
            vista = vistaReciclada
            fila = vista.tag as FilaCita
        }

        val cita = citas[posicion]

        // Nombre del médico: si está vacío muestra "Cita #id"
        fila.tvMedicoNombre.text =
            cita.medicoNombre.ifBlank { context.getString(R.string.titulo_mis_citas) + " #${cita.id}" }

        // Especialidad (puede estar vacía)
        fila.tvEspecialidad.text = cita.medicoEspecialidad
        fila.tvEspecialidad.visibility =
            if (cita.medicoEspecialidad.isBlank()) View.GONE else View.VISIBLE

        // Fecha y hora juntas
        fila.tvFechaHora.text = "${cita.fecha} · ${cita.hora}"

        // Motivo (puede estar vacío)
        fila.tvMotivo.text = cita.motivo
        fila.tvMotivo.visibility =
            if (cita.motivo.isBlank()) View.GONE else View.VISIBLE

        // Chip de modalidad con color diferenciado
        fila.tvModalidad.text = cita.modalidad
        val (textColor, bgColor) = when (cita.modalidad.lowercase()) {
            "virtual"   -> Pair(
                context.getColor(R.color.azul_ojo),
                context.getColor(R.color.celeste_suave)
            )
            else        -> Pair(                          // presencial u otro
                context.getColor(R.color.verde_salud),
                context.getColor(R.color.menta_suave)
            )
        }
        fila.tvModalidad.setTextColor(textColor)
        fila.tvModalidad.setBackgroundColor(bgColor)

        return vista
    }

    // Reemplaza la lista mostrada (al cambiar de pestaña) sin crear otro adapter.
    fun actualizar(nuevaLista: List<Cita>) {
        citas = nuevaLista
        notifyDataSetChanged()
    }
}
