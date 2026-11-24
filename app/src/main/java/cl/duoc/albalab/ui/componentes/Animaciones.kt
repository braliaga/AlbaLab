package cl.duoc.albalab.ui.componentes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

// Entrada escalonada: fade + subida de 8 dp en 200 ms. Solo la primera vez (rememberSaveable evita repetirla al rotar
// o al volver a ver un ítem de una lista). El delay va dentro del tween para que respete "Quitar animaciones".
fun Modifier.aparecer(indice: Int = 0): Modifier = composed {
    var visto by rememberSaveable { mutableStateOf(false) }
    val progreso = remember { Animatable(if (visto) 1f else 0f) }
    val desplazamiento = with(LocalDensity.current) { 8.dp.toPx() }

    LaunchedEffect(Unit) {
        if (!visto) {
            progreso.animateTo(
                targetValue = 1f,
                animationSpec = tween(200, delayMillis = indice.coerceIn(0, 6) * 40, easing = LinearOutSlowInEasing)
            )
            visto = true
        }
    }

    graphicsLayer {
        alpha = progreso.value
        translationY = (1f - progreso.value) * desplazamiento
    }
}
