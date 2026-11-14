package com.example.aplicacion_healthcare.models

data class Cita(
    val id: Int,
    val medicoId: Int,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val modalidad: String
)
