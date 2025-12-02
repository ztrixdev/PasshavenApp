package ru.ztrixdev.projects.passhavenapp

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object QuickComposables {
    @Composable
    fun Titlebar(text: String, onBackButtonClickAction: () -> Unit) {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .padding(all = 10.dp)
        ) {
            IconButton(
                onClick = onBackButtonClickAction,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "An arrow facing backwards, damnit",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(start = 40.dp)
            )
        }
    }

    @Composable
    fun FolderNameFromUri(uri: Uri) {
        val localctx = LocalContext.current
        val contentResolver = localctx.contentResolver

        var folderName by remember(uri) {
            mutableStateOf<String?>(null)
        }

        LaunchedEffect(uri) {
            launch(Dispatchers.IO) {
                val resolvedName = getFolderName(uri, localctx, contentResolver)
                withContext(Dispatchers.Main) {
                    folderName = resolvedName
                }
            }
        }

        Text(
            text = folderName ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }

    private fun getFolderName(uri: Uri, context: Context, contentResolver: ContentResolver): String? {
        // Don't try to query an empty or invalid URI
        if (uri.toString().isEmpty() || uri == "".toUri()) {
            return null
        }

        val docUri = DocumentFile.fromTreeUri(context, uri)
        if (docUri == null) {
            return null
        }

        val cursor = contentResolver.query(
            docUri.uri,
            arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME), // We only need the display name
            null,
            null,
            null
        )

        var displayName: String? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (nameIndex != -1) {
                    displayName = it.getString(nameIndex)
                }
            }
        }
        return displayName
    }

    @Composable
    fun WaitingDialog(text: String) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment= Center,
                modifier = Modifier
                    .width(296.dp)
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}