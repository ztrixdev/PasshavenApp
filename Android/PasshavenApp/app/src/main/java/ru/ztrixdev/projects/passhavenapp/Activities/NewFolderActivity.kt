package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import ru.ztrixdev.projects.passhavenapp.DateTimeProcessor
import ru.ztrixdev.projects.passhavenapp.EntryManagers.SortingKeys
import ru.ztrixdev.projects.passhavenapp.Preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.QuickComposables
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.Card
import ru.ztrixdev.projects.passhavenapp.ViewModels.NewFolderViewModel
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme

class NewFolderActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val newFolderViewModel: NewFolderViewModel by viewModels()

        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContent {
            val localctx = LocalContext.current
            LaunchedEffect(Unit) {
                newFolderViewModel.loadEntries(localctx)
                newFolderViewModel.sortEntries()
            }
            LaunchedEffect(newFolderViewModel.folderCreated.value) {
                if (newFolderViewModel.folderCreated.value) {
                    val intent = Intent(this@NewFolderActivity, VaultOverviewActivity::class.java)
                    this@NewFolderActivity.startActivity(intent)
                }
            }

            PasshavenTheme(
                themeType = ThemePrefs.getSelectedTheme(LocalContext.current),
                darkTheme = ThemePrefs.getDarkThemeBool(LocalContext.current),
                dynamicColors = ThemePrefs.getDynamicColorsBool(LocalContext.current),
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    MainBody(newFolderViewModel)
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    private fun MainBody(newFolderViewModel: NewFolderViewModel) {
        QuickComposables.Titlebar(stringResource(R.string.newfolderactivity_titlebar)) {
            val intent = Intent(this@NewFolderActivity, VaultOverviewActivity::class.java)
            this@NewFolderActivity.startActivity(intent)
        }
        Spacer(
            modifier = Modifier.height(20.dp)
        )
        NameTextField(newFolderViewModel = newFolderViewModel)
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Text(
            text = stringResource(R.string.sort),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(start = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortingKeySelectionDropdown(newFolderViewModel = newFolderViewModel)
            ReverseSortingCheckbox(newFolderViewModel = newFolderViewModel)
        }
        Spacer(
            modifier = Modifier.height(12.dp)
        )
        EntryTable(newFolderViewModel = newFolderViewModel)
        val localctx = LocalContext.current
        Button(
            onClick = {
                lifecycleScope.launch {
                    newFolderViewModel.createFolder(localctx)
                    newFolderViewModel.folderCreated.value = true
                }
            },
            enabled = newFolderViewModel.newFolderName.value.text.isNotEmpty(),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.DarkGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                )
        ) {
            Text(stringResource(R.string.continue_button))
        }
        Spacer(
            modifier = Modifier.height(80.dp)
        )
    }

    @Composable
    private fun NameTextField(newFolderViewModel: NewFolderViewModel) {
        var foldernameIsEmptyProblem by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = newFolderViewModel.newFolderName.value,
            onValueChange = {
                foldernameIsEmptyProblem = it.text.isEmpty()
                newFolderViewModel.newFolderName.value = it
            },
            label = {
                Text(text = stringResource(R.string.folder_name_label))
            },
            placeholder = {
                Text(text = stringResource(R.string.folder_name_placeholder))
            },
            isError = foldernameIsEmptyProblem,
            singleLine = true,
            supportingText = @Composable {
                if (foldernameIsEmptyProblem)
                    Text(text = stringResource(R.string.fill_this_field_up_pls),
                        style = MaterialTheme.typography.bodySmall)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                errorSupportingTextColor = MaterialTheme.colorScheme.error,
            )
        )
    }

    @Composable
    private fun SortingKeySelectionDropdown(newFolderViewModel: NewFolderViewModel) {
        val isDropDownExpanded = remember {
            mutableStateOf(false)
        }

        val itemPosition = remember {
            mutableIntStateOf(0)
        }

        val sortingKeyStringsToEntryTypes = mapOf(
            stringResource(R.string.sort_by_alphabet) to SortingKeys.ByAlphabet,
            stringResource(R.string.sort_by_date) to SortingKeys.ByDate,
            stringResource(R.string.sort_by_type) to SortingKeys.ByType
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .clickable {
                            isDropDownExpanded.value = true
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_drop_down_24px),
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        contentDescription = "Dropdown arrow lol :3"
                    )
                    Text(
                        text = sortingKeyStringsToEntryTypes.keys.toList()[itemPosition.intValue],
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                DropdownMenu(
                    expanded = isDropDownExpanded.value,
                    onDismissRequest = {
                        isDropDownExpanded.value = false
                    }) {
                    sortingKeyStringsToEntryTypes.keys.forEachIndexed { index, typeString ->
                        DropdownMenuItem(text = {
                            Text(text = typeString)
                        },
                            onClick = {
                                isDropDownExpanded.value = false
                                itemPosition.intValue = index
                                newFolderViewModel.setSelectedSortingKey(sortingKeyStringsToEntryTypes[sortingKeyStringsToEntryTypes.keys.toList()[itemPosition.intValue]] as SortingKeys)
                                newFolderViewModel.sortEntries()
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ReverseSortingCheckbox(newFolderViewModel: NewFolderViewModel)  {
        val isCheckboxTicked = remember {
            mutableStateOf(false)
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                checked = isCheckboxTicked.value,
                onCheckedChange = {
                    isCheckboxTicked.value = it
                    newFolderViewModel.reversedSorting.value = it
                    newFolderViewModel.sortEntries()
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.reverse_sorting),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }

    // fixed the bug when checkboxes would shuffle after changing the sorting key
    @Composable
    private fun EntryTable(newFolderViewModel: NewFolderViewModel) {
        val includedUuids = newFolderViewModel.getIncludedUuids()

        Column(
            Modifier
                .heightIn(320.dp)
                .verticalScroll(rememberScrollState())
        ) {
            newFolderViewModel.entries.forEach { entry ->
                val entryId = when (entry) {
                    is Card -> entry.uuid
                    is Account -> entry.uuid
                    else -> null
                } ?: return@forEach

                key(entryId) {
                    val isChecked = remember { mutableStateOf(includedUuids.contains(entryId)) }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        Checkbox(
                            checked = isChecked.value,
                            onCheckedChange = {
                                isChecked.value = it
                                if (it) {
                                    newFolderViewModel.includeEntry(entryId)
                                } else {
                                    newFolderViewModel.removeEntry(entryId)
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text =
                                when (entry) {
                                    is Card -> entry.name
                                    is Account -> entry.name
                                    else -> "ERR_UNRECOGNIZABLE_TYPE"
                                },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    top = 8.dp
                                )
                                .width(56.dp)
                        )
                        Text(
                            text =
                                when (entry) {
                                    is Card -> stringResource(R.string.card)
                                    is Account -> stringResource(R.string.account)
                                    else -> "ERR_UNRECOGNIZABLE_TYPE"
                                },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    top = 8.dp
                                )
                                .width(56.dp)
                        )
                        Text(
                            text =
                                when (entry) {
                                    is Card -> entry.number
                                    is Account -> entry.username
                                    else -> "ERR_UNRECOGNIZABLE_TYPE"
                                },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    top = 8.dp
                                )
                                .width(64.dp)
                        )
                        Text(
                            text =
                                when (entry) {
                                    is Card -> DateTimeProcessor.convertToHumanReadable(entry.dateCreated)
                                    is Account -> DateTimeProcessor.convertToHumanReadable(entry.dateCreated)
                                    else -> "ERR_UNRECOGNIZABLE_TYPE"
                                },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    top = 8.dp
                                )
                                .width(96.dp)
                        )
                    }
                }
            }
        }
    }
}
