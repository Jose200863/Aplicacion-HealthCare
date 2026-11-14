package com.example.aplicacion_healthcare.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class HealthCareDbHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {

    companion object {
        const val DATABASE_NAME = "HealthCarePlus.db"

        const val DATABASE_VERSION = 1

        const val TABLE_MEDICO = "medico"

        const val COL_MEDICO_ID = "id"
        const val COL_MEDICO_NOMBRE = "nombre"
        const val COL_MEDICO_ESPECIALIDAD = "especialidad"
        const val COL_MEDICO_DESCRIPCION = "descripcion"
        const val COL_MEDICO_MODALIDAD = "modalidad"
        const val COL_MEDICO_PRECIO = "precio"
        const val COL_MEDICO_EXPERIENCIA = "experiencia"
        const val COL_MEDICO_FOTO = "foto"

        const val TABLE_CITA = "cita"

        const val COL_CITA_ID = "id"
        const val COL_CITA_MEDICO_ID = "medico_id"
        const val COL_CITA_FECHA = "fecha"
        const val COL_CITA_HORA = "hora"
        const val COL_CITA_MOTIVO = "motivo"
        const val COL_CITA_MODALIDAD = "modalidad"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {

        val createMedicos = """
            CREATE TABLE $TABLE_MEDICO (
                $COL_MEDICO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_MEDICO_NOMBRE TEXT NOT NULL,
                $COL_MEDICO_ESPECIALIDAD TEXT NOT NULL,
                $COL_MEDICO_DESCRIPCION TEXT,
                $COL_MEDICO_MODALIDAD TEXT,
                $COL_MEDICO_PRECIO REAL,
                $COL_MEDICO_EXPERIENCIA INTEGER,
                $COL_MEDICO_FOTO TEXT
            )
        """.trimIndent()

        db.execSQL(createMedicos)

        val createCitas = """
            CREATE TABLE $TABLE_CITA (
                $COL_CITA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CITA_MEDICO_ID INTEGER NOT NULL,
                $COL_CITA_FECHA TEXT NOT NULL,
                $COL_CITA_HORA TEXT NOT NULL,
                $COL_CITA_MOTIVO TEXT NOT NULL,
                $COL_CITA_MODALIDAD TEXT,
                FOREIGN KEY ($COL_CITA_MEDICO_ID) 
                REFERENCES $TABLE_MEDICO ($COL_MEDICO_ID)
            )
        """.trimIndent()

        db.execSQL(createCitas)

    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CITA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEDICO")
        onCreate(db)
    }
}

