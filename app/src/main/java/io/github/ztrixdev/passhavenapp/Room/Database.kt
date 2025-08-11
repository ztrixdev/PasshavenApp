package io.github.ztrixdev.passhavenapp.Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Account::class, Card::class, Folder::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun cardDao(): CardDao
    abstract fun folderDao(): FolderDao
}



