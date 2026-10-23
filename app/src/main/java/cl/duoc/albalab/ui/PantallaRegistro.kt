package cl.duoc.albalab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.model.ContactoPreferido
import cl.duoc.albalab.model.TipoAyuda
import cl.duoc.albalab.ui.componentes.CampoValidado
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.RegistroViewModel

@Composable
fun PantallaRegistro(
    vm: RegistroViewModel,
    onVerRegistros: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s by vm.formulario.collectAsState()
    val teclado = KeyboardOptions(keyboardType = KeyboardType.Number)

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

            Text("¿Cómo prefieres que te contactemos?", style = MaterialTheme.typography.titleMedium)
            ContactoPreferido.entries.forEach { opcion ->
                FilaRadio(
                    texto = opcion.etiqueta,
                    seleccionado = s.contacto == opcion,
                    onClick = { vm.onContacto(opcion) }
                )
            }
            MensajeError(s.errores[Campo.CONTACTO])

            Text("¿Cómo quieres ayudar?", style = MaterialTheme.typography.titleMedium)
            TipoAyuda.entries.forEach { opcion ->
                FilaRadio(
                    texto = opcion.etiqueta,
                    seleccionado = s.tipoAyuda == opcion,
                    onClick = { vm.onTipoAyuda(opcion) }
                )
            }
            MensajeError(s.errores[Campo.TIPO_AYUDA])

            Text("Foto de perfil (opcional)", style = MaterialTheme.typography.titleMedium)
            SelectorFoto(onFotoSeleccionada = { uri -> vm.onFoto(uri.toString()) })
            if (s.fotoPath != null) {
                coil.compose.AsyncImage(
                    model = s.fotoPath,
                    contentDescription = "Foto seleccionada",
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(androidx.compose.foundation.shape.CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = s.aceptaContacto,
                        onValueChange = vm::onAceptaContacto,
                        role = Role.Checkbox
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = s.aceptaContacto, onCheckedChange = null)
                Text("Acepto que me contacten", modifier = Modifier.padding(start = 8.dp))
            }
            MensajeError(s.errores[Campo.ACEPTA])

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = s.recibeNovedades,
                        onValueChange = vm::onRecibeNovedades,
                        role = Role.Switch
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Quiero recibir novedades")
                Switch(checked = s.recibeNovedades, onCheckedChange = null)
            }

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

@Composable
private fun FilaRadio(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = seleccionado, onClick = onClick, role = Role.RadioButton),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = seleccionado, onClick = null)
        Text(texto, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun MensajeError(mensaje: String?) {
    AnimatedVisibility(visible = mensaje != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = mensaje,
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                mensaje ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
