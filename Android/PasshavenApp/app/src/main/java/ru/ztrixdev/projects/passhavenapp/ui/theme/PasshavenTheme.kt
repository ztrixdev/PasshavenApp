package ru.ztrixdev.projects.passhavenapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ru.ztrixdev.projects.passhavenapp.ui.theme.lion.lionDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.lion.lionLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.mint.mintDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.mint.mintLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.ubuntu.ubuntuDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.ubuntu.ubuntuLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w10.w10DarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w10.w10LightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w81.w81DarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w81.w81LightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w10.AppTypography

enum class AppThemeType {
    LION, W10, UBUNTU, MINT, W81
}

@Composable
fun PasshavenTheme(
    themeType: AppThemeType,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorFamily = when (themeType) {
        AppThemeType.LION -> if (darkTheme) lionDarkScheme else lionLightScheme
        AppThemeType.W10 -> if (darkTheme) w10DarkScheme else w10LightScheme
        AppThemeType.W81 -> if (darkTheme) w81DarkScheme else w81LightScheme
        AppThemeType.UBUNTU -> if (darkTheme) ubuntuDarkScheme else ubuntuLightScheme
        AppThemeType.MINT -> if (darkTheme) mintDarkScheme else mintLightScheme
    }

    MaterialTheme(
        colorScheme = colorFamily,  // Optional: dark/light color schemes
        typography = AppTypography,  // Your typography if needed
        content = content
    )
}
