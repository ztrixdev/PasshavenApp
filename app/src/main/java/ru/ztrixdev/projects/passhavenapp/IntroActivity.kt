package ru.ztrixdev.projects.passhavenapp

// the AWFULLY LARGE IMPORTS
// java and kotlin are just a joke when it comes to imports
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.IntroStages
import ru.ztrixdev.projects.passhavenapp.ViewModels.IntroViewModel

val ivm = IntroViewModel()

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                when (ivm.currentStage.value) {
                    IntroStages.Greeting -> IntroPartGreeting()
                    IntroStages.PINCreation -> IntroPartCreatePIN()
                    IntroStages.MasterPasswordGenerator -> IntroPartCreateMPG()
                    IntroStages.ManualMPSet -> IntroPartCreateMPM()
                    IntroStages.CreateVault -> {
                        ivm.tryCreateVault(this.applicationContext)
                        this.applicationContext.startActivity(Intent(this.applicationContext, VaultOverviewActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                }
            }
        }
    }
}


// Greeting block starts here
// =====================================================================

@Composable
private fun IntroPartGreeting() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkColorScheme().background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.intro_part_greeting_text_welcome),
            modifier = Modifier.padding(bottom = 24.dp),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            color = darkColorScheme().primary
        )
        Icon(
            painter = painterResource(R.drawable.lock_person_40px),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 24.dp),
            tint = darkColorScheme().secondary
        )
        Spacer(modifier = Modifier.size(32.dp))
        Text(
            text = stringResource(R.string.intro_part_greeting_text_description_p1),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            color = darkColorScheme().primary
        )
        Text(
            text = stringResource(R.string.intro_part_greeting_text_description_p2),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            color = darkColorScheme().primary
        )
        Spacer(modifier = Modifier.size(32.dp))
        Button(
            onClick = {
                ivm.currentStage.value = IntroStages.MasterPasswordGenerator
            },
            modifier = Modifier.padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = darkColorScheme().secondaryContainer)
        ) {
            Text(
                text = stringResource(R.string.intro_part_greeting_button_get_started),
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
private fun IntroPartCreateMPG() {
    MPCreationG()
}

@Composable
private fun IntroPartCreateMPM() {
    MPCreationM()
}

@Composable
private fun MPCreationG() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkColorScheme().background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.mp_creation_title),
            modifier = Modifier.padding(bottom = 24.dp),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            color = darkColorScheme().primary
        )

        Icon(
            painter = painterResource(R.drawable.password_40px),
            contentDescription = "Password Icon",
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 16.dp),
            tint = darkColorScheme().secondary
        )

        Text(
            text = stringResource(R.string.mp_creation_text_description_p1),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp),
            color = darkColorScheme().primary
        )
        Text(
            text = stringResource(R.string.mp_creation_text_description_p2),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp),
            color = darkColorScheme().primary
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(darkColorScheme().background)
                .border(2.dp, darkColorScheme().secondaryContainer, shape = MaterialTheme.shapes.medium)
        ) {
            if (ivm.currentMP.value.isBlank()) {
                Text(
                    text = stringResource(R.string.click_regenerate_once),
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = darkColorScheme().secondary
                )
            } else {
                Text(
                    text = ivm.currentMP.value,
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = darkColorScheme().secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        TextButton(onClick = { ivm.generateMP() }, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.mp_creation_regen_button_text), fontSize = 16.sp)
        }

        TextButton(
            onClick = {
                        ivm.currentMP.value = ""
                        ivm.currentStage.value = IntroStages.ManualMPSet
                      }, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.mp_creation_man_start), fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (ivm.mp.verify(ivm.currentMP.value)) {
                    ivm.currentStage.value = IntroStages.PINCreation
                } else {
                    // a weird generation might happen and the generator won't generate a normal passowrd
                    ivm.generateMP()
                }
            },
            enabled = (ivm.currentMP.value != "click regenerate at least once"),
            modifier = Modifier.padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = darkColorScheme().secondaryContainer)
        ) {
            Text(
                text = stringResource(R.string.continue_button),
                fontSize = 18.sp,
                color = darkColorScheme().primary
            )
        }
    }
}

@Composable
private fun MPCreationM() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkColorScheme().background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.mp_creation_title),
            modifier = Modifier.padding(bottom = 24.dp),
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            color = darkColorScheme().primary,
        )

        Icon(
            painter = painterResource(R.drawable.password_40px),
            contentDescription = "Password Icon",
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 16.dp),
            tint = darkColorScheme().secondary
        )

        Text(
            text = stringResource(R.string.mp_creation_text_description_p1),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = darkColorScheme().primary
        )

        var text by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                ivm.currentMP.value = newText.text
                ivm.checkMP()
            },
            placeholder = {
                Text(text = stringResource(R.string.mp_creation_man_enter_mp))
                          },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent, errorContainerColor = Color.Transparent, focusedTextColor = Color.White)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = ivm.containsEnoughSpecialChars.value,
                    onCheckedChange = null,
                    enabled = false,
                    modifier = Modifier.padding(end = 5.dp),
                )
                Text(stringResource(R.string.mp_creation_checkbox_special_chars),  color = darkColorScheme().primary, fontSize =12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = ivm.containsEnoughDigits.value,
                    onCheckedChange = null,
                    enabled = false,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(stringResource(R.string.mp_creation_checkbox_digits), color = darkColorScheme().primary, fontSize =12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = ivm.containsEnoughUppercaseLetters.value,
                    onCheckedChange = null,
                    enabled = false,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(stringResource(R.string.mp_creation_checkbox_uppercase),  color = darkColorScheme().primary,  fontSize =12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = ivm.containsEnoughLettersOverall.value,
                    onCheckedChange = null,
                    enabled = false,
                    modifier = Modifier.padding(end = 5.dp),
                )
                Text(stringResource(R.string.mp_creation_checkbox_length),
                    color = darkColorScheme().primary,
                    fontSize =12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (ivm.mp.verify(ivm.currentMP.value))
                    ivm.currentStage.value = IntroStages.PINCreation
            },
            enabled = ivm.mp.verify(ivm.currentMP.value),
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



// MP bloc ends here
// =====================================================================

// PIN block starts here
// =====================================================================
@Composable
private fun IntroPartCreatePIN() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CPINInfo()
        CPINDigits()
        CPINPad()
    }
}


@Composable
private fun CPINInfo() {
    Text(
        text = stringResource(R.string.pin_creation_title),
        modifier = Modifier.padding(bottom = 24.dp),
        style = MaterialTheme.typography.displaySmall,
        textAlign = TextAlign.Center,
        color = darkColorScheme().primary
    )
    Icon(
        painter = painterResource(R.drawable.pin_48px),
        contentDescription = "PIN material icon lol",
        modifier = Modifier
            .size(128.dp)
            .padding(bottom = 24.dp),
        tint = darkColorScheme().secondary
    )
    Text(
        text = stringResource(R.string.pin_creation_description),
        modifier = Modifier.padding(bottom = 24.dp),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = darkColorScheme().primary
    )
    if (ivm.firstPromptDone.intValue == 1) {
        Text(
            text = stringResource(R.string.pin_creation_enter_pin_again),
            modifier = Modifier.padding(bottom = 24.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = darkColorScheme().primary
        )
    }
    else {
        Text(
            text = stringResource(R.string.pin_creation_enter_pin),
            modifier = Modifier.padding(bottom = 24.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = darkColorScheme().primary
        )
    }
}

@Composable
private fun CPINDigits() {
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
            if (ivm.firstPromptDone.intValue == 0) {
                for (i in 0 until ivm.firstPromptPin.value.length) {
                    Box(
                        modifier = Modifier
                            .size(22.dp) // Set the size of the circle
                            .background(darkColorScheme().secondaryContainer, shape = CircleShape) // Set the background color and shape
                    )
                }
            } else {
                for (i in 0 until ivm.secondPromptPin.value.length) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(darkColorScheme().secondaryContainer, shape = CircleShape)
                    )
                }
            }
        }
    }
}

private const val backspace = "⌫"
private const val tick = "✔"

@Composable
private fun CPINPad() {
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
                    onClick = { ivm.onCPINPadClick(btnClicked = element) },
                    modifier = Modifier
                        .padding(6.dp)
                        .size(60.dp), // Set a fixed size for buttons
                    colors = ButtonDefaults.buttonColors(containerColor = darkColorScheme().secondaryContainer)
                ) {
                    Text(
                        text = element,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

// PIN block ends here
// =====================================================================

