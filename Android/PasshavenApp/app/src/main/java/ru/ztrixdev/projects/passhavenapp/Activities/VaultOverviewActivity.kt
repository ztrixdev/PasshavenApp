package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.ztrixdev.projects.passhavenapp.EntryManagers.EntryManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.Handlers.ExportTemplates
import ru.ztrixdev.projects.passhavenapp.Handlers.ExportsHandler
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Folder

class VaultOverviewActivity: ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent()
        {
            // this some raw shii, don't mind it, its really ugly
            MaterialTheme {
                var gotoNEA by remember { mutableStateOf(false) }
                var gotoNFA by remember { mutableStateOf(false) }
                val ctx = LocalContext.current

                LaunchedEffect(gotoNEA) {
                    if (gotoNEA) {
                        ctx .startActivity(
                            Intent(ctx, NewEntryActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                }
                LaunchedEffect(gotoNFA) {
                    if (gotoNFA) {
                        ctx .startActivity(
                            Intent(ctx, NewFolderActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                }
                Column(
                    Modifier.verticalScroll(rememberScrollState())
                ){
                    val localctx = LocalContext.current
                    val db = DatabaseProvider.getDatabase(LocalContext.current)
                    var key = byteArrayOf()
                    val folders = remember { mutableStateListOf<Folder>() }
                    val entries = remember { mutableStateListOf<Any>() }
                    val isABackupDue = remember { mutableStateOf<Boolean>(false) }
                    val areAllJobsDone = remember { mutableStateOf<Boolean>(false) }
                    LaunchedEffect(Unit) {
                        key = VaultHandler().getEncryptionKey(localctx)
                        folders.addAll(FolderManager.getFolders(localctx))
                        entries.addAll(EntryManager.getAllEntriesForUI(db, key))
                        isABackupDue.value = ExportsHandler.checkIfABackupIsDue(localctx)
                        areAllJobsDone.value = true
                    }

                    if (areAllJobsDone.value) {
                        Text(text = entries.toString(), color = Color.White, style = TextStyle.Default)
                        val export = ExportsHandler.getExport(ExportTemplates.Passhaven, entries, folders)
                        Text(text = export, color = Color.White, style = TextStyle.Default)
                        Text(text = "Is a backup due atm: ${isABackupDue.value}", color = Color.White, style = TextStyle.Default)
                        val resolver = this@VaultOverviewActivity.contentResolver
                        val launcher = rememberDirectoryPickerLauncher { directory ->
                            GlobalScope.launch(Dispatchers.IO) {
                                val uri = directory.toString().toUri()
                                VaultHandler().setBackupFolder(uri, localctx)
                                ExportsHandler.exportToFolder(resolver,export, localctx)
                            }
                        }
                        Button(
                            onClick = {
                                gotoNEA = true
                            },
                            modifier = Modifier.padding(all = 30.dp)
                        ) {
                            Text("Go to New Entry Activity")
                        }
                        Button(
                            onClick = {
                                launcher.launch()
                            },
                            modifier = Modifier.padding(all = 30.dp)
                        ) {
                            Text("Export vault to folder")
                        }
                        Button(
                            onClick = {
                                gotoNFA = true
                            },
                            modifier = Modifier.padding(all = 30.dp)
                        ) {
                            Text("Go to New Folder Activity")
                        }
                    }
                    else {
                        Text(text = "Please wait until your vault finishes loading.", color = Color.White, style = TextStyle.Default)
                    }
                }
            }
        }
    }
}
