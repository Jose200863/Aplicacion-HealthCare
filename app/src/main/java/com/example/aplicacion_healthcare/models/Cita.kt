package com.example.aplicacion_healthcare.models

data class Cita(
    val id: Int = 0,
    val medicoId: Int,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val modalidad: String,
    val notas: String = "",
    val medicoNombre: String = "",
    val medicoEspecialidad: String = ""
)
