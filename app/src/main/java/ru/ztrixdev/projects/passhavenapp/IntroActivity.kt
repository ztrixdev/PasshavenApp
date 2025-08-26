package ru.ztrixdev.projects.passhavenapp

// the AWFULLY LARGE IMPORTS
// java and kotlin are just a joke when it comes to imports
import android.content.Context
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenAppTheme
import kotlin.text.iterator

private val mp = MasterPassword()

private val currentStage = mutableStateOf<IntroStages>(IntroStages.Greeting)

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PasshavenAppTheme {
                if (currentStage.value == IntroStages.Greeting)
                    IntroPartGreeting()
                else if (currentStage.value == IntroStages.PINCreation)
                    IntroPartCreatePIN()
                else if (currentStage.value == IntroStages.MasterPasswordGenerator)
                    IntroPartCreateMPG()
                else if (currentStage.value == IntroStages.ManualMPSet)
                    IntroPartCreateMPM()
                else if (currentStage.value == IntroStages.CreateVault)
                    tryCreateVault(this.applicationContext)
            }
        }
    }
}

fun tryCreateVault(ctx: Context) {
    Thread {
        val vh = VaultHandler()
        if (mp.verify(currentMP) && mp.verifyPIN(secondPromptPin.value.toInt())) {
            vh.createVault(currentMP, secondPromptPin.value.toInt(),ctx)
        }
        //todo: transfer the user to the vault itself.
    }.start()
}

// Greeting block starts here
// =====================================================================

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
            onClick = {
                currentStage.value = IntroStages.MasterPasswordGenerator
            },
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

// Greeting block ends here
// =====================================================================


// MP block starts here
// =====================================================================

var currentMP by mutableStateOf("click regenerate at least once")

fun generateMP() {
    currentMP = mp.genMP(6)
}

@Composable
fun IntroPartCreateMPG() {
    MPCreationG()
}

@Composable
fun IntroPartCreateMPM() {
    MPCreationM()
}

@Composable
fun MPCreationG() {
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

        TextButton(
            onClick = {
                        currentMP = ""
                        currentStage.value = IntroStages.ManualMPSet
                      }, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.mp_creation_man_start), fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (mp.verify(currentMP)) {
                    currentStage.value = IntroStages.PINCreation
                } else {
                    // a weird generation might happen and the generator won't generate a normal passowrd
                    generateMP()
                }
            },
            enabled = (currentMP != "click regenerate at least once"),
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

@Composable
fun MPCreationM() {
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
            modifier = Modifier.padding(bottom = 8.dp)
        )

        var text by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                currentMP = newText.text
                checkMP()
            },
            placeholder = {
                Text(text = stringResource(R.string.mp_creation_man_enter_mp))
                          },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent, errorContainerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = containsEnoughSpecialChars.value,
                    onCheckedChange = null,
                    enabled = false,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(stringResource(R.string.mp_creation_checkbox_special_chars), fontSize =12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = containsEnoughDigits.value,
                    onCheckedChange = null,
                    enabled = false,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(stringResource(R.string.mp_creation_checkbox_digits), fontSize =12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = containsEnoughUppercaseLetters.value,
                    onCheckedChange = null,
                    enabled = false,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(stringResource(R.string.mp_creation_checkbox_uppercase), fontSize =12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = containsEnoughLettersOverall.value,
                    onCheckedChange = null,
                    enabled = false,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(stringResource(R.string.mp_creation_checkbox_length), fontSize =12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (mp.verify(currentMP))
                    currentStage.value = IntroStages.PINCreation
            },
            enabled = mp.verify(currentMP),
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

val containsEnoughSpecialChars = mutableStateOf<Boolean>(false)
val containsEnoughDigits = mutableStateOf<Boolean>(false)
val containsEnoughUppercaseLetters = mutableStateOf<Boolean>(false)
val containsEnoughLettersOverall = mutableStateOf<Boolean>(false)

fun checkMP() {
    if (currentMP.length < 8)
        containsEnoughLettersOverall.value = false
    if (currentMP.length > 8 && currentMP.length < 18)
        containsEnoughLettersOverall.value = true
    if (currentMP.length >= 18)
        containsEnoughLettersOverall.value = true

    val specialCharacters = listOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', ';', ':', '\'', '"', '\\', '|', ',', '<', '.', '>', '/', '?')
    var digitNumber = 0; var specialCharNumber = 0; var uppercaseNumber = 0
    for (char: Char in currentMP) {
        if (char.isDigit())
            digitNumber++
        if (specialCharacters.contains(char))
            specialCharNumber++
        if (char.isUpperCase())
            uppercaseNumber++
    }

    containsEnoughUppercaseLetters.value = uppercaseNumber >= 2
    containsEnoughSpecialChars.value = specialCharNumber >= 2
    containsEnoughDigits.value = digitNumber >= 2
}

// MP bloc ends here
// =====================================================================

// PIN block starts here
// =====================================================================
@Composable
fun IntroPartCreatePIN() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CPINInfo()
        CPINDigits()
        CPINPad()
    }
}

private val firstPromptDone = mutableIntStateOf(0)
private val firstPromptPin = mutableStateOf("")
private val secondPromptPin = mutableStateOf("")

@Composable
fun CPINInfo() {
    Text(
        text = stringResource(R.string.pin_creation_title),
        modifier = Modifier.padding(bottom = 24.dp),
        fontSize = 28.sp,
        textAlign = TextAlign.Center
    )
    Icon(
        painter = painterResource(R.drawable.pin_48px),
        contentDescription = "PIN material icon lol",
        modifier = Modifier
            .size(128.dp)
            .padding(bottom = 24.dp)
    )
    Text(
        text = stringResource(R.string.pin_creation_description),
        modifier = Modifier.padding(bottom = 24.dp),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
    if (firstPromptDone.intValue == 1) {
        Text(
            text = stringResource(R.string.pin_creation_enter_pin_again),
            modifier = Modifier.padding(bottom = 24.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
    else {
        Text(
            text = stringResource(R.string.pin_creation_enter_pin),
            modifier = Modifier.padding(bottom = 24.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CPINDigits() {
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
            if (firstPromptDone.intValue == 0) {
                for (i in 0 until firstPromptPin.value.length) {
                    Box(
                        modifier = Modifier
                            .size(22.dp) // Set the size of the circle
                            .background(Color.DarkGray, shape = CircleShape) // Set the background color and shape
                    )
                }
            } else {
                for (i in 0 until secondPromptPin.value.length) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(Color.DarkGray, shape = CircleShape)
                    )
                }
            }
        }
    }
}

private val backspace = "⌫"
private val tick = "✔\uFE0F"

@Composable
fun CPINPad() {
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
                    onClick = { onCPINPadClick(btnClicked = element) },
                    modifier = Modifier
                        .padding(6.dp)
                        .size(60.dp), // Set a fixed size for buttons
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
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

@OptIn(ExperimentalStdlibApi::class, ExperimentalComposeUiApi::class)
fun onCPINPadClick(btnClicked: Any) {
    if (btnClicked.toString().isDigitsOnly()) {
        try {
            val newNumber = btnClicked.toString()
            if (firstPromptDone.intValue == 1) {
                if (secondPromptPin.value.length < 12)
                    secondPromptPin.value += newNumber
            }
            else {
                if (firstPromptPin.value.length < 12)
                    firstPromptPin.value += newNumber
            }
        } catch (e: NumberFormatException) {
            println("Somehow the clicked button contains a digit, but, it can't be parsed by Kotlin .toInt extension function. Weird lol")
        }
    } else if (btnClicked.toString() == backspace) {
        if (firstPromptDone.intValue == 1)
            secondPromptPin.value = secondPromptPin.value.dropLast(1)
        else
            firstPromptPin.value = firstPromptPin.value.dropLast(1)
    } else if (btnClicked.toString() == tick) {
        if (firstPromptDone.intValue == 1) {
            if (firstPromptPin.value.contentEquals(secondPromptPin.value)) {
                    currentStage.value = IntroStages.CreateVault
            }
            else
                resetPIN()
        }
        else {
            if (!mp.verifyPIN(firstPromptPin.value.toInt()))
                resetPIN()

            firstPromptDone.intValue = 1
        }
    }
}

fun resetPIN() {
    firstPromptDone.intValue = 0
    firstPromptPin.value = ""
    secondPromptPin.value = ""
}

// PIN block ends here
// =====================================================================

