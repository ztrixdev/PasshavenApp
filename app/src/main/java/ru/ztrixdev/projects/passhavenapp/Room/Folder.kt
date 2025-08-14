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
data class Folder (
    @PrimaryKey val uuid: Uuid,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "entries") val entries: List<Uuid>,
)

@Dao
@OptIn(ExperimentalUuidApi::class)
interface FolderDao {
    @Query("select * from folder")
    fun getALl(): List<Folder>

    @Query("select * from folder where uuid like :folderUUID limit 1")
    fun getFolderByUUID(folderUUID: Uuid): Folder

    @Insert
    fun insert(vararg fld: Folder)

    @Update
    fun update(vararg fld: Folder)

    @Delete
    fun delete(vararg fld: Folder)
}

