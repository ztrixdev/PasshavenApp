package ru.ztrixdev.projects.passhavenapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ru.ztrixdev.projects.passhavenapp.ui.theme.lion.lionDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.lion.lionLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.mint.mintDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.mint.mintLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.ubuntu.ubuntuDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.ubuntu.ubuntuLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w10.AppTypography
import ru.ztrixdev.projects.passhavenapp.ui.theme.w10.w10DarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w10.w10LightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w81.w81DarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w81.w81LightScheme

enum class AppThemeType {
    LION, W10, UBUNTU, MINT, W81
}

@Composable
fun PasshavenTheme(
    themeType: AppThemeType,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColors: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        // Material You (Android 12+)
        dynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        }

        else -> when (themeType) {
            AppThemeType.LION ->
                if (darkTheme) lionDarkScheme else lionLightScheme
            AppThemeType.W10 ->
                if (darkTheme) w10DarkScheme else w10LightScheme
            AppThemeType.W81 ->
                if (darkTheme) w81DarkScheme else w81LightScheme
            AppThemeType.UBUNTU ->
                if (darkTheme) ubuntuDarkScheme else ubuntuLightScheme
            AppThemeType.MINT ->
                if (darkTheme) mintDarkScheme else mintLightScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

