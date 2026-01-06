package ru.ztrixdev.projects.passhavenapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.ztrixdev.projects.passhavenapp.preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.SpecialCharNames
import ru.ztrixdev.projects.passhavenapp.viewModels.enums.LoginMethods
import ru.ztrixdev.projects.passhavenapp.viewModels.LoginViewModel
import ru.ztrixdev.projects.passhavenapp.specialCharacters
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val loginViewModel: LoginViewModel by viewModels()

        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                return
            }
        })


        setContent {
            val context = this.applicationContext

            PasshavenTheme(
                themeType = ThemePrefs.getSelectedTheme(context),
                darkTheme = ThemePrefs.getDarkThemeBool(context),
                dynamicColors = ThemePrefs.getDynamicColorsBool(context)
            )
            {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (loginViewModel.loginMethod.value == LoginMethods.ByPIN) {
                            LoginByPIN(loginViewModel)
                        } else if (loginViewModel.loginMethod.value == LoginMethods.ByMP) {
                            LoginByMP(loginViewModel)
                        }
                    }
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
    }

    @Composable
    private fun LoginByPIN(loginViewModel: LoginViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.login_to_passhaven),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            LPINDigits(loginViewModel)
            if (!loginViewModel.loginSuccessful.value && loginViewModel.pinLoginAttempts.intValue > 0) {
                Text(
                    text = stringResource(R.string.login_incorrect_pin),
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Spacer(modifier = Modifier.height(28.dp))
            }
            LPINPad(loginViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { loginViewModel.loginMethod.value = LoginMethods.ByMP }
            ) {
                Text(
                    text = stringResource(R.string.login_with_mp_instead),
                    style = MaterialTheme.typography.labelLarge,
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
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                for (i in 0 until loginViewModel.pinLength.intValue) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    private fun LPINPad(loginViewModel: LoginViewModel) {
        val padElements = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", specialCharacters[SpecialCharNames.Backspace].toString(), "0", specialCharacters[SpecialCharNames.Tick].toString())
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(padElements.size) { index ->
                val element = padElements[index]
                val localContext = LocalContext.current
                val isAction = element.length > 1

                FilledTonalButton(
                    onClick = {
                        GlobalScope.launch(Dispatchers.IO) {
                            loginViewModel.onLPINPadClicked(btnClicked = element, ctx = localContext)
                        }
                    },
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isAction) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                    )
                ) {
                    Text(
                        text = element,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.login_to_passhaven),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            var text by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                },
                label = {
                    Text(text = stringResource(R.string.login_enter_mp))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = loginViewModel.mpLoginAttempts.intValue > 0 && !loginViewModel.loginSuccessful.value
            )
            if (!loginViewModel.loginSuccessful.value && loginViewModel.mpLoginAttempts.intValue > 0) {
                Text(
                    text = stringResource(R.string.login_incorrect_mp),
                    modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            val localContext = LocalContext.current
            ElevatedButton(
                onClick = {
                    GlobalScope.launch(Dispatchers.IO) {
                        loginViewModel.tryLoginWithMP(mp = text.text, ctx = localContext)
                    }
                },
                enabled = text.text.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.continue_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { loginViewModel.loginMethod.value = LoginMethods.ByPIN }
            ) {
                Text(
                    text = stringResource(id = R.string.login_with_pin_instead), 
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}