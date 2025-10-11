package ru.ztrixdev.projects.passhavenapp.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
@OptIn(ExperimentalUuidApi::class)
data class Folder (
    @PrimaryKey val uuid: Uuid,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "entries") var entries: List<Uuid>,
    @ColumnInfo(name = "date_created") var dateCreated: Long
)
