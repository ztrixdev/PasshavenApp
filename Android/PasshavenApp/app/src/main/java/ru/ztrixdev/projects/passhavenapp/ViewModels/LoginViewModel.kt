package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.SpecialCharNames
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.LoginMethods
import ru.ztrixdev.projects.passhavenapp.pHbeKt.PIN_LENGTH_LIMIT
import ru.ztrixdev.projects.passhavenapp.specialCharacters

class LoginViewModel(private val vaultHandler: VaultHandler = VaultHandler()) : ViewModel() {
    val loginMethod = mutableStateOf<LoginMethods>(LoginMethods.ByPIN)
    val loginSuccessful = mutableStateOf(false)
    private val pin = mutableStateOf("")
    val pinLength = mutableIntStateOf(pin.value.length)
    val pinLoginAttempts = mutableIntStateOf(0)
    val mpLoginAttempts = mutableIntStateOf(0)

    suspend fun onLPINPadClicked(btnClicked: Any, ctx: Context) {
        if (btnClicked.toString().isDigitsOnly()) {
            val newNumber = btnClicked.toString()
            if (pin.value.length < PIN_LENGTH_LIMIT)
                pin.value += newNumber
            pinLength.intValue = pin.value.length
        } else if (btnClicked.toString() == specialCharacters[SpecialCharNames.Backspace].toString()) {
            pin.value = pin.value.dropLast(1)
            pinLength.intValue = pin.value.length
        } else if (btnClicked.toString() == specialCharacters[SpecialCharNames.Tick].toString()) {
            val loginResult = vaultHandler.loginByPIN(pin.value, ctx)
            if (!loginResult) {
                loginSuccessful.value = false
                resetPIN()
            }
            else loginSuccessful.value = true
            pinLoginAttempts.intValue++
        }
    }

    suspend fun tryLoginWithMP(mp: String, ctx: Context): Boolean {
        val loginResult = vaultHandler.loginByPassword(mp, ctx)
        loginSuccessful.value = loginResult
        mpLoginAttempts.intValue++
        return loginResult
    }

    private fun resetPIN() {
        pin.value = ""
        pinLength.intValue = 0
    }
}


