package cl.duoc.albalab.ui.navegacion

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.albalab.ui.screens.PantallaCatalogo
import cl.duoc.albalab.ui.screens.PantallaEditarRegistro
import cl.duoc.albalab.data.remote.Sesion
import cl.duoc.albalab.ui.screens.PantallaInicio
import cl.duoc.albalab.ui.screens.PantallaLogin
import cl.duoc.albalab.ui.screens.PantallaMisRegistros
import cl.duoc.albalab.ui.screens.PantallaProgramas
import cl.duoc.albalab.ui.screens.PantallaRegistro
import cl.duoc.albalab.utils.autenticarConBiometria
import cl.duoc.albalab.viewmodel.ContenidoViewModel

object Rutas {
    const val INICIO = "inicio"
    const val PROGRAMAS = "programas"
    const val CATALOGO = "catalogo"
    const val REGISTRO = "registro"
    const val REGISTROS = "registros"
    const val EDITAR = "editar/{id}"
    const val LOGIN = "login"
}

private const val PERMISO_RED_LOCAL = "android.permission.ACCESS_LOCAL_NETWORK"

private data class Destino(val ruta: String, val etiqueta: String, val icono: ImageVector)

// Pestañas de la barra inferior (o del rail en horizontal). EDITAR no es pestaña: es hija de REGISTROS.
private val destinos = listOf(
    Destino(Rutas.INICIO, "Inicio", Icons.Filled.Home),
    Destino(Rutas.PROGRAMAS, "Programas", Icons.Filled.VolunteerActivism),
    Destino(Rutas.CATALOGO, "Catálogo", Icons.Filled.Storefront),
    Destino(Rutas.REGISTRO, "Colaborar", Icons.Filled.Favorite),
    Destino(Rutas.REGISTROS, "Registros", Icons.Filled.Lock)
)
private val rutasPestana = destinos.map { it.ruta }.toSet()

// Entre pestañas se usa fade-through; hacia y desde una pantalla hija se mantiene el deslizamiento.
private fun AnimatedContentTransitionScope<NavBackStackEntry>.cambioDePestana() =
    initialState.destination.route in rutasPestana && targetState.destination.route in rutasPestana

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val contexto = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route
    val esPestana = rutaActual in rutasPestana
    val esHorizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // El contenido informativo (ms-contenidos + APIs externas) se comparte entre Inicio, Programas y Catálogo.
    // Registro, MisRegistros, Editar y Login crean su propio ViewModel dentro de cada pantalla.
    val contenidoViewModel: ContenidoViewModel = viewModel(factory = ContenidoViewModel.Factory)
    val contenidoState by contenidoViewModel.uiState.collectAsState()

    // Android 17 bloquea las conexiones a la red local (donde corre el backend) sin este permiso.
    // Si se concede, se recarga el contenido; si se niega, la app sigue con la copia local y avisa en cada pantalla.
    val permisoRedLocal = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) contenidoViewModel.cargar()
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 37 &&
            ContextCompat.checkSelfPermission(contexto, PERMISO_RED_LOCAL) != PackageManager.PERMISSION_GRANTED
        ) {
            permisoRedLocal.launch(PERMISO_RED_LOCAL)
        }
    }

    // Navegación de pestaña: no apila pantallas y conserva el estado de cada una.
    fun navegarA(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Login del gestor. Desde una pantalla de gestión (sesión expirada) limpia la pila para no volver atrás a ella.
    fun irALogin() {
        navController.navigate(Rutas.LOGIN) {
            popUpTo(Rutas.INICIO)
            launchSingleTop = true
        }
    }

    // Único camino a REGISTROS: biometría del dispositivo y, si no hay sesión, login con la cuenta del gestor (JWT).
    fun irARegistros() {
        if (rutaActual == Rutas.REGISTROS) return
        val activity = contexto as? FragmentActivity ?: return
        autenticarConBiometria(
            activity = activity,
            onExito = { if (Sesion.activa) navegarA(Rutas.REGISTROS) else irALogin() },
            onError = { mensaje -> Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show() }
        )
    }

    fun alTocarPestana(ruta: String) = if (ruta == Rutas.REGISTROS) irARegistros() else navegarA(ruta)

    val tituloActual = when (rutaActual) {
        Rutas.INICIO -> "Alba Lab"
        Rutas.PROGRAMAS -> "Programas e Impacto"
        Rutas.CATALOGO -> "Catálogo Solidario"
        Rutas.REGISTRO -> "Quiero Colaborar"
        Rutas.REGISTROS -> "Mis Registros"
        Rutas.EDITAR -> "Editar Registro"
        Rutas.LOGIN -> "Ingreso gestor"
        else -> "Alba Lab"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloActual) },
                navigationIcon = {
                    if (rutaActual != null && !esPestana) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!esHorizontal) {
                AnimatedVisibility(
                    visible = esPestana,
                    enter = slideInVertically(tween(200, easing = LinearOutSlowInEasing)) { it } +
                        fadeIn(tween(200, easing = LinearOutSlowInEasing)),
                    exit = slideOutVertically(tween(150, easing = FastOutLinearInEasing)) { it } +
                        fadeOut(tween(150, easing = FastOutLinearInEasing))
                ) {
                    NavigationBar {
                        destinos.forEach { d ->
                            NavigationBarItem(
                                selected = rutaActual == d.ruta,
                                onClick = { alTocarPestana(d.ruta) },
                                icon = { Icon(d.icono, contentDescription = null) },
                                label = { Text(d.etiqueta) }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Row(modifier = Modifier.padding(padding)) {
            if (esHorizontal) {
                AnimatedVisibility(
                    visible = esPestana,
                    enter = slideInHorizontally(tween(200, easing = LinearOutSlowInEasing)) { -it } +
                        fadeIn(tween(200, easing = LinearOutSlowInEasing)),
                    exit = slideOutHorizontally(tween(150, easing = FastOutLinearInEasing)) { -it } +
                        fadeOut(tween(150, easing = FastOutLinearInEasing))
                ) {
                    // Los insets ya vienen en el padding del Scaffold.
                    NavigationRail(windowInsets = WindowInsets(0.dp)) {
                        destinos.forEach { d ->
                            NavigationRailItem(
                                selected = rutaActual == d.ruta,
                                onClick = { alTocarPestana(d.ruta) },
                                icon = { Icon(d.icono, contentDescription = null) },
                                label = { Text(d.etiqueta) }
                            )
                        }
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = Rutas.INICIO,
                modifier = Modifier.weight(1f),
                enterTransition = {
                    if (cambioDePestana()) fadeIn(tween(200, easing = LinearOutSlowInEasing))
                    else slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))
                },
                exitTransition = {
                    if (cambioDePestana()) fadeOut(tween(150, easing = FastOutLinearInEasing))
                    else slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300))
                },
                popEnterTransition = {
                    if (cambioDePestana()) fadeIn(tween(200, easing = LinearOutSlowInEasing))
                    else slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300))
                },
                popExitTransition = {
                    if (cambioDePestana()) fadeOut(tween(150, easing = FastOutLinearInEasing))
                    else slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))
                }
            ) {
                composable(Rutas.INICIO) {
                    PantallaInicio(
                        estado = contenidoState,
                        onVerProgramas = { navegarA(Rutas.PROGRAMAS) },
                        onVerCatalogo = { navegarA(Rutas.CATALOGO) },
                        onQuieroColaborar = { navegarA(Rutas.REGISTRO) },
                        onVerRegistros = { irARegistros() }
                    )
                }
                composable(Rutas.PROGRAMAS) {
                    PantallaProgramas(
                        estado = contenidoState,
                        onQuieroColaborar = { navegarA(Rutas.REGISTRO) }
                    )
                }
                composable(Rutas.CATALOGO) {
                    PantallaCatalogo(
                        estado = contenidoState,
                        onQuieroColaborar = { navegarA(Rutas.REGISTRO) }
                    )
                }
                composable(Rutas.REGISTRO) {
                    PantallaRegistro(onVerRegistros = { irARegistros() })
                }
                composable(Rutas.REGISTROS) {
                    PantallaMisRegistros(
                        onEditar = { id -> navController.navigate("editar/$id") },
                        onSesionExpirada = { irALogin() }
                    )
                }
                composable(
                    route = Rutas.EDITAR,
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) {
                    // El id llega a EdicionViewModel por SavedStateHandle.
                    PantallaEditarRegistro(
                        onVolver = { navController.navigateUp() },
                        onSesionExpirada = { irALogin() }
                    )
                }
                composable(Rutas.LOGIN) {
                    PantallaLogin(
                        onIngreso = {
                            navController.popBackStack()
                            navegarA(Rutas.REGISTROS)
                        }
                    )
                }
            }
        }
    }
}
