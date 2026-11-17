package com.example.aplicacion_healthcare.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.aplicacion_healthcare.database.HealthCareDbHelper
import com.example.aplicacion_healthcare.models.Cita

// Centraliza la administración de las citas.
// Las pantallas llaman a esta clase; nunca escriben SQL directamente.

class CitasRepository(context: Context) {

    private val dbHelper = HealthCareDbHelper(context)

    companion object {
        private val QUERY_BASE = """
            SELECT c.*, 
                   m.${HealthCareDbHelper.COL_MEDICO_NOMBRE} AS medico_nombre, 
                   m.${HealthCareDbHelper.COL_MEDICO_ESPECIALIDAD} AS medico_especialidad
            FROM ${HealthCareDbHelper.TABLE_CITA} c
            LEFT JOIN ${HealthCareDbHelper.TABLE_MEDICO} m ON c.${HealthCareDbHelper.COL_CITA_MEDICO_ID} = m.${HealthCareDbHelper.COL_MEDICO_ID}
        """.trimIndent()
    }

    // --------------------------------------------------
    // READ: Obtener las citas próximas (hoy en adelante)
    // --------------------------------------------------
    fun obtenerProximas(): List<Cita> {
        val db = dbHelper.readableDatabase
        val sql = "$QUERY_BASE WHERE c.${HealthCareDbHelper.COL_CITA_FECHA} >= date('now', 'localtime') ORDER BY c.${HealthCareDbHelper.COL_CITA_FECHA} ASC, c.${HealthCareDbHelper.COL_CITA_HORA} ASC"
        val cursor = db.rawQuery(sql, null)

        val lista = mutableListOf<Cita>()
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
        val sql = "$QUERY_BASE WHERE c.${HealthCareDbHelper.COL_CITA_FECHA} < date('now', 'localtime') ORDER BY c.${HealthCareDbHelper.COL_CITA_FECHA} DESC, c.${HealthCareDbHelper.COL_CITA_HORA} DESC"
        val cursor = db.rawQuery(sql, null)

        val lista = mutableListOf<Cita>()
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
        val sql = "$QUERY_BASE WHERE c.${HealthCareDbHelper.COL_CITA_ID} = ?"
        val cursor = db.rawQuery(sql, arrayOf(id.toString()))

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
            put(HealthCareDbHelper.COL_CITA_NOTAS, cita.notas)
        }

        return db.insert(
            HealthCareDbHelper.TABLE_CITA,
            null,
            values
        )
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
            put(HealthCareDbHelper.COL_CITA_NOTAS, cita.notas)
        }

        return db.update(
            HealthCareDbHelper.TABLE_CITA,
            values,
            "${HealthCareDbHelper.COL_CITA_ID} = ?",
            arrayOf(cita.id.toString())
        )
    }

    // --------------------------------------------------
    // DELETE: Eliminar una cita
    // --------------------------------------------------
    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase

        return db.delete(
            HealthCareDbHelper.TABLE_CITA,
            "${HealthCareDbHelper.COL_CITA_ID} = ?",
            arrayOf(id.toString())
        )
    }

    // --------------------------------------------------
    // Convierte la fila actual del cursor en una Cita.
    // --------------------------------------------------
    private fun cursorACita(it: Cursor): Cita {
        val getColString = { colName: String ->
            val idx = it.getColumnIndex(colName)
            if (idx != -1 && !it.isNull(idx)) it.getString(idx) else ""
        }
        val getColInt = { colName: String ->
            val idx = it.getColumnIndex(colName)
            if (idx != -1 && !it.isNull(idx)) it.getInt(idx) else 0
        }

        return Cita(
            id = getColInt(HealthCareDbHelper.COL_CITA_ID),
            medicoId = getColInt(HealthCareDbHelper.COL_CITA_MEDICO_ID),
            fecha = getColString(HealthCareDbHelper.COL_CITA_FECHA),
            hora = getColString(HealthCareDbHelper.COL_CITA_HORA),
            motivo = getColString(HealthCareDbHelper.COL_CITA_MOTIVO),
            modalidad = getColString(HealthCareDbHelper.COL_CITA_MODALIDAD),
            notas = getColString(HealthCareDbHelper.COL_CITA_NOTAS),
            medicoNombre = getColString("medico_nombre"),
            medicoEspecialidad = getColString("medico_especialidad")
        )
    }

    fun cerrar() {
        dbHelper.close()
    }
}
