package ru.ztrixdev.projects.passhavenapp.pHbeKt

import android.app.Activity
import android.content.Context

import java.io.FileOutputStream
import java.io.OutputStreamWriter
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class Keyfile {
    val transformation: String = "AES/GCM/NoPadding"

    fun write(encryptedKnSPair: Map<CryptoNames, ByteArray>, activity: Activity) {
        val fileOutputStream: FileOutputStream = activity.openFileOutput(CryptoNames.keyfile.toString(), Context.MODE_PRIVATE)
        val outputWriter = OutputStreamWriter(fileOutputStream)
        outputWriter.write(encryptedKnSPair.toString())
        outputWriter.close()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun encryptKnSPair(unencryptedKey: String?, unencryptedSalt: String?, secretKey: SecretKey?): Map<CryptoNames, ByteArray> {
        if (unencryptedSalt == null || unencryptedKey == null || secretKey == null) {
            throw IllegalArgumentException("Arguments shouldn't be null.")
        }

        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val plaintext = unencryptedSalt + unencryptedKey
        val cipherText = cipher.doFinal(plaintext.toByteArray())
        return mapOf(CryptoNames.cipher to cipherText, CryptoNames.iv to cipher.iv)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun decryptKnSPair(encryptionResults: Map<CryptoNames, ByteArray>?, secretKey: SecretKey?): ByteArray {
        if (encryptionResults == null || encryptionResults[CryptoNames.cipher] == null || encryptionResults[CryptoNames.iv] == null || secretKey == null) {
            throw IllegalArgumentException("Arguments shouldn't be null.")
        }

        val iv = encryptionResults[CryptoNames.iv]
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

        val cipherBytes = encryptionResults[CryptoNames.cipher]
        val decrypted = cipher.doFinal(cipherBytes)
        return decrypted
    }
}
