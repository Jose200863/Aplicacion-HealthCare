package com.example.aplicacion_healthcare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacion_healthcare.adapters.MedicoAdapter
import com.example.aplicacion_healthcare.data.MedicosRepository
import com.example.aplicacion_healthcare.models.Medico

// Pantalla 3: lista de médicos desde SQLite con búsqueda y filtro por especialidad.
class MedicosActivity : AppCompatActivity() {

    private lateinit var btnAtras: ImageButton
    private lateinit var tvTitulo: TextView
    private lateinit var etBuscar: EditText
    private lateinit var spEspecialidad: Spinner
    private lateinit var tvCantidad: TextView
    private lateinit var tvVacio: TextView
    private lateinit var lvMedicos: ListView

    private lateinit var repositorio: MedicosRepository
    private lateinit var adapter: MedicoAdapter

    // Médicos que se están mostrando ahora (para saber cuál se tocó).
    private var medicosActuales: List<Medico> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicos)

        // Respeta las barras del sistema (estado y navegación).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.main)) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

        enlazarVistas()
        repositorio = MedicosRepository(this)

        adapter = MedicoAdapter(this, emptyList())
        lvMedicos.adapter = adapter
        lvMedicos.emptyView = tvVacio

        configurarSpinner()
        configurarBusqueda()
        configurarEventos()

        cargarMedicos()
    }

    // Conecta las variables con las vistas del layout.
    private fun enlazarVistas() {
        btnAtras = findViewById(R.id.btnAtras)
        tvTitulo = findViewById(R.id.tvTitulo)
        etBuscar = findViewById(R.id.etBuscar)
        spEspecialidad = findViewById(R.id.spEspecialidad)
        tvCantidad = findViewById(R.id.tvCantidad)
        tvVacio = findViewById(R.id.tvVacio)
        lvMedicos = findViewById(R.id.lvMedicos)
    }

    // Llena el Spinner con "Todas las especialidades" + las que salen de SQLite.
    private fun configurarSpinner() {

        val opciones = mutableListOf(getString(R.string.medicos_todas_especialidades))

        try {
            opciones.addAll(repositorio.obtenerEspecialidades())
        } catch (e: Exception) {
            Toast.makeText(this, R.string.medicos_error_cargar, Toast.LENGTH_SHORT).show()
        }

        val adaptadorSpinner = ArrayAdapter(
            this,
            R.layout.item_spinner_especialidad,
            opciones
        )
        adaptadorSpinner.setDropDownViewResource(R.layout.item_spinner_especialidad_dropdown)
        spEspecialidad.adapter = adaptadorSpinner

        // Si llega la especialidad desde Inicio, queda seleccionada.
        val especialidadInicial = intent.getStringExtra("especialidad")
        if (!especialidadInicial.isNullOrBlank()) {
            val posicion = opciones.indexOf(especialidadInicial)
            if (posicion >= 0) {
                spEspecialidad.setSelection(posicion)
            }
        }

        // Al cambiar la especialidad se vuelve a cargar la lista.
        spEspecialidad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                padre: AdapterView<*>?,
                vista: View?,
                posicion: Int,
                id: Long
            ) {
                cargarMedicos()
            }

            override fun onNothingSelected(padre: AdapterView<*>?) {}
        }
    }

    // Al escribir en el buscador se vuelve a cargar la lista.
    private fun configurarBusqueda() {
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                cargarMedicos()
            }
        })
    }

    private fun configurarEventos() {

        btnAtras.setOnClickListener {
            finish()
        }

        // Al tocar un médico se abre su detalle con medico_id.
        lvMedicos.setOnItemClickListener { _, _, posicion, _ ->
            val intentDetalle = Intent(this, DetalleMedicoActivity::class.java)
            intentDetalle.putExtra("medico_id", medicosActuales[posicion].id)
            startActivity(intentDetalle)
        }
    }

    // Consulta SQLite según el texto y la especialidad, y actualiza lista y contador.
    private fun cargarMedicos() {

        val texto = etBuscar.text.toString().trim()

        // La posición 0 es "Todas las especialidades": no filtra.
        val especialidad = if (spEspecialidad.selectedItemPosition > 0) {
            spEspecialidad.selectedItem as String
        } else {
            null
        }

        medicosActuales = try {
            repositorio.filtrar(texto, especialidad)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.medicos_error_cargar, Toast.LENGTH_SHORT).show()
            emptyList()
        }

        adapter.actualizar(medicosActuales)

        // El contador depende de la lista, nunca es un número fijo.
        val cantidad = medicosActuales.size
        tvCantidad.text = resources.getQuantityString(
            R.plurals.medicos_cantidad,
            cantidad,
            cantidad
        )
    }
}
