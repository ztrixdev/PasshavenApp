package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider


private sealed class Destination {
    object Loading : Destination()
    object Intro : Destination()
    object Login : Destination()
}

class MainActivity : ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            var destination by remember { mutableStateOf<Destination>(Destination.Loading)}

            LaunchedEffect(Unit) {
                val vaultExists = withContext(Dispatchers.IO) {
                    val vlt = DatabaseProvider.getDatabase(context).vaultDao().getVault()
                    vlt.isNotEmpty()
                }
                destination = if (vaultExists) {
                    Destination.Login
                } else {
                    Destination.Intro
                }
            }

            LaunchedEffect(destination) {
                when (val dest = destination) {
                    is Destination.Login -> {
                        context.startActivity(
                            Intent(context, LoginActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    is Destination.Intro -> {
                        context.startActivity(
                            Intent(context, IntroActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    is Destination.Loading -> {

                    }
                }
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                }
            }
        }
    }
}
