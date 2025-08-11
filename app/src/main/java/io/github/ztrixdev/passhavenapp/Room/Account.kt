package io.github.ztrixdev.passhavenapp.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
@OptIn(ExperimentalUuidApi::class)
data class Account(
    @PrimaryKey val uuid: Uuid,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(type)
)
