package ru.ztrixdev.projects.passhavenapp.Room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
@OptIn(ExperimentalUuidApi::class)
interface FolderDao {
    @Query("select * from folder")
    fun getALl(): List<Folder>

    @Query("select * from folder where uuid like :folderUuid limit 1")
    fun getFolderByUuid(folderUuid: Uuid): Folder?

    @Query("update folder set entries=:newEntryList where uuid like :folderUUID")
    fun resetEntryList(newEntryList: List<Uuid>, folderUUID: Uuid)

    @Insert
    fun insert(vararg fld: Folder)

    @Update
    fun update(vararg fld: Folder)

    @Delete
    fun delete(vararg fld: Folder)
}
