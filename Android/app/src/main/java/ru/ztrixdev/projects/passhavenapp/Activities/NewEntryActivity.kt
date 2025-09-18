package ru.ztrixdev.projects.passhavenapp.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes
import ru.ztrixdev.projects.passhavenapp.ViewModels.NewEntryViewModel

class NewEntryActivity: ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        val newEntryViewModel: NewEntryViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent() {
            MaterialTheme {
                Column(
                    Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                ) {
                    FolderSelection(newEntryViewModel)
                    TypeSelection(newEntryViewModel)
                }
            }
        }
    }
}

@Composable
fun FolderSelection(newEntryViewModel: NewEntryViewModel) {
    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }
    
    val itemPosition = remember {
        mutableIntStateOf(-1)
    }

    val folders = newEntryViewModel.getFolders(LocalContext.current)

    Column(
        modifier = Modifier.fillMaxSize(),
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
                    text = when (itemPosition.intValue) {
                        -1 -> stringResource(R.string.unfoldered)
                        else -> {
                            folders[itemPosition.intValue].name
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = {
                    isDropDownExpanded.value = false
                }) {
                folders.forEachIndexed { index, folder ->
                    DropdownMenuItem(text = {
                        Text(text = folder.name)
                    },
                        onClick = {
                            isDropDownExpanded.value = false
                            itemPosition.intValue = index
                            newEntryViewModel.setSelectedFolder(folder)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TypeSelection(newEntryViewModel: NewEntryViewModel) {
    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    val itemPosition = remember {
        mutableIntStateOf(0)
    }

    val typeStringsToEntryTypes = mapOf(
        stringResource(R.string.account) to EntryTypes.Account,
        stringResource(R.string.card) to EntryTypes.Card
    )

    Column(
        modifier = Modifier.fillMaxSize(),
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
                    text = typeStringsToEntryTypes.keys.toList()[itemPosition.intValue],
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = {
                    isDropDownExpanded.value = false
                }) {
                typeStringsToEntryTypes.keys.forEachIndexed { index, typeString ->
                    DropdownMenuItem(text = {
                        Text(text = typeString)
                    },
                        onClick = {
                            isDropDownExpanded.value = false
                            itemPosition.intValue = index
                            newEntryViewModel._setSelectedEntryType(typeStringsToEntryTypes[typeStringsToEntryTypes.keys.toList()[itemPosition.intValue]] as EntryTypes)
                        }
                    )
                }
            }
        }
    }
}


