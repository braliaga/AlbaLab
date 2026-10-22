package cl.duoc.albalab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.viewmodel.RegistroViewModel

@Composable
fun PantallaEditarRegistro(
    id: Int,
    vm: RegistroViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Editando registro ID: $id", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("El formulario de edición final se conectará aquí.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onVolver) {
                Text("Volver")
            }
        }
    }
}