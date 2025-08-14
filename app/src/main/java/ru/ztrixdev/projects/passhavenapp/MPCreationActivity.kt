package ru.ztrixdev.projects.passhavenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenAppTheme

class MPCreationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PasshavenAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MPCreation()
                }
            }
        }
    }
}

var currentMP by mutableStateOf("click regenerate at least oncce")

fun generateMP() {
    currentMP = MasterPassword().genMP(6)
}


@Preview
@Composable
fun MPCreation() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.mp_creation_title),
            modifier = Modifier.padding(bottom = 24.dp),
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )

        Icon(
            painter = painterResource(R.drawable.password_40px),
            contentDescription = "Password Icon",
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 16.dp),
            tint = Color.DarkGray
        )

        Text(
            text = stringResource(R.string.mp_creation_text_description_p1),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = stringResource(R.string.mp_creation_text_description_p2),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xffe0f8f9))
                .border(2.dp, Color.Gray, shape = MaterialTheme.shapes.medium)
        ) {
            Text(
                text = currentMP,
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.titleLarge,
            )
        }


        Spacer(modifier = Modifier.height(6.dp))

        TextButton(onClick = { generateMP() }, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.mp_creation_regen_button_text), fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* TODO: continue with mp creation */ },
            modifier = Modifier.padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text(
                text = stringResource(R.string.continue_button),
                fontSize = 18.sp
            )
        }
    }
}
