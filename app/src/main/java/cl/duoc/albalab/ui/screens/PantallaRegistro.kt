package cl.duoc.albalab.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.ui.componentes.CampoValidado
import cl.duoc.albalab.ui.componentes.FilaCheckbox
import cl.duoc.albalab.ui.componentes.FilaSwitch
import cl.duoc.albalab.ui.componentes.GrupoRadio
import cl.duoc.albalab.ui.componentes.MensajeError
import cl.duoc.albalab.ui.componentes.SelectorFoto
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.RegistroViewModel
import coil.compose.AsyncImage

@Composable
fun PantallaRegistro(
    onVerRegistros: () -> Unit,
    modifier: Modifier = Modifier,
    vm: RegistroViewModel = viewModel(factory = RegistroViewModel.Factory)
) {
    val s by vm.formulario.collectAsState()
    val teclado = KeyboardOptions(keyboardType = KeyboardType.Number)

    PedirPermisoNotificaciones()

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Registro de Interés", style = MaterialTheme.typography.headlineSmall)

            CampoValidado(
                valor = s.nombre,
                onCambio = { vm.onCambio(Campo.NOMBRE, it) },
                etiqueta = "Nombre y apellido",
                error = s.errores[Campo.NOMBRE],
                modifier = Modifier.fillMaxWidth()
            )
            CampoValidado(
                valor = s.telefono,
                onCambio = { vm.onCambio(Campo.TELEFONO, it) },
                etiqueta = "Teléfono",
                error = s.errores[Campo.TELEFONO],
                modifier = Modifier.fillMaxWidth(),
                teclado = teclado
            )
            CampoValidado(
                valor = s.correo,
                onCambio = { vm.onCambio(Campo.CORREO, it) },
                etiqueta = "Correo",
                error = s.errores[Campo.CORREO],
                modifier = Modifier.fillMaxWidth(),
                teclado = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            CampoValidado(
                valor = s.direccion,
                onCambio = { vm.onCambio(Campo.DIRECCION, it) },
                etiqueta = "Dirección",
                error = s.errores[Campo.DIRECCION],
                modifier = Modifier.fillMaxWidth()
            )
            CampoValidado(
                valor = s.personas,
                onCambio = { vm.onCambio(Campo.PERSONAS, it) },
                etiqueta = "Cantidad de personas",
                error = s.errores[Campo.PERSONAS],
                modifier = Modifier.fillMaxWidth(),
                teclado = teclado
            )
            CampoValidado(
                valor = s.fecha,
                onCambio = { vm.onCambio(Campo.FECHA, it) },
                etiqueta = "Fecha de contacto (dd/MM/aaaa)",
                error = s.errores[Campo.FECHA],
                modifier = Modifier.fillMaxWidth()
            )

            GrupoRadio(
                titulo = "¿Cómo prefieres que te contactemos?",
                opciones = ContactoPreferido.entries,
                seleccionado = s.contacto,
                etiqueta = { it.etiqueta },
                onSeleccion = vm::onContacto,
                error = s.errores[Campo.CONTACTO]
            )

            GrupoRadio(
                titulo = "¿Cómo quieres ayudar?",
                opciones = TipoAyuda.entries,
                seleccionado = s.tipoAyuda,
                etiqueta = { it.etiqueta },
                onSeleccion = vm::onTipoAyuda,
                error = s.errores[Campo.TIPO_AYUDA]
            )

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

            FilaCheckbox(
                texto = "Acepto que me contacten",
                marcado = s.aceptaContacto,
                onCambio = vm::onAceptaContacto
            )
            MensajeError(s.errores[Campo.ACEPTA])

            FilaSwitch(
                texto = "Quiero recibir novedades",
                activo = s.recibeNovedades,
                onCambio = vm::onRecibeNovedades
            )

            Button(
                onClick = vm::guardar,
                enabled = !s.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (s.guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Enviar registro")
                }
            }

            AnimatedVisibility(visible = s.guardadoOk) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "¡Registro enviado! Gracias por querer colaborar.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(onClick = onVerRegistros) { Text("Ver registros") }
                }
            }
        }
    }
}

// Pide el permiso de notificaciones una sola vez al entrar (Android 13+). Si se deniega no se muestra nada:
// el registro se guarda igual y el mensaje de éxito en pantalla ya informa.
@Composable
private fun PedirPermisoNotificaciones() {
    if (Build.VERSION.SDK_INT < 33) return

    val context = LocalContext.current
    // rememberSaveable: al rotar la pantalla no se vuelve a preguntar.
    var yaPedido by rememberSaveable { mutableStateOf(false) }
    val launcherPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(Unit) {
        val concedido = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!yaPedido && !concedido) {
            yaPedido = true
            launcherPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
