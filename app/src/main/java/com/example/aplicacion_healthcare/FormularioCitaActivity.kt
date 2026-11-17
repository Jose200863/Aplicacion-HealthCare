package com.example.aplicacion_healthcare

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacion_healthcare.data.CitasRepository
import com.example.aplicacion_healthcare.models.Cita

// Pantalla formulario: crear o editar una cita.
// Si llega "cita_id" por Intent es modo edición; si no, es modo creación.
class FormularioCitaActivity : AppCompatActivity() {

    private lateinit var etFecha: EditText
    private lateinit var etHora: EditText
    private lateinit var etMotivo: EditText
    private lateinit var etModalidad: EditText
    private lateinit var etNotas: EditText
    private lateinit var btnGuardar: Button

    private var citaId: Int = -1
    private var medicoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_cita)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

        citaId = intent.getIntExtra("cita_id", -1)
        medicoId = intent.getIntExtra("medico_id", -1)

        enlazarVistas()

        if (citaId != -1) {
            cargarCita()
        }

        btnGuardar.setOnClickListener {
            guardarCita()
        }
    }

    private fun enlazarVistas() {
        etFecha = findViewById(R.id.etFecha)
        etHora = findViewById(R.id.etHora)
        etMotivo = findViewById(R.id.etMotivo)
        etModalidad = findViewById(R.id.etModalidad)
        etNotas = findViewById(R.id.etNotas)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun cargarCita() {
        try {
            val cita = CitasRepository(this).obtenerPorId(citaId) ?: return

            medicoId = cita.medicoId
            etFecha.setText(cita.fecha)
            etHora.setText(cita.hora)
            etMotivo.setText(cita.motivo)
            etModalidad.setText(cita.modalidad)
            etNotas.setText(cita.notas)

        } catch (e: Exception) {
            Toast.makeText(this, R.string.formulario_cita_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarCita() {
        val fecha = etFecha.text.toString().trim()
        val hora = etHora.text.toString().trim()
        val motivo = etMotivo.text.toString().trim()
        val modalidad = etModalidad.text.toString().trim()
        val notas = etNotas.text.toString().trim()

        if (fecha.isBlank() || hora.isBlank() || motivo.isBlank()) {
            Toast.makeText(this, R.string.formulario_campos_requeridos, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val repo = CitasRepository(this)
            val cita = Cita(
                id = if (citaId != -1) citaId else 0,
                medicoId = medicoId,
                fecha = fecha,
                hora = hora,
                motivo = motivo,
                modalidad = modalidad,
                notas = notas
            )

            if (citaId != -1) {
                repo.actualizar(cita)
            } else {
                repo.insertar(cita)
            }

            Toast.makeText(this, R.string.formulario_cita_guardada, Toast.LENGTH_SHORT).show()
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, R.string.formulario_cita_error, Toast.LENGTH_SHORT).show()
        }
    }
}
