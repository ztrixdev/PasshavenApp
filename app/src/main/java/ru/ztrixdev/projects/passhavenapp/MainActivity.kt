package ru.ztrixdev.projects.passhavenapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Vault
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PasshavenAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val context = LocalContext.current
                    Thread {
                        if (DatabaseProvider.getDatabase(context).vaultDao().getVault() == emptyList<Vault>()) {
                            val intent = Intent(context, IntroActivity::class.java)
                            context.startActivity(intent)
                        }
                    }.start()
                }
            }
        }
    }
}
