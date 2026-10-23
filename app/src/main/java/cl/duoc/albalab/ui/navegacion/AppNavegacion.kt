package cl.duoc.albalab.ui.navegacion

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duoc.albalab.ui.PantallaCatalogo
import cl.duoc.albalab.ui.PantallaInicio
import cl.duoc.albalab.ui.PantallaProgramas
import cl.duoc.albalab.ui.PantallaRegistro
import cl.duoc.albalab.viewmodel.RegistroViewModel
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.ui.PantallaEditarRegistro
import cl.duoc.albalab.ui.PantallaMisRegistros

object Rutas {
    const val INICIO = "inicio"
    const val PROGRAMAS = "programas"
    const val CATALOGO = "catalogo"
    const val REGISTRO = "registro"
    const val REGISTROS = "registros"
    const val EDITAR = "editar/{id}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    // Una sola instancia del ViewModel, compartida por registro, lista y edición
    val viewModel: RegistroViewModel = viewModel(factory = RegistroViewModel.Factory)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alba Lab") },
                navigationIcon = {
                    if (rutaActual != Rutas.INICIO && rutaActual != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.INICIO,
            modifier = Modifier.padding(padding),
            enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
            exitTransition = { slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
            popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) }
        ) {
            composable(Rutas.INICIO) {
                PantallaInicio(
                    onVerProgramas = { navController.navigate(Rutas.PROGRAMAS) },
                    onVerCatalogo = { navController.navigate(Rutas.CATALOGO) },
                    onQuieroColaborar = { navController.navigate(Rutas.REGISTRO) },
                    onVerRegistros = { navController.navigate(Rutas.REGISTROS) }
                )
            }
            composable(Rutas.PROGRAMAS) {
                PantallaProgramas(onQuieroColaborar = { navController.navigate(Rutas.REGISTRO) })
            }
            composable(Rutas.CATALOGO) {
                PantallaCatalogo(onQuieroColaborar = { navController.navigate(Rutas.REGISTRO) })
            }
            composable(Rutas.REGISTRO) {
                PantallaRegistro(
                    vm = viewModel,
                    onVerRegistros = { navController.navigate(Rutas.REGISTROS) {
                        popUpTo(Rutas.INICIO) // Limpia el stack para no volver al registro completado
                    } }
                )
            }
            composable(route = Rutas.REGISTROS) {
                PantallaMisRegistros(
                    vm = viewModel,
                    onEditar = { id -> navController.navigate("editar/$id") }
                )
            }
            composable(
                route = Rutas.EDITAR,
                arguments = listOf(androidx.navigation.navArgument("id") {
                    type = androidx.navigation.NavType.IntType
                })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                PantallaEditarRegistro(
                    id = id,
                    vm = viewModel,
                    onVolver = { navController.navigateUp() }
                )
            }
        }
    }
}