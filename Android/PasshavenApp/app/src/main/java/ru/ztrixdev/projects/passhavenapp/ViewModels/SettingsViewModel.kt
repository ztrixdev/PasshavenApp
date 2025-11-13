package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.Preferences.SecurityPrefs
import ru.ztrixdev.projects.passhavenapp.Room.Vault
import ru.ztrixdev.projects.passhavenapp.SpecialCharNames
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.IntroStages
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword
import ru.ztrixdev.projects.passhavenapp.pHbeKt.PIN_LENGTH_LIMIT
import ru.ztrixdev.projects.passhavenapp.specialCharacters
import kotlin.math.PI

class SettingsViewModel: ViewModel() {
    val openAppearance = mutableStateOf(false)
    val openSecurity = mutableStateOf(false)
    val openExports = mutableStateOf(false)
    val openImports = mutableStateOf(false)
    val openInfo = mutableStateOf(false)
    val openPINChange = mutableStateOf(false)

    val currentPINConfirmed = mutableStateOf(false)
    val currentPIN = mutableStateOf("")
    val firstPromptDone = mutableStateOf(false)
    val firstPromptPin = mutableStateOf("")
    val secondPromptDone = mutableStateOf(false)
    val secondPromptPin = mutableStateOf("")

    var pinLastChanged = mutableStateOf(0L)
    var mpLastChanged = mutableStateOf(0L)

    fun onSecurityOpened(context: Context) {
        pinLastChanged.value = SecurityPrefs.getLastPINChange(context)
        mpLastChanged.value  = SecurityPrefs.getLastMPChange(context)
    }

    @OptIn(ExperimentalStdlibApi::class, ExperimentalComposeUiApi::class)
    suspend fun onCPINPadClick(btnClicked: Any, ctx: Context) {
        if (btnClicked.toString().isDigitsOnly()) {
            try {
                val newNumber = btnClicked.toString()
                when {
                    !currentPINConfirmed.value -> if (currentPIN.value.length < PIN_LENGTH_LIMIT) currentPIN.value += newNumber
                    !firstPromptDone.value -> if (firstPromptPin.value.length < PIN_LENGTH_LIMIT) firstPromptPin.value += newNumber
                    !secondPromptDone.value -> if (secondPromptPin.value.length < PIN_LENGTH_LIMIT) secondPromptPin.value += newNumber
                }
            } catch (_: NumberFormatException) {
                println("Somehow the clicked button contains a digit, but, it can't be parsed by Kotlin .toInt extension private function. Weird lol")
            }
        } else if (btnClicked.toString() == specialCharacters[SpecialCharNames.Backspace].toString()) {
            when {
                !currentPINConfirmed.value -> currentPIN.value = currentPIN.value.dropLast(1)
                !firstPromptDone.value -> firstPromptPin.value = firstPromptPin.value.dropLast(1)
                !secondPromptDone.value -> secondPromptPin.value = secondPromptPin.value.dropLast(1)
            }
        } else if (btnClicked.toString() == specialCharacters[SpecialCharNames.Tick].toString()) {
            when {
                !currentPINConfirmed.value -> {
                    val pinMatches = VaultHandler().loginByPIN(currentPIN.value, ctx)
                    if (pinMatches)
                        currentPINConfirmed.value = true
                    else
                        resetPIN()
                }
                !firstPromptDone.value -> {
                    if (!MasterPassword.verifyPIN(firstPromptPin.value))
                        resetPIN()
                    else
                        firstPromptDone.value = true
                }
                !secondPromptDone.value -> {
                    if (firstPromptPin.value.contentEquals(secondPromptPin.value)) {
                        changePIN(ctx)
                        secondPromptDone.value = true
                        openPINChange.value = false
                    }
                    else
                        resetPIN()
                }
            }
        }
    }


    suspend fun _setSelectedFlabs(flabs: Int, context: Context) {
        VaultHandler().updateFlabs(flabs, context)
    }

    fun getCurrentlyEditedPINsLen(): Int {
        return when {
            !currentPINConfirmed.value -> currentPIN.value.length
            !firstPromptDone.value -> firstPromptPin.value.length
            !secondPromptDone.value -> secondPromptPin.value.length
            else -> 0
        }
    }

    suspend fun changePIN(ctx: Context) {
        VaultHandler().changePIN(secondPromptPin.value, ctx)
        SecurityPrefs.saveLastPINChange(System.currentTimeMillis(), ctx)
    }


    fun resetPIN() {
        currentPINConfirmed.value = false
        currentPIN.value = ""
        firstPromptDone.value = false
        firstPromptPin.value = ""
        secondPromptDone.value = false
        secondPromptPin.value = ""
    }

}
