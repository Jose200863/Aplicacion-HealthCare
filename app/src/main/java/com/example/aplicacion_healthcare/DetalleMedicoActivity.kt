package com.example.aplicacion_healthcare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacion_healthcare.data.MedicosRepository
import com.example.aplicacion_healthcare.models.Medico

class DetalleMedicoActivity : AppCompatActivity() {

    private lateinit var ivFoto: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvEspecialidad: TextView
    private lateinit var tvExperiencia: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvModalidad: TextView
    private lateinit var tvPrecio: TextView
    private lateinit var btnAgendar: Button


    private var medicoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_medico)

        enlazarVistas()

        medicoId = intent.getIntExtra("medico_id", -1)

        val medico = MedicosRepository(this).obtenerPorId(medicoId)

        if (medico == null) {
            Toast.makeText(
                this,
                R.string.detalle_medico_no_encontrado,
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        mostrarMedico(medico)

        btnAgendar.setOnClickListener {
            val intentFormulario = Intent(this, FormularioCitaActivity::class.java)
            intentFormulario.putExtra("medico_id", medicoId)
            startActivity(intentFormulario)
        }
    }

    private  fun enlazarVistas() {
        ivFoto = findViewById(R.id.ivFoto)
        tvNombre = findViewById(R.id.tvNombre)
        tvEspecialidad = findViewById(R.id.tvEspecialidad)
        tvExperiencia = findViewById(R.id.tvExperiencia)
        tvDescripcion = findViewById(R.id.tvDescripcion)
        tvModalidad = findViewById(R.id.tvModalidad)
        tvPrecio = findViewById(R.id.tvPrecio)
        btnAgendar = findViewById(R.id.btnAgendar)
    }

    // Muestra los datos del médico en pantalla.
    private fun mostrarMedico(medico: Medico) {
        tvNombre.text = medico.nombre
        tvEspecialidad.text = medico.especialidad
        tvExperiencia.text = getString(
            R.string.detalle_medico_experiencia,
            medico.experiencia
        )
        tvDescripcion.text = medico.descripcion
        tvModalidad.text = getString(
            R.string.detalle_medico_modalidad,
            medico.modalidad
        )
        tvPrecio.text = getString(
            R.string.detalle_medico_precio,
            String.format("%,.0f", medico.precio)
        )

        // La foto se busca en drawable por su nombre (ej. "doc_elena").
        val idFoto = resources.getIdentifier(medico.foto, "drawable", packageName)
        if (idFoto != 0) {
            ivFoto.setImageResource(idFoto)
        }
    }

}