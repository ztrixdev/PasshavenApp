package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class VaultOverviewActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent()
        {
            // this some raw shii, don't mind it, its really ugly
            MaterialTheme {
                var gotoNEA by remember { mutableStateOf(false) }
                val ctx = LocalContext.current
                LaunchedEffect(gotoNEA) {
                    if (gotoNEA) {
                        ctx .startActivity(
                            Intent(ctx, NewEntryActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                }
                Text("This section is actively under construction", color = Color.White, style = MaterialTheme.typography.displayMedium, modifier = Modifier.padding(40.dp))
                Button(
                    onClick = {
                        gotoNEA = true
                    }
                ) {
                    Text("Go to New Entry Activity")
                }
            }
        }
    }
}
