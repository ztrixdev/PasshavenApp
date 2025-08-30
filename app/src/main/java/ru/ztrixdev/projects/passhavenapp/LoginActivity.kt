package ru.ztrixdev.projects.passhavenapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.LoginMethods
import ru.ztrixdev.projects.passhavenapp.ViewModels.LoginViewModel

private val lvm = LoginViewModel()
class LoginActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if (lvm.loginMethod.value == LoginMethods.ByPIN) {
                LoginByPIN(this.applicationContext)
            } else if (lvm.loginMethod.value == LoginMethods.ByMP) {
                LoginByMP(this.applicationContext)
            }
            if (lvm.loginSuccessful.value) {
                val intent = Intent(this.applicationContext, VaultOverviewActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.applicationContext.startActivity(intent)
            }
        }
    }
}

var PINLoginAttemptCount = mutableIntStateOf(0)
var MPLoginAttemptsCount = mutableIntStateOf(0)

@Composable
private fun LoginByPIN(ctx: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login_to_passhaven),
            style = MaterialTheme.typography.displaySmall,
            color = darkColorScheme().primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(64.dp))
        LPINDigits()
        if (!lvm.loginSuccessful.value && PINLoginAttemptCount.intValue > 0) {
            Text(
                text = stringResource(R.string.login_incorrect_pin),
                modifier = Modifier.padding(bottom = 10.dp),
                color = darkColorScheme().error
            )
        }
        LPINPad(ctx)
        Spacer(modifier = Modifier.size(16.dp))
        TextButton(
            onClick = { lvm.loginMethod.value = LoginMethods.ByMP }
        ) {
            Text(
                text = stringResource(R.string.login_with_mp_instead),
                style = MaterialTheme.typography.titleSmall,
                color = darkColorScheme().primary
            )
        }
    }
}

@Composable
private fun LPINDigits() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            for (i in 0 until lvm.pinLength.intValue) {
                Box(
                    modifier = Modifier
                        .size(22.dp) // Set the size of the circle
                        .background(darkColorScheme().secondaryContainer, shape = CircleShape) // Set the background color and shape
                )
            }
        }
    }
}

private const val backspace = "⌫"
private const val tick = "✔"

@Composable
private fun LPINPad(ctx: Context) {
    val padElements = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", backspace, "0", tick)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally // Center align the buttons
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp) // Increase spacing for better appearance
        ) {
            items(padElements.size) { index ->
                val element = padElements[index]
                Button(
                    onClick = { lvm.onLPINPadClicked(btnClicked = element, ctx = ctx ) },
                    modifier = Modifier
                        .padding(6.dp)
                        .size(60.dp), // Set a fixed size for buttons
                    colors = ButtonDefaults.buttonColors(containerColor = darkColorScheme().secondaryContainer)
                ) {
                    Text(
                        text = element,
                        fontSize = 24.sp,
                        color = darkColorScheme().onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun LoginByMP(ctx: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login_to_passhaven),
            style = MaterialTheme.typography.displaySmall,
            color = darkColorScheme().primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(64.dp))
        var text by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
            },
            placeholder = {
                Text(text = stringResource(R.string.login_enter_mp))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent, errorContainerColor = Color.Transparent, focusedTextColor = Color.White)
        )
        if (!lvm.loginSuccessful.value && MPLoginAttemptsCount.intValue > 0) {
            Text(
                text = stringResource(R.string.login_incorrect_mp),
                modifier = Modifier.padding(top = 10.dp),
                color = darkColorScheme().error
            )
        }
        Spacer(modifier = Modifier.size(32.dp))
        Button(
            onClick = {
                lvm.tryLoginWithMP(mp = text.text, ctx = ctx)
                MPLoginAttemptsCount.intValue++
            },
            enabled = true,
            modifier = Modifier.padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = darkColorScheme().secondaryContainer)
        ) {
            Text(
                text = stringResource(R.string.continue_button),
                fontSize = 18.sp,
                color = darkColorScheme().onSecondaryContainer
            )
        }
    }
}



