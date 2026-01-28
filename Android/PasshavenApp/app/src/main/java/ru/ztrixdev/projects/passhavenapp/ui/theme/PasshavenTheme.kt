package ru.ztrixdev.projects.passhavenapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ru.ztrixdev.projects.passhavenapp.ui.theme.amoled.amoledDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.amoled.amoledLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.aqua.aquaDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.aqua.aquaLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.codered.codeRedDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.codered.codeRedLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.hotpink.hotpinkDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.hotpink.hotpinkLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.monochroma.monochromaDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.monochroma.monochromaLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.peppermint.peppermintDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.peppermint.peppermintLightScheme

enum class AppThemeType {
    Amoled, Aqua, CodeRED, HotPink, Monochroma, Peppermint
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
            AppThemeType.Amoled ->
                if (darkTheme) amoledDarkScheme else amoledLightScheme
            AppThemeType.CodeRED ->
                if (darkTheme) codeRedDarkScheme else codeRedLightScheme
            AppThemeType.Peppermint ->
                if (darkTheme) peppermintDarkScheme else peppermintLightScheme
            AppThemeType.Monochroma ->
                if (darkTheme) monochromaDarkScheme else monochromaLightScheme
            AppThemeType.Aqua  ->
                if (darkTheme) aquaDarkScheme else aquaLightScheme
            AppThemeType.HotPink ->
                if (darkTheme) hotpinkDarkScheme else hotpinkLightScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

