package ru.ztrixdev.projects.passhavenapp.pHbeKt.Crypto

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object AndroidCrypto {
    private const val transformation: String = "AES/GCM/NoPadding"

    @OptIn(ExperimentalStdlibApi::class)
    fun encrypt(unencryptedData: ByteArray?, secretKey: SecretKey?): Map<CryptoNames, ByteArray> {
        if (unencryptedData == null || secretKey == null) {
            throw IllegalArgumentException("Arguments shouldn't be null.")
        }

        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val cipherText = cipher.doFinal(unencryptedData)
        return mapOf(CryptoNames.cipher to cipherText, CryptoNames.iv to cipher.iv)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun decrypt(encryptionResults: Map<CryptoNames, ByteArray>?, secretKey: SecretKey?): ByteArray {
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
