package ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto

import com.goterl.lazysodium.interfaces.SecretBox
import com.goterl.lazysodium.utils.Key
import ru.ztrixdev.projects.passhavenapp.pHbeKt.NONCE_HEXED_LENGTH

object SodiumCrypto {
    val sodium = SodiumHelper.getSodium()

    fun encrypt(blob: String, key: ByteArray): String {
        val nonce = sodium.randomBytesBuf(SecretBox.NONCEBYTES)
        val encrypted = sodium.cryptoSecretBoxEasy(blob, nonce, Key.fromBytes(key))
        return encrypted + sodium.sodiumBin2Hex(nonce)
    }

    fun decrypt(cipherText: String, key: ByteArray): String {
        val invalidCipherTextException =
            IllegalArgumentException("I don't think the passed argument is valid ciphertext. A nonce appended to the end of the ciphertext is exactly 48 characters long. It should also have the cipher itself. Double check your ciphertext and try again")
        if (cipherText.length < NONCE_HEXED_LENGTH + 2) {
            throw invalidCipherTextException
        }
        val nonce = cipherText.substring(cipherText.length - NONCE_HEXED_LENGTH, cipherText.length)
        val text = cipherText.dropLast(NONCE_HEXED_LENGTH)

        val decrypted =
            sodium.cryptoSecretBoxOpenEasy(text, sodium.sodiumHex2Bin(nonce), Key.fromBytes(key))
        return decrypted
    }
}