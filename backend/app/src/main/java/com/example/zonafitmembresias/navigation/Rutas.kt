package com.example.zonafitmembresias.navigation

sealed class Rutas(val ruta: String, val titulo: String) {
    data object Acceso : Rutas("acceso", "Acceso")
    data object Membresias : Rutas("membresias", "Membresias")
    data object Planes : Rutas("planes", "Planes")
    data object Clientes : Rutas("clientes", "Clientes")

    companion object {
        val todas = listOf(Acceso, Membresias, Planes, Clientes)
    }
}
