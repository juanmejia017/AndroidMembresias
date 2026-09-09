package com.example.zonafitmembresias.data.repository

import com.example.zonafitmembresias.data.model.*
import com.example.zonafitmembresias.data.remote.ApiService
import com.example.zonafitmembresias.data.remote.RetrofitClient
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

/** Resultado tipado: evita propagar excepciones crudas hasta la UI. */
sealed class ResultadoApi<out T> {
    data class Exito<T>(val datos: T) : ResultadoApi<T>()
    data class Error(val mensaje: String) : ResultadoApi<Nothing>()
}

/**
 * Repositorio: unica puerta de entrada a la API para el resto de la app.
 * Ningun ViewModel llama a RetrofitClient.apiService directamente.
 */
class MembresiaRepository(
    private val api: ApiService = RetrofitClient.apiService
) {
    private suspend fun <T> ejecutar(llamada: suspend () -> T): ResultadoApi<T> {
        return try {
            ResultadoApi.Exito(llamada())
        } catch (e: HttpException) {
            ResultadoApi.Error(extraerDetalle(e))
        } catch (e: IOException) {
            ResultadoApi.Error("No fue posible conectar con el servidor. Verifica tu conexion.")
        } catch (e: Exception) {
            ResultadoApi.Error("Ocurrio un error inesperado: ${e.message}")
        }
    }

    /** Extrae el campo "detail" que envia FastAPI en sus errores HTTPException. */
    private fun extraerDetalle(e: HttpException): String {
        return try {
            val cuerpo = e.response()?.errorBody()?.string()
            JSONObject(cuerpo ?: "{}").optString("detail", "Error del servidor (${e.code()})")
        } catch (ex: Exception) {
            "Error del servidor (${e.code()})"
        }
    }

    // ---------- Clientes ----------

    suspend fun listarClientes() = ejecutar { api.listarClientes() }

    suspend fun crearCliente(nombre: String, cedula: String, correo: String, telefono: String?) =
        ejecutar { api.crearCliente(ClienteRequest(nombre, cedula, correo, telefono)) }

    // ---------- Planes ----------

    suspend fun listarPlanes(soloActivos: Boolean = false) =
        ejecutar { api.listarPlanes(soloActivos) }

    suspend fun crearPlan(nombre: String, tipoDuracion: String, categoria: String, precioBase: Double) =
        ejecutar { api.crearPlan(PlanRequest(nombre, tipoDuracion, categoria, precioBase)) }

    suspend fun actualizarPrecioPlan(planId: Int, nuevoPrecio: Double) =
        ejecutar { api.actualizarPlan(planId, PlanUpdateRequest(precioBase = nuevoPrecio)) }

    suspend fun eliminarPlan(planId: Int): ResultadoApi<Unit> {
        return try {
            val resp = api.eliminarPlan(planId)
            if (resp.isSuccessful) ResultadoApi.Exito(Unit)
            else ResultadoApi.Error(
                JSONObject(resp.errorBody()?.string() ?: "{}")
                    .optString("detail", "No se pudo eliminar el plan (${resp.code()})")
            )
        } catch (e: IOException) {
            ResultadoApi.Error("No fue posible conectar con el servidor.")
        }
    }

    // ---------- Membresias ----------

    suspend fun listarMembresias(estado: String? = null) =
        ejecutar { api.listarMembresias(estado) }

    suspend fun asignarMembresia(clienteId: Int, planId: Int) =
        ejecutar { api.asignarMembresia(MembresiaRequest(clienteId, planId)) }

    suspend fun renovarMembresia(membresiaId: Int) =
        ejecutar { api.renovarMembresia(membresiaId) }

    suspend fun cancelarMembresia(membresiaId: Int) =
        ejecutar { api.cancelarMembresia(membresiaId) }

    suspend fun validarAcceso(clienteId: Int) =
        ejecutar { api.validarAcceso(clienteId) }
}
