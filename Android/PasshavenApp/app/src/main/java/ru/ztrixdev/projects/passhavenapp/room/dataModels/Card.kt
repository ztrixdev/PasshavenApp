package ru.ztrixdev.projects.passhavenapp.room.dataModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.SodiumCrypto
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
