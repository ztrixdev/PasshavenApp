package ru.ztrixdev.projects.passhavenapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.ztrixdev.projects.passhavenapp.room.dao.AccountDao
import ru.ztrixdev.projects.passhavenapp.room.dao.CardDao
import ru.ztrixdev.projects.passhavenapp.room.dao.FolderDao
import ru.ztrixdev.projects.passhavenapp.room.dao.VaultDao
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Account
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Card
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Folder
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Vault
import ru.ztrixdev.projects.passhavenapp.room.typeConverters.StringListConverter
import ru.ztrixdev.projects.passhavenapp.room.typeConverters.UriConverter
import ru.ztrixdev.projects.passhavenapp.room.typeConverters.UuidConverter

@Database(entities = [Account::class, Card::class, Folder::class, Vault::class], version = 1, exportSchema = true)
@TypeConverters(UuidConverter::class, StringListConverter::class, UriConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun cardDao(): CardDao
    abstract fun folderDao(): FolderDao
    abstract fun vaultDao(): VaultDao
}



