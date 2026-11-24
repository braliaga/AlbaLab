package cl.duoc.albalab.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import cl.duoc.albalab.ui.componentes.aparecer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.ui.componentes.ContenedorPantalla
import cl.duoc.albalab.viewmodel.ContenidoState
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun PantallaProgramas(
    estado: ContenidoState,
    onQuieroColaborar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandidoId by rememberSaveable { mutableStateOf<Int?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        ContenedorPantalla(
            permitirScroll = false,
            paddingVertical = 0.dp
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Programas e Impacto",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                itemsIndexed(estado.programas) { indice, programa ->
                    val isExpanded = expandidoId == programa.id
                    val giro by animateFloatAsState(if (isExpanded) 180f else 0f, tween(200), label = "chevron")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aparecer(indice)
                            .animateContentSize()
                            .clickable { expandidoId = if (isExpanded) null else programa.id }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = programa.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = if (isExpanded) "Contraer" else "Expandir",
                                    modifier = Modifier.graphicsLayer { rotationZ = giro }
                                )
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

                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Casos de Éxito",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                itemsIndexed(estado.casosExito) { indice, caso ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth().aparecer(indice)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(caso.aliasParticipante, fontWeight = FontWeight.Bold)
                            Text(caso.historia, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "Logro: ${caso.logro}",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Destino de los Aportes",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                items(
                    items = estado.destinoAportes,
                    key = { it.area }
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