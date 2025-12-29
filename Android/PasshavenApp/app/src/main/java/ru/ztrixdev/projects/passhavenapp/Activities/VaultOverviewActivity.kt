package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.DelicateCoroutinesApi
import ru.ztrixdev.projects.passhavenapp.Handlers.SessionHandler
import ru.ztrixdev.projects.passhavenapp.Preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme


class VaultOverviewActivity: ComponentActivity() {
    override fun onResume() {
        val isSessionExpd = SessionHandler.isSessionExpired(this.applicationContext)
        if (isSessionExpd) {
            this.applicationContext.startActivity(
                Intent(this.applicationContext, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContent()
        {
            // this some raw shii, don't mind it, its really ugly
            PasshavenTheme(
                themeType = ThemePrefs.getSelectedTheme(LocalContext.current),
                darkTheme = ThemePrefs.getDarkThemeBool(LocalContext.current),
                dynamicColors = ThemePrefs.getDynamicColorsBool(LocalContext.current),
            )
                {

                }
        }


    }

    @Composable
    private fun BottomNav() {

    }
}
