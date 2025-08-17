package ru.ztrixdev.projects.passhavenapp.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.ztrixdev.projects.passhavenapp.Room.TypeConverters.UuidConverter
import ru.ztrixdev.projects.passhavenapp.Room.TypeConverters.StringListConverter

val db_name = "pHdb"

@Database(entities = [Account::class, Card::class, Folder::class, Vault::class], version = 1)
@TypeConverters(UuidConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun cardDao(): CardDao
    abstract fun folderDao(): FolderDao
    abstract fun vaultDao(): VaultDao
}

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                db_name
            ).build()
        }
        return instance!!
    }
}


