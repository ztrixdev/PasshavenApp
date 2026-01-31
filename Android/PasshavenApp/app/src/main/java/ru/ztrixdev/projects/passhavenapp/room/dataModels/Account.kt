package ru.ztrixdev.projects.passhavenapp.room.dataModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
@OptIn(ExperimentalUuidApi::class)
data class Account (
    @PrimaryKey var uuid: Uuid,
    @ColumnInfo(name = "reprompt") var reprompt: Boolean,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "mfa_secret") var mfaSecret: String?,
    @ColumnInfo(name = "recovery_codes") var recoveryCodes: List<String>?,
    @ColumnInfo(name = "additional_note") var additionalNote: String?,
    @ColumnInfo(name = "date_created") var dateCreated: Long
)