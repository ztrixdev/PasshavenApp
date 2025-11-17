package ru.ztrixdev.projects.passhavenapp.pHbeKt

import kotlin.random.Random

object MasterPassword {
    fun genMP(wordCount: Int): String {
        if (wordCount <= 0) {
            throw RuntimeException("Word count must be greater than zero!")
        }

        val words = WordsList.getWords()

        if (words.isEmpty()) {
            throw RuntimeException("Words list is empty!")
        }

        val masterPassword = StringBuilder()
        repeat(wordCount) {
            val randomIndex = Random.nextInt(words.size)
            masterPassword.append(words[randomIndex]).append(" ")
        }

        return masterPassword.toString().trimEnd()
    }

    fun verify(passwd: String): Boolean {
        if (passwd.length >= MP_ABSOLUTE_LENGTH)
            return true
        if (passwd.length <= MP_LENGTH_MINIMUM)
            return false

        val specialCharacters = listOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', ';', ':', '\'', '"', '\\', '|', ',', '<', '.', '>', '/', '?')
        var digitNumber = 0; var specialCharNumber = 0; var uppercaseNumber = 0
        for (char: Char in passwd) {
            if (char.isDigit())
                digitNumber++
            if (specialCharacters.contains(char))
                specialCharNumber++
            if (char.isUpperCase())
                uppercaseNumber++
        }

        // A good password would look like this: JFKAssasination1963!?
        return digitNumber >= MP_DIGITS_MINIMUM && specialCharNumber >= MP_SPECCHARS_MINIMUM && uppercaseNumber >= MP_UPPERCASE_MINIMUM
    }

    fun verifyPIN(PIN: String): Boolean {
        return PIN.length in PIN_LENGTH_MINIMUM..PIN_LENGTH_LIMIT && !PIN.contains("1234") && !PIN.contains("4321")
    }
}