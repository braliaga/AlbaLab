package cl.duoc.albalab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.viewmodel.RegistroViewModel

@Composable
fun PantallaMisRegistros(
    vm: RegistroViewModel,
    onEditar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Escuchamos la lista de la base de datos (Room)
    val registros by vm.registros.collectAsState(initial = emptyList())

    if (registros.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay colaboradores registrados aún.")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(registros) { registro ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            // Mostrar los datos básicos
                            Text(text = registro.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Tel: ${registro.telefono}", style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { onEditar(registro.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                }
            }
        }
    }
}