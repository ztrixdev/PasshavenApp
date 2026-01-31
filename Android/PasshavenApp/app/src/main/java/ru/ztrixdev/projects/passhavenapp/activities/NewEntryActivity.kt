package ru.ztrixdev.projects.passhavenapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import ru.ztrixdev.projects.passhavenapp.QuickComposables
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.handlers.MFAHandler
import ru.ztrixdev.projects.passhavenapp.preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Folder
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme
import ru.ztrixdev.projects.passhavenapp.viewModels.NewEntryViewModel
import ru.ztrixdev.projects.passhavenapp.viewModels.enums.EntryTypes
import kotlin.uuid.Uuid

@OptIn(DelicateCoroutinesApi::class)
class NewEntryActivity : ComponentActivity() {

    private val newEntryViewModel: NewEntryViewModel by viewModels()

    private val qrScanLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents != null) {
            val qr = MFAHandler.processQR(result.contents)
            newEntryViewModel.mfaSecret = newEntryViewModel.mfaSecret.copy(text = qr.secret)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val localctx = LocalContext.current
            var nameIsEmptyProblem by remember { mutableStateOf(false) }

            LaunchedEffect(newEntryViewModel.entryCreated.value) {
                if (newEntryViewModel.entryCreated.value) {
                    val intent = Intent(this@NewEntryActivity, VaultOverviewActivity::class.java)
                    this@NewEntryActivity.startActivity(intent)
                }
            }

            PasshavenTheme(
                themeType = ThemePrefs.getSelectedTheme(localctx),
                darkTheme = ThemePrefs.getDarkThemeBool(localctx),
                dynamicColors = ThemePrefs.getDynamicColorsBool(localctx),
            ) {
                Scaffold(
                    topBar = {
                        QuickComposables.BackButtonTitlebar(stringResource(R.string.newentryactivity_titlebar)) {
                            val intent = Intent(this@NewEntryActivity, VaultOverviewActivity::class.java)
                            this@NewEntryActivity.startActivity(intent)
                        }
                    },
                    floatingActionButton = {
                        if (newEntryViewModel.allRequiredFieldsAreFilled) {
                            ExtendedFloatingActionButton(
                                text = { Text(stringResource(R.string.continue_button)) },
                                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                                onClick = {
                                    lifecycleScope.launch {
                                        finish(localctx, newEntryViewModel) {
                                            val intent = Intent(localctx, VaultOverviewActivity::class.java)
                                            localctx.startActivity(intent)
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            EntryTypeTabs(newEntryViewModel)
                        }

                        item {
                            OutlinedTextField(
                                value = newEntryViewModel.newEntryName,
                                onValueChange = {
                                    nameIsEmptyProblem = it.text.isEmpty()
                                    newEntryViewModel.newEntryName = it
                                    newEntryViewModel.checkRequiredFields()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.new_entry_name)) },
                                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                                singleLine = true,
                                isError = nameIsEmptyProblem,
                                supportingText = {
                                    if (nameIsEmptyProblem) Text(stringResource(R.string.name_cannot_be_empty))
                                }
                            )
                        }

                        when (newEntryViewModel.selectedEntryType) {
                            EntryTypes.Account -> item { AccountSpecificFields(newEntryViewModel) }
                            EntryTypes.Card -> item { CardSpecificFields(newEntryViewModel) }
                            else -> {}
                        }

                        item { FolderSelectionCard(newEntryViewModel) }
                        item {
                            OutlinedTextField(
                                value = newEntryViewModel.additionalNote,
                                onValueChange = {
                                    newEntryViewModel.additionalNote = it
                                    newEntryViewModel.checkRequiredFields()
                                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.additional_note)) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            )
                        }

                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    private suspend fun finish(localctx: Context, newEntryViewModel: NewEntryViewModel, onSuccess: () -> Unit) {
        var newEntryUuid = Uuid.random()
        val newEntryUuidClone = newEntryUuid

        if (newEntryViewModel.selectedEntryType == EntryTypes.Card)
            newEntryUuid = newEntryViewModel.pushNewEntry(card = newEntryViewModel.createCard(), context = localctx)
        if (newEntryViewModel.selectedEntryType == EntryTypes.Account)
            newEntryUuid = newEntryViewModel.pushNewEntry(account = newEntryViewModel.createAccount(), context = localctx)

        newEntryViewModel.entryCreated.value = newEntryUuid != newEntryUuidClone
        if (newEntryUuid != newEntryUuidClone) {
            newEntryViewModel.entryCreated.value = true
            onSuccess()
        }
    }

    @Composable
    private fun EntryTypeTabs(viewModel: NewEntryViewModel) {
        val typeStringsToEntryTypes = mapOf(
            stringResource(R.string.account) to EntryTypes.Account,
            stringResource(R.string.card) to EntryTypes.Card
        )
        val tabIndex = if (viewModel.selectedEntryType == EntryTypes.Account) 0 else 1

        PrimaryTabRow(selectedTabIndex = tabIndex) {
            typeStringsToEntryTypes.forEach { (title, type) ->
                val index = if (type == EntryTypes.Account) 0 else 1
                Tab(
                    selected = tabIndex == index,
                    onClick = {
                        viewModel._setSelectedEntryType(type)
                        newEntryViewModel.checkRequiredFields()
                    },
                    text = { Text(text = title) },
                    icon = {
                        when (type) {
                            EntryTypes.Account -> Icon(Icons.Default.Person, contentDescription = null)
                            EntryTypes.Card -> Icon(painterResource(R.drawable.credit_card_24px), contentDescription = null)
                            else -> {}
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun FolderSelectionCard(viewModel: NewEntryViewModel) {
        val localctx = LocalContext.current
        var isExpanded by remember { mutableStateOf(false) }
        val folders = remember { mutableStateListOf<Folder>() }

        LaunchedEffect(Unit) {
            folders.addAll(viewModel.getFolders(localctx))
        }

        val selectedFolderName = viewModel.selectedFolder?.name ?: stringResource(R.string.unfoldered)

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { isExpanded = true }
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(R.drawable.folder_open_24px), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text(text = selectedFolderName)
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand folders")
                }
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier.widthIn(max = 400.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.unfoldered)) },
                        onClick = {
                            isExpanded = false
                        }
                    )
                    folders.forEach { folder ->
                        DropdownMenuItem(
                            text = { Text(folder.name) },
                            onClick = {
                                viewModel._setSelectedFolder(folder)
                                isExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AccountSpecificFields(newEntryViewModel: NewEntryViewModel) {
        var usernameIsEmptyProblem by remember { mutableStateOf(false) }
        var passwordIsEmptyProblem by remember { mutableStateOf(false) }
        var mfaSecretIsInvalidProblem by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = newEntryViewModel.username,
                onValueChange = {
                    usernameIsEmptyProblem = it.text.isEmpty()
                    newEntryViewModel.username = it
                    newEntryViewModel.checkRequiredFields()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.username)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                isError = usernameIsEmptyProblem,
                supportingText = { if (usernameIsEmptyProblem) Text(stringResource(R.string.fill_this_field_up_pls)) }
            )

            OutlinedTextField(
                value = newEntryViewModel.password,
                onValueChange = {
                    passwordIsEmptyProblem = it.text.isEmpty()
                    newEntryViewModel.password = it
                    newEntryViewModel.checkRequiredFields()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.password)) },
                leadingIcon = { Icon(painter = painterResource(R.drawable.password_40px), contentDescription = null) },
                singleLine = true,
                isError = passwordIsEmptyProblem,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) painterResource(R.drawable.visibility_off_24px) else painterResource(R.drawable.visibility_24px)
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = image, contentDescription = null)
                    }
                },
                supportingText = { if (passwordIsEmptyProblem) Text(stringResource(R.string.fill_this_field_up_pls)) }
            )

            TextButton(
                onClick = { newEntryViewModel.generatePassword() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.generate_password_for_me))
            }

            OutlinedTextField(
                value = newEntryViewModel.mfaSecret,
                onValueChange = {
                    mfaSecretIsInvalidProblem = it.text.isNotEmpty() && !MFAHandler.verifySecret(it.text)
                    newEntryViewModel.mfaSecret = it
                    newEntryViewModel.checkRequiredFields()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.mfa_secret)) },
                singleLine = true,
                isError = mfaSecretIsInvalidProblem,
                trailingIcon = {
                    IconButton(onClick = { qrScanLauncher.launch(newEntryViewModel.defaultQRScanOpts) }) {
                        Icon(painter = painterResource(R.drawable.qr_code_scanner_24px), contentDescription = "Scan QR Code")
                    }
                },
                supportingText = {
                    if (mfaSecretIsInvalidProblem) Text(stringResource(R.string.mfa_secret_invalid))
                    else Text(stringResource(R.string.mfa_secret_supporting_text))
                }
            )
        }
    }

    @Composable
    private fun CardSpecificFields(newEntryViewModel: NewEntryViewModel) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = newEntryViewModel.cardholderName,
                onValueChange = { newEntryViewModel.cardholderName = it
                    newEntryViewModel.checkRequiredFields() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.cardholder_name)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true
            )

            OutlinedTextField(
                value = newEntryViewModel.cardNumber,
                onValueChange = {
                    newEntryViewModel.cardNumber = it
                    newEntryViewModel.checkRequiredFields()
                                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.card_number)) },
                leadingIcon = { Icon(painterResource(id = R.drawable.credit_card_24px), contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newEntryViewModel.expirationMMYY,
                    onValueChange = { newEntryViewModel.expirationMMYY = it
                        newEntryViewModel.checkRequiredFields() },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.card_expdate)) },
                    placeholder = { Text("MM/YY") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = newEntryViewModel.cvcCVV,
                    onValueChange = {
                        newEntryViewModel.cvcCVV = it
                        newEntryViewModel.checkRequiredFields()
                                    },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.card_cvc_cvv_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}
