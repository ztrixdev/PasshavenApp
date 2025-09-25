package ru.ztrixdev.projects.passhavenapp.pHbeKt.Crypto

import com.goterl.lazysodium.LazySodiumAndroid
import com.goterl.lazysodium.SodiumAndroid

object SodiumHelper {
    fun getSodium(): LazySodiumAndroid {
        val sodium = SodiumAndroid()
        if (sodium.sodium_init() < 0)
            throw RuntimeException("Couldn't initialize sodium!")

        val lazySodium = LazySodiumAndroid(sodium)
        if (lazySodium.sodiumInit() < 0)
            throw RuntimeException("Couldn't initialize lazySodium!")

        return lazySodium
    }
}