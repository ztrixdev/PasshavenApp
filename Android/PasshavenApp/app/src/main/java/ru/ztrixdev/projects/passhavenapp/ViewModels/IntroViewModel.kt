package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.Preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.SpecialCharNames
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.IntroStages
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_ABSOLUTE_LENGTH
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_DIGITS_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_LENGTH_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_SPECCHARS_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_UPPERCASE_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword
import ru.ztrixdev.projects.passhavenapp.pHbeKt.PIN_LENGTH_LIMIT
import ru.ztrixdev.projects.passhavenapp.specialCharacters
import ru.ztrixdev.projects.passhavenapp.ui.theme.AppThemeType

class IntroViewModel() : ViewModel() {
    private val masterPassword = MasterPassword

    val currentStage = mutableStateOf(IntroStages.Greeting)

    val containsEnoughSpecialChars = mutableStateOf(false)
    val containsEnoughDigits = mutableStateOf(false)
    val containsEnoughUppercaseLetters = mutableStateOf(false)
    val containsEnoughLettersOverall = mutableStateOf(false)
        

    fun checkMP() {
        if (currentMP.value.length < MP_LENGTH_MINIMUM)
            containsEnoughLettersOverall.value = false
        if (currentMP.value.length in 9..<MP_ABSOLUTE_LENGTH)
            containsEnoughLettersOverall.value = true
        if (currentMP.value.length >= MP_ABSOLUTE_LENGTH)
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

        containsEnoughUppercaseLetters.value = uppercaseNumber >= MP_UPPERCASE_MINIMUM
        containsEnoughSpecialChars.value = specialCharNumber >= MP_SPECCHARS_MINIMUM
        containsEnoughDigits.value = digitNumber >= MP_DIGITS_MINIMUM
    }

    var currentMP = mutableStateOf("")

    fun generateMP() {
        currentMP.value = masterPassword.genMP(8)
    }


    val firstPromptDone = mutableIntStateOf(0)
        
    val firstPromptPin = mutableStateOf("")
        
    val secondPromptPin = mutableStateOf("")
        

    @OptIn(ExperimentalStdlibApi::class, ExperimentalComposeUiApi::class)
    fun onCPINPadClick(btnClicked: Any) {
        if (btnClicked.toString().isDigitsOnly()) {
            try {
                val newNumber = btnClicked.toString()
                if (firstPromptDone.intValue == 1) {
                    if (secondPromptPin.value.length < PIN_LENGTH_LIMIT)
                        secondPromptPin.value += newNumber
                }
                else {
                    if (firstPromptPin.value.length < PIN_LENGTH_LIMIT)
                        firstPromptPin.value += newNumber
                }
            } catch (_: NumberFormatException) {
                println("Somehow the clicked button contains a digit, but, it can't be parsed by Kotlin .toInt extension private function. Weird lol")
            }
        } else if (btnClicked.toString() == specialCharacters[SpecialCharNames.Backspace].toString()) {
            if (firstPromptDone.intValue == 1)
                secondPromptPin.value = secondPromptPin.value.dropLast(1)
            else
                firstPromptPin.value = firstPromptPin.value.dropLast(1)
        } else if (btnClicked.toString() == specialCharacters[SpecialCharNames.Tick].toString()) {
            if (firstPromptDone.intValue == 1) {
                if (firstPromptPin.value.contentEquals(secondPromptPin.value)) {
                    currentStage.value = IntroStages.CreateVault
                }
                else
                    resetPIN()
            }
            else {
                if (!masterPassword.verifyPIN(firstPromptPin.value))
                    resetPIN()

                firstPromptDone.intValue = 1
            }
        }
    }

    suspend fun tryCreateVault(ctx: Context) {
        ThemePrefs.saveSelectedTheme(ctx, AppThemeType.W10)
        ThemePrefs.saveDarkThemeBool(ctx, true)
        val vh = VaultHandler
        if (masterPassword.verify(currentMP.value) && masterPassword.verifyPIN(secondPromptPin.value)) {
            vh.createVault(currentMP.value, secondPromptPin.value,ctx)
        }
    }

    private fun resetPIN() {
        firstPromptDone.intValue = 0
        firstPromptPin.value = ""
        secondPromptPin.value = ""
    }
}