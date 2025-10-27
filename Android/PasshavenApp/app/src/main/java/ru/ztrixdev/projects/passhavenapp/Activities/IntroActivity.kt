package ru.ztrixdev.projects.passhavenapp.Activities

// the AWFULLY LARGE IMPORTS
// java and kotlin are just a joke when it comes to imports
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.runBlocking
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.SpecialCharNames
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.IntroStages
import ru.ztrixdev.projects.passhavenapp.ViewModels.IntroViewModel
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword
import ru.ztrixdev.projects.passhavenapp.specialCharacters

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val introViewModel: IntroViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val localctx = LocalContext.current
            MaterialTheme {
                when (introViewModel.currentStage.value) {
                    IntroStages.Greeting -> IntroPartGreeting(introViewModel)
                    IntroStages.PINCreation -> IntroPartCreatePIN(introViewModel)
                    IntroStages.MasterPasswordGenerator -> IntroPartCreateMPG(introViewModel)
                    IntroStages.ManualMPSet -> IntroPartCreateMPM(introViewModel)
                    IntroStages.CreateVault -> {
                        runBlocking {
                            introViewModel.tryCreateVault(localctx)
                            localctx.startActivity(
                                Intent(
                                    localctx,
                                    VaultOverviewActivity::class.java
                                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                }
            }
        }
    }


// Greeting block starts here
// =====================================================================
    @Composable
    private fun IntroPartGreeting(introViewModel: IntroViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.intro_part_greeting_text_welcome),
                modifier = Modifier.padding(bottom = 24.dp),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                painter = painterResource(R.drawable.lock_person_40px),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 24.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.size(32.dp))
            Text(
                text = stringResource(R.string.intro_part_greeting_text_description_p1),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.intro_part_greeting_text_description_p2),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(32.dp))
            Button(
                onClick = {
                    introViewModel.currentStage.value = IntroStages.MasterPasswordGenerator
                },
                modifier = Modifier.padding(horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = stringResource(R.string.intro_part_greeting_button_get_started),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp
                )
            }
        }
    }

// Greeting block ends here
// =====================================================================


// MP block starts here
// =====================================================================

    @Composable
    private fun IntroPartCreateMPG(introViewModel: IntroViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.mp_creation_title),
                modifier = Modifier.padding(bottom = 24.dp),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Icon(
                painter = painterResource(R.drawable.password_40px),
                contentDescription = "Password Icon",
                modifier = Modifier
                    .size(128.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = stringResource(R.string.mp_creation_text_description_p1),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.mp_creation_text_description_p2),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .border(2.dp, MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium)
            ) {
                if (introViewModel.currentMP.value.isBlank()) {
                    Text(
                        text = stringResource(R.string.click_regenerate_once),
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    Text(
                        text = introViewModel.currentMP.value,
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(onClick = { introViewModel.generateMP() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.mp_creation_regen_button_text), fontSize = 16.sp)
            }

            TextButton(
                onClick = {
                    introViewModel.currentMP.value = ""
                    introViewModel.currentStage.value = IntroStages.ManualMPSet
                }, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.mp_creation_man_start), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (MasterPassword.verify(introViewModel.currentMP.value)) {
                        introViewModel.currentStage.value = IntroStages.PINCreation
                    } else {
                        // a weird generation might happen and the generator won't generate a normal passowrd
                        introViewModel.generateMP()
                    }
                },
                enabled = (introViewModel.currentMP.value != "click regenerate at least once"),
                modifier = Modifier.padding(horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = stringResource(R.string.continue_button),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    @Composable
    private fun IntroPartCreateMPM(introViewModel: IntroViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.mp_creation_title),
                modifier = Modifier.padding(bottom = 24.dp),
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
            )

            Icon(
                painter = painterResource(R.drawable.password_40px),
                contentDescription = "Password Icon",
                modifier = Modifier
                    .size(128.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = stringResource(R.string.mp_creation_text_description_p1),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            var text by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    introViewModel.currentMP.value = newText.text
                    introViewModel.checkMP()
                },
                placeholder = {
                    Text(text = stringResource(R.string.mp_creation_man_enter_mp))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent, errorContainerColor = Color.Transparent, focusedTextColor = MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = introViewModel.containsEnoughSpecialChars.value,
                        onCheckedChange = null,
                        enabled = false,
                        modifier = Modifier.padding(end = 5.dp),
                    )
                    Text(stringResource(R.string.mp_creation_checkbox_special_chars),  color = MaterialTheme.colorScheme.primary, fontSize =12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = introViewModel.containsEnoughDigits.value,
                        onCheckedChange = null,
                        enabled = false,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(stringResource(R.string.mp_creation_checkbox_digits), color = MaterialTheme.colorScheme.primary, fontSize =12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = introViewModel.containsEnoughUppercaseLetters.value,
                        onCheckedChange = null,
                        enabled = false,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(stringResource(R.string.mp_creation_checkbox_uppercase),  color = MaterialTheme.colorScheme.primary,  fontSize =12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = introViewModel.containsEnoughLettersOverall.value,
                        onCheckedChange = null,
                        enabled = false,
                        modifier = Modifier.padding(end = 5.dp),
                    )
                    Text(stringResource(R.string.mp_creation_checkbox_length),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize =12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (MasterPassword.verify(introViewModel.currentMP.value))
                        introViewModel.currentStage.value = IntroStages.PINCreation
                },
                enabled = MasterPassword.verify(introViewModel.currentMP.value),
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



// MP bloc ends here
// =====================================================================

    // PIN block starts here
// =====================================================================
    @Composable
    private fun IntroPartCreatePIN(introViewModel: IntroViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CPINInfo(introViewModel)
            CPINDigits(introViewModel)
            CPINPad(introViewModel)
        }
    }


    @Composable
    private fun CPINInfo(introViewModel: IntroViewModel) {
        Text(
            text = stringResource(R.string.pin_creation_title),
            modifier = Modifier.padding(bottom = 24.dp),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Icon(
            painter = painterResource(R.drawable.pin_48px),
            contentDescription = "PIN material icon lol",
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 24.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(R.string.pin_creation_description),
            modifier = Modifier.padding(bottom = 24.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        if (introViewModel.firstPromptDone.intValue == 1) {
            Text(
                text = stringResource(R.string.pin_creation_enter_pin_again),
                modifier = Modifier.padding(bottom = 24.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
        else {
            Text(
                text = stringResource(R.string.pin_creation_enter_pin),
                modifier = Modifier.padding(bottom = 24.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    private fun CPINDigits(introViewModel: IntroViewModel) {
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
                if (introViewModel.firstPromptDone.intValue == 0) {
                    for (i in 0 until introViewModel.firstPromptPin.value.length) {
                        Box(
                            modifier = Modifier
                                .size(22.dp) // Set the size of the circle
                                .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape) // Set the background color and shape
                        )
                    }
                } else {
                    for (i in 0 until introViewModel.secondPromptPin.value.length) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CPINPad(introViewModel: IntroViewModel) {
        val padElements = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", specialCharacters[SpecialCharNames.Backspace].toString(), "0", specialCharacters[SpecialCharNames.Tick].toString())
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
                        onClick = { introViewModel.onCPINPadClick(btnClicked = element) },
                        modifier = Modifier
                            .padding(6.dp)
                            .size(60.dp), // Set a fixed size for buttons
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = element,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }

// PIN block ends here
// =====================================================================
}
