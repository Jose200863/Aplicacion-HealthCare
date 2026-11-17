package com.example.aplicacion_healthcare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacion_healthcare.adapters.MedicoAdapter
import com.example.aplicacion_healthcare.data.CitasRepository
import com.example.aplicacion_healthcare.data.MedicosRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var tvUsuario: TextView
    private lateinit var etBuscar: EditText
    private lateinit var tvProxHora: TextView
    private lateinit var tvProxMedico: TextView
    private lateinit var btnVerDetalle: MaterialButton
    private lateinit var tvVerTodas: TextView
    private lateinit var lvRecientes: ListView
    private lateinit var tvSinRecientes: TextView
    private lateinit var fabAgendar: FloatingActionButton
    private lateinit var barraInferior: BottomNavigationView

    private var proximaCitaId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val raiz = findViewById<View>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(raiz) { view, insets ->
            val barras = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            view.setPadding(
                barras.left,
                barras.top,
                barras.right,
                barras.bottom
            )
            insets
        }

        tvSaludo = findViewById(R.id.tvSaludo)
        tvUsuario = findViewById(R.id.tvUsuario)
        etBuscar = findViewById(R.id.etBuscar)
        tvProxHora = findViewById(R.id.tvProxHora)
        tvProxMedico = findViewById(R.id.tvProxMedico)
        btnVerDetalle = findViewById(R.id.btnVerDetalle)
        tvVerTodas = findViewById(R.id.tvVerTodas)
        lvRecientes = findViewById(R.id.lvRecientes)
        tvSinRecientes = findViewById(R.id.tvSinRecientes)
        fabAgendar = findViewById(R.id.fabAgendar)
        barraInferior = findViewById(R.id.barraInferior)

        configurarBusqueda()
        configurarEspecialidades()
        configurarAccesos()
        configurarBarraInferior()
    }

    override fun onResume() {
        super.onResume()
        cargarProximaCita()
        cargarMedicosRecientes()

        barraInferior.menu.findItem(
            R.id.nav_inicio
        ).isChecked = true
    }

    private fun configurarBusqueda() {
        etBuscar.setOnClickListener {
            abrirListaMedicos()
        }
    }

    private fun configurarEspecialidades() {

        findViewById<View>(R.id.catMedicina)
            .setOnClickListener {
                abrirListaMedicos(
                    getString(R.string.especialidad_medicina)
                )
            }

        findViewById<View>(R.id.catCardiologia)
            .setOnClickListener {
                abrirListaMedicos(
                    getString(R.string.especialidad_cardiologia)
                )
            }

        findViewById<View>(R.id.catDermatologia)
            .setOnClickListener {
                abrirListaMedicos(
                    getString(R.string.especialidad_dermatologia)
                )
            }

        findViewById<View>(R.id.catOftalmologia)
            .setOnClickListener {
                abrirListaMedicos(
                    getString(R.string.especialidad_oftalmologia)
                )
            }
    }

    private fun configurarAccesos() {

        tvVerTodas.setOnClickListener {
            abrirListaMedicos()
        }

        btnVerDetalle.setOnClickListener {
            val id = proximaCitaId ?: return@setOnClickListener

            val intent = Intent(
                this,
                DetalleCitaActivity::class.java
            )
            intent.putExtra("cita_id", id)
            startActivity(intent)
        }

        fabAgendar.setOnClickListener {
            abrirListaMedicos()
        }
    }

    private fun configurarBarraInferior() {

        barraInferior.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_inicio -> true

                R.id.nav_medicos -> {
                    abrirListaMedicos()
                    true
                }

                R.id.nav_citas -> {
                    startActivity(
                        Intent(
                            this,
                            MisCitasActivity::class.java
                        )
                    )
                    true
                }

                else -> false
            }
        }
    }

    private fun abrirListaMedicos(
        especialidad: String? = null
    ) {

        val intent = Intent(
            this,
            MedicosActivity::class.java
        )

        if (!especialidad.isNullOrBlank()) {
            intent.putExtra(
                "especialidad",
                especialidad
            )
        }

        startActivity(intent)
    }

    private fun cargarProximaCita() {

        try {

            val cita = CitasRepository(this).obtenerProxima()

            if (cita == null) {

                proximaCitaId = null

                tvProxHora.setText(
                    R.string.sin_citas_proximas
                )

                tvProxMedico.setText(
                    R.string.agenda_tu_primera_cita
                )

                btnVerDetalle.visibility = View.GONE

            } else {

                proximaCitaId = cita.id

                tvProxHora.text = getString(
                    R.string.proxima_fecha_hora,
                    cita.fecha,
                    cita.hora
                )

                tvProxMedico.text = cita.medicoNombre

                btnVerDetalle.visibility = View.VISIBLE
            }

        } catch (e: Exception) {

            proximaCitaId = null

            tvProxHora.setText(
                R.string.no_se_pudo_cargar_cita
            )

            tvProxMedico.setText(
                R.string.intenta_de_nuevo
            )

            btnVerDetalle.visibility = View.GONE
        }
    }

    private fun cargarMedicosRecientes() {

        try {

            val medicos = MedicosRepository(this)
                .obtenerRecientes()

            if (medicos.isEmpty()) {

                lvRecientes.visibility = View.GONE
                tvSinRecientes.visibility = View.VISIBLE

                tvSinRecientes.setText(
                    R.string.sin_medicos_recientes
                )

            } else {

                tvSinRecientes.visibility = View.GONE
                lvRecientes.visibility = View.VISIBLE

                lvRecientes.adapter = MedicoAdapter(
                    this,
                    medicos
                )

                lvRecientes.setOnItemClickListener {
                        _, _, position, _ ->

                    val medico = medicos[position]

                    val intent = Intent(
                        this,
                        DetalleMedicoActivity::class.java
                    )

                    intent.putExtra(
                        "medico_id",
                        medico.id
                    )

                    startActivity(intent)
                }
            }

        } catch (e: Exception) {

            lvRecientes.visibility = View.GONE
            tvSinRecientes.visibility = View.VISIBLE

            tvSinRecientes.setText(
                R.string.no_se_pudieron_cargar_medicos
            )
        }
    }
}