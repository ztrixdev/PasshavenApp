package ru.ztrixdev.projects.passhavenapp.EntryManagers

import android.content.Context
import ru.ztrixdev.projects.passhavenapp.Room.AppDatabase
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import kotlin.uuid.Uuid

object FolderManager {
    enum class EntryFolderOperations {
        Add, Remove, Contains
    }

    enum class EntryFolderOperationResults {
        Error_TargetFolderDoesntExist,
        Error_TargetFolderAlreadyContainsTheEntry,
        Error_EntryInQuestionDoesntExist,
        Error_CannotAddAFolderToAFolder,
        Error_CannotRemoveAnEntryThatIsntInTheTargetFolder,
        TargetFolderDoesntContainTheEntry,
        Success
    }

    fun performEntryFolderOper(operation: EntryFolderOperations, database: AppDatabase, entryUuid: Uuid, targetFolderUuid: Uuid): EntryFolderOperationResults {
        val dao = database.folderDao()

        val targetFolder = dao.getFolderByUuid(targetFolderUuid)
        val newObject = EntryManager.getEntryByUuid(database, soughtUuid = entryUuid)
        if (targetFolder == null)
            return EntryFolderOperationResults.Error_TargetFolderDoesntExist
        if (newObject == null)
            return EntryFolderOperationResults.Error_EntryInQuestionDoesntExist
        if (newObject is Folder)
            return EntryFolderOperationResults.Error_CannotAddAFolderToAFolder

        val updatedEntryList: MutableList<Uuid> = targetFolder.entries.toMutableList()
        when (operation) {
            EntryFolderOperations.Add -> {
                if (targetFolder.entries.contains(entryUuid))
                    return EntryFolderOperationResults.Error_TargetFolderAlreadyContainsTheEntry

                updatedEntryList.add(entryUuid)
            }
            EntryFolderOperations.Remove -> {
                if (!targetFolder.entries.contains(entryUuid))
                    return EntryFolderOperationResults.Error_CannotRemoveAnEntryThatIsntInTheTargetFolder

                updatedEntryList.remove(entryUuid)
            }
            EntryFolderOperations.Contains -> {
                if (!targetFolder.entries.contains(entryUuid))
                    return EntryFolderOperationResults.TargetFolderDoesntContainTheEntry
                return EntryFolderOperationResults.Success
            }
        }

        dao.resetEntryList(
            newEntryList = updatedEntryList.toList(),
            folderUUID = targetFolderUuid
        )
        return EntryFolderOperationResults.Success
    }

    fun getFolders(context: Context): List<Folder> {
        val db = DatabaseProvider.getDatabase(context = context)
        return db.folderDao().getALl()
    }

    fun getFolderByUuid(context: Context, uuid: Uuid): Folder? {
        val db = DatabaseProvider.getDatabase(context = context)
        return db.folderDao().getFolderByUuid(uuid)
    }

    fun createFolder(db: AppDatabase, newFolder: Folder) {
        db.folderDao().insert(newFolder)
    }
}

