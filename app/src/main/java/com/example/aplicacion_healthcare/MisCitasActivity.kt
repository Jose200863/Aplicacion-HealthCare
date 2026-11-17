package com.example.aplicacion_healthcare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacion_healthcare.adapters.CitasAdapter
import com.example.aplicacion_healthcare.data.CitasRepository
import com.example.aplicacion_healthcare.models.Cita
import com.google.android.material.tabs.TabLayout

// Pantalla 6: lista de citas (próximas y anteriores) con pestañas.
class MisCitasActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var lvCitas: ListView
    private lateinit var tvSinCitas: TextView

    private var citasProximas: List<Cita> = emptyList()
    private var citasAnteriores: List<Cita> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_citas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

        tabLayout = findViewById(R.id.tabLayout)
        lvCitas = findViewById(R.id.lvCitas)
        tvSinCitas = findViewById(R.id.tvSinCitas)

        configurarTabs()
    }

    override fun onResume() {
        super.onResume()
        cargarCitas()
    }

    private fun configurarTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.mis_citas_proximas))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.mis_citas_anteriores))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mostrarCitas(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun cargarCitas() {
        try {
            val repo = CitasRepository(this)
            citasProximas = repo.obtenerProximas()
            citasAnteriores = repo.obtenerAnteriores()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar las citas", Toast.LENGTH_SHORT).show()
        }

        mostrarCitas(tabLayout.selectedTabPosition)
    }

    private fun mostrarCitas(tab: Int) {
        val lista: List<Cita> = if (tab == 0) citasProximas else citasAnteriores
        val mensajeSin = if (tab == 0) R.string.mis_citas_sin_proximas else R.string.mis_citas_sin_anteriores

        if (lista.isEmpty()) {
            lvCitas.visibility = View.GONE
            tvSinCitas.visibility = View.VISIBLE
            tvSinCitas.setText(mensajeSin)
        } else {
            tvSinCitas.visibility = View.GONE
            lvCitas.visibility = View.VISIBLE

            val adapter = CitasAdapter(this, lista)
            lvCitas.adapter = adapter

            lvCitas.setOnItemClickListener { _, _, position, _ ->
                val cita = lista[position]
                val intent = Intent(this, DetalleCitaActivity::class.java)
                intent.putExtra("cita_id", cita.id)
                startActivity(intent)
            }
        }
    }
}
