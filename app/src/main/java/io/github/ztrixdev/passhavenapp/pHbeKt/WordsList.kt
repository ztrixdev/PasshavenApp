package io.github.ztrixdev.passhavenapp.pHbeKt

import java.io.File

class WordsList {
    fun getWords(txtFilePath: String): List<String> {
        if (txtFilePath.isEmpty()) {
            throw IllegalArgumentException("A file path cannot be empty!")
        }

        val file = File(txtFilePath)
        if (!file.exists() || !file.canRead()) {
            throw IllegalArgumentException("Could not open the file!")
        }

        return file.readLines()
            .filter { it.isNotBlank() && it != "\n" && it != " " }
    }
}