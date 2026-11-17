package com.example.aplicacion_healthcare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacion_healthcare.data.CitasRepository
import com.example.aplicacion_healthcare.models.Cita
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale

// Pantalla 7: resumen de la cita, con botones Editar, Eliminar y Volver.
class DetalleCitaActivity : AppCompatActivity() {

    private lateinit var tvMedico: TextView
    private lateinit var tvEspecialidad: TextView
    private lateinit var tvCodigo: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvHora: TextView
    private lateinit var tvModalidad: TextView
    private lateinit var tvMotivo: TextView
    private lateinit var tvNotas: TextView
    private lateinit var btnEditar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnVolver: Button

    private var citaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_cita)

        // Respeta las barras del sistema (estado y navegación).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.main)) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

        enlazarVistas()

        citaId = intent.getIntExtra("cita_id", -1)

        configurarEventos()
    }

    // Recarga la cita cada vez que se vuelve (por ejemplo, después de editar).
    override fun onResume() {
        super.onResume()
        cargarCita()
    }

    // Conecta las variables con las vistas del layout.
    private fun enlazarVistas() {
        tvMedico = findViewById(R.id.tvMedico)
        tvEspecialidad = findViewById(R.id.tvEspecialidad)
        tvCodigo = findViewById(R.id.tvCodigo)
        tvFecha = findViewById(R.id.tvFecha)
        tvHora = findViewById(R.id.tvHora)
        tvModalidad = findViewById(R.id.tvModalidad)
        tvMotivo = findViewById(R.id.tvMotivo)
        tvNotas = findViewById(R.id.tvNotas)
        btnEditar = findViewById(R.id.btnEditar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnVolver = findViewById(R.id.btnVolver)
    }

    private fun configurarEventos() {

        // Editar abre el formulario con cita_id (modo editar).
        btnEditar.setOnClickListener {
            val intentFormulario = Intent(this, FormularioCitaActivity::class.java)
            intentFormulario.putExtra("cita_id", citaId)
            startActivity(intentFormulario)
        }

        btnEliminar.setOnClickListener {
            confirmarEliminacion()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    // Busca la cita en SQLite y la muestra. Si ya no existe, cierra la pantalla.
    private fun cargarCita() {

        val cita = try {
            CitasRepository(this).obtenerPorId(citaId)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.detalle_cita_error_cargar, Toast.LENGTH_SHORT).show()
            null
        }

        if (cita == null) {
            Toast.makeText(this, R.string.detalle_cita_no_encontrada, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mostrarCita(cita)
    }

    // Muestra los datos de la cita en pantalla.
    private fun mostrarCita(cita: Cita) {

        tvMedico.text = cita.medicoNombre
        tvEspecialidad.text = cita.medicoEspecialidad
        tvCodigo.text = getString(R.string.detalle_cita_codigo, cita.id)
        tvFecha.text = getString(R.string.detalle_cita_fecha, formatearFecha(cita.fecha))
        tvHora.text = getString(R.string.detalle_cita_hora, cita.hora)
        tvModalidad.text = getString(R.string.detalle_cita_modalidad, cita.modalidad)
        tvMotivo.text = getString(R.string.detalle_cita_motivo, cita.motivo)

        // Las notas son opcionales: si están vacías se muestra "Sin notas".
        tvNotas.text = if (cita.notas.isBlank()) {
            getString(R.string.detalle_cita_sin_notas)
        } else {
            getString(R.string.detalle_cita_notas, cita.notas)
        }
    }

    // La fecha se guarda como yyyy-MM-dd; aquí se muestra más legible.
    private fun formatearFecha(fecha: String): String {
        return try {
            val formatoGuardado = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val formatoMostrado = SimpleDateFormat(
                "d 'de' MMMM 'de' yyyy",
                Locale.forLanguageTag("es-CR")
            )
            formatoMostrado.format(formatoGuardado.parse(fecha)!!)
        } catch (e: Exception) {
            fecha
        }
    }

    // Pide confirmación antes de borrar.
    private fun confirmarEliminacion() {

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.detalle_cita_confirmar_titulo)
            .setMessage(R.string.detalle_cita_confirmar_mensaje)
            .setNegativeButton(R.string.detalle_cita_confirmar_no, null)
            .setPositiveButton(R.string.detalle_cita_confirmar_si) { _, _ ->
                eliminarCita()
            }
            .show()
    }

    // Borra la cita, avisa con Toast y vuelve a Mis citas.
    private fun eliminarCita() {

        try {
            val filas = CitasRepository(this).eliminar(citaId)

            if (filas > 0) {
                Toast.makeText(this, R.string.detalle_cita_eliminada, Toast.LENGTH_SHORT).show()

                val intentMisCitas = Intent(this, MisCitasActivity::class.java)
                intentMisCitas.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intentMisCitas)
                finish()
            } else {
                Toast.makeText(this, R.string.detalle_cita_error_eliminar, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, R.string.detalle_cita_error_eliminar, Toast.LENGTH_SHORT).show()
        }
    }
}
