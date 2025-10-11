package ru.ztrixdev.projects.passhavenapp.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Crypto.SodiumCrypto
import java.util.Date
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
@OptIn(ExperimentalUuidApi::class)
data class Card (
    @PrimaryKey var uuid: Uuid,
    @ColumnInfo(name = "reprompt") var reprompt: Boolean,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "number") var number: String,
    @ColumnInfo(name = "expiration_date") var expirationDate: String,
    @ColumnInfo(name = "cvc_cvv") var cvcCvv: String,
    @ColumnInfo(name = "brand") var brand: String,
    @ColumnInfo(name = "cardholder") var cardholder: String,
    @ColumnInfo(name = "additional_note") var additionalNote: String?,
    @ColumnInfo(name = "date_created") var dateCreated: Long
)

fun Card.encrypt(key: ByteArray) {
    name = SodiumCrypto.encrypt(name, key)
    number = SodiumCrypto.encrypt(number, key)
    expirationDate = SodiumCrypto.encrypt(expirationDate, key)
    cvcCvv = SodiumCrypto.encrypt(cvcCvv, key)
    brand = SodiumCrypto.encrypt(brand, key)
    cardholder = SodiumCrypto.encrypt(cardholder, key)
    if (additionalNote != null)
        additionalNote = SodiumCrypto.encrypt(additionalNote as String, key)
}

fun Card.decrypt(key: ByteArray) {
    name = SodiumCrypto.decrypt(name, key)
    number = SodiumCrypto.decrypt(number, key)
    expirationDate = SodiumCrypto.decrypt(expirationDate, key)
    cvcCvv = SodiumCrypto.decrypt(cvcCvv, key)
    brand = SodiumCrypto.decrypt(brand, key)
    cardholder = SodiumCrypto.decrypt(cardholder, key)
    if (additionalNote != null)
        additionalNote = SodiumCrypto.decrypt(additionalNote as String, key)
}

