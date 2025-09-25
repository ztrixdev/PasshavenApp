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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardBrands
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardCredentials
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
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    MainBody(newEntryViewModel)
                }
            }
        }
    }

    @Composable
    private fun FolderSelectionDropdown(newEntryViewModel: NewEntryViewModel) {
        val isDropDownExpanded = remember {
            mutableStateOf(false)
        }

        val itemPosition = remember {
            mutableIntStateOf(-1)
        }

        val folders = newEntryViewModel.getFolders(LocalContext.current)

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
                    // todo: insert a cool transition to VaultOverviewActivity when it's done here
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
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(start = 40.dp)
            )
        }
    }

    @Composable
    private fun MainBody(newEntryViewModel: NewEntryViewModel) {
        Box(
            Modifier.height(30.dp).background(MaterialTheme.colorScheme.secondaryContainer)
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
                    Text(stringResource(R.string.name_cannot_be_empty))
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
                EntryTypes.Account -> AccountSpecificFields()
                EntryTypes.Card -> CardSpecificFields(newEntryViewModel)
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
                    if (newEntryViewModel.selectedEntryType == EntryTypes.Card)
                        newEntryViewModel.pushNewEntry(card = newEntryViewModel.createCard(), context =  localctx)
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
        }
    }

    @Composable
    private fun AccountSpecificFields() {

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
                placeholder = {
                    Text(text = stringResource(R.string.card_number_placeholder))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = cardNumberInvalidProblem || cardNumberTooShortProblem || cardNumberTooLongProblem,
                supportingText = @Composable {
                    if (cardNumberInvalidProblem) {
                        Text(text = stringResource(R.string.error_card_number_invalid))
                    } else if (cardNumberTooShortProblem) {
                        Text(text = stringResource(R.string.error_card_number_too_short))
                    }
                    else if (cardNumberTooLongProblem) {
                        Text(text = stringResource(R.string.error_card_number_too_long))
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
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    placeholder = {
                        Text(text = stringResource(R.string.card_mm_yy_placeholder))
                    },
                    modifier = Modifier.weight(1f),
                    isError = cardExpirationDateInvalidProblem,
                    supportingText = @Composable {
                        if (cardExpirationDateInvalidProblem) {
                            Text(text = stringResource(R.string.error_card_expdate_invalid))
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
                            Text(text = stringResource(R.string.error_card_cvccvv_invalid))
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
                placeholder =  {
                    Text(text = stringResource(R.string.cardholder_name_placeholder))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
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