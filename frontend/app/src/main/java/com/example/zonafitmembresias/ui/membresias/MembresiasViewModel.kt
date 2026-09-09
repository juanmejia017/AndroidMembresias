package com.example.zonafitmembresias.ui.membresias

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zonafitmembresias.data.model.ClienteDto
import com.example.zonafitmembresias.data.model.MembresiaDto
import com.example.zonafitmembresias.data.model.PlanDto
import com.example.zonafitmembresias.data.repository.MembresiaRepository
import com.example.zonafitmembresias.data.repository.ResultadoApi
import kotlinx.coroutines.launch

class MembresiasViewModel(
    private val repo: MembresiaRepository = MembresiaRepository()
) : ViewModel() {

    val membresias = mutableStateOf<List<MembresiaDto>>(emptyList())
    val clientes = mutableStateOf<List<ClienteDto>>(emptyList())
    val planesActivos = mutableStateOf<List<PlanDto>>(emptyList())
    val cargando = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    init {
        cargarDatosBase()
        cargarMembresias()
    }

    fun cargarDatosBase() {
        viewModelScope.launch {
            when (val r = repo.listarClientes()) {
                is ResultadoApi.Exito -> clientes.value = r.datos
                is ResultadoApi.Error -> error.value = r.mensaje
            }
            when (val r = repo.listarPlanes(soloActivos = true)) {
                is ResultadoApi.Exito -> planesActivos.value = r.datos
                is ResultadoApi.Error -> error.value = r.mensaje
            }
        }
    }

    fun cargarMembresias(estado: String? = null) {
        viewModelScope.launch {
            cargando.value = true
            when (val resultado = repo.listarMembresias(estado)) {
                is ResultadoApi.Exito -> {
                    membresias.value = resultado.datos
                    error.value = null
                }
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
            cargando.value = false
        }
    }

    fun asignarMembresia(clienteId: Int, planId: Int) {
        viewModelScope.launch {
            when (val resultado = repo.asignarMembresia(clienteId, planId)) {
                is ResultadoApi.Exito -> {
                    error.value = null
                    cargarMembresias()
                }
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
        }
    }

    fun renovarMembresia(membresiaId: Int) {
        viewModelScope.launch {
            when (val resultado = repo.renovarMembresia(membresiaId)) {
                is ResultadoApi.Exito -> cargarMembresias()
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
        }
    }

    fun cancelarMembresia(membresiaId: Int) {
        viewModelScope.launch {
            when (val resultado = repo.cancelarMembresia(membresiaId)) {
                is ResultadoApi.Exito -> cargarMembresias()
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
        }
    }
}
