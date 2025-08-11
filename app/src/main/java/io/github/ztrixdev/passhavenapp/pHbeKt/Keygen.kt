package io.github.ztrixdev.passhavenapp.pHbeKt

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.goterl.lazysodium.interfaces.PwHash
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class Keygen {
    @OptIn(ExperimentalStdlibApi::class)
    fun deriveKeySaltPairFromMP(password: String): Map<CryptoNames, ByteArray> {
        val lazySodium = SodiumHelper().getSodium()

        val key = ByteArray(32)
        val salt = lazySodium.randomBytesBuf(PwHash.SALTBYTES)

        val computingSuccess = lazySodium.cryptoPwHash(
            key,
            32,
            password.toByteArray(),
            password.length,
            salt,
            PwHash.OPSLIMIT_SENSITIVE,
            PwHash.MEMLIMIT_SENSITIVE,
            PwHash.Alg.PWHASH_ALG_ARGON2I13
        )
        if (!computingSuccess)
            throw RuntimeException("Couldn't compute a key!")

        return mapOf(CryptoNames.key to key, CryptoNames.salt to salt)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getKeyWithMPnSalt(password: String, salt: ByteArray): ByteArray {
        val lazySodium = SodiumHelper().getSodium()

        if (salt.size != PwHash.SALTBYTES) {
            throw IllegalArgumentException("Salt length should be ${PwHash.SALTBYTES} bytes long.")
        }

        val key = ByteArray(32)
        val computingSuccess = lazySodium.cryptoPwHash(
            key,
            32,
            password.toByteArray(),
            password.length,
            salt,
            PwHash.OPSLIMIT_SENSITIVE,
            PwHash.MEMLIMIT_SENSITIVE,
            PwHash.Alg.PWHASH_ALG_ARGON2I13
        )
        if (!computingSuccess)
            throw RuntimeException("Couldn't compute a key!")

        return key
    }

    fun generateAndroidKey(alias: String) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val builder = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        }

        keyGenerator.init(builder.build())
        keyGenerator.generateKey()
    }
}



