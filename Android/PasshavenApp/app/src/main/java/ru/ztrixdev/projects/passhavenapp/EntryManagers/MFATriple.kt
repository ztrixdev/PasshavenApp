package ru.ztrixdev.projects.passhavenapp.EntryManagers

import kotlin.uuid.Uuid

data class MFATriple (
    val originalUuid: Uuid,
    val name: String,
    val secret: String
)
