package ru.ztrixdev.projects.passhavenapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.ztrixdev.projects.passhavenapp.entryManagers.SortingKeys
import ru.ztrixdev.projects.passhavenapp.handlers.SessionHandler
import ru.ztrixdev.projects.passhavenapp.preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.QuickComposables
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.room.Account
import ru.ztrixdev.projects.passhavenapp.room.Card
import ru.ztrixdev.projects.passhavenapp.viewModels.NewFolderViewModel
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme

class NewFolderActivity : ComponentActivity() {

    override fun onResume() {
        super.onResume()
        if (SessionHandler.isSessionExpired(this.applicationContext)) {
            val intent = Intent(this.applicationContext, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.applicationContext.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val newFolderViewModel: NewFolderViewModel by viewModels()

        super.onCreate(savedInstanceState)
        setContent {
            val localctx = LocalContext.current
            // Load initial data
            LaunchedEffect(Unit) {
                newFolderViewModel.loadEntries(localctx)
                newFolderViewModel.sortEntries()
            }
            // Navigate away when the folder is successfully created
            LaunchedEffect(newFolderViewModel.folderCreated.value) {
                if (newFolderViewModel.folderCreated.value) {
                    val intent = Intent(this@NewFolderActivity, VaultOverviewActivity::class.java)
                    this@NewFolderActivity.startActivity(intent)
                }
            }

            PasshavenTheme(
                themeType = ThemePrefs.getSelectedTheme(localctx),
                darkTheme = ThemePrefs.getDarkThemeBool(localctx),
                dynamicColors = ThemePrefs.getDynamicColorsBool(localctx),
            ) {
                Scaffold(
                    topBar = {
                        QuickComposables.BackButtonTitlebar(stringResource(R.string.newfolderactivity_titlebar)) {
                            val intent = Intent(this@NewFolderActivity, VaultOverviewActivity::class.java)
                            this@NewFolderActivity.startActivity(intent)
                        }
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            text = { Text(stringResource(R.string.create_folder)) },
                            icon =  { Icon(painter = painterResource(id = R.drawable.add_24px), contentDescription = stringResource(R.string.create_folder)) },
                            onClick = {
                                lifecycleScope.launch {
                                    newFolderViewModel.createFolder(localctx)
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        MainBody(newFolderViewModel)
                    }
                }
            }
        }
    }

    @Composable
    private fun MainBody(newFolderViewModel: NewFolderViewModel) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                NameTextField(newFolderViewModel = newFolderViewModel)
            }

            item {
                Column {
                    Text(
                        text = stringResource(R.string.sort_and_select),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SortingKeySelectionDropdown(newFolderViewModel = newFolderViewModel)
                        ReverseSortingCheckbox(newFolderViewModel = newFolderViewModel)
                    }
                }
            }

            items(
                items = newFolderViewModel.entries,
                key = { entry ->
                    when (entry) {
                        is Card -> entry.uuid
                        is Account -> entry.uuid
                        else -> "unknown"
                    }
                }
            ) { entry ->
                EntryRow(entry = entry, newFolderViewModel = newFolderViewModel)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    @Composable
    private fun EntryRow(entry: Any, newFolderViewModel: NewFolderViewModel) {
        val entryId = when (entry) {
            is Card -> entry.uuid
            is Account -> entry.uuid
            else -> return
        }

        val isChecked = remember { mutableStateOf(newFolderViewModel.getIncludedUuids().contains(entryId)) }

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                isChecked.value = !isChecked.value
                if (isChecked.value) {
                    newFolderViewModel.includeEntry(entryId)
                } else {
                    newFolderViewModel.removeEntry(entryId)
                }
            },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconRes = when (entry) {
                    is Card -> R.drawable.credit_card_24px
                    is Account -> R.drawable.person_24px
                    else -> R.drawable.browse_24px
                }
                Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = MaterialTheme.colorScheme.primary)

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    val name = when (entry) {
                        is Card -> entry.name
                        is Account -> entry.name
                        else -> "N/A"
                    }
                    val type = when (entry) {
                        is Card -> stringResource(R.string.card)
                        is Account -> stringResource(R.string.account)
                        else -> "N/A"
                    }
                    Text(text = name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = type, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = { checked ->
                        isChecked.value = checked
                        if (checked) {
                            newFolderViewModel.includeEntry(entryId)
                        } else {
                            newFolderViewModel.removeEntry(entryId)
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun NameTextField(newFolderViewModel: NewFolderViewModel) {
        OutlinedTextField(
            value = newFolderViewModel.newFolderName.value,
            onValueChange = { newFolderViewModel.newFolderName.value = it },
            label = { Text(text = stringResource(R.string.folder_name_label)) },
            placeholder = { Text(text = stringResource(R.string.folder_name_placeholder)) },
            isError = newFolderViewModel.newFolderName.value.text.isEmpty(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    private fun SortingKeySelectionDropdown(newFolderViewModel: NewFolderViewModel) {
        var isExpanded by remember { mutableStateOf(false) }
        val sortingKeyStringsToEntryTypes = mapOf(
            stringResource(R.string.sort_by_alphabet) to SortingKeys.ByAlphabet,
            stringResource(R.string.sort_by_date) to SortingKeys.ByDate,
            stringResource(R.string.sort_by_type) to SortingKeys.ByType
        )
        val selectedText = sortingKeyStringsToEntryTypes.entries.find { it.value == newFolderViewModel._selectedSortingKey.value }?.key ?: ""

        Box {
            Row(
                modifier = Modifier.clickable { isExpanded = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_drop_down_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(text = selectedText, color = MaterialTheme.colorScheme.primary)
            }
            DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                sortingKeyStringsToEntryTypes.forEach { (text, key) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            newFolderViewModel.setSelectedSortingKey(key)
                            newFolderViewModel.sortEntries()
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun ReverseSortingCheckbox(newFolderViewModel: NewFolderViewModel) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                newFolderViewModel.reversedSorting.value = !newFolderViewModel.reversedSorting.value
                newFolderViewModel.sortEntries()
            }
        ) {
            Checkbox(
                checked = newFolderViewModel.reversedSorting.value,
                onCheckedChange = {
                    newFolderViewModel.reversedSorting.value = it
                    newFolderViewModel.sortEntries()
                }
            )
            Text(text = stringResource(R.string.reverse_sorting))
        }
    }
}

