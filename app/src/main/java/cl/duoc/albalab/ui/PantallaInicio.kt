package cl.duoc.albalab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.data.ContenidoFicticio

@Composable
fun PantallaInicio(
    onVerProgramas: () -> Unit,
    onVerCatalogo: () -> Unit,
    onQuieroColaborar: () -> Unit,
    onVerRegistros: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 600.dp).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("CORPALIV • Alba Lab", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(ContenidoFicticio.propositoInstitucional, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ContenidoFicticio.indicadores.forEach { ind ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(ind.cifra, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Text(ind.etiqueta, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FilledTonalButton(onClick = onVerProgramas, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Info, contentDescription = "Programas")
                Spacer(Modifier.width(8.dp))
                Text("Conocer Programas")
            }

            FilledTonalButton(onClick = onVerCatalogo, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Catálogo")
                Spacer(Modifier.width(8.dp))
                Text("Ver Catálogo de Productos")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onQuieroColaborar, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Icon(Icons.Default.Favorite, contentDescription = "Colaborar")
                Spacer(Modifier.width(8.dp))
                Text("Quiero Colaborar")
            }

            TextButton(onClick = onVerRegistros) {
                Text("Ver registros (Equipo Vinculación)")
            }
        }
    }
}