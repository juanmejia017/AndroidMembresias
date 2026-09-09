package com.example.zonafitmembresias.data.model

import com.google.gson.annotations.SerializedName

data class ClienteDto(
    val id: Int,
    val nombre: String,
    val cedula: String,
    val correo: String,
    val telefono: String?,
    @SerializedName("fecha_registro") val fechaRegistro: String
)

data class ClienteRequest(
    val nombre: String,
    val cedula: String,
    val correo: String,
    val telefono: String?
)
