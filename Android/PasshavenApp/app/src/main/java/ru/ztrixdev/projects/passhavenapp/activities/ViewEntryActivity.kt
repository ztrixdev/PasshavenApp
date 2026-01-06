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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import kotlinx.coroutines.launch
import ru.ztrixdev.projects.passhavenapp.handlers.MFAHandler
import ru.ztrixdev.projects.passhavenapp.preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.QuickComposables
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.room.Folder
import ru.ztrixdev.projects.passhavenapp.viewModels.enums.EntryTypes
import ru.ztrixdev.projects.passhavenapp.viewModels.ViewEntryViewModel
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme
import kotlin.uuid.Uuid


const val EDIT_ENTRY_ACTIVITY_EXTRA_ENTRY_UUID_KEY = "entry_uuid"
const val EDIT_ENTRY_ACTIVITY_EXTRA_PREV_ACTIVITY_KEY = "previous_activity"

class ViewEntryActivity : ComponentActivity() {

    private val viewEntryViewModel: ViewEntryViewModel by viewModels()

    private val qrScanLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            result.contents?.let {
                val qr = MFAHandler.processQR(it)
                viewEntryViewModel.mfaSecret = TextFieldValue(qr.secret)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewEntryViewModel.entryUuid =
            intent.getStringExtra(EDIT_ENTRY_ACTIVITY_EXTRA_ENTRY_UUID_KEY)

        setContent {
            val localctx = LocalContext.current

            LaunchedEffect(Unit) {
                viewEntryViewModel.getCurrentData(localctx)
            }

            PasshavenTheme(
                themeType = ThemePrefs.getSelectedTheme(localctx),
                darkTheme = ThemePrefs.getDarkThemeBool(localctx),
                dynamicColors = ThemePrefs.getDynamicColorsBool(localctx)
            ) {
                Scaffold(
                    topBar = {
                        QuickComposables.BackButtonTitlebar(
                            stringResource(R.string.editentryactivity_titlebar)
                        ) {
                            startActivity(
                                Intent(this@ViewEntryActivity, VaultOverviewActivity::class.java)
                            )
                        }
                    },
                    floatingActionButton = {
                        ViewEntryFab(localctx)
                    }
                ) { padding ->
                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        item { FolderSelectionCard() }
                        item { NameField() }

                        when (viewEntryViewModel.type) {
                            EntryTypes.Account -> item { AccountSpecificFields() }
                            EntryTypes.Card -> item { CardSpecificFields() }
                            else -> {}
                        }

                        item { AdditionalNoteField() }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    /* -------------------------------------------------- FAB */

    @Composable
    private fun ViewEntryFab(localctx: Context) {
        if (!viewEntryViewModel.editMode) {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.edit)) },
                icon = { Icon(Icons.Default.Edit, null) },
                onClick = {
                    viewEntryViewModel.editMode = true
                    viewEntryViewModel.isPasswordVisible = true
                    viewEntryViewModel.isCvcCvvVisible = true
                }
            )
        } else if (viewEntryViewModel.allRequiredFieldsAreFilled) {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.confirm)) },
                icon = { Icon(painterResource(R.drawable.check_24px), null) },
                onClick = {
                    lifecycleScope.launch {
                        finish(localctx) {
                            viewEntryViewModel.editMode = false
                        }
                    }
                }
            )
        }
    }

    /* -------------------------------------------------- Folder */

    @Composable
    private fun FolderSelectionCard() {
        val localctx = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
        val folders = remember { mutableStateListOf<Folder>() }

        LaunchedEffect(Unit) {
            folders.addAll(viewEntryViewModel.getFolders(localctx))
        }

        var selected =
            viewEntryViewModel.inFolder?.name ?: stringResource(R.string.unfoldered)

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { if (viewEntryViewModel.editMode) expanded = true }
        ) {
            Box {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.folder_open_24px),
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(selected)
                    }
                    Icon(Icons.Default.ArrowDropDown, null)
                }

                DropdownMenu(expanded, { expanded = false }) {
                    folders.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                viewEntryViewModel.allRequiredFieldsAreFilled =
                                    viewEntryViewModel.checkRequiredFields()
                                selected = it.name
                                viewEntryViewModel.setSelectedFolder(it)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }

    /* -------------------------------------------------- Common Fields */

    @Composable
    private fun NameField() {
        OutlinedTextField(
            value = viewEntryViewModel.newEntryName,
            onValueChange = {
                viewEntryViewModel.newEntryName = it
                viewEntryViewModel.allRequiredFieldsAreFilled =
                    viewEntryViewModel.checkRequiredFields()
            },
            readOnly = !viewEntryViewModel.editMode,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.new_entry_name)) },
            leadingIcon = { Icon(Icons.Default.AccountCircle, null) }
        )
    }

    @Composable
    private fun AdditionalNoteField() {
        OutlinedTextField(
            value = viewEntryViewModel.additionalNote,
            onValueChange = { viewEntryViewModel.additionalNote = it },
            readOnly = !viewEntryViewModel.editMode,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.additional_note)) },
            leadingIcon = { Icon(Icons.Default.Edit, null) }
        )
    }

    @Composable
    private fun AccountSpecificFields() {
        var passwordVisible by remember { mutableStateOf(false) }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            OutlinedTextField(
                value = viewEntryViewModel.username,
                onValueChange = {
                    viewEntryViewModel.username = it
                    viewEntryViewModel.allRequiredFieldsAreFilled =
                        viewEntryViewModel.checkRequiredFields()
                },
                readOnly = !viewEntryViewModel.editMode,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.username)) },
                leadingIcon = { Icon(Icons.Default.Email, null) }
            )

            OutlinedTextField(
                value = viewEntryViewModel.password,
                onValueChange = {
                    viewEntryViewModel.password = it
                    viewEntryViewModel.allRequiredFieldsAreFilled =
                        viewEntryViewModel.checkRequiredFields()
                },
                readOnly = !viewEntryViewModel.editMode,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.password)) },
                leadingIcon = {
                    Icon(painterResource(R.drawable.password_40px), null, Modifier.size(24.dp))
                },
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter =
                                if (passwordVisible)
                                    painterResource(R.drawable.visibility_off_24px)
                                else
                                    painterResource(R.drawable.visibility_24px),
                            null
                        )
                    }
                }
            )

            OutlinedTextField(
                value = viewEntryViewModel.mfaSecret,
                onValueChange = {
                    viewEntryViewModel.mfaSecret = it
                    viewEntryViewModel.allRequiredFieldsAreFilled =
                        viewEntryViewModel.checkRequiredFields()
                },
                readOnly = !viewEntryViewModel.editMode,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.mfa_secret)) },
                trailingIcon = {
                    IconButton(
                        enabled = viewEntryViewModel.editMode,
                        onClick = {
                            qrScanLauncher.launch(viewEntryViewModel.defaultQRScanOpts)
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.qr_code_scanner_24px),
                            null
                        )
                    }
                }
            )
        }
    }

    /* -------------------------------------------------- CARD */

    @Composable
    private fun CardSpecificFields() {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            OutlinedTextField(
                value = viewEntryViewModel.cardholderName,
                onValueChange = {
                    viewEntryViewModel.cardholderName = it
                    viewEntryViewModel.allRequiredFieldsAreFilled =
                        viewEntryViewModel.checkRequiredFields()
                },
                readOnly = !viewEntryViewModel.editMode,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.cardholder_name)) },
                leadingIcon = { Icon(Icons.Default.Person, null) }
            )

            OutlinedTextField(
                value = viewEntryViewModel.cardNumber,
                onValueChange = {
                    viewEntryViewModel.cardNumber = it
                    viewEntryViewModel.allRequiredFieldsAreFilled =
                        viewEntryViewModel.checkRequiredFields()
                },
                readOnly = !viewEntryViewModel.editMode,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.card_number)) },
                leadingIcon = {
                    Icon(painterResource(R.drawable.credit_card_24px), null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewEntryViewModel.expirationMMYY,
                    onValueChange = {
                        viewEntryViewModel.expirationMMYY = it
                        viewEntryViewModel.allRequiredFieldsAreFilled =
                            viewEntryViewModel.checkRequiredFields()
                    },
                    readOnly = !viewEntryViewModel.editMode,
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.card_expdate)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = viewEntryViewModel.cvcCVV,
                    onValueChange = {
                        viewEntryViewModel.cvcCVV = it
                        viewEntryViewModel.allRequiredFieldsAreFilled =
                            viewEntryViewModel.checkRequiredFields()
                    },
                    readOnly = !viewEntryViewModel.editMode,
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.card_cvc_cvv_placeholder)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation =
                        if (viewEntryViewModel.isCvcCvvVisible)
                            VisualTransformation.None
                        else PasswordVisualTransformation()
                )
            }
        }
    }

    /* -------------------------------------------------- SAVE */

    private suspend fun finish(localctx: Context, onSuccess: () -> Unit) {
        var uuid = Uuid.random()
        val clone = uuid

        if (viewEntryViewModel.type == EntryTypes.Card)
            uuid = viewEntryViewModel.updateEntry(
                card = viewEntryViewModel.createCard(),
                context = localctx
            )

        if (viewEntryViewModel.type == EntryTypes.Account)
            uuid = viewEntryViewModel.updateEntry(
                account = viewEntryViewModel.createAccount(),
                context = localctx
            )

        if (uuid != clone) onSuccess()
    }
}
