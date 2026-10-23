package cl.duoc.albalab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.model.ContactoPreferido
import cl.duoc.albalab.ui.componentes.CampoValidado
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.RegistroViewModel

@Composable
fun PantallaEditarRegistro(
    id: Int,
    vm: RegistroViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(id) {
        vm.cargarEdicion(id)
    }

    val state by vm.edicion.collectAsState()

    // Volver si se guardó con éxito
    LaunchedEffect(state.guardadoOk) {
        if (state.guardadoOk) {
            onVolver()
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Editando registro ID: $id", style = MaterialTheme.typography.headlineMedium)

            CampoValidado(
                valor = state.telefono,
                onCambio = vm::onEdicionTelefono,
                etiqueta = "Teléfono",
                error = state.errores[Campo.TELEFONO],
                modifier = Modifier.fillMaxWidth(),
                teclado = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("¿Cómo prefieres que te contactemos?", style = MaterialTheme.typography.titleMedium)
                ContactoPreferido.entries.forEach { opcion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = state.contacto == opcion,
                                onValueChange = { vm.onEdicionContacto(opcion) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = state.contacto == opcion, onClick = null)
                        Text(opcion.etiqueta, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = state.recibeNovedades,
                        onValueChange = vm::onEdicionNovedades,
                        role = Role.Switch
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Quiero recibir novedades")
                Switch(checked = state.recibeNovedades, onCheckedChange = null)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f)) {
                    Text("Cancelar")
                }
                Button(onClick = vm::guardarEdicion, modifier = Modifier.weight(1f)) {
                    Text("Guardar")
                }
            }
        }
    }
}