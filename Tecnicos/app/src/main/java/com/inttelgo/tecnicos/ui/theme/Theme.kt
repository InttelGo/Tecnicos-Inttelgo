package com.inttelgo.tecnicos.ui.theme

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
    // Colores primarios
    primary = Slate400,
    onPrimary = Slate50,
    primaryContainer = Slate700,
    onPrimaryContainer = Slate50,

    // Colores secundarios
    secondary = Slate500,
    onSecondary = Slate50,
    secondaryContainer = DeepOrange700,
    onSecondaryContainer = Slate950,

    // Colores terciarios
    tertiary = Orange600,
    onTertiary = Slate50,
    tertiaryContainer = Orange800,
    onTertiaryContainer = Orange100,

    // Colores de error
    error = DeepOrange500,
    onError = Slate50,
    errorContainer = DeepOrange800,
    onErrorContainer = DeepOrange100,

    // Fondos y superficies para modo oscuro usando slate
    background = Slate900,
    onBackground = Slate100,
    surface = Slate800,
    onSurface = Slate100,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate300,

    // Bordes y contornos
    outline = Slate500,
    outlineVariant = Slate600,
    scrim = BlackAlpha50,

    // Colores inversos
    inverseSurface = Slate100,
    inverseOnSurface = Slate800,
    inversePrimary = Orange600,

    // Contenedores de superficie
    surfaceDim = Slate800,
    surfaceBright = Slate700,
    surfaceContainerLowest = Slate950,
    surfaceContainerLow = Slate900,
    surfaceContainer = Slate800,
    surfaceContainerHigh = Slate700,
    surfaceContainerHighest = Slate600
)

private val LightColorScheme = lightColorScheme(
    // Colores primarios
    primary = Orange400,
    onPrimary = Slate50,
    primaryContainer = Slate100,
    onPrimaryContainer = Slate900,

    // Colores secundarios
    secondary = DeepOrange500,
    onSecondary = Slate50,
    secondaryContainer = DeepOrange100,
    onSecondaryContainer = DeepOrange900,

    // Colores terciarios
    tertiary = Orange600,
    onTertiary = Slate50,
    tertiaryContainer = Orange200,
    onTertiaryContainer = Orange800,

    // Colores de error
    error = DeepOrange500,
    onError = Slate50,
    errorContainer = DeepOrange100,
    onErrorContainer = DeepOrange800,

    // Fondos y superficies para modo claro usando slate
    background = Slate50,
    onBackground = Slate900,
    surface = Slate100,
    onSurface = Slate900,
    surfaceVariant = Slate200,
    onSurfaceVariant = Slate700,

    // Bordes y contornos
    outline = Slate400,
    outlineVariant = Slate300,
    scrim = BlackAlpha50,

    // Colores inversos
    inverseSurface = Slate800,
    inverseOnSurface = Slate100,
    inversePrimary = Orange200,

    // Contenedores de superficie
    surfaceDim = Slate100,
    surfaceBright = Slate50,
    surfaceContainerLowest = White,
    surfaceContainerLow = Slate100,
    surfaceContainer = Slate200,
    surfaceContainerHigh = Slate300,
    surfaceContainerHighest = Slate400,
)

@Composable
fun TecnicosTheme(
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