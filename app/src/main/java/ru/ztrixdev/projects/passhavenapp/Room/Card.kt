package ru.ztrixdev.projects.passhavenapp.Room

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
@OptIn(ExperimentalUuidApi::class)
data class Card (
    @PrimaryKey val uuid: Uuid,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "is_second_layer_protected") val isSLPd: Boolean,
    @ColumnInfo(name = "mp_reprompt") val mpReprompt: Boolean?,
    @ColumnInfo(name = "second_layer_salt") val SLS: String?,
    @ColumnInfo(name = "number") val number: String,
    @ColumnInfo(name = "expiration_date") val expirationDate: String,
    @ColumnInfo(name = "cvc_cvv") val cvcCvv: String,
    @ColumnInfo(name = "brand") val brand: String,
    @ColumnInfo(name = "cardholder") val cardholder: String,
    @ColumnInfo(name = "additional_note") val additionalNote: String?
)

@Dao
@OptIn(ExperimentalUuidApi::class)
interface CardDao {
    @Query("select * from card")
    fun getALl(): List<Card>

    @Query("select * from card where uuid like :cardUUID limit 1")
    fun getFolderByUUID(cardUUID: Uuid): Card

    @Insert
    fun insert(vararg crd: Card)

    @Update
    fun update(vararg crd: Card)

    @Delete
    fun delete(vararg crd: Card)
}

