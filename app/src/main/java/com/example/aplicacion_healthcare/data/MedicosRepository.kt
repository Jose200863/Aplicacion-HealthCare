package com.example.aplicacion_healthcare.data

import android.content.Context
import android.database.Cursor
import com.example.aplicacion_healthcare.database.HealthCareDbHelper
import com.example.aplicacion_healthcare.models.Medico

// Centraliza las consultas de la tabla medico (solo lectura).
// Las pantallas llaman a esta clase; nunca escriben SQL directamente.

class MedicosRepository(context: Context) {

    // Instancia que permite acceder a la base de datos.
    private val dbHelper = HealthCareDbHelper(context)

    // --------------------------------------------------
    // READ: Obtener todos los médicos
    // --------------------------------------------------
    fun obtenerTodos(): List<Medico> {
        return consultar(null, null, null)
    }

    // --------------------------------------------------
    // READ: Obtener un médico por su identificador
    // --------------------------------------------------
    fun obtenerPorId(id: Int): Medico? {
        return consultar(
            "${HealthCareDbHelper.COL_MEDICO_ID} = ?",
            arrayOf(id.toString()),
            null
        ).firstOrNull()
    }

    // --------------------------------------------------
    // READ: Obtener los médicos de una especialidad
    // --------------------------------------------------
    fun obtenerPorEspecialidad(especialidad: String): List<Medico> {
        return consultar(
            "${HealthCareDbHelper.COL_MEDICO_ESPECIALIDAD} = ?",
            arrayOf(especialidad),
            null
        )
    }

    // --------------------------------------------------
    // READ: Buscar por nombre o especialidad (LIKE)
    // --------------------------------------------------
    fun buscar(texto: String): List<Medico> {
        val patron = "%$texto%"
        return consultar(
            "${HealthCareDbHelper.COL_MEDICO_NOMBRE} LIKE ? OR " +
                    "${HealthCareDbHelper.COL_MEDICO_ESPECIALIDAD} LIKE ?",
            arrayOf(patron, patron),
            null
        )
    }

    // --------------------------------------------------
    // READ: Búsqueda y Spinner combinados (los usa MedicosActivity).
    // Si especialidad es null, no se filtra por especialidad.
    // --------------------------------------------------
    fun filtrar(texto: String, especialidad: String?): List<Medico> {

        val condiciones = mutableListOf<String>()
        val argumentos = mutableListOf<String>()

        if (texto.isNotBlank()) {
            condiciones.add(
                "(${HealthCareDbHelper.COL_MEDICO_NOMBRE} LIKE ? OR " +
                        "${HealthCareDbHelper.COL_MEDICO_ESPECIALIDAD} LIKE ?)"
            )
            argumentos.add("%$texto%")
            argumentos.add("%$texto%")
        }

        if (!especialidad.isNullOrBlank()) {
            condiciones.add("${HealthCareDbHelper.COL_MEDICO_ESPECIALIDAD} = ?")
            argumentos.add(especialidad)
        }

        return consultar(
            if (condiciones.isEmpty()) null else condiciones.joinToString(" AND "),
            if (argumentos.isEmpty()) null else argumentos.toTypedArray(),
            null
        )
    }

    // --------------------------------------------------
    // READ: Los primeros médicos para "Atendido recientemente" (Inicio)
    // --------------------------------------------------
    fun obtenerRecientes(): List<Medico> {
        return consultar(null, null, "2")
    }

    // --------------------------------------------------
    // READ: Especialidades sin repetir (para el Spinner)
    // --------------------------------------------------
    fun obtenerEspecialidades(): List<String> {

        val db = dbHelper.readableDatabase

        val cursor = db.query(
            true,
            HealthCareDbHelper.TABLE_MEDICO,
            arrayOf(HealthCareDbHelper.COL_MEDICO_ESPECIALIDAD),
            null,
            null,
            null,
            null,
            "${HealthCareDbHelper.COL_MEDICO_ESPECIALIDAD} ASC",
            null
        )

        val lista = mutableListOf<String>()

        cursor.use {
            while (it.moveToNext()) {
                lista.add(it.getString(0))
            }
        }

        return lista
    }

    // --------------------------------------------------
    // Ejecuta la consulta y convierte cada fila en un Medico.
    // Se usa en todos los métodos para no repetir código.
    // --------------------------------------------------
    private fun consultar(
        seleccion: String?,
        argumentos: Array<String>?,
        limite: String?
    ): List<Medico> {

        val db = dbHelper.readableDatabase

        val cursor = db.query(
            HealthCareDbHelper.TABLE_MEDICO,
            null,
            seleccion,
            argumentos,
            null,
            null,
            "${HealthCareDbHelper.COL_MEDICO_ID} ASC",
            limite
        )

        val lista = mutableListOf<Medico>()

        cursor.use {
            while (it.moveToNext()) {
                lista.add(cursorAMedico(it))
            }
        }

        return lista
    }

    // Convierte la fila actual del cursor en un Medico.
    private fun cursorAMedico(it: Cursor): Medico {

        return Medico(
            id = it.getInt(
                it.getColumnIndexOrThrow(HealthCareDbHelper.COL_MEDICO_ID)
            ),
            nombre = it.getString(
                it.getColumnIndexOrThrow(HealthCareDbHelper.COL_MEDICO_NOMBRE)
            ) ?: "",
            especialidad = it.getString(
                it.getColumnIndexOrThrow(HealthCareDbHelper.COL_MEDICO_ESPECIALIDAD)
            ) ?: "",
            descripcion = it.getString(
                it.getColumnIndexOrThrow(HealthCareDbHelper.COL_MEDICO_DESCRIPCION)
            ) ?: "",
            modalidad = it.getString(
                it.getColumnIndexOrThrow(HealthCareDbHelper.COL_MEDICO_MODALIDAD)
            ) ?: "",
            precio = it.getDouble(
                it.getColumnIndexOrThrow(HealthCareDbHelper.COL_MEDICO_PRECIO)
            ),
            experiencia = it.getInt(
                it.getColumnIndexOrThrow(HealthCareDbHelper.COL_MEDICO_EXPERIENCIA)
            ),
            foto = it.getString(
                it.getColumnIndexOrThrow(HealthCareDbHelper.COL_MEDICO_FOTO)
            ) ?: ""
        )
    }

    // Liberar los recursos de SQLite cuando corresponda.
    fun cerrar() {
        dbHelper.close()
    }
}
