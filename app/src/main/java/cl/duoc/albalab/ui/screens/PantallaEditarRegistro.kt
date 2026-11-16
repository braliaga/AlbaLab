package cl.duoc.albalab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.ui.componentes.CampoValidado
import cl.duoc.albalab.ui.componentes.FilaSwitch
import cl.duoc.albalab.ui.componentes.GrupoRadio
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.EdicionViewModel

@Composable
fun PantallaEditarRegistro(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    vm: EdicionViewModel = viewModel(factory = EdicionViewModel.Factory)
) {
    val state by vm.estado.collectAsState()

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Editar registro", style = MaterialTheme.typography.headlineMedium)

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f)) {
                    Text("Cancelar")
                }
                Button(onClick = vm::guardar, modifier = Modifier.weight(1f)) {
                    Text("Guardar")
                }
            }
        }
    }
}
