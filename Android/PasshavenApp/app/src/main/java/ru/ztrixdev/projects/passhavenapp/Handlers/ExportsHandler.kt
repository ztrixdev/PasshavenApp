package ru.ztrixdev.projects.passhavenapp.Handlers

import com.google.gson.Gson
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.Card
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes

object ExportsHandler {
    fun export(template: ExportTemplates, entries: List<Any>, folders: List<Folder>): String {
        when (template) {
            ExportTemplates.Passhaven -> {
                return _exportPH(entries, folders)
            }
            else -> {

            }
        }

        return ""
    }


    private fun _exportPH(entries: List<Any>, folders: List<Folder>): String {
        val export: MutableList<Map<EntryTypes, Any>> = mutableListOf()
        val cards = entries.filter { it is Card }
        val accounts = entries.filter { it is Account }

        export.add(mapOf(EntryTypes.Folder to folders))
        export.add(mapOf(EntryTypes.Card to cards))
        export.add(mapOf(EntryTypes.Account to accounts))

        return Gson().toJson(export)
    }
}