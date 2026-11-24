package cl.duoc.albalab.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import cl.duoc.albalab.ui.componentes.aparecer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.ui.componentes.ImagenRemota
import cl.duoc.albalab.utils.aPesosChilenos
import cl.duoc.albalab.utils.equivalenteUfUsd
import cl.duoc.albalab.viewmodel.ContenidoState

@Composable
fun PantallaCatalogo(
    estado: ContenidoState,
    onQuieroColaborar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val esHorizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val columnas = if (esHorizontal) 2 else 1

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnas),
            modifier = Modifier
                .widthIn(max = if (esHorizontal) 900.dp else 600.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "Catálogo con Sentido",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "El 100% de la venta retorna a los talleres Alba Lab.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            itemsIndexed(estado.catalogo) { indice, prod ->
                OutlinedCard(modifier = Modifier.fillMaxWidth().aparecer(indice)) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImagenRemota(
                            url = prod.imagenUrl,
                            descripcion = "Imagen de ${prod.nombre}",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text = prod.nombre,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(prod.descripcion, style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = prod.precioClp.aPesosChilenos(),
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            // Equivalencia con la UF y el dólar del día (API externa mindicador.cl).
                            estado.economia?.let { e ->
                                Text(
                                    text = equivalenteUfUsd(prod.precioClp, e.uf, e.dolar),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onQuieroColaborar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Colaborar") },
            text = { Text("Quiero Colaborar") }
        )
    }
}