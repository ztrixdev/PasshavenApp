package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.VaultOverviewActivity
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.IntroStages
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword

class IntroViewModel {
    val mp = MasterPassword()
    val currentStage = mutableStateOf(IntroStages.Greeting)

    val containsEnoughSpecialChars = mutableStateOf(false)
    val containsEnoughDigits = mutableStateOf(false)
    val containsEnoughUppercaseLetters = mutableStateOf(false)
    val containsEnoughLettersOverall = mutableStateOf(false)

    fun checkMP() {
        if (currentMP.value.length < 8)
            containsEnoughLettersOverall.value = false
        if (currentMP.value.length > 8 && currentMP.value.length < 18)
            containsEnoughLettersOverall.value = true
        if (currentMP.value.length >= 18)
            containsEnoughLettersOverall.value = true

        val specialCharacters = listOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', ';', ':', '\'', '"', '\\', '|', ',', '<', '.', '>', '/', '?')
        var digitNumber = 0; var specialCharNumber = 0; var uppercaseNumber = 0
        for (char: Char in currentMP.value) {
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

    var currentMP = mutableStateOf("")

    fun generateMP() {
        currentMP.value = mp.genMP(6)
    }


    val firstPromptDone = mutableIntStateOf(0)
    val firstPromptPin = mutableStateOf("")
    val secondPromptPin = mutableStateOf("")

    private val backspace = "⌫"
    private val tick = "✔"

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
            } catch (_: NumberFormatException) {
                println("Somehow the clicked button contains a digit, but, it can't be parsed by Kotlin .toInt extension private function. Weird lol")
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

    fun tryCreateVault(ctx: Context) {
        Thread {
            val vh = VaultHandler()
            if (mp.verify(currentMP.value) && mp.verifyPIN(secondPromptPin.value.toInt())) {
                vh.createVault(currentMP.value, secondPromptPin.value.toInt(),ctx)
            }
        }.start()
    }

    private fun resetPIN() {
        firstPromptDone.intValue = 0
        firstPromptPin.value = ""
        secondPromptPin.value = ""
    }
}