package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.PINLoginAttemptCount
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.LoginMethods

val vh = VaultHandler()

class LoginViewModel {
    val loginMethod = mutableStateOf(LoginMethods.ByPIN)

    private val backspace = "⌫"
    private val tick = "✔"

    val loginSuccessful = mutableStateOf<Boolean>(false)

    private val pin = mutableStateOf("")
    val pinLength = mutableIntStateOf(pin.value.length)

    fun onLPINPadClicked(btnClicked: Any, ctx: Context) {
        if (btnClicked.toString().isDigitsOnly()) {
            try {
                val newNumber = btnClicked.toString()
                if (pin.value.length < 12)
                    pin.value += newNumber
                pinLength.intValue = pin.value.length
            } catch (_: NumberFormatException) {
                println("Somehow the clicked button contains a digit, but, it can't be parsed by Kotlin .toInt extension function. Weird lol")
            }
        } else if (btnClicked.toString() == backspace) {
            pin.value = pin.value.dropLast(1)
            pinLength.intValue = pin.value.length
        } else if (btnClicked.toString() == tick) {
            PINLoginAttemptCount.intValue++
            try {
                val parsedPIN = pin.value.toInt()
                Thread {
                    val loginResult = vh.loginByPIN(parsedPIN, ctx)
                    if (!loginResult) {
                        loginSuccessful.value = false
                        resetPIN()
                    }
                    else
                        loginSuccessful.value = true
                }.start()
            } catch (_: java.lang.NumberFormatException) {
                resetPIN()
            }
        }
    }

    fun tryLoginWithMP(mp: String, ctx: Context): Boolean {
        var loginResult = false
        Thread {
             loginResult = vh.loginByPassword(mp, ctx)
        }.start()
        loginSuccessful.value = loginResult
        return loginResult
    }

    private fun resetPIN() {
        pin.value = ""
        pinLength.intValue = 0
    }
}


