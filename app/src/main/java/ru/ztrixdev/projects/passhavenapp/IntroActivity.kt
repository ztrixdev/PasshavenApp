package ru.ztrixdev.projects.passhavenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenAppTheme

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PasshavenAppTheme {
                IntroPartGreeting()
            }
        }
    }
}

@Preview
@Composable
fun IntroPartGreeting() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.intro_part_greeting_text_welcome),
            modifier = Modifier.padding(bottom = 24.dp),
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )
        Icon(
            painter = painterResource(R.drawable.lock_person_40px),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 24.dp),
            tint = Color.DarkGray
        )
        Spacer(modifier = Modifier.size(32.dp))
        Text(
            text = stringResource(R.string.intro_part_greeting_text_description_p1),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        Text(
            text = stringResource(R.string.intro_part_greeting_text_description_p2),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.size(32.dp))
        Button(
            onClick = { /* TODO: handle creating the actual vault */ },
            modifier = Modifier.padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text(
                text = stringResource(R.string.intro_part_greeting_button_get_started),
                fontSize = 18.sp
            )
        }
    }
}

