package com.example.zonafitmembresias.ui.clientes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zonafitmembresias.data.model.ClienteDto

@Composable
fun ClientesScreen(viewModel: ClientesViewModel = viewModel()) {
    // Refresca la lista cada vez que se entra a esta pantalla.
    LaunchedEffect(Unit) {
        viewModel.cargarClientes()
    }

    val clientes by viewModel.clientes
    val error by viewModel.error
    val mensajeExito by viewModel.mensajeExito

    var nombre by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Clientes", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = { viewModel.cargarClientes() }) { Text("Actualizar") }
            }
        }

        item {
            OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(cedula, { cedula = it }, label = { Text("Cedula") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(correo, { correo = it }, label = { Text("Correo electronico") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(telefono, { telefono = it }, label = { Text("Telefono") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            Button(
                onClick = {
                    if (nombre.isNotBlank() && cedula.isNotBlank() && correo.isNotBlank()) {
                        viewModel.crearCliente(nombre, cedula, correo, telefono)
                        nombre = ""; cedula = ""; correo = ""; telefono = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Registrar cliente") }
        }

        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        mensajeExito?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }

        item { HorizontalDivider() }
        item { Text("Clientes registrados", style = MaterialTheme.typography.titleMedium) }

        items(clientes) { c -> TarjetaCliente(c) }
    }
}

@Composable
private fun TarjetaCliente(c: ClienteDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("#${c.id} - ${c.nombre}")
            Text("Cedula: ${c.cedula}")
            Text("Correo: ${c.correo}")
            c.telefono?.let { Text("Telefono: $it") }
        }
    }
}