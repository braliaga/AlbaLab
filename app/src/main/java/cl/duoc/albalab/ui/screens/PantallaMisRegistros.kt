package cl.duoc.albalab.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.albalab.data.model.RegistroInteres
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.ui.componentes.ContenedorPantalla
import cl.duoc.albalab.viewmodel.RegistrosViewModel
import coil.compose.AsyncImage

private enum class Vista { CARGANDO, VACIO, SIN_RESULTADOS, LISTA }

@Composable
fun PantallaMisRegistros(
    onEditar: (Int) -> Unit,
    modifier: Modifier = Modifier,
    vm: RegistrosViewModel = viewModel(factory = RegistrosViewModel.Factory)
) {
    val estado by vm.estado.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val tipo by vm.tipo.collectAsState()

    val vista = when {
        !estado.cargado -> Vista.CARGANDO
        estado.total == 0 -> Vista.VACIO
        estado.filtrados.isEmpty() -> Vista.SIN_RESULTADOS
        else -> Vista.LISTA
    }

    ContenedorPantalla(
        modifier = modifier,
        permitirScroll = false,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // El buscador y los chips quedan visibles en todos los estados, para no dejar al usuario sin salida.
        OutlinedTextField(
            value = busqueda,
            onValueChange = vm::onBusqueda,
            label = { Text("Buscar por nombre") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { vm.onBusqueda("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Borrar búsqueda")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChipTipo("Todos", seleccionado = tipo == null) { vm.onTipo(null) }
            TipoAyuda.entries.forEach { t ->
                ChipTipo(t.etiqueta, seleccionado = tipo == t) { vm.onTipo(if (tipo == t) null else t) }
            }
        }

        if (estado.cargado) {
            AnimatedContent(
                targetState = estado.filtrados.size to estado.total,
                transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                label = "contador"
            ) { (mostrados, total) ->
                Text(
                    text = "Mostrando $mostrados de $total",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Crossfade(targetState = vista, animationSpec = tween(200), modifier = Modifier.weight(1f), label = "vista") { v ->
            when (v) {
                Vista.CARGANDO -> Box(Modifier.fillMaxSize())
                Vista.VACIO -> EstadoVacio(
                    icono = Icons.Filled.Inbox,
                    titulo = "Aún no hay registros",
                    texto = "Cuando alguien envíe el formulario de Colaborar, aparecerá aquí."
                )
                Vista.SIN_RESULTADOS -> EstadoVacio(
                    icono = Icons.Filled.SearchOff,
                    titulo = "Sin resultados",
                    texto = "Ningún registro coincide con la búsqueda o el tipo elegido."
                ) {
                    OutlinedButton(onClick = vm::limpiarFiltros) { Text("Limpiar filtros") }
                }
                Vista.LISTA -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(estado.filtrados, key = { it.id }) { registro ->
                        TarjetaRegistro(
                            registro = registro,
                            onClick = { onEditar(registro.id) },
                            // Al filtrar, las tarjetas entran, salen y se reacomodan en vez de saltar.
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(200),
                                placementSpec = tween(200),
                                fadeOutSpec = tween(150)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipTipo(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(texto) },
        leadingIcon = if (seleccionado) {
            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
        } else {
            null
        }
    )
}

@Composable
private fun TarjetaRegistro(registro: RegistroInteres, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                if (registro.fotoPath != null) {
                    AsyncImage(
                        model = registro.fotoPath,
                        contentDescription = "Foto de ${registro.nombre}",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Column {
                    Text(
                        text = registro.tipoAyuda.etiqueta,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(text = registro.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Tel: ${registro.telefono}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun EstadoVacio(
    icono: ImageVector,
    titulo: String,
    texto: String,
    accion: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Text(
            texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        accion()
    }
}
