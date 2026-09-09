package com.example.zonafitmembresias.ui.membresias

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zonafitmembresias.data.model.ClienteDto
import com.example.zonafitmembresias.data.model.MembresiaDto
import com.example.zonafitmembresias.data.model.PlanDto

private val ETIQUETA_ESTADO = mapOf(
    "activa" to "Activa", "vencida" to "Vencida", "morosa" to "Morosa", "cancelada" to "Cancelada"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembresiasScreen(viewModel: MembresiasViewModel = viewModel()) {
    // Se ejecuta cada vez que se entra a esta pantalla, para traer los
    // clientes/planes/membresias mas recientes (por si se crearon desde
    // otra pestana despues de la primera vez que se visito esta).
    LaunchedEffect(Unit) {
        viewModel.cargarDatosBase()
        viewModel.cargarMembresias()
    }

    val membresias by viewModel.membresias
    val clientes by viewModel.clientes
    val planes by viewModel.planesActivos
    val error by viewModel.error

    var clienteSel by remember { mutableStateOf<ClienteDto?>(null) }
    var planSel by remember { mutableStateOf<PlanDto?>(null) }
    var filtroEstado by remember { mutableStateOf("(todas)") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Membresias", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = {
                    viewModel.cargarDatosBase()
                    viewModel.cargarMembresias()
                }) { Text("Actualizar") }
            }
        }

        item {
            if (clientes.isEmpty() || planes.isEmpty()) {
                Text("Registra al menos un cliente y un plan activo antes de asignar membresias.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SelectorDesplegable(
                        etiqueta = "Cliente",
                        opciones = clientes.map { "${it.nombre} (CC ${it.cedula})" to it },
                        seleccionado = clienteSel?.let { "${it.nombre} (CC ${it.cedula})" },
                        onSeleccion = { clienteSel = it }
                    )
                    SelectorDesplegable(
                        etiqueta = "Plan",
                        opciones = planes.map {
                            "${it.nombre} - ${it.tipoDuracion}/${it.categoria} - \$${it.precioBase}" to it
                        },
                        seleccionado = planSel?.let {
                            "${it.nombre} - ${it.tipoDuracion}/${it.categoria} - \$${it.precioBase}"
                        },
                        onSeleccion = { planSel = it }
                    )
                    Button(
                        onClick = {
                            val c = clienteSel; val p = planSel
                            if (c != null && p != null) viewModel.asignarMembresia(c.id, p.id)
                        },
                        enabled = clienteSel != null && planSel != null,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Asignar membresia") }
                }
            }
        }

        item { HorizontalDivider() }

        item {
            SelectorDesplegable(
                etiqueta = "Filtrar por estado",
                opciones = listOf("(todas)", "activa", "vencida", "morosa", "cancelada").map { it to it },
                seleccionado = filtroEstado,
                onSeleccion = {
                    filtroEstado = it
                    viewModel.cargarMembresias(if (it == "(todas)") null else it)
                }
            )
        }

        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }

        items(membresias) { m -> TarjetaMembresia(m, viewModel) }
    }
}

@Composable
private fun TarjetaMembresia(m: MembresiaDto, viewModel: MembresiasViewModel) {
    var expandido by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .padding(12.dp)
                .clickable { expandido = !expandido }
        ) {
            Text("[${ETIQUETA_ESTADO[m.estado] ?: m.estado}] #${m.id} - ${m.cliente.nombre} - ${m.plan.nombre}")
            if (expandido) {
                Spacer(Modifier.height(8.dp))
                Text("Inicio: ${m.fechaInicio}  -  Vence: ${m.fechaVencimiento}")
                Text("Precio pagado: \$${m.precioPagado}")
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { viewModel.renovarMembresia(m.id) }) { Text("Renovar") }
                    if (m.estado != "cancelada") {
                        OutlinedButton(onClick = { viewModel.cancelarMembresia(m.id) }) { Text("Cancelar") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SelectorDesplegable(
    etiqueta: String,
    opciones: List<Pair<String, T>>,
    seleccionado: String?,
    onSeleccion: (T) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = seleccionado ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { (texto, valor) ->
                DropdownMenuItem(
                    text = { Text(texto) },
                    onClick = { onSeleccion(valor); expandido = false }
                )
            }
        }
    }
}