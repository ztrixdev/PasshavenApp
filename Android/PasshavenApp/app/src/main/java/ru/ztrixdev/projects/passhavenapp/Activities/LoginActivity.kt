package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.SpecialCharNames
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.LoginMethods
import ru.ztrixdev.projects.passhavenapp.ViewModels.LoginViewModel
import ru.ztrixdev.projects.passhavenapp.specialCharacters

class LoginActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val loginViewModel: LoginViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = this.applicationContext

            if (loginViewModel.loginMethod.value == LoginMethods.ByPIN) {
                LoginByPIN(loginViewModel)
            } else if (loginViewModel.loginMethod.value == LoginMethods.ByMP) {
                LoginByMP(loginViewModel)
            }
            LaunchedEffect(loginViewModel.loginSuccessful.value) {
                if (loginViewModel.loginSuccessful.value) {
                    context.startActivity(
                        Intent(context, VaultOverviewActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
        }
    }

    @Composable
    private fun LoginByPIN(loginViewModel: LoginViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.login_to_passhaven),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(64.dp))
            LPINDigits(loginViewModel)
            if (!loginViewModel.loginSuccessful.value && loginViewModel.pinLoginAttempts.intValue > 0) {
                Text(
                    text = stringResource(R.string.login_incorrect_pin),
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
            LPINPad(loginViewModel)
            Spacer(modifier = Modifier.size(16.dp))
            TextButton(
                onClick = { loginViewModel.loginMethod.value = LoginMethods.ByMP }
            ) {
                Text(
                    text = stringResource(R.string.login_with_mp_instead),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    @Composable
    private fun LPINDigits(loginViewModel: LoginViewModel) {
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
                for (i in 0 until loginViewModel.pinLength.intValue) {
                    Box(
                        modifier = Modifier
                            .size(22.dp) // Set the size of the circle
                            .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape) // Set the background color and shape
                    )
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    private fun LPINPad(loginViewModel: LoginViewModel) {
        val padElements = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", specialCharacters[SpecialCharNames.Backspace].toString(), "0", specialCharacters[SpecialCharNames.Tick])
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
                    val localContext = LocalContext.current
                    Button(
                        onClick =  {
                                     GlobalScope.launch(Dispatchers.IO) {
                                        loginViewModel.onLPINPadClicked(btnClicked = element.toString(), ctx = localContext)
                                    }
                                  },
                        modifier = Modifier
                            .padding(6.dp)
                            .size(60.dp), // Set a fixed size for buttons
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = element.toString(),
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    private fun LoginByMP(loginViewModel: LoginViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.login_to_passhaven),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
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
            if (!loginViewModel.loginSuccessful.value && loginViewModel.mpLoginAttempts.intValue > 0) {
                Text(
                    text = stringResource(R.string.login_incorrect_mp),
                    modifier = Modifier.padding(top = 10.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.size(32.dp))
            val localContext = LocalContext.current
            Button(
                onClick = {
                    GlobalScope.launch(Dispatchers.IO) {
                        loginViewModel.tryLoginWithMP(mp = text.text, ctx = localContext)
                    }
                },
                enabled = true,
                modifier = Modifier.padding(horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = stringResource(R.string.continue_button),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}



