package cl.duoc.albalab.ui.navegacion

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.albalab.ui.screens.PantallaCatalogo
import cl.duoc.albalab.ui.screens.PantallaEditarRegistro
import cl.duoc.albalab.ui.screens.PantallaInicio
import cl.duoc.albalab.ui.screens.PantallaMisRegistros
import cl.duoc.albalab.ui.screens.PantallaProgramas
import cl.duoc.albalab.ui.screens.PantallaRegistro
import cl.duoc.albalab.utils.autenticarConBiometria
import cl.duoc.albalab.viewmodel.ContenidoViewModel
import cl.duoc.albalab.viewmodel.RegistroViewModel

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
    val contexto = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    // ViewModels: uno para registros (Branco) y uno para contenido informativo (tuyo)
    val registroViewModel: RegistroViewModel = viewModel(factory = RegistroViewModel.Factory)
    val contenidoViewModel: ContenidoViewModel = viewModel()
    val contenidoState by contenidoViewModel.uiState.collectAsState()

    val tituloActual = when (rutaActual) {
        Rutas.INICIO -> "Alba Lab"
        Rutas.PROGRAMAS -> "Programas e Impacto"
        Rutas.CATALOGO -> "Catálogo Solidario"
        Rutas.REGISTRO -> "Quiero Colaborar"
        Rutas.REGISTROS -> "Mis Registros"
        Rutas.EDITAR -> "Editar Registro"
        else -> "Alba Lab"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloActual) },
                navigationIcon = {
                    if (rutaActual != Rutas.INICIO && rutaActual != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
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
                    estado = contenidoState,
                    onVerProgramas = { navController.navigate(Rutas.PROGRAMAS) },
                    onVerCatalogo = { navController.navigate(Rutas.CATALOGO) },
                    onQuieroColaborar = { navController.navigate(Rutas.REGISTRO) },
                    onVerRegistros = {
                        val activity = contexto as? FragmentActivity
                        if (activity != null) {
                            autenticarConBiometria(
                                activity = activity,
                                onExito = { navController.navigate(Rutas.REGISTROS) },
                                onError = { mensaje ->
                                    Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                )
            }
            composable(Rutas.PROGRAMAS) {
                PantallaProgramas(
                    estado = contenidoState,
                    onQuieroColaborar = { navController.navigate(Rutas.REGISTRO) }
                )
            }
            composable(Rutas.CATALOGO) {
                PantallaCatalogo(
                    estado = contenidoState,
                    onQuieroColaborar = { navController.navigate(Rutas.REGISTRO) }
                )
            }
            composable(Rutas.REGISTRO) {
                PantallaRegistro(
                    vm = registroViewModel,
                    onVerRegistros = {
                        navController.navigate(Rutas.REGISTROS) {
                            popUpTo(Rutas.INICIO)
                        }
                    }
                )
            }
            composable(route = Rutas.REGISTROS) {
                PantallaMisRegistros(
                    vm = registroViewModel,
                    onEditar = { id -> navController.navigate("editar/$id") }
                )
            }
            composable(
                route = Rutas.EDITAR,
                arguments = listOf(navArgument("id") {
                    type = NavType.IntType
                })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                PantallaEditarRegistro(
                    id = id,
                    vm = registroViewModel,
                    onVolver = { navController.navigateUp() }
                )
            }
        }
    }
}