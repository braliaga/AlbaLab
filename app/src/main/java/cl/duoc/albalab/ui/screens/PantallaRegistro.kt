package cl.duoc.albalab.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.ui.componentes.CampoValidado
import cl.duoc.albalab.ui.componentes.ContenedorPantalla
import cl.duoc.albalab.ui.componentes.FilaCheckbox
import cl.duoc.albalab.ui.componentes.FilaSwitch
import cl.duoc.albalab.ui.componentes.GrupoRadio
import cl.duoc.albalab.ui.componentes.MensajeError
import cl.duoc.albalab.ui.componentes.SelectorFoto
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.RegistroViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantallaRegistro(
    onVerRegistros: () -> Unit,
    modifier: Modifier = Modifier,
    vm: RegistroViewModel = viewModel(factory = RegistroViewModel.Factory)
) {
    val s by vm.formulario.collectAsState()
    val teclado = KeyboardOptions(keyboardType = KeyboardType.Number)
    val contexto = LocalContext.current

    // Permiso de notificaciones (Android 13+): se pide una vez al entrar. Si se deniega, el registro se guarda igual.
    var permisoPedido by rememberSaveable { mutableStateOf(false) }
    val launcherPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (!permisoPedido && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permisoPedido = true
            launcherPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Al rechazar un envío, scroll animado al primer campo con error (el orden de Campo es el de la pantalla).
    val requesters = remember { Campo.entries.associateWith { BringIntoViewRequester() } }
    LaunchedEffect(s.intentosFallidos) {
        if (s.intentosFallidos > 0) {
            Campo.entries.firstOrNull { it in s.errores }?.let { requesters.getValue(it).bringIntoView() }
        }
    }
    // Al guardar, el formulario se limpia y el mensaje de éxito queda abajo: se lleva a la vista tras su animación.
    val requesterExito = remember { BringIntoViewRequester() }
    LaunchedEffect(s.guardadoOk) {
        if (s.guardadoOk) {
            delay(250)
            requesterExito.bringIntoView()
        }
    }
    val focusManager = LocalFocusManager.current
    fun enVista(campo: Campo) = Modifier.fillMaxWidth().bringIntoViewRequester(requesters.getValue(campo))

    ContenedorPantalla(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Registro de Interés", style = MaterialTheme.typography.headlineSmall)

        CampoValidado(
            valor = s.nombre,
            onCambio = { vm.onCambio(Campo.NOMBRE, it) },
            etiqueta = "Nombre y apellido",
            error = s.errores[Campo.NOMBRE],
            modifier = enVista(Campo.NOMBRE)
        )
        CampoValidado(
            valor = s.telefono,
            onCambio = { vm.onCambio(Campo.TELEFONO, it) },
            etiqueta = "Teléfono",
            error = s.errores[Campo.TELEFONO],
            modifier = enVista(Campo.TELEFONO),
            teclado = teclado
        )
        CampoValidado(
            valor = s.correo,
            onCambio = { vm.onCambio(Campo.CORREO, it) },
            etiqueta = "Correo",
            error = s.errores[Campo.CORREO],
            modifier = enVista(Campo.CORREO),
            teclado = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        CampoValidado(
            valor = s.direccion,
            onCambio = { vm.onCambio(Campo.DIRECCION, it) },
            etiqueta = "Dirección",
            error = s.errores[Campo.DIRECCION],
            modifier = enVista(Campo.DIRECCION)
        )
        CampoValidado(
            valor = s.personas,
            onCambio = { vm.onCambio(Campo.PERSONAS, it) },
            etiqueta = "Cantidad de personas",
            error = s.errores[Campo.PERSONAS],
            modifier = enVista(Campo.PERSONAS),
            teclado = teclado
        )
        CampoValidado(
            valor = s.fecha,
            onCambio = { vm.onCambio(Campo.FECHA, it) },
            etiqueta = "Fecha de contacto (dd/MM/aaaa)",
            error = s.errores[Campo.FECHA],
            modifier = enVista(Campo.FECHA)
        )
        // API externa de feriados: informa, no bloquea. Color terciario para no confundirlo con un error.
        AnimatedVisibility(visible = s.avisoFecha != null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.EventBusy, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Text(
                    s.avisoFecha ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Column(modifier = enVista(Campo.CONTACTO)) {
            GrupoRadio(
                titulo = "¿Cómo prefieres que te contactemos?",
                opciones = ContactoPreferido.entries,
                seleccionado = s.contacto,
                etiqueta = { it.etiqueta },
                onSeleccion = vm::onContacto,
                error = s.errores[Campo.CONTACTO]
            )
        }

        Column(modifier = enVista(Campo.TIPO_AYUDA)) {
            GrupoRadio(
                titulo = "¿Cómo quieres ayudar?",
                opciones = TipoAyuda.entries,
                seleccionado = s.tipoAyuda,
                etiqueta = { it.etiqueta },
                onSeleccion = vm::onTipoAyuda,
                error = s.errores[Campo.TIPO_AYUDA]
            )
        }

        Text("Foto de perfil (opcional)", style = MaterialTheme.typography.titleMedium)
        SelectorFoto(onFotoSeleccionada = { uri -> vm.onFoto(uri.toString()) })
        if (s.fotoPath != null) {
            AsyncImage(
                model = s.fotoPath,
                contentDescription = "Foto seleccionada",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = enVista(Campo.ACEPTA)) {
            FilaCheckbox(
                texto = "Acepto que me contacten",
                marcado = s.aceptaContacto,
                onCambio = vm::onAceptaContacto
            )
            MensajeError(s.errores[Campo.ACEPTA])
        }

        FilaSwitch(
            texto = "Quiero recibir novedades",
            activo = s.recibeNovedades,
            onCambio = vm::onRecibeNovedades
        )

        // Error al enviar a ms-vinculacion: los datos quedan escritos para reintentar.
        MensajeError(s.errorEnvio)

        Button(
            onClick = {
                focusManager.clearFocus() // cierra el teclado y quita el borde de foco antes de validar
                vm.guardar()
            },
            enabled = !s.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = s.guardando,
                transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                label = "botonEnviar"
            ) { guardando ->
                if (guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (s.errorEnvio != null) "Reintentar envío" else "Enviar registro")
                }
            }
        }

        AnimatedVisibility(visible = s.guardadoOk, modifier = Modifier.bringIntoViewRequester(requesterExito)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "¡Registro enviado! Gracias por querer colaborar.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(onClick = onVerRegistros) { Text("Ver registros") }
            }
        }
    }
}
