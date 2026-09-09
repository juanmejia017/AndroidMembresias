package com.example.zonafitmembresias.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Rutas(val ruta: String, val titulo: String, val icono: ImageVector) {
    data object Acceso : Rutas("acceso", "Acceso", Icons.Filled.Verified)
    data object Membresias : Rutas("membresias", "Membresias", Icons.Filled.CardMembership)
    data object Planes : Rutas("planes", "Planes", Icons.Filled.List)
    data object Clientes : Rutas("clientes", "Clientes", Icons.Filled.People)

    companion object {
        val todas: List<Rutas> = listOf(Acceso, Membresias, Planes, Clientes)
    }
}