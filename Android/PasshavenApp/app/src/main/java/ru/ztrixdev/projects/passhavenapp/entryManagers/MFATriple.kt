package ru.ztrixdev.projects.passhavenapp.entryManagers

import kotlin.uuid.Uuid

data class MFATriple (
    val originalUuid: Uuid,
    val name: String,
    val secret: String
)


