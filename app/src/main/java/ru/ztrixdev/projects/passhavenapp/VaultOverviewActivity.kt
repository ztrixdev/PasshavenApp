package ru.ztrixdev.projects.passhavenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.pHbeKt.AndroidCrypto
import ru.ztrixdev.projects.passhavenapp.pHbeKt.CryptoNames
import ru.ztrixdev.projects.passhavenapp.pHbeKt.SodiumCrypto
import java.security.KeyStore
import javax.crypto.SecretKey

class VaultOverviewActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent()
        {
            MaterialTheme {
                Text("your vault is created and will soon be here!", color = Color.White, style = MaterialTheme.typography.displayMedium, modifier = Modifier.padding(40.dp))
            }
        }
    }
}