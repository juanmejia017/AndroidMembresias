package com.example.zonafitmembresias.ui.planes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zonafitmembresias.data.model.PlanDto

private val TIPOS_DURACION = listOf("diario", "quincenal", "mensual", "trimestral")
private val CATEGORIAS = listOf("normal", "estudiante", "vip", "corporativo")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesScreen(viewModel: PlanesViewModel = viewModel()) {
    val planes by viewModel.planes
    val error by viewModel.error

    var nombre by remember { mutableStateOf("") }
    var precioBase by remember { mutableStateOf("") }
    var tipoDuracion by remember { mutableStateOf(TIPOS_DURACION.first()) }
    var categoria by remember { mutableStateOf(CATEGORIAS.first()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Planes de membresia", style = MaterialTheme.typography.titleLarge) }

        item {
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre del plan") }, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = precioBase, onValueChange = { precioBase = it },
                label = { Text("Precio base (COP)") }, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TIPOS_DURACION.forEach { t ->
                    FilterChip(selected = tipoDuracion == t, onClick = { tipoDuracion = t }, label = { Text(t) })
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CATEGORIAS.forEach { c ->
                    FilterChip(selected = categoria == c, onClick = { categoria = c }, label = { Text(c) })
                }
            }
        }
        item {
            Button(
                onClick = {
                    val precio = precioBase.toDoubleOrNull()
                    if (nombre.isNotBlank() && precio != null && precio > 0) {
                        viewModel.crearPlan(nombre, tipoDuracion, categoria, precio)
                        nombre = ""; precioBase = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Crear plan") }
        }

        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        item { HorizontalDivider() }

        items(planes) { plan -> TarjetaPlan(plan, viewModel) }
    }
}

@Composable
private fun TarjetaPlan(plan: PlanDto, viewModel: PlanesViewModel) {
    var expandido by remember { mutableStateOf(false) }
    var nuevoPrecio by remember(plan.id) { mutableStateOf(plan.precioBase.toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido = !expandido }
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("#${plan.id} - ${plan.nombre} - ${plan.tipoDuracion} / ${plan.categoria}")
            Text("Precio base: \$${plan.precioBase}  -  Activo: ${if (plan.activo) "Si" else "No"}")

            if (expandido) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = nuevoPrecio, onValueChange = { nuevoPrecio = it },
                    label = { Text("Nuevo precio base") }, modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = {
                        nuevoPrecio.toDoubleOrNull()?.let { viewModel.actualizarPrecio(plan.id, it) }
                    }) { Text("Actualizar precio") }

                    OutlinedButton(onClick = { viewModel.eliminarPlan(plan.id) }) { Text("Eliminar plan") }
                }
            }
        }
    }
}
