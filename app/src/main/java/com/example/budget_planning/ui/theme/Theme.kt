package com.example.budget_planning.ui.theme

import android.app.Activity
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
import com.example.budget_planning.data.repository.AppTheme

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    background = Color(0xFFF7F9FF), // Light Blue-tinted background
    surface = Color(0xFFF7F9FF),    // Light Blue-tinted surface
    surfaceVariant = BlueGray90,
    onSurfaceVariant = BlueGray30,
    outline = BlueGray50,
    inverseOnSurface = BlueGray95,
    inverseSurface = BlueGray20,
    inversePrimary = PrimaryDark,
    surfaceTint = PrimaryLight,
    outlineVariant = BlueGray80,
    scrim = Color(0xFF000000)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = BlueGray10,
    surface = BlueGray10,
    surfaceVariant = BlueGray20,
    onSurfaceVariant = BlueGray80,
    outline = BlueGray50,
    inverseOnSurface = BlueGray10,
    inverseSurface = BlueGray90,
    inversePrimary = PrimaryLight,
    surfaceTint = PrimaryDark,
    outlineVariant = BlueGray30,
    scrim = Color(0xFF000000)
)

private val HighContrastLightColorScheme = lightColorScheme(
    primary = HighContrastLightPrimary,
    onPrimary = HighContrastLightOnPrimary,
    background = HighContrastLightBackground,
    onBackground = HighContrastLightOnBackground,
    surface = HighContrastLightSurface,
    onSurface = HighContrastLightOnSurface,
    primaryContainer = Color.White,
    onPrimaryContainer = Color.Black,
    secondary = Color.Yellow,
    onSecondary = Color.Black,
    secondaryContainer = Color.Cyan,
    onSecondaryContainer = Color.Black,
    tertiary = Color.Magenta,
    onTertiary = Color.Black,
    tertiaryContainer = Color.Yellow,
    onTertiaryContainer = Color.Black,
    surfaceVariant = Color.White,
    onSurfaceVariant = Color.Black,
    outline = Color.Black,
    error = Color.Red,
    onError = Color.White
)

private val HighContrastDarkColorScheme = darkColorScheme(
    primary = HighContrastDarkPrimary,
    onPrimary = HighContrastDarkOnPrimary,
    background = HighContrastDarkBackground,
    onBackground = HighContrastDarkOnBackground,
    surface = HighContrastDarkSurface,
    onSurface = HighContrastDarkOnSurface,
    primaryContainer = Color.Black,
    onPrimaryContainer = Color.White,
    secondary = Color.Yellow,
    onSecondary = Color.Black,
    secondaryContainer = Color.Cyan,
    onSecondaryContainer = Color.Black,
    tertiary = Color.Magenta,
    onTertiary = Color.Black,
    tertiaryContainer = Color.Yellow,
    onTertiaryContainer = Color.Black,
    surfaceVariant = Color.Black,
    onSurfaceVariant = Color.White,
    outline = Color.White,
    error = Color.Red,
    onError = Color.White
)

@Composable
fun BudgetplanningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppTheme = AppTheme.LOW_CONTRAST,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.HIGH_CONTRAST -> {
            if (darkTheme) HighContrastDarkColorScheme else HighContrastLightColorScheme
        }
        AppTheme.LOW_CONTRAST -> {
            when {
                // If using dynamicColor, it will follow system palette (usually vibrant)
                // We'll prioritize the custom Oxford Blue theme by disabling dynamic color for Low Contrast
                // unless specifically requested.
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                }
                darkTheme -> DarkColorScheme
                else -> LightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
