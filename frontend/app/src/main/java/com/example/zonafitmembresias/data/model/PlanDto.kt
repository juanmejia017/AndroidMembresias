package com.example.zonafitmembresias.data.model

import com.google.gson.annotations.SerializedName

data class PlanDto(
    val id: Int,
    val nombre: String,
    @SerializedName("tipo_duracion") val tipoDuracion: String,
    val categoria: String,
    @SerializedName("precio_base") val precioBase: Double,
    val activo: Boolean
)

data class PlanRequest(
    val nombre: String,
    @SerializedName("tipo_duracion") val tipoDuracion: String,
    val categoria: String,
    @SerializedName("precio_base") val precioBase: Double
)

/**
 * Para la actualizacion parcial (PUT /planes/{id}) solo se envian los
 * campos que cambian. Gson, por defecto, NO serializa propiedades en
 * null, asi que dejar el resto en null equivale a "no tocar ese campo"
 * -- igual que espera el backend (PlanUpdate con exclude_unset).
 */
data class PlanUpdateRequest(
    val nombre: String? = null,
    @SerializedName("precio_base") val precioBase: Double? = null,
    val activo: Boolean? = null
)
