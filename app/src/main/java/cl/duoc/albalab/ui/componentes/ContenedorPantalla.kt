package cl.duoc.albalab.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ContenedorPantalla(
    modifier: Modifier = Modifier,
    paddingHorizontal: Dp = 16.dp,
    paddingVertical: Dp = 16.dp,
    permitirScroll: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val scrollModifier = if (permitirScroll) {
            Modifier.verticalScroll(rememberScrollState())
        } else {
            Modifier
        }

        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .then(scrollModifier)
                .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = contenido
        )
    }
}