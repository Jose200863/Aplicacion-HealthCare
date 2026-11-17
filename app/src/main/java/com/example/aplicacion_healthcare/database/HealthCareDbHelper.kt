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

        const val DATABASE_VERSION = 2

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
        const val COL_CITA_NOTAS = "notas"
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
                $COL_CITA_NOTAS TEXT,
                FOREIGN KEY ($COL_CITA_MEDICO_ID) 
                REFERENCES $TABLE_MEDICO ($COL_MEDICO_ID)
            )
        """.trimIndent()

        db.execSQL(createCitas)

        insertarDatosIniciales(db)
    }

    private fun insertarDatosIniciales(db: SQLiteDatabase) {
        val insertMedicos = """
            INSERT INTO $TABLE_MEDICO ($COL_MEDICO_NOMBRE, $COL_MEDICO_ESPECIALIDAD, $COL_MEDICO_DESCRIPCION, $COL_MEDICO_MODALIDAD, $COL_MEDICO_PRECIO, $COL_MEDICO_EXPERIENCIA, $COL_MEDICO_FOTO) VALUES
            ('Dra. Elena Rostova', 'Medicina general', 'Especialista en medicina preventiva y salud familiar.', 'Presencial / Virtual', 25000.0, 10, 'doc_elena'),
            ('Dr. Carlos Mendoza', 'Cardiología', 'Cardiólogo clínico especializado en salud cardiovascular.', 'Presencial', 40000.0, 15, 'doc_carlos'),
            ('Dr. Mateo Silva', 'Dermatología', 'Experto en dermatología clínica y cuidado integral de la piel.', 'Presencial / Virtual', 35000.0, 8, 'doc_mateo'),
            ('Dra. Sofía Castro', 'Oftalmología', 'Oftalmóloga especialista en salud visual integral.', 'Presencial', 30000.0, 12, 'doc_oftalmologia'),
            ('Dra. Lucía Morales', 'Pediatría', 'Atención integral para niños y adolescentes.', 'Presencial', 28000.0, 7, 'doc_pediatria')
        """.trimIndent()

        db.execSQL(insertMedicos)

        val insertCitas = """
            INSERT INTO $TABLE_CITA ($COL_CITA_MEDICO_ID, $COL_CITA_FECHA, $COL_CITA_HORA, $COL_CITA_MOTIVO, $COL_CITA_MODALIDAD, $COL_CITA_NOTAS) VALUES
            (1, date('now', '+1 day'), '09:00', 'Chequeo general de rutina', 'Presencial', 'Paciente prefiere consulta matutina'),
            (2, date('now', '+3 days'), '14:30', 'Control de presión arterial', 'Presencial', 'Llevar exámenes recientes')
        """.trimIndent()

        db.execSQL(insertCitas)
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

