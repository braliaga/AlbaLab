package cl.duoc.albalab.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.EstadoSolicitud
import cl.duoc.albalab.ui.componentes.CampoValidado
import cl.duoc.albalab.ui.componentes.ContenedorPantalla
import cl.duoc.albalab.ui.componentes.FilaSwitch
import cl.duoc.albalab.ui.componentes.GrupoRadio
import cl.duoc.albalab.ui.componentes.MensajeError
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.EdicionViewModel

@Composable
fun PantallaEditarRegistro(
    onVolver: () -> Unit,
    onSesionExpirada: () -> Unit,
    modifier: Modifier = Modifier,
    vm: EdicionViewModel = viewModel(factory = EdicionViewModel.Factory)
) {
    val state by vm.estado.collectAsState()
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }

    // Volver a la lista al guardar o eliminar con éxito.
    LaunchedEffect(state.guardadoOk, state.eliminado) {
        if (state.guardadoOk || state.eliminado) onVolver()
    }
    LaunchedEffect(state.sesionExpirada) {
        if (state.sesionExpirada) onSesionExpirada()
    }

    ContenedorPantalla(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            Text("Editar registro", style = MaterialTheme.typography.headlineMedium)
            Text(
                state.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        CampoValidado(
            valor = state.telefono,
            onCambio = vm::onTelefono,
            etiqueta = "Teléfono",
            error = state.errores[Campo.TELEFONO],
            modifier = Modifier.fillMaxWidth(),
            teclado = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        GrupoRadio(
            titulo = "¿Cómo prefieres que te contactemos?",
            opciones = ContactoPreferido.entries,
            seleccionado = state.contacto,
            etiqueta = { it.etiqueta },
            onSeleccion = vm::onContacto,
            error = state.errores[Campo.CONTACTO]
        )

        FilaSwitch(
            texto = "Quiero recibir novedades",
            activo = state.recibeNovedades,
            onCambio = vm::onNovedades
        )

        // Seguimiento de la solicitud: cada cambio queda en el historial de ms-vinculacion.
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Estado de la solicitud", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadoSolicitud.entries.forEach { e ->
                    val elegido = state.estadoElegido == e
                    FilterChip(
                        selected = elegido,
                        onClick = { vm.onEstado(e) },
                        enabled = !state.estadoActual.esFinal,
                        label = { Text(e.etiqueta) },
                        leadingIcon = if (elegido) {
                            { Icon(Icons.Filled.Check, null, Modifier.size(FilterChipDefaults.IconSize)) }
                        } else {
                            null
                        }
                    )
                }
            }
            if (state.estadoActual.esFinal) {
                Text(
                    "Una solicitud ${state.estadoActual.etiqueta.lowercase()} ya no cambia de estado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        MensajeError(state.error)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Button(onClick = vm::guardar, enabled = !state.procesando, modifier = Modifier.weight(1f)) {
                if (state.procesando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar cambios")
                }
            }
        }

        OutlinedButton(
            onClick = { confirmarEliminar = true },
            enabled = !state.procesando,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null)
            Text("Eliminar registro", modifier = Modifier.padding(start = 8.dp))
        }
    }

    // Confirmación con nombre: una eliminación no se puede deshacer.
    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar a ${state.nombre}?") },
            text = { Text("Se borrará la solicitud y su historial de estados. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarEliminar = false
                        vm.eliminar()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}
