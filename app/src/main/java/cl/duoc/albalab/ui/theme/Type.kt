package cl.duoc.albalab.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cl.duoc.albalab.R

// Las mismas fuentes del sitio: Josefin Sans en títulos y DM Sans en el resto (incluidas en res/font, sin internet).
private val JosefinSans = FontFamily(
    Font(R.font.josefin_sans_semibold, FontWeight.SemiBold),
    Font(R.font.josefin_sans_bold, FontWeight.Bold)
)

private val DmSans = FontFamily(
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium, FontWeight.Medium),
    Font(R.font.dm_sans_bold, FontWeight.Bold)
)

private val base = Typography()

val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = JosefinSans, fontWeight = FontWeight.Bold),
    displayMedium = base.displayMedium.copy(fontFamily = JosefinSans, fontWeight = FontWeight.Bold),
    displaySmall = base.displaySmall.copy(fontFamily = JosefinSans, fontWeight = FontWeight.Bold),
    headlineLarge = base.headlineLarge.copy(fontFamily = JosefinSans, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(
        fontFamily = JosefinSans,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = base.headlineSmall.copy(fontFamily = JosefinSans, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(
        fontFamily = JosefinSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleSmall = base.titleSmall.copy(fontFamily = DmSans, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodySmall = base.bodySmall.copy(fontFamily = DmSans),
    labelLarge = base.labelLarge.copy(fontFamily = DmSans, fontWeight = FontWeight.Medium),
    labelMedium = base.labelMedium.copy(fontFamily = DmSans, fontWeight = FontWeight.Medium),
    labelSmall = base.labelSmall.copy(fontFamily = DmSans, fontWeight = FontWeight.Medium)
)
