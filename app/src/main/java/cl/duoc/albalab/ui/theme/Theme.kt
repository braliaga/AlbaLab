package cl.duoc.albalab.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Los surfaceContainer* van explícitos: si no, Material 3 usa neutros lilas en barras y tarjetas.
private val LightColorScheme = lightColorScheme(
    primary = RojoAlba,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD2),
    onPrimaryContainer = Color(0xFF410100),
    secondary = AmbarTexto,
    onSecondary = Color.White,
    secondaryContainer = AmarilloAlba,
    onSecondaryContainer = TextoAlba,
    tertiary = NaranjoTexto,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBC8),
    onTertiaryContainer = Color(0xFF3A1400),
    background = CremaAlba,
    onBackground = TextoAlba,
    surface = Color(0xFFFFFBF7),
    onSurface = TextoAlba,
    surfaceVariant = Color(0xFFF0E0D6),
    onSurfaceVariant = Color(0xFF534340),
    outline = Color(0xFF857370),
    outlineVariant = Color(0xFFD8C2BC),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFBF5EE),
    surfaceContainer = Color(0xFFF5EDE4),
    surfaceContainerHigh = Color(0xFFEFE6DC),
    surfaceContainerHighest = Color(0xFFE9DFD4)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4A5),
    onPrimary = Color(0xFF690100),
    primaryContainer = Color(0xFF931A05),
    onPrimaryContainer = Color(0xFFFFDAD2),
    secondary = AmarilloAlba,
    onSecondary = Color(0xFF3F2E00),
    secondaryContainer = Color(0xFF5C4300),
    onSecondaryContainer = Color(0xFFFFDF93),
    tertiary = Color(0xFFFFB68D),
    onTertiary = Color(0xFF552000),
    tertiaryContainer = Color(0xFF7A3300),
    onTertiaryContainer = Color(0xFFFFDBC8),
    background = Color(0xFF1D1B19),
    onBackground = Color(0xFFEDE0DB),
    surface = Color(0xFF1D1B19),
    onSurface = Color(0xFFEDE0DB),
    surfaceVariant = Color(0xFF534340),
    onSurfaceVariant = Color(0xFFD8C2BC),
    outline = Color(0xFFA08C88),
    outlineVariant = Color(0xFF534340),
    surfaceContainerLowest = Color(0xFF171513),
    surfaceContainerLow = Color(0xFF25221F),
    surfaceContainer = Color(0xFF2A2623),
    surfaceContainerHigh = Color(0xFF34302D),
    surfaceContainerHighest = Color(0xFF3F3B37)
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
