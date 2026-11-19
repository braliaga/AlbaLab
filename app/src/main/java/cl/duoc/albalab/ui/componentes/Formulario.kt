package cl.duoc.albalab.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun FilaRadio(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
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
fun FilaCheckbox(texto: String, marcado: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = marcado, onValueChange = onCambio, role = Role.Checkbox),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = marcado, onCheckedChange = null)
        Text(texto, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun FilaSwitch(texto: String, activo: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = activo, onValueChange = onCambio, role = Role.Switch),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(texto)
        Switch(checked = activo, onCheckedChange = null)
    }
}

@Composable
fun MensajeError(mensaje: String?) {
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

// Título, una fila de radio por opción y el mensaje de error del grupo.
@Composable
fun <T> GrupoRadio(
    titulo: String,
    opciones: List<T>,
    seleccionado: T?,
    etiqueta: (T) -> String,
    onSeleccion: (T) -> Unit,
    error: String?
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        opciones.forEach { opcion ->
            FilaRadio(
                texto = etiqueta(opcion),
                seleccionado = seleccionado == opcion,
                onClick = { onSeleccion(opcion) }
            )
        }
        MensajeError(error)
    }
}
