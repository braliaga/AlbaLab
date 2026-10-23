package cl.duoc.albalab.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

// Descarga la imagen con Coil; mientras carga o si no hay conexión se muestra un ícono en su lugar.
@Composable
fun ImagenRemota(
    url: String,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = url,
        contentDescription = descripcion,
        placeholder = rememberVectorPainter(Icons.Filled.Image),
        error = rememberVectorPainter(Icons.Filled.BrokenImage),
        contentScale = ContentScale.Crop,
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)
    )
}
