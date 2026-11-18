package ar.edu.uade.unitreeg1hmi99.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = HmiBluePrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = HmiBlueContainer,
    secondary = HmiTealSecondaryLight,
    onSecondary = Color(0xFF042F2E),
    secondaryContainer = Color(0xFF115E59),
    onSecondaryContainer = HmiTealContainer,
    tertiary = HmiVioletTertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF4C1D95),
    onTertiaryContainer = HmiVioletContainer,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = StatusDangerBg,
    background = HmiBackgroundDark,
    onBackground = HmiOnSurfaceDark,
    surface = HmiSurfaceDark,
    onSurface = HmiOnSurfaceDark,
    surfaceVariant = HmiSurfaceVariantDark,
    onSurfaceVariant = HmiOnSurfaceVariantDark,
    outline = HmiOutlineDark,
    outlineVariant = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = HmiBluePrimary,
    onPrimary = Color.White,
    primaryContainer = HmiBlueContainer,
    onPrimaryContainer = HmiOnBlueContainer,
    secondary = HmiTealSecondary,
    onSecondary = Color.White,
    secondaryContainer = HmiTealContainer,
    onSecondaryContainer = HmiOnTealContainer,
    tertiary = HmiVioletTertiary,
    onTertiary = Color.White,
    tertiaryContainer = HmiVioletContainer,
    onTertiaryContainer = HmiOnVioletContainer,
    error = StatusDanger,
    onError = Color.White,
    errorContainer = StatusDangerBg,
    onErrorContainer = StatusDangerContent,
    background = HmiBackgroundLight,
    onBackground = HmiOnSurfaceLight,
    surface = HmiSurfaceLight,
    onSurface = HmiOnSurfaceLight,
    surfaceVariant = HmiSurfaceVariantLight,
    onSurfaceVariant = HmiOnSurfaceVariantLight,
    outline = HmiOutlineLight,
    outlineVariant = HmiOutlineVariantLight
)

@Composable
fun Unitreeg1hmi99Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Se desactiva dynamicColor por defecto para preservar la semántica visual industrial HMI
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
