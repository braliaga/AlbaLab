package cl.duoc.albalab.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.ui.componentes.ContenedorPantalla
import cl.duoc.albalab.ui.componentes.aparecer
import cl.duoc.albalab.ui.componentes.ImagenRemota
import cl.duoc.albalab.viewmodel.ContenidoState

@Composable
fun PantallaInicio(
    estado: ContenidoState,
    onVerProgramas: () -> Unit,
    onVerCatalogo: () -> Unit,
    onQuieroColaborar: () -> Unit,
    onVerRegistros: () -> Unit,
    modifier: Modifier = Modifier
) {
    val esHorizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (esHorizontal) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = 900.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BloqueIdentidad(estado = estado)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BloqueAcciones(
                        onVerProgramas = onVerProgramas,
                        onVerCatalogo = onVerCatalogo,
                        onQuieroColaborar = onQuieroColaborar,
                        onVerRegistros = onVerRegistros
                    )
                }
            }
        }
    } else {
        ContenedorPantalla(modifier = modifier, permitirScroll = true) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BloqueIdentidad(estado = estado)
                Spacer(modifier = Modifier.height(8.dp))
                BloqueAcciones(
                    onVerProgramas = onVerProgramas,
                    onVerCatalogo = onVerCatalogo,
                    onQuieroColaborar = onQuieroColaborar,
                    onVerRegistros = onVerRegistros
                )
            }
        }
    }
}

@Composable
private fun BloqueIdentidad(estado: ContenidoState) {
    ImagenRemota(
        url = estado.bannerInicioUrl,
        descripcion = "Imagen ilustrativa de Alba Lab",
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .aparecer(0)
            .clip(RoundedCornerShape(16.dp))
    )
    Text(
        text = "CORPALIV • Alba Lab",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.aparecer(1)
    )
    Text(
        text = estado.propositoInstitucional,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.aparecer(2)
    )
    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .aparecer(3),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        estado.indicadores.forEach { ind ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = ind.cifra,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = ind.etiqueta,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BloqueAcciones(
    onVerProgramas: () -> Unit,
    onVerCatalogo: () -> Unit,
    onQuieroColaborar: () -> Unit,
    onVerRegistros: () -> Unit
) {
    FilledTonalButton(onClick = onVerProgramas, modifier = Modifier.fillMaxWidth().aparecer(4)) {
        Icon(Icons.Default.Info, contentDescription = "Programas")
        Spacer(Modifier.width(8.dp))
        Text("Conocer Programas")
    }

    FilledTonalButton(onClick = onVerCatalogo, modifier = Modifier.fillMaxWidth().aparecer(5)) {
        Icon(Icons.Default.ShoppingCart, contentDescription = "Catálogo")
        Spacer(Modifier.width(8.dp))
        Text("Ver Catálogo de Productos")
    }

    Spacer(modifier = Modifier.height(4.dp))

    Button(
        onClick = onQuieroColaborar,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .aparecer(6)
    ) {
        Icon(Icons.Default.Favorite, contentDescription = "Colaborar")
        Spacer(Modifier.width(8.dp))
        Text("Quiero Colaborar")
    }

    TextButton(onClick = onVerRegistros, modifier = Modifier.aparecer(6)) {
        Text("Ver registros (Equipo Vinculación)")
    }
}