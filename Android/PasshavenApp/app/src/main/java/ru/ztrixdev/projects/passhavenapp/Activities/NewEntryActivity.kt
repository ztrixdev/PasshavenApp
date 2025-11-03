package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import ru.ztrixdev.projects.passhavenapp.Handlers.MFAHandler
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardBrands
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardCredentials
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes
import ru.ztrixdev.projects.passhavenapp.ViewModels.NewEntryViewModel
import ru.ztrixdev.projects.passhavenapp.ui.theme.AppThemeType
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme
import kotlin.uuid.Uuid


class NewEntryActivity: ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        val newEntryViewModel: NewEntryViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent() {
            var selectedTheme by remember { mutableStateOf(AppThemeType.W10) }
            PasshavenTheme(themeType = selectedTheme, darkTheme = true) {
                val scrollState = rememberScrollState()
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(scrollState)
                ) {
                    MainBody(newEntryViewModel)
                }
            }
        }
    }

    @Composable
    private fun FolderSelectionDropdown(newEntryViewModel: NewEntryViewModel) {
        val localctx = LocalContext.current

        val isDropDownExpanded = remember {
            mutableStateOf(false)
        }

        val itemPosition = remember {
            mutableIntStateOf(-1)
        }

        var folders = emptyList<Folder>()
        LaunchedEffect(Unit) {
           folders = newEntryViewModel.getFolders(localctx)
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
    private fun TypeSelectionDropdown(newEntryViewModel: NewEntryViewModel) {
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

    @Composable
    private fun Titlebar() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            IconButton(
                onClick = {
                    val intent = Intent(this@NewEntryActivity, VaultOverviewActivity::class.java)
                    this@NewEntryActivity.startActivity(intent)
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
                text = stringResource(R.string.newentryactivity_titlebar),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(start = 40.dp)
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    private fun MainBody(newEntryViewModel: NewEntryViewModel) {
        Box(
            Modifier
                .height(30.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        )
        Titlebar()
        Spacer(
            modifier = Modifier.height(40.dp)
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
                    text = stringResource(R.string.select_type),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                TypeSelectionDropdown(newEntryViewModel = newEntryViewModel)
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.select_folder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                FolderSelectionDropdown(newEntryViewModel = newEntryViewModel)
            }

            var nameIsEmptyProblem by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = newEntryViewModel.newEntryName,
                onValueChange = { it ->
                    nameIsEmptyProblem = false
                    if (it.text.isEmpty())
                        nameIsEmptyProblem = true
                    newEntryViewModel.newEntryName = it
                    newEntryViewModel.allRequiredFieldsAreFilled = newEntryViewModel.checkRequiredFields()
                },
                label = {
                    Text(text = stringResource(R.string.new_entry_name))
                },
                singleLine = true,
                isError = nameIsEmptyProblem,
                supportingText = @Composable {
                    Text(text =stringResource(R.string.name_cannot_be_empty),
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

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            when (newEntryViewModel.selectedEntryType) {
                EntryTypes.Account -> AccountSpecificFields(newEntryViewModel)
                EntryTypes.Card -> CardSpecificFields(newEntryViewModel)
                EntryTypes.Folder -> {}
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = newEntryViewModel.additionalNote,
                onValueChange = { it ->
                    newEntryViewModel.additionalNote = it
                },
                label = {
                    Text(text = stringResource(R.string.additional_note))
                },
                singleLine = false,
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

            val localctx = LocalContext.current
            Button(
                onClick = {
                    lifecycleScope.launch {
                        finish(localctx, newEntryViewModel) {
                            val intent = Intent(localctx, VaultOverviewActivity::class.java)
                            localctx.startActivity(intent)
                        }
                    }
                },
                enabled = newEntryViewModel.allRequiredFieldsAreFilled,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.continue_button))
            }
            Spacer(
                modifier = Modifier.height(80.dp)
            )
        }
    }

    private suspend fun finish(localctx: Context, newEntryViewModel: NewEntryViewModel, onSuccess: () -> Unit) {
        var newEntryUuid = Uuid.random()
        val newEntryUuidClone = newEntryUuid

        if (newEntryViewModel.selectedEntryType == EntryTypes.Card)
            newEntryUuid = newEntryViewModel.pushNewEntry(card = newEntryViewModel.createCard(), context =  localctx)
        if (newEntryViewModel.selectedEntryType == EntryTypes.Account)
            newEntryUuid = newEntryViewModel.pushNewEntry(account = newEntryViewModel.createAccount(), context = localctx)

        newEntryViewModel.entryCreated.value = newEntryUuid != newEntryUuidClone
        if (newEntryUuid != newEntryUuidClone) {
            newEntryViewModel.entryCreated.value = true
            onSuccess()
        }
    }

    @Composable
    private fun AccountSpecificFields(newEntryViewModel: NewEntryViewModel) {
        var usernameIsEmptyProblem by remember { mutableStateOf(false) }
        var passwordIsEmptyProblem by remember { mutableStateOf(false) }
        var mfaSecretIsInvalidProblem by remember { mutableStateOf(false) }

        Column {
            // Username textfield
            OutlinedTextField(
                value = newEntryViewModel.username,
                onValueChange = { it ->
                    usernameIsEmptyProblem = it.text.isEmpty()
                    newEntryViewModel.username = it
                    newEntryViewModel.allRequiredFieldsAreFilled = newEntryViewModel.checkRequiredFields()
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

            // Password textfield
            OutlinedTextField(
                value = newEntryViewModel.password,
                onValueChange = { it ->
                    usernameIsEmptyProblem = it.text.isEmpty()
                    newEntryViewModel.password = it
                    newEntryViewModel.allRequiredFieldsAreFilled = newEntryViewModel.checkRequiredFields()
                },
                label = {
                    Text(text = stringResource(R.string.password))
                },
                placeholder = {
                    Text(text = stringResource(R.string.password_placeholder))
                },
                singleLine = true,
                isError = passwordIsEmptyProblem,
                supportingText = @Composable {
                    if (passwordIsEmptyProblem)
                        Text(text = stringResource(R.string.fill_this_field_up_pls),
                            style = MaterialTheme.typography.bodySmall)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    errorSupportingTextColor = MaterialTheme.colorScheme.error,
                )
            )

            // Generate password button
            TextButton(
                onClick = {
                    newEntryViewModel.generatePassword()
                }
            ) {
                Text(
                    text = stringResource(R.string.generate_password_for_me),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // MFA textfield
            OutlinedTextField(
                value = newEntryViewModel.mfaSecret,
                onValueChange = { it ->
                    mfaSecretIsInvalidProblem = !MFAHandler().verifySecret(it.text)
                    newEntryViewModel.mfaSecret = it
                    newEntryViewModel.allRequiredFieldsAreFilled = newEntryViewModel.checkRequiredFields()
                },
                label = {
                    Text(text = stringResource(R.string.mfa_secret))
                },
                singleLine = true,
                isError = mfaSecretIsInvalidProblem,
                supportingText = @Composable {
                    if (mfaSecretIsInvalidProblem)
                        Text(text = stringResource(R.string.mfa_secret_invalid),
                            style = MaterialTheme.typography.bodySmall)
                    else
                        Text(
                            text = stringResource(R.string.mfa_secret_supporting_text),
                            style = MaterialTheme.typography.bodySmall
                        )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    errorSupportingTextColor = MaterialTheme.colorScheme.error,
                )
            )

            // Recovery codes section
            Column {
                if (!newEntryViewModel.recoveryCodes.isEmpty()) {
                    repeat(newEntryViewModel.recoveryCodes.size) { index ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = newEntryViewModel.recoveryCodes[index],
                                onValueChange = { it ->
                                    newEntryViewModel.recoveryCodes[index] = it
                                },
                                label = {
                                    // recovery code #i
                                    Text(text = "${stringResource(R.string.recovery_code_label)}${index}")
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    errorSupportingTextColor = MaterialTheme.colorScheme.error,
                                )
                            )
                            if (newEntryViewModel.recoveryCodes.size > 1) {
                                IconButton(
                                    onClick = {newEntryViewModel.deleteRecoveryCode(index = index)},
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.delete_24px),
                                        contentDescription = "Trash bin icon. Do we really need content descriptions? I am thinking of putting some rap lyrics or Exotic Butters into a CD soon. ," ,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // Add some space before the button
                    TextButton(
                        onClick = {
                            newEntryViewModel.addRecoveryCode()
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_24px),
                            contentDescription = "Add button.",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // Space between icon and text
                        Text(text = stringResource(R.string.add_another_recovery_code))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CardSpecificFields(newEntryViewModel: NewEntryViewModel) {
        var cardNumberInvalidProblem by remember { mutableStateOf(false) }
        var cardNumberTooShortProblem by remember { mutableStateOf(false) }
        var cardNumberTooLongProblem by remember { mutableStateOf(false) }
        var cardExpirationDateInvalidProblem by remember { mutableStateOf(false) }
        var cardCvcCvvInvalidProblem by remember { mutableStateOf(false) }

        Column {
            val brandStringsToBrands= mapOf(
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
                    Text (
                        text = stringResource(R.string.cards_brand),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                    Text(
                        text = brandStringsToBrands.filterValues { it == newEntryViewModel.selectedCardBrand }.keys.first(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            // Card Number Field
            OutlinedTextField(
                value = newEntryViewModel.cardNumber,
                onValueChange = { it ->
                        try {
                            cardNumberTooLongProblem = !newEntryViewModel.validateCardCredentials(CardCredentials.Number, it.text)
                            cardNumberInvalidProblem = false
                            cardNumberTooShortProblem = false
                        } catch (e: IllegalArgumentException) {
                            if (e.message.contentEquals(newEntryViewModel.CARD_NUMBER_TOO_SHORT_EXCEPTION.message))
                                cardNumberTooShortProblem = true
                        } finally {
                            newEntryViewModel.cardNumber = it
                            newEntryViewModel.allRequiredFieldsAreFilled = newEntryViewModel.checkRequiredFields()
                        }
                },
                label = {
                    Text(text = stringResource(R.string.card_number))
                },
                placeholder = {
                    Text(text = stringResource(R.string.card_number_placeholder))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = cardNumberInvalidProblem || cardNumberTooShortProblem || cardNumberTooLongProblem,
                supportingText = @Composable {
                    if (cardNumberInvalidProblem) {
                        Text(text = stringResource(R.string.error_card_number_invalid),
                            style = MaterialTheme.typography.bodySmall)
                    } else if (cardNumberTooShortProblem) {
                        Text(text = stringResource(R.string.error_card_number_too_short),
                            style = MaterialTheme.typography.bodySmall)
                    }
                    else if (cardNumberTooLongProblem) {
                        Text(text = stringResource(R.string.error_card_number_too_long),
                            style = MaterialTheme.typography.bodySmall)
                    }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Expiration MM/YY Field
                OutlinedTextField(
                    value = newEntryViewModel.expirationMMYY,
                    onValueChange = { it ->
                        try {
                              cardExpirationDateInvalidProblem = !newEntryViewModel.validateCardCredentials(CardCredentials.ExpirationDate, it.text)
                        } catch (e: IllegalArgumentException) {
                            // have no idea how would this happen, considering that exceptions are only meant to happen with card numbers inside of that function, xd :^
                            println(e)
                        } finally {
                            newEntryViewModel.expirationMMYY = it
                        } },
                    label = {
                        Text(text = stringResource(R.string.card_expdate))
                    },
                    placeholder = {
                        Text(text = stringResource(R.string.card_mm_yy_placeholder))
                    },
                    modifier = Modifier.weight(1f),
                    isError = cardExpirationDateInvalidProblem,
                    supportingText = @Composable {
                        if (cardExpirationDateInvalidProblem) {
                            Text(text = stringResource(R.string.error_card_expdate_invalid),
                                style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        errorSupportingTextColor = MaterialTheme.colorScheme.error,
                    )
                )

                // CVC/CVV Field
                OutlinedTextField(
                    value = newEntryViewModel.cvcCVV,
                    onValueChange = { it ->
                        try {
                                cardCvcCvvInvalidProblem = !newEntryViewModel.validateCardCredentials(CardCredentials.CVC_CVV, it.text)
                        } catch (e: IllegalArgumentException) {
                            // Justin Case: Prepared for anything.
                            // https://youtube.com/shorts/h7EZR6ppVR8?si=EqTZ2WWq2sQnUJSN
                            println(e)
                        } finally {
                            newEntryViewModel.cvcCVV = it
                            newEntryViewModel.allRequiredFieldsAreFilled = newEntryViewModel.checkRequiredFields()
                        }
                    },
                    label = {
                        Text(text = stringResource(R.string.card_cvc_cvv_placeholder))
                    },
                    modifier = Modifier.weight(1f),
                    isError = cardCvcCvvInvalidProblem,
                    supportingText = @Composable {
                        if (cardCvcCvvInvalidProblem)
                            Text(text = stringResource(R.string.error_card_cvccvv_invalid),
                                style = MaterialTheme.typography.bodySmall)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        errorSupportingTextColor = MaterialTheme.colorScheme.error,
                    )
                )
            }
            // Cardholder Name Field
            OutlinedTextField(
                value = newEntryViewModel.cardholderName,
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
                onValueChange = { it ->
                    newEntryViewModel.cardholderName = it
                    newEntryViewModel.allRequiredFieldsAreFilled = newEntryViewModel.checkRequiredFields()
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    errorSupportingTextColor = MaterialTheme.colorScheme.error,
                )
            )
        }
    }


}