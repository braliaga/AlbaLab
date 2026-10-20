package cl.duoc.albalab.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AzulClaroOscuro,
    onPrimary = SobreAzulOscuro,
    primaryContainer = AzulContenedorOscuro,
    onPrimaryContainer = SobreAzulBlanco,
    secondary = VerdeClaroOscuro,
    onSecondary = SobreVerdeOscuro,
    background = FondoOscuro,
    onBackground = TextoClaro,
    surface = SuperficieOscura,
    onSurface = TextoClaro
)

private val LightColorScheme = lightColorScheme(
    primary = AzulProfundoAlba,
    onPrimary = SobreAzulBlanco,
    primaryContainer = AzulContenedorClaro,
    onPrimaryContainer = SobreAzulContenedor,
    secondary = VerdeInclusion,
    onSecondary = SobreVerdeBlanco,
    secondaryContainer = VerdeContenedorClaro,
    onSecondaryContainer = SobreVerdeContenedor,
    tertiary = AmbarCalido,
    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = SuperficieClara,
    onSurface = TextoOscuro
)

@Composable
fun AlbaLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}