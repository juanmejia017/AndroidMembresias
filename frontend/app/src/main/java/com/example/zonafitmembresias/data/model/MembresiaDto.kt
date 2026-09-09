package com.example.zonafitmembresias.data.model

import com.google.gson.annotations.SerializedName

data class MembresiaDto(
    val id: Int,
    val cliente: ClienteDto,
    val plan: PlanDto,
    @SerializedName("precio_pagado") val precioPagado: Double,
    @SerializedName("fecha_inicio") val fechaInicio: String,
    @SerializedName("fecha_vencimiento") val fechaVencimiento: String,
    val estado: String,
    @SerializedName("fecha_creacion") val fechaCreacion: String
)

data class MembresiaRequest(
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("plan_id") val planId: Int
)

data class ValidarAccesoDto(
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("nombre_cliente") val nombreCliente: String,
    @SerializedName("acceso_permitido") val accesoPermitido: Boolean,
    val estado: String,
    @SerializedName("fecha_vencimiento") val fechaVencimiento: String?,
    val mensaje: String
)
