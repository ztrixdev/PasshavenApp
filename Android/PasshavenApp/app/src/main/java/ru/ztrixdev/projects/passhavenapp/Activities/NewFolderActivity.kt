package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.ztrixdev.projects.passhavenapp.EntryManagers.SortingKeys
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes
import ru.ztrixdev.projects.passhavenapp.ViewModels.NewEntryViewModel
import ru.ztrixdev.projects.passhavenapp.ViewModels.NewFolderViewModel

class NewFolderActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    @Preview
    @Composable
    private fun Titlebar() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            IconButton(
                onClick = {
                    val intent = Intent(this@NewFolderActivity, VaultOverviewActivity::class.java)
                    this@NewFolderActivity.startActivity(intent)
                },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    // f#ck me sideways, I hate content descriptions, as if we still have phones that can't withstand a freaking ICON.
                    // however it is probably mostly used so that devs don't get lost in their drawables. I don't know what happened to cmd+click but ok, ig?
                    contentDescription = "An arrow facing backwards, damnit",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = stringResource(R.string.newfolderactivity_titlebar),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(start = 40.dp)
            )
        }
    }

    @Composable
    private fun NameTextField(newFolderViewModel: NewFolderViewModel) {
        var foldernameIsEmptyProblem by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = newFolderViewModel.newFolderName.value,
            onValueChange = { it ->
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
                .padding(top = 16.dp),
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
                    modifier = Modifier.clickable {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCheckboxTicked.value,
                onCheckedChange = {
                    isCheckboxTicked.value = it
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

}