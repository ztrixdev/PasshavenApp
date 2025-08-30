package ru.ztrixdev.projects.passhavenapp.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.ztrixdev.projects.passhavenapp.Room.TypeConverters.StringListConverter
import ru.ztrixdev.projects.passhavenapp.Room.TypeConverters.UuidConverter

val db_name = "pHdb"

@Database(entities = [Account::class, Card::class, Folder::class, Vault::class], version = 1, exportSchema = true)
@TypeConverters(UuidConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun cardDao(): CardDao
    abstract fun folderDao(): FolderDao
    abstract fun vaultDao(): VaultDao
}



