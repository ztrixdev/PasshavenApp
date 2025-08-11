package io.github.ztrixdev.passhavenapp.pHbeKt

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
}