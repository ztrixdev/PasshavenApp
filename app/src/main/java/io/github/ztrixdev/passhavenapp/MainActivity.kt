package io.github.ztrixdev.passhavenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.ztrixdev.passhavenapp.pHbeKt.Keyfile
import io.github.ztrixdev.passhavenapp.pHbeKt.Keygen
import io.github.ztrixdev.passhavenapp.pHbeKt.MasterPassword
import io.github.ztrixdev.passhavenapp.ui.theme.PasshavenAppTheme
import java.security.KeyStore
import javax.crypto.SecretKey


class MainActivity : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    // TODO
    }
}
