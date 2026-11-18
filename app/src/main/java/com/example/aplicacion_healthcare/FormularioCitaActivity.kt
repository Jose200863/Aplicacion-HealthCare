package com.example.aplicacion_healthcare

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacion_healthcare.data.CitasRepository
import com.example.aplicacion_healthcare.models.Cita
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Pantalla formulario: crear o editar una cita.
// Si llega "cita_id" por Intent es modo edición; si no, es modo creación.
class FormularioCitaActivity : AppCompatActivity() {

    private lateinit var etFecha: EditText
    private lateinit var etHora: EditText
    private lateinit var etMotivo: EditText
    private lateinit var spModalidad: Spinner
    private lateinit var etNotas: EditText
    private lateinit var btnGuardar: Button

    private var citaId: Int = -1
    private var medicoId: Int = -1
    private var fechaParaGuardar: String = ""

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

        etFecha.setOnClickListener { mostrarSelectorFecha() }

        btnGuardar.setOnClickListener {
            guardarCita()
        }
    }

    private fun enlazarVistas() {
        etFecha = findViewById(R.id.etFecha)
        etHora = findViewById(R.id.etHora)
        etMotivo = findViewById(R.id.etMotivo)
        spModalidad = findViewById(R.id.spModalidad)
        etNotas = findViewById(R.id.etNotas)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun cargarCita() {
        try {
            val cita = CitasRepository(this).obtenerPorId(citaId) ?: return

            medicoId = cita.medicoId
            fechaParaGuardar = cita.fecha
            val fechaGuardada = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(cita.fecha)
            if (fechaGuardada != null) {
                etFecha.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fechaGuardada))
            }
            etHora.setText(cita.hora)
            etMotivo.setText(cita.motivo)
            seleccionarModalidad(cita.modalidad)
            etNotas.setText(cita.notas)

        } catch (e: Exception) {
            Toast.makeText(this, R.string.formulario_cita_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarCita() {
        val fecha = etFecha.text.toString().trim()
        val hora = etHora.text.toString().trim()
        val motivo = etMotivo.text.toString().trim()
        val modalidad = spModalidad.selectedItem?.toString().orEmpty()
        val notas = etNotas.text.toString().trim()

        if (fecha.isBlank() || fechaParaGuardar.isBlank() || hora.isBlank() || motivo.isBlank()) {
            Toast.makeText(this, R.string.formulario_campos_requeridos, Toast.LENGTH_SHORT).show()
            return
        }

        if (medicoId <= 0) {
            Toast.makeText(this, "Selecciona un médico válido", Toast.LENGTH_SHORT).show()
            return
        }

        val hoyIso = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
        if (fechaParaGuardar < hoyIso) {
            Toast.makeText(this, "Ingresa una fecha válida de hoy en adelante", Toast.LENGTH_SHORT).show()
            return
        }

        val formatoHora = SimpleDateFormat("HH:mm", Locale.US).apply {
            isLenient = false
        }
        val posicionHora = ParsePosition(0)
        val horaValida = formatoHora.parse(hora, posicionHora)

        if (horaValida == null || posicionHora.index != hora.length) {
            Toast.makeText(this, "Ingresa la hora en formato HH:mm", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val repo = CitasRepository(this)
            val cita = Cita(
                id = if (citaId != -1) citaId else 0,
                medicoId = medicoId,
                fecha = fechaParaGuardar,
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

    private fun mostrarSelectorFecha() {
        val hoy = Calendar.getInstance()
        val fechaActual = etFecha.text.toString().trim()
        val fechaInicial = try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaActual)
        } catch (e: Exception) {
            null
        }
        val calendarioInicial = Calendar.getInstance().apply {
            if (fechaInicial != null) time = fechaInicial
        }

        DatePickerDialog(
            this,
            { _, anio, mes, dia ->
                etFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dia, mes + 1, anio))
                fechaParaGuardar = String.format(Locale.US, "%04d-%02d-%02d", anio, mes + 1, dia)
            },
            calendarioInicial.get(Calendar.YEAR),
            calendarioInicial.get(Calendar.MONTH),
            calendarioInicial.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = hoy.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            show()
        }
    }

    private fun seleccionarModalidad(modalidad: String) {
        val opciones = resources.getStringArray(R.array.opciones_modalidad).toMutableList()
        if (modalidad.isNotBlank() && modalidad !in opciones) {
            opciones.add(modalidad)
            spModalidad.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                opciones
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }
        val posicion = opciones.indexOf(modalidad)
        if (posicion >= 0) {
            spModalidad.setSelection(posicion)
        }
    }

}
