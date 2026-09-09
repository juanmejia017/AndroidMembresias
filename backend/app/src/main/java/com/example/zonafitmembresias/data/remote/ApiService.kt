package com.example.zonafitmembresias.data.remote

import com.example.zonafitmembresias.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------- Clientes ----------

    @GET("clientes")
    suspend fun listarClientes(): List<ClienteDto>

    @POST("clientes")
    suspend fun crearCliente(@Body data: ClienteRequest): ClienteDto

    // ---------- Planes ----------

    @GET("planes")
    suspend fun listarPlanes(@Query("solo_activos") soloActivos: Boolean = false): List<PlanDto>

    @POST("planes")
    suspend fun crearPlan(@Body data: PlanRequest): PlanDto

    @PUT("planes/{planId}")
    suspend fun actualizarPlan(
        @Path("planId") planId: Int,
        @Body data: PlanUpdateRequest
    ): PlanDto

    @DELETE("planes/{planId}")
    suspend fun eliminarPlan(@Path("planId") planId: Int): Response<Unit>

    // ---------- Membresias ----------

    @POST("membresias")
    suspend fun asignarMembresia(@Body data: MembresiaRequest): MembresiaDto

    @GET("membresias")
    suspend fun listarMembresias(@Query("estado") estado: String? = null): List<MembresiaDto>

    @PUT("membresias/{membresiaId}/renovar")
    suspend fun renovarMembresia(@Path("membresiaId") membresiaId: Int): MembresiaDto

    @PUT("membresias/{membresiaId}/cancelar")
    suspend fun cancelarMembresia(@Path("membresiaId") membresiaId: Int): MembresiaDto

    @GET("membresias/validar-acceso/{clienteId}")
    suspend fun validarAcceso(@Path("clienteId") clienteId: Int): ValidarAccesoDto
}
