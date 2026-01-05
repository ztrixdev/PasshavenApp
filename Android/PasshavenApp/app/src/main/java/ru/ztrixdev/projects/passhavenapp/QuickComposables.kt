package ru.ztrixdev.projects.passhavenapp

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object QuickComposables {

    @Composable
    fun BackButtonTitlebar(text: String, onBackButtonClickAction: () -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically, // Center items vertically
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .statusBarsPadding()
                .padding(vertical = 8.dp)
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
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
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
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(8.dp)
                    )
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

    @Composable
    fun uniformTextFieldColors(): TextFieldColors {
        val colors = OutlinedTextFieldDefaults.colors()
        return colors.copy(
            disabledTextColor = colors.focusedTextColor,
            disabledLabelColor = colors.unfocusedLabelColor,
            disabledLeadingIconColor = colors.unfocusedLeadingIconColor,
            disabledTrailingIconColor = colors.unfocusedTrailingIconColor,
            disabledSupportingTextColor = MaterialTheme.colorScheme.onBackground,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            errorSupportingTextColor = MaterialTheme.colorScheme.error,
        )
    }

    @Composable
    fun makeCopiedToast() {
        val localctx = LocalContext.current
        Toast.makeText(
            localctx,
            stringResource(R.string.copied_to_clipboard),
            Toast.LENGTH_SHORT)
            .show()
    }

    @Composable
    fun ThirtySecondsProgressbar(fillMaxWidth: Boolean, callback: () -> Unit) {
        var currentProgress by remember { mutableFloatStateOf(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                for (i in 300 downTo 1  ) {
                    currentProgress = i / 300f
                    delay(100)
                }
                currentProgress = 0f
                callback()
            }
        }
        LinearProgressIndicator(
            progress = { currentProgress },
            modifier =  if (fillMaxWidth) {
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            } else {
                Modifier
                    .widthIn(100.dp, 400.dp)
                    .padding(bottom = 12.dp)
            },
            color = if (currentProgress > 0.2f)
                MaterialTheme.colorScheme.inversePrimary
            else
                MaterialTheme.colorScheme.errorContainer
        )
    }
}