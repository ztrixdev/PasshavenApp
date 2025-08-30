package ru.ztrixdev.projects.passhavenapp.pHbeKt

import kotlin.random.Random

class MasterPassword {
    fun genMP(wordCount: Int): String {
        if (wordCount <= 0) {
            throw RuntimeException("Word count must be greater than zero!")
        }

        val words = WordsList().getWords()

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
        if (passwd.length >= 18)
            return true
        if (passwd.length <= 8)
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
        return digitNumber >= 3 && specialCharNumber >= 2 && uppercaseNumber >= 2
    }

    fun verifyPIN(PIN: Int): Boolean {
        return PIN.toString().length >= 6 && PIN.toString().length <= 12 && !PIN.toString().contains("1234") && !PIN.toString().contains("4321")
    }
}