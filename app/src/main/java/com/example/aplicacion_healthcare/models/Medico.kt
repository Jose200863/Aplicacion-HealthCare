package com.example.aplicacion_healthcare.models

data class Medico (
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val descripcion: String,
    val modalidad: String,
    val precio: Double,
    val experiencia: Int,
    val foto: String
)
