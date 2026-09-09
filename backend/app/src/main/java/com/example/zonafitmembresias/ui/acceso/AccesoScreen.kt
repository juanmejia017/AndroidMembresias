package com.example.zonafitmembresias.ui.acceso

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zonafitmembresias.data.model.ClienteDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccesoScreen(viewModel: AccesoViewModel = viewModel()) {
    val clientes by viewModel.clientes
    val resultado by viewModel.resultadoValidacion
    val error by viewModel.error

    var clienteSeleccionado by remember { mutableStateOf<ClienteDto?>(null) }
    var expandido by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Validar acceso", style = MaterialTheme.typography.titleLarge)
        Text(
            "Simula el punto de control de acceso: verifica si la membresia del cliente esta vigente.",
            style = MaterialTheme.typography.bodyMedium
        )

        if (clientes.isEmpty()) {
            Text("Registra al menos un cliente para poder validar el acceso.")
            return@Column
        }

        ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
            OutlinedTextField(
                value = clienteSeleccionado?.let { "${it.nombre} (CC ${it.cedula})" } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Cliente que solicita ingreso") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                clientes.forEach { cliente ->
                    DropdownMenuItem(
                        text = { Text("${cliente.nombre} (CC ${cliente.cedula})") },
                        onClick = {
                            clienteSeleccionado = cliente
                            expandido = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = { clienteSeleccionado?.let { viewModel.validarAcceso(it.id) } },
            enabled = clienteSeleccionado != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Validar ingreso")
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        resultado?.let { r ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (r.accesoPermitido)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (r.accesoPermitido) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (r.accesoPermitido) "ACCESO PERMITIDO" else "ACCESO DENEGADO",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(r.mensaje)
                    Text("Estado actual: ${r.estado}")
                    r.fechaVencimiento?.let { Text("Fecha de vencimiento: $it") }
                }
            }
        }
    }
}
