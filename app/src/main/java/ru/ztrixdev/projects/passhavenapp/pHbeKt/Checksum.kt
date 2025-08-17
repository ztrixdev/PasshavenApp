package ru.ztrixdev.projects.passhavenapp.pHbeKt

import org.kotlincrypto.hash.sha3.Keccak512
import java.nio.charset.StandardCharsets

class Checksum {
    fun keccak512(input: String): ByteArray {
        val hasher = Keccak512()
        hasher.update(input.toByteArray(StandardCharsets.UTF_8))
        return hasher.digest()
    }
}
