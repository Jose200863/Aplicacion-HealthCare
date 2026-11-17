package com.example.aplicacion_healthcare.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.aplicacion_healthcare.database.HealthCareDbHelper
import com.example.aplicacion_healthcare.models.Cita

// Centraliza la administración de las citas.
// Las pantallas llaman a esta clase; nunca escriben SQL directamente.

class CitasRepository(context: Context) {

    // Instancia que permite acceder a la base de datos.
    private val dbHelper = HealthCareDbHelper(context)

    // --------------------------------------------------
    // READ: Obtener las citas próximas (hoy en adelante)
    // --------------------------------------------------
    fun obtenerProximas(): List<Cita> {

        val db = dbHelper.readableDatabase

        val cursor = db.query(
            HealthCareDbHelper.TABLE_CITA,
            null,
            "${HealthCareDbHelper.COL_CITA_FECHA} >= date('now', 'localtime')",
            null,
            null,
            null,
            "${HealthCareDbHelper.COL_CITA_FECHA} ASC, " +
                    "${HealthCareDbHelper.COL_CITA_HORA} ASC"
        )

        val lista = mutableListOf<Cita>()

        // Recorremos los resultados obtenidos desde SQLite.
        cursor.use {

            while (it.moveToNext()) {
                lista.add(cursorACita(it))
            }
        }

        return lista
    }

    // --------------------------------------------------
    // READ: Obtener las citas anteriores (antes de hoy)
    // --------------------------------------------------
    fun obtenerAnteriores(): List<Cita> {

        val db = dbHelper.readableDatabase

        val cursor = db.query(
            HealthCareDbHelper.TABLE_CITA,
            null,
            "${HealthCareDbHelper.COL_CITA_FECHA} < date('now', 'localtime')",
            null,
            null,
            null,
            "${HealthCareDbHelper.COL_CITA_FECHA} DESC, " +
                    "${HealthCareDbHelper.COL_CITA_HORA} DESC"
        )

        val lista = mutableListOf<Cita>()

        // Recorremos los resultados obtenidos desde SQLite.
        cursor.use {

            while (it.moveToNext()) {
                lista.add(cursorACita(it))
            }
        }

        return lista
    }

    // --------------------------------------------------
    // READ: Obtener una cita por su identificador
    // --------------------------------------------------
    fun obtenerPorId(id: Int): Cita? {

        val db = dbHelper.readableDatabase

        val cursor = db.query(
            HealthCareDbHelper.TABLE_CITA,
            null,
            "${HealthCareDbHelper.COL_CITA_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {

            if (it.moveToFirst()) {
                return cursorACita(it)
            }
        }

        return null
    }

    // --------------------------------------------------
    // READ: Obtener la cita más cercana desde hoy
    // (se usa en la tarjeta "Tu próxima cita" de Inicio)
    // --------------------------------------------------
    fun obtenerProxima(): Cita? {

        return obtenerProximas().firstOrNull()
    }

    // --------------------------------------------------
    // CREATE: Agregar una cita
    // --------------------------------------------------
    fun insertar(cita: Cita): Long {

        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {

            put(HealthCareDbHelper.COL_CITA_MEDICO_ID, cita.medicoId)
            put(HealthCareDbHelper.COL_CITA_FECHA, cita.fecha)
            put(HealthCareDbHelper.COL_CITA_HORA, cita.hora)
            put(HealthCareDbHelper.COL_CITA_MOTIVO, cita.motivo)
            put(HealthCareDbHelper.COL_CITA_MODALIDAD, cita.modalidad)
        }

        // insert() devuelve el ID generado o -1 si falla.
        val resultado = db.insert(
            HealthCareDbHelper.TABLE_CITA,
            null,
            values
        )

        return resultado
    }

    // --------------------------------------------------
    // UPDATE: Actualizar una cita
    // --------------------------------------------------
    fun actualizar(cita: Cita): Int {

        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {

            put(HealthCareDbHelper.COL_CITA_MEDICO_ID, cita.medicoId)
            put(HealthCareDbHelper.COL_CITA_FECHA, cita.fecha)
            put(HealthCareDbHelper.COL_CITA_HORA, cita.hora)
            put(HealthCareDbHelper.COL_CITA_MOTIVO, cita.motivo)
            put(HealthCareDbHelper.COL_CITA_MODALIDAD, cita.modalidad)
        }

        // El signo ? evita concatenar directamente el ID en SQL.
        val resultado = db.update(
            HealthCareDbHelper.TABLE_CITA,
            values,
            "${HealthCareDbHelper.COL_CITA_ID} = ?",
            arrayOf(cita.id.toString())
        )

        return resultado
    }

    // --------------------------------------------------
    // DELETE: Eliminar una cita
    // --------------------------------------------------
    fun eliminar(id: Int): Int {

        val db = dbHelper.writableDatabase

        val resultado = db.delete(
            HealthCareDbHelper.TABLE_CITA,
            "${HealthCareDbHelper.COL_CITA_ID} = ?",
            arrayOf(id.toString())
        )

        return resultado
    }

    // --------------------------------------------------
    // Convierte la fila actual del cursor en una Cita.
    // Se usa en todas las consultas para no repetir código.
    // --------------------------------------------------
    private fun cursorACita(it: Cursor): Cita {

        return Cita(
            id = it.getInt(
                it.getColumnIndexOrThrow(
                    HealthCareDbHelper.COL_CITA_ID
                )
            ),
            medicoId = it.getInt(
                it.getColumnIndexOrThrow(
                    HealthCareDbHelper.COL_CITA_MEDICO_ID
                )
            ),
            fecha = it.getString(
                it.getColumnIndexOrThrow(
                    HealthCareDbHelper.COL_CITA_FECHA
                )
            ),
            hora = it.getString(
                it.getColumnIndexOrThrow(
                    HealthCareDbHelper.COL_CITA_HORA
                )
            ),
            motivo = it.getString(
                it.getColumnIndexOrThrow(
                    HealthCareDbHelper.COL_CITA_MOTIVO
                )
            ),
            modalidad = it.getString(
                it.getColumnIndexOrThrow(
                    HealthCareDbHelper.COL_CITA_MODALIDAD
                )
            ) ?: ""
        )
    }

    // Liberar los recursos de SQLite cuando corresponda.
    fun cerrar() {
        dbHelper.close()
    }
}