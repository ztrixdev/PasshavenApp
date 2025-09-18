package ru.ztrixdev.projects.passhavenapp.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class VaultOverviewActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent()
        {
            MaterialTheme {
                Text("This section is actively under construction", color = Color.White, style = MaterialTheme.typography.displayMedium, modifier = Modifier.padding(40.dp))
            }
        }
    }
}
