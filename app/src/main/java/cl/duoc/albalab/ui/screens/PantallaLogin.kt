package cl.duoc.albalab.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.albalab.ui.componentes.CampoValidado
import cl.duoc.albalab.ui.componentes.ContenedorPantalla
import cl.duoc.albalab.ui.componentes.MensajeError
import cl.duoc.albalab.viewmodel.LoginViewModel

// Segundo paso para entrar a "Mis registros": después de la biometría, el gestor ingresa con su cuenta (JWT).
@Composable
fun PantallaLogin(
    onIngreso: () -> Unit,
    modifier: Modifier = Modifier,
    vm: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
) {
    val s by vm.estado.collectAsState()
    val foco = LocalFocusManager.current

    LaunchedEffect(s.ingresoOk) {
        if (s.ingresoOk) onIngreso()
    }

    ContenedorPantalla(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Filled.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )
        Text("Acceso equipo de vinculación", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Ingresa con tu cuenta de gestor para revisar y gestionar las solicitudes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CampoValidado(
            valor = s.email,
            onCambio = vm::onEmail,
            etiqueta = "Correo",
            error = s.errorEmail,
            modifier = Modifier.fillMaxWidth(),
            teclado = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        CampoValidado(
            valor = s.password,
            onCambio = vm::onPassword,
            etiqueta = "Contraseña",
            error = s.errorPassword,
            modifier = Modifier.fillMaxWidth(),
            teclado = KeyboardOptions(keyboardType = KeyboardType.Password),
            ocultar = true
        )

        MensajeError(s.error)

        Button(
            onClick = {
                foco.clearFocus()
                vm.ingresar()
            },
            enabled = !s.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = s.cargando,
                transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                label = "botonIngresar"
            ) { cargando ->
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Ingresar")
                }
            }
        }
    }
}
