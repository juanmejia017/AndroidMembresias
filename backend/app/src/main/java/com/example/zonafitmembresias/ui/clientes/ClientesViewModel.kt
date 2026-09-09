package com.example.zonafitmembresias.ui.clientes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zonafitmembresias.data.model.ClienteDto
import com.example.zonafitmembresias.data.repository.MembresiaRepository
import com.example.zonafitmembresias.data.repository.ResultadoApi
import kotlinx.coroutines.launch

class ClientesViewModel(
    private val repo: MembresiaRepository = MembresiaRepository()
) : ViewModel() {

    val clientes = mutableStateOf<List<ClienteDto>>(emptyList())
    val cargando = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val mensajeExito = mutableStateOf<String?>(null)

    init {
        cargarClientes()
    }

    fun cargarClientes() {
        viewModelScope.launch {
            cargando.value = true
            when (val resultado = repo.listarClientes()) {
                is ResultadoApi.Exito -> {
                    clientes.value = resultado.datos
                    error.value = null
                }
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
            cargando.value = false
        }
    }

    fun crearCliente(nombre: String, cedula: String, correo: String, telefono: String) {
        viewModelScope.launch {
            // El telefono vacio se envia como null (no como el texto "null").
            val telefonoFinal = telefono.ifBlank { null }
            when (val resultado = repo.crearCliente(nombre, cedula, correo, telefonoFinal)) {
                is ResultadoApi.Exito -> {
                    mensajeExito.value = "Cliente '$nombre' registrado correctamente."
                    error.value = null
                    cargarClientes()
                }
                is ResultadoApi.Error -> error.value = resultado.mensaje
            }
        }
    }
}
