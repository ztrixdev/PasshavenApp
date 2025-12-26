package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.ztrixdev.projects.passhavenapp.Handlers.MFAHandler
import ru.ztrixdev.projects.passhavenapp.Preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.QuickComposables
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardBrands
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardCredentials
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes
import ru.ztrixdev.projects.passhavenapp.ViewModels.ViewEntryViewModel
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme
import kotlin.uuid.Uuid


const val EDIT_ENTRY_ACTIVITY_EXTRA_ENTRY_UUID_KEY = "entry_uuid"
const val EDIT_ENTRY_ACTIVITY_EXTRA_PREV_ACTIVITY_KEY = "previous_activity"

class ViewEntryActivity : ComponentActivity() {
    val viewEntryViewModel: ViewEntryViewModel by viewModels()

    val qrScanLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            val qr = MFAHandler.processQR(result.contents)
            viewEntryViewModel.mfaSecret = TextFieldValue(qr.secret)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receivedIntent = intent
        viewEntryViewModel.entryUuid = receivedIntent.getStringExtra(EDIT_ENTRY_ACTIVITY_EXTRA_ENTRY_UUID_KEY)

        // enableEdgeToEdge()
        setContent {
            val localctx = LocalContext.current
            LaunchedEffect(Unit) {
                viewEntryViewModel.getCurrentData(localctx)
            }

            PasshavenTheme(
                themeType = ThemePrefs.getSelectedTheme(LocalContext.current),
                darkTheme = ThemePrefs.getDarkThemeBool(LocalContext.current),
                dynamicColors = ThemePrefs.getDynamicColorsBool(LocalContext.current),
            )
            {
                val scrollState = rememberScrollState()
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(scrollState)
                ) {
                    MainBody(viewEntryViewModel)
                }
            }
        }
    }

    @Composable
    private fun FolderSelectionDropdown(viewEntryViewModel: ViewEntryViewModel) {
        val localctx = LocalContext.current

        val isDropDownExpanded = remember {
            mutableStateOf(false)
        }

        val itemPosition = remember {
            mutableIntStateOf(-1)
        }

        // how in the world did this bug appear????
        // it was just a list before... i remember it worked smh
        val folders = remember {
            mutableStateListOf<Folder>()
        }
        LaunchedEffect(Unit) {
            folders.addAll(viewEntryViewModel.getFolders(localctx))
        }

        LaunchedEffect(viewEntryViewModel.dataFetchDone) {
            if (viewEntryViewModel.inFolder != null) {
                itemPosition.intValue = folders.indexOf(viewEntryViewModel.inFolder)
            }
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        isDropDownExpanded.value = viewEntryViewModel.editMode
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
                                viewEntryViewModel.setSelectedFolder(folder)
                                viewEntryViewModel.allRequiredFieldsAreFilled = viewEntryViewModel.checkRequiredFields()
                            }
                        )
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    private fun MainBody(viewEntryViewModel: ViewEntryViewModel) {
        QuickComposables.Titlebar(stringResource(R.string.editentryactivity_titlebar)) {
            val intent = Intent(this@ViewEntryActivity, VaultOverviewActivity::class.java)
            this@ViewEntryActivity.startActivity(intent)
        }
        Spacer(
            modifier = Modifier.height(20.dp)
        )
        val localctx = LocalContext.current
        TextButton(onClick = {
            viewEntryViewModel.allRequiredFieldsAreFilled = viewEntryViewModel.checkRequiredFields()
            if (viewEntryViewModel.editMode) {
                lifecycleScope.launch {
                    finish(localctx, viewEntryViewModel) {
                        viewEntryViewModel.editMode = false
                    }
                }
            } else {
                viewEntryViewModel.editMode = true
                viewEntryViewModel.isPasswordVisible = true
                viewEntryViewModel.isCvcCvvVisible = true
            }
        },
            enabled = if (viewEntryViewModel.editMode) viewEntryViewModel.allRequiredFieldsAreFilled else true,
            modifier = Modifier
                .padding(start = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (viewEntryViewModel.editMode) {
                    Icon(
                        painter = painterResource(R.drawable.check_24px),
                        contentDescription = "Check icon.",
                        tint = MaterialTheme.colorScheme.inversePrimary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.confirm),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.edit_24px),
                        contentDescription = "Edit icon.",
                        tint = MaterialTheme.colorScheme.inversePrimary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.edit),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 36.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text =
                        if (viewEntryViewModel.editMode)
                            stringResource(R.string.select_folder)
                        else
                            stringResource(R.string.in_folder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                FolderSelectionDropdown(viewEntryViewModel = viewEntryViewModel)
            }

            var nameIsEmptyProblem by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = viewEntryViewModel.newEntryName,
                onValueChange = {
                    nameIsEmptyProblem = false
                    if (it.text.isEmpty())
                        nameIsEmptyProblem = true
                    viewEntryViewModel.newEntryName = it
                    viewEntryViewModel.allRequiredFieldsAreFilled = viewEntryViewModel.checkRequiredFields()
                },
                label = {
                    Text(text = stringResource(R.string.new_entry_name))
                },
                enabled = viewEntryViewModel.editMode,
                singleLine = true,
                isError = nameIsEmptyProblem,
                supportingText = @Composable {
                    if (nameIsEmptyProblem) {
                        Text(text =stringResource(R.string.name_cannot_be_empty),
                            style = MaterialTheme.typography.bodySmall)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = QuickComposables.uniformTextFieldColors()
            )

            if (!viewEntryViewModel.editMode) {
                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            } else {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }

            when (viewEntryViewModel.type) {
                EntryTypes.Account -> AccountSpecificFields(viewEntryViewModel)
                EntryTypes.Card -> CardSpecificFields(viewEntryViewModel)
                EntryTypes.Folder -> {}
            }

            if (!viewEntryViewModel.editMode) {
                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            } else {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }


            OutlinedTextField(
                value = viewEntryViewModel.additionalNote,
                onValueChange = {
                    viewEntryViewModel.additionalNote = it
                },
                label = {
                    Text(text = stringResource(R.string.additional_note))
                },
                singleLine = false,
                enabled = viewEntryViewModel.editMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = QuickComposables.uniformTextFieldColors()
            )
            Spacer(
                modifier = Modifier.height(80.dp)
            )
        }
    }

    private suspend fun finish(localctx: Context, viewEntryViewModel: ViewEntryViewModel, onSuccess: () -> Unit) {
        var editEntryUuid = Uuid.random()
        val editEntryUuidClone = editEntryUuid

        if (viewEntryViewModel.type == EntryTypes.Card)
            editEntryUuid = viewEntryViewModel.updateEntry(card = viewEntryViewModel.createCard(), context =  localctx)
        if (viewEntryViewModel.type == EntryTypes.Account)
            editEntryUuid = viewEntryViewModel.updateEntry(account = viewEntryViewModel.createAccount(), context = localctx)

        viewEntryViewModel.entryUpdated = editEntryUuid != editEntryUuidClone
        viewEntryViewModel.isPasswordVisible = false
        viewEntryViewModel.isCvcCvvVisible = false
        if (editEntryUuid != editEntryUuidClone) {
            viewEntryViewModel.entryUpdated = true
            onSuccess()
        }
    }

    @Composable
    private fun AccountSpecificFields(viewEntryViewModel: ViewEntryViewModel) {
        var usernameIsEmptyProblem by remember { mutableStateOf(false) }
        var passwordIsEmptyProblem by remember { mutableStateOf(false) }
        var mfaSecretIsInvalidProblem by remember { mutableStateOf(false) }

        Column {
            // Username textfield
            OutlinedTextField(
                value = viewEntryViewModel.username,
                onValueChange = {
                    usernameIsEmptyProblem = it.text.isEmpty()
                    viewEntryViewModel.username = it
                    viewEntryViewModel.allRequiredFieldsAreFilled =
                        viewEntryViewModel.checkRequiredFields()
                },
                label = {
                    Text(text = stringResource(R.string.username))
                },
                placeholder = {
                    Text(text = stringResource(R.string.username_placeholder))
                },
                singleLine = true,
                isError = usernameIsEmptyProblem,
                supportingText = @Composable {
                    if (usernameIsEmptyProblem)
                        Text(
                            text = stringResource(R.string.fill_this_field_up_pls),
                            style = MaterialTheme.typography.bodySmall
                        )
                },
                enabled = viewEntryViewModel.editMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = QuickComposables.uniformTextFieldColors()
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewEntryViewModel.password,
                    onValueChange = { it ->
                        if (viewEntryViewModel.isPasswordVisible) {
                            viewEntryViewModel.password = it
                        }
                        passwordIsEmptyProblem = it.text.isEmpty()
                        viewEntryViewModel.allRequiredFieldsAreFilled =
                            viewEntryViewModel.checkRequiredFields()
                    },
                    label = {
                        Text(text = stringResource(R.string.password))
                    },
                    placeholder = {
                        Text(text = stringResource(R.string.password_placeholder))
                    },
                    enabled = viewEntryViewModel.editMode,
                    singleLine = true,
                    visualTransformation =
                        if (viewEntryViewModel.isPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                    isError = passwordIsEmptyProblem,
                    supportingText = @Composable {
                        if (passwordIsEmptyProblem)
                            Text(
                                text = stringResource(R.string.fill_this_field_up_pls),
                                style = MaterialTheme.typography.bodySmall
                            )
                    },
                    modifier = Modifier
                        .padding(top = 8.dp),
                    colors = QuickComposables.uniformTextFieldColors()
                )
                if (!viewEntryViewModel.editMode) {
                    val localctx = LocalContext.current
                    var showCopiedToast by remember { mutableStateOf(false) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        IconButton(
                            onClick = {
                                viewEntryViewModel.togglePasswordVisibility()
                            },
                            Modifier.size(20.dp)
                        ) {
                            Icon(
                                painter =
                                    if (viewEntryViewModel.isPasswordVisible)
                                        painterResource(R.drawable.visibility_24px)
                                    else
                                        painterResource(R.drawable.visibility_off_24px),
                                contentDescription = "Change visibility",
                                tint = MaterialTheme.colorScheme.inversePrimary,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                viewEntryViewModel.copy(ViewEntryViewModel.Copyable.Password, localctx)
                                showCopiedToast = true
                            },
                            Modifier.size(20.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.content_copy_24px),
                                contentDescription = "Copy password",
                                tint = MaterialTheme.colorScheme.inversePrimary,
                            )
                        }
                        if (showCopiedToast) {
                            QuickComposables.makeCopiedToast()
                        }
                    }

                }
            }

            // Generate password button
            if (viewEntryViewModel.editMode) {
                TextButton(
                    onClick = {
                        viewEntryViewModel.allRequiredFieldsAreFilled =
                            viewEntryViewModel.checkRequiredFields()
                        viewEntryViewModel.generatePassword()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.generate_password_for_me),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            val localctx = LocalContext.current
            if (!viewEntryViewModel.currentMFAValue.text.isEmpty() || viewEntryViewModel.editMode) {
                // MFA textfield
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = when {
                                !viewEntryViewModel.editMode -> viewEntryViewModel.currentMFAValue
                                else -> viewEntryViewModel.mfaSecret
                            },
                            onValueChange = { it ->
                                mfaSecretIsInvalidProblem = !MFAHandler.verifySecret(it.text)
                                viewEntryViewModel.mfaSecret = it
                                viewEntryViewModel.allRequiredFieldsAreFilled =
                                    viewEntryViewModel.checkRequiredFields()
                            },
                            label = {
                                Text(
                                    if (!viewEntryViewModel.editMode)
                                        stringResource(R.string.mfa_code_label)
                                    else
                                        stringResource(R.string.mfa_secret)
                                )
                            },
                            singleLine = true,
                            enabled = viewEntryViewModel.editMode,
                            isError = mfaSecretIsInvalidProblem,
                            supportingText = {
                                if (mfaSecretIsInvalidProblem) {
                                    Text(
                                        text = stringResource(R.string.mfa_secret_invalid),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f) // 🔹 key part
                                .padding(top = 8.dp),
                            colors = QuickComposables.uniformTextFieldColors()
                        )

                        if (viewEntryViewModel.editMode) {
                            IconButton(
                                onClick = {
                                    qrScanLauncher.launch(viewEntryViewModel.defaultQRScanOpts)
                                    viewEntryViewModel.allRequiredFieldsAreFilled =
                                        viewEntryViewModel.checkRequiredFields()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.qr_code_scanner_24px),
                                    contentDescription = "QR button",
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colorScheme.inversePrimary
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    viewEntryViewModel.copy(ViewEntryViewModel.Copyable.TOTP,localctx)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.content_copy_24px),
                                    tint = MaterialTheme.colorScheme.inversePrimary,
                                    contentDescription = "Copy password"
                                )
                            }
                        }
                    }

                    if (!viewEntryViewModel.editMode) MFAProgressbar()
            }
            }


            // Recovery codes section
            Column {
                if (!viewEntryViewModel.recoveryCodes.isEmpty() || viewEntryViewModel.editMode) {
                    if (viewEntryViewModel.editMode && viewEntryViewModel.recoveryCodes.isEmpty())
                        viewEntryViewModel.recoveryCodes.add(TextFieldValue(""))
                    if (!viewEntryViewModel.editMode && viewEntryViewModel.recoveryCodes.first().text.isEmpty())
                        return;
                    repeat(viewEntryViewModel.recoveryCodes.size) { index ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = viewEntryViewModel.recoveryCodes[index],
                                onValueChange = {
                                    viewEntryViewModel.recoveryCodes[index] = it
                                },
                                label = {
                                    // recovery code #i
                                    Text(text = "${stringResource(R.string.recovery_code_label)}${index}")
                                },
                                singleLine = true,
                                enabled = viewEntryViewModel.editMode,
                                colors = QuickComposables.uniformTextFieldColors()
                            )
                            if (viewEntryViewModel.editMode && viewEntryViewModel.recoveryCodes.size > 1) {
                                IconButton(
                                    onClick = {
                                        viewEntryViewModel.deleteRecoveryCode(index)
                                        viewEntryViewModel.allRequiredFieldsAreFilled =
                                            viewEntryViewModel.checkRequiredFields()
                                    },
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.delete_24px),
                                        contentDescription = "Delete recovery code",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // Add some space before the button
                    if (viewEntryViewModel.editMode) {
                        TextButton(
                            onClick = {
                                viewEntryViewModel.allRequiredFieldsAreFilled =
                                    viewEntryViewModel.checkRequiredFields()
                                viewEntryViewModel.addRecoveryCode()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.add_24px),
                                contentDescription = "Add recovery code",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = stringResource(R.string.add_another_recovery_code))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MFAProgressbar() {
        var currentProgress by remember { mutableFloatStateOf(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                for (i in 1..300) {
                    currentProgress = i / 300f
                    delay(100)
                }
                currentProgress = 0f
                viewEntryViewModel.updateMFA()
            }
        }
        LinearProgressIndicator(
            progress = { currentProgress },
            modifier = Modifier
                .widthIn(100.dp, 400.dp)
                .padding(bottom = 12.dp),
            color = MaterialTheme.colorScheme.inversePrimary
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CardSpecificFields(viewEntryViewModel: ViewEntryViewModel) {
        var cardNumberInvalidProblem by remember { mutableStateOf(false) }
        var cardNumberTooShortProblem by remember { mutableStateOf(false) }
        var cardNumberTooLongProblem by remember { mutableStateOf(false) }
        var cardExpirationDateInvalidProblem by remember { mutableStateOf(false) }
        var cardCvcCvvInvalidProblem by remember { mutableStateOf(false) }

        val localctx = LocalContext.current

        Column {
            val brandStringsToBrands = mapOf(
                stringResource(R.string.visa) to CardBrands.Visa,
                stringResource(R.string.mastercard) to CardBrands.Mastercard,
                stringResource(R.string.unionpay) to CardBrands.UnionPay,
                stringResource(R.string.mir) to CardBrands.MIR,
                stringResource(R.string.maestro) to CardBrands.Maestro,
                stringResource(R.string.amex) to CardBrands.AmericanExpress,
            )

            Box {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.cards_brand),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                    Text(
                        text = brandStringsToBrands.filterValues { it == viewEntryViewModel.selectedCardBrand }.keys.first(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            // Card Number Field
            OutlinedTextField(
                value = viewEntryViewModel.cardNumber,
                onValueChange = {
                    try {
                        cardNumberTooLongProblem = !viewEntryViewModel.validateCardCredentials(
                            CardCredentials.Number,
                            it.text
                        )
                        cardNumberInvalidProblem = false
                        cardNumberTooShortProblem = false
                    } catch (e: IllegalArgumentException) {
                        if (e.message.contentEquals(viewEntryViewModel.CARD_NUMBER_TOO_SHORT_EXCEPTION.message))
                            cardNumberTooShortProblem = true
                    } finally {
                        viewEntryViewModel.cardNumber = it
                        viewEntryViewModel.allRequiredFieldsAreFilled =
                            viewEntryViewModel.checkRequiredFields()
                    }
                },
                label = {
                    Text(text = stringResource(R.string.card_number))
                },
                placeholder = {
                    Text(text = stringResource(R.string.card_number_placeholder))
                },
                singleLine = true,
                enabled = viewEntryViewModel.editMode,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = cardNumberInvalidProblem || cardNumberTooShortProblem || cardNumberTooLongProblem,
                supportingText = @Composable {
                    if (cardNumberInvalidProblem) {
                        Text(
                            text = stringResource(R.string.error_card_number_invalid),
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (cardNumberTooShortProblem) {
                        Text(
                            text = stringResource(R.string.error_card_number_too_short),
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (cardNumberTooLongProblem) {
                        Text(
                            text = stringResource(R.string.error_card_number_too_long),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable(
                        enabled = !viewEntryViewModel.editMode,
                        onClick = {
                            viewEntryViewModel.copy(ViewEntryViewModel.Copyable.CardNumber, localctx)
                        }
                    ),
                colors = QuickComposables.uniformTextFieldColors()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Expiration MM/YY Field
                OutlinedTextField(
                    value = viewEntryViewModel.expirationMMYY,
                    onValueChange = {
                        try {
                            cardExpirationDateInvalidProblem =
                                !viewEntryViewModel.validateCardCredentials(
                                    CardCredentials.ExpirationDate,
                                    it.text
                                )
                        } catch (e: IllegalArgumentException) {
                            // have no idea how would this happen, considering that exceptions are only meant to happen with card numbers inside of that function, xd :^
                            println(e)
                        } finally {
                            viewEntryViewModel.expirationMMYY = it
                        }
                    },
                    label = {
                        Text(text = stringResource(R.string.card_expdate))
                    },
                    placeholder = {
                        Text(text = stringResource(R.string.card_mm_yy_placeholder))
                    },
                    modifier = Modifier.weight(1f),
                    isError = cardExpirationDateInvalidProblem,
                    singleLine = true,
                    enabled = viewEntryViewModel.editMode,
                    supportingText = @Composable {
                        if (cardExpirationDateInvalidProblem) {
                            Text(
                                text = stringResource(R.string.error_card_expdate_invalid),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    colors = QuickComposables.uniformTextFieldColors()
                )

                // CVC/CVV Field
                OutlinedTextField(
                    value = viewEntryViewModel.cvcCVV,
                    onValueChange = {
                        try {
                            cardCvcCvvInvalidProblem =
                                !viewEntryViewModel.validateCardCredentials(
                                    CardCredentials.CVC_CVV,
                                    it.text
                                )
                        } catch (e: IllegalArgumentException) {
                            // Justin Case: Prepared for anything.
                            // https://youtube.com/shorts/h7EZR6ppVR8?si=EqTZ2WWq2sQnUJSN
                            println(e)
                        } finally {
                            viewEntryViewModel.cvcCVV = it
                            viewEntryViewModel.allRequiredFieldsAreFilled =
                                viewEntryViewModel.checkRequiredFields()
                        }
                    },
                    label = {
                        Text(text = stringResource(R.string.card_cvc_cvv_placeholder))
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = !viewEntryViewModel.editMode,
                            onClick = {
                                viewEntryViewModel.toggleCvcVisibility()
                                viewEntryViewModel.copy(ViewEntryViewModel.Copyable.CVC, localctx)
                            }
                        ),
                    enabled = viewEntryViewModel.editMode,
                    isError = cardCvcCvvInvalidProblem,
                    supportingText = @Composable {
                        if (cardCvcCvvInvalidProblem)
                            Text(
                                text = stringResource(R.string.error_card_cvccvv_invalid),
                                style = MaterialTheme.typography.bodySmall
                            )
                    },
                    visualTransformation =
                        if (viewEntryViewModel.isCvcCvvVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = QuickComposables.uniformTextFieldColors()
                )
            }

            // Cardholder Name Field
            OutlinedTextField(
                value = viewEntryViewModel.cardholderName,
                label = {
                    Text(text = stringResource(R.string.cardholder_name))
                },
                placeholder =  {
                    Text(text = stringResource(R.string.cardholder_name_placeholder))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true,
                enabled = viewEntryViewModel.editMode,
                onValueChange = {
                    viewEntryViewModel.cardholderName = it
                    viewEntryViewModel.allRequiredFieldsAreFilled = viewEntryViewModel.checkRequiredFields()
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                colors = QuickComposables.uniformTextFieldColors()
            )
        }
    }
}
