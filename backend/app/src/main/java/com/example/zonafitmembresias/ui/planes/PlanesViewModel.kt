package com.example.zonafitmembresias.ui.planes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zonafitmembresias.data.model.PlanDto
import com.example.zonafitmembresias.data.repository.MembresiaRepository
import com.example.zonafitmembresias.data.repository.ResultadoApi
import kotlinx.coroutines.launch

class PlanesViewModel(
    private val repo: MembresiaRepository = MembresiaRepository()
) : ViewModel() {

    val planes = mutableStateOf<List<PlanDto>>(emptyList())
    val cargando = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    init {
        cargarPlanes()
    }

    fun cargarPlanes() {
        viewModelScope.launch {
            cargando.value = true
            when (val resultado = repo.listarPlanes()) {
                is ResultadoApi.Exito -> {
                    planes.value = resultado.datos
                    error.value = null
                }
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
            cargando.value = false
        }
    }

    fun crearPlan(nombre: String, tipoDuracion: String, categoria: String, precioBase: Double) {
        viewModelScope.launch {
            when (val resultado = repo.crearPlan(nombre, tipoDuracion, categoria, precioBase)) {
                is ResultadoApi.Exito -> {
                    error.value = null
                    cargarPlanes()
                }
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
        }
    }

    fun actualizarPrecio(planId: Int, nuevoPrecio: Double) {
        viewModelScope.launch {
            when (val resultado = repo.actualizarPrecioPlan(planId, nuevoPrecio)) {
                is ResultadoApi.Exito -> cargarPlanes()
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
        }
    }

    fun eliminarPlan(planId: Int) {
        viewModelScope.launch {
            // CU-03 Escenario 3: el backend responde 409 si el plan
            // tiene membresias activas asociadas.
            when (val resultado = repo.eliminarPlan(planId)) {
                is ResultadoApi.Exito -> cargarPlanes()
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
        }
    }
}
