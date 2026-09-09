package com.example.zonafitmembresias.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zonafitmembresias.ui.acceso.AccesoScreen
import com.example.zonafitmembresias.ui.clientes.ClientesScreen
import com.example.zonafitmembresias.ui.membresias.MembresiasScreen
import com.example.zonafitmembresias.ui.planes.PlanesScreen

private val ICONOS = mapOf(
    Rutas.Acceso.ruta to Icons.Filled.Verified,
    Rutas.Membresias.ruta to Icons.Filled.CardMembership,
    Rutas.Planes.ruta to Icons.Filled.List,
    Rutas.Clientes.ruta to Icons.Filled.People
)

@Composable
fun ZonaFitNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val destinoActual = navBackStackEntry?.destination

                Rutas.todas.forEach { pantalla ->
                    NavigationBarItem(
                        selected = destinoActual?.hierarchy?.any { it.route == pantalla.ruta } == true,
                        onClick = {
                            navController.navigate(pantalla.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(ICONOS[pantalla.ruta]!!, contentDescription = pantalla.titulo) },
                        label = { Text(pantalla.titulo) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.Acceso.ruta,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(Rutas.Acceso.ruta) { AccesoScreen() }
            composable(Rutas.Membresias.ruta) { MembresiasScreen() }
            composable(Rutas.Planes.ruta) { PlanesScreen() }
            composable(Rutas.Clientes.ruta) { ClientesScreen() }
        }
    }
}
