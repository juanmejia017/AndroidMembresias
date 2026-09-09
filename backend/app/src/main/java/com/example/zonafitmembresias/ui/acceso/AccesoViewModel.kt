package com.example.zonafitmembresias.ui.acceso

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zonafitmembresias.data.model.ClienteDto
import com.example.zonafitmembresias.data.model.ValidarAccesoDto
import com.example.zonafitmembresias.data.repository.MembresiaRepository
import com.example.zonafitmembresias.data.repository.ResultadoApi
import kotlinx.coroutines.launch

class AccesoViewModel(
    private val repo: MembresiaRepository = MembresiaRepository()
) : ViewModel() {

    val clientes = mutableStateOf<List<ClienteDto>>(emptyList())
    val resultadoValidacion = mutableStateOf<ValidarAccesoDto?>(null)
    val error = mutableStateOf<String?>(null)

    init {
        cargarClientes()
    }

    fun cargarClientes() {
        viewModelScope.launch {
            when (val r = repo.listarClientes()) {
                is ResultadoApi.Exito -> clientes.value = r.datos
                is ResultadoApi.Error -> error.value = r.mensaje
            }
        }
    }

    fun validarAcceso(clienteId: Int) {
        viewModelScope.launch {
            when (val resultado = repo.validarAcceso(clienteId)) {
                is ResultadoApi.Exito -> {
                    resultadoValidacion.value = resultado.datos
                    error.value = null
                }
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
        }
    }
}
