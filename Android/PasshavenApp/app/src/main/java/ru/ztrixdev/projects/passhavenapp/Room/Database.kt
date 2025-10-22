package ru.ztrixdev.projects.passhavenapp.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.ztrixdev.projects.passhavenapp.Room.Dao.AccountDao
import ru.ztrixdev.projects.passhavenapp.Room.Dao.CardDao
import ru.ztrixdev.projects.passhavenapp.Room.Dao.FolderDao
import ru.ztrixdev.projects.passhavenapp.Room.Dao.VaultDao
import ru.ztrixdev.projects.passhavenapp.Room.TypeConverters.StringListConverter
import ru.ztrixdev.projects.passhavenapp.Room.TypeConverters.UriConverter
import ru.ztrixdev.projects.passhavenapp.Room.TypeConverters.UuidConverter

@Database(entities = [Account::class, Card::class, Folder::class, Vault::class], version = 1, exportSchema = true)
@TypeConverters(UuidConverter::class, StringListConverter::class, UriConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun cardDao(): CardDao
    abstract fun folderDao(): FolderDao
    abstract fun vaultDao(): VaultDao
}



