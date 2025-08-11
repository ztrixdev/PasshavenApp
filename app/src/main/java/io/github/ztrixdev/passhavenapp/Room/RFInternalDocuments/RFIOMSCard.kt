package io.github.ztrixdev.passhavenapp.Room.RFInternalDocuments

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
@OptIn(ExperimentalUuidApi::class)
data class OMSCard(
    @PrimaryKey val uuid: Uuid,
    @ColumnInfo(name = "number") val number: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "patronymic") val patronymic: String?,
    @ColumnInfo(name = "birth_date") val birthDate: String,
    @ColumnInfo(name = "issuer") val issuer: String,
    @ColumnInfo(name = "issue_date") val issueDate: String,
    @ColumnInfo(name = "expiration_date") val expirationDate: String?
)