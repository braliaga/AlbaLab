package cl.duoc.albalab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.data.ContenidoFicticio

@Composable
fun PantallaCatalogo(
    onQuieroColaborar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 600.dp).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Catálogo con Sentido", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text("El 100% de la venta retorna a los talleres Alba Lab.", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
            }

            items(ContenidoFicticio.catalogoProductos) { prod ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Producto", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(prod.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(prod.descripcion, style = MaterialTheme.typography.bodySmall)
                            Text("$${prod.precioClp} CLP", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
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