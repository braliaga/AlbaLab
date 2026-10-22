package cl.duoc.albalab.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.data.ContenidoFicticio

@Composable
fun PantallaProgramas(
    onQuieroColaborar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandidoId by remember { mutableStateOf<Int?>(null) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 600.dp).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Programas e Impacto", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) }

            items(ContenidoFicticio.programas) { programa ->
                val isExpanded = expandidoId == programa.id
                Card(
                    modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expandidoId = if (isExpanded) null else programa.id }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(programa.titulo, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = "Expandir")
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(programa.resumen, style = MaterialTheme.typography.bodyMedium)
                        if (isExpanded) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(programa.detalleExpandido, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)); Text("Casos de Éxito", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary) }

            items(ContenidoFicticio.casosExito) { caso ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(caso.aliasParticipante, fontWeight = FontWeight.Bold)
                        Text(caso.historia, style = MaterialTheme.typography.bodyMedium)
                        Text("Logro: ${caso.logro}", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)); Text("Destino de los Aportes", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.tertiary) }

            items(
                items = ContenidoFicticio.destinoAportes,
                key = { it.area } // Optimización de Compose
            ) { destino ->
                ListItem(
                    headlineContent = { Text(destino.area, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text(destino.explicacion) },
                    trailingContent = {
                        Text(
                            text = destino.porcentaje,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = onQuieroColaborar,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Colaborar") },
            text = { Text("Quiero Colaborar") }
        )
    }
}