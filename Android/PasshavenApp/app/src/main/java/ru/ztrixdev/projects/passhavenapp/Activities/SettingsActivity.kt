package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.ztrixdev.projects.passhavenapp.DateTimeProcessor
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.Preferences.SecurityPrefs
import ru.ztrixdev.projects.passhavenapp.Preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.QuickComposables
import ru.ztrixdev.projects.passhavenapp.QuickComposables.FolderNameFromUri
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.SpecialCharNames
import ru.ztrixdev.projects.passhavenapp.TimeInMillis
import ru.ztrixdev.projects.passhavenapp.ViewModels.SettingsViewModel
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_DIGITS_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_LENGTH_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_SPECCHARS_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_UPPERCASE_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword
import ru.ztrixdev.projects.passhavenapp.specialCharacters
import ru.ztrixdev.projects.passhavenapp.ui.theme.AppThemeType
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.lion.lionDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.lion.lionLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.mint.mintDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.mint.mintLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.ubuntu.ubuntuDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.ubuntu.ubuntuLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w10.w10DarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w10.w10LightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w81.w81DarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.w81.w81LightScheme


class SettingsActivity : ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel by viewModels()
            val localctx = LocalContext.current
            var selectedTheme by remember {
                mutableStateOf(ThemePrefs.getSelectedTheme(localctx))
            }
            var darkTheme by remember {
                mutableStateOf(ThemePrefs.getDarkThemeBool(localctx))
            }
            PasshavenTheme(themeType = selectedTheme, darkTheme = darkTheme) {
                Column(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxHeight()
                ) {
                    when {
                        settingsViewModel.openAppearance.value -> {
                            AppearanceSettings(
                                settingsViewModel = settingsViewModel,
                                themeChanged = { newTheme -> selectedTheme = newTheme },
                                darkBoolChanged = { newBool -> darkTheme = newBool }
                            )
                        }

                        settingsViewModel.openSecurity.value -> {
                            if (settingsViewModel.openPINChange.value) {
                                ChangePIN(settingsViewModel = settingsViewModel)
                            }
                            else {
                                settingsViewModel.onSecurityOpened(context = localctx)
                                SecuritySettings(settingsViewModel)
                            }
                        }

                        settingsViewModel.openExports.value -> {
                            ExportsSettings(settingsViewModel = settingsViewModel)
                        }

                        settingsViewModel.openImports.value -> {
                            ImportsSettings()
                        }

                        settingsViewModel.openInfo.value -> {
                            Info(settingsViewModel = settingsViewModel)
                        }

                        else -> {
                            QuickComposables.Titlebar(stringResource(R.string.settings_titlebar)) {
                                val intent = Intent(this@SettingsActivity, VaultOverviewActivity::class.java)
                                this@SettingsActivity.startActivity(intent)
                            }
                            Spacer(Modifier.height(16.dp))
                            SettingsList(settingsViewModel)
                        }
                    }
                }
            }
        }
    }


    @Composable
    private fun SettingsList(settingsViewModel: SettingsViewModel) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Appearance
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)

                    .clickable(true, onClick = {
                        settingsViewModel.openAppearance.value = true
                    })
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(all = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.palette_24px),
                        contentDescription = "A palette.",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.appearance_setting),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(start = 20.dp)
                    )
                }
            }
            // Security
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)

                    .clickable(true, onClick = {
                        settingsViewModel.openSecurity.value = true
                    })
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(all = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.security_24px),
                        contentDescription = "A shield.",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.security_setting),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(start = 20.dp)
                    )
                }
            }
            // Exports
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)

                    .clickable(true, onClick = {
                        settingsViewModel.openExports.value = true
                    })
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(all = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.upload_24px),
                        contentDescription = "An arrow coming from a box.",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.export_backup_setting),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(start = 20.dp)
                    )
                }
            }
            // Imports
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)

                    .clickable(true, onClick = {
                        settingsViewModel.openImports.value = true
                    })
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(all = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.download_24px),
                        contentDescription = "An arrow smashin into a box lol.",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.import_setting),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(start = 20.dp)
                    )
                }
            }
            // Info
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)

                    .clickable(true, onClick = {
                        settingsViewModel.openInfo.value = true
                    })
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(all = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.info_24px),
                        contentDescription = "An information symbol",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.info_setting),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(start = 20.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun AppearanceSettings(settingsViewModel: SettingsViewModel, themeChanged: (AppThemeType) -> Unit, darkBoolChanged: (Boolean) -> Unit) {
        QuickComposables.Titlebar(stringResource(R.string.appearance_setting)) {
            settingsViewModel.openAppearance.value = false
        }

        // Theme selection
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            val localctx = LocalContext.current

            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            var AreWeDarkThemedRn by remember { mutableStateOf(
                ThemePrefs.getDarkThemeBool(localctx)
            ) }
            Row (
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ){
                Text(
                    text = stringResource(R.string.dark_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = AreWeDarkThemedRn,
                    onCheckedChange = { AreWeDarkThemedRn = it },
                )
            }

            LaunchedEffect(AreWeDarkThemedRn) {
                ThemePrefs.saveDarkThemeBool(localctx, AreWeDarkThemedRn)
                darkBoolChanged(AreWeDarkThemedRn)
            }

            val themes = listOf(
                Triple(R.string.theme_name_mint, mintDarkScheme, mintLightScheme),
                Triple(R.string.theme_name_w10, w10DarkScheme, w10LightScheme),
                Triple(R.string.theme_name_w81, w81DarkScheme, w81LightScheme),
                Triple(R.string.theme_name_osxlion, lionDarkScheme, lionLightScheme),
                Triple(R.string.theme_name_ubuntu, ubuntuDarkScheme, ubuntuLightScheme)
            )

            val nameResToATT = mapOf(
                AppThemeType.LION to R.string.theme_name_osxlion,
                AppThemeType.W10 to  R.string.theme_name_w10,
                AppThemeType.UBUNTU to R.string.theme_name_ubuntu,
                AppThemeType.MINT to R.string.theme_name_mint,
                AppThemeType.W81 to R.string.theme_name_w81,
            )
            val currentTheme = ThemePrefs.getSelectedTheme(localctx)
            val selectedTheme = remember { mutableStateOf(
                nameResToATT[currentTheme]
            ) }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columns
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(themes) { (nameRes, darkScheme, lightScheme) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTheme.value = nameRes }
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(nameRes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                            RadioButton(
                                selected = selectedTheme.value == nameRes,
                                onClick = null // handled by row click
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.dark_mode_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                ThemeColorsShowcase(darkScheme)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.light_mode_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                ThemeColorsShowcase(lightScheme)
                            }
                        }
                    }
                }
            }

            LaunchedEffect(selectedTheme.value) {
                val themeATT = nameResToATT.entries.firstOrNull {
                    it.value == selectedTheme.value
                }?.key
                ThemePrefs.saveSelectedTheme(localctx, themeATT)
                themeChanged(themeATT as AppThemeType)
            }
        }
    }

    @Composable
    private fun ThemeColorsShowcase(colorScheme: ColorScheme) {
        Row {
            Box(Modifier
                .background(colorScheme.primaryContainer)
                .size(20.dp)
                .padding(start = 4.dp)
            )
            Box(Modifier
                .background(colorScheme.primary)
                .size(20.dp)
                .padding(start = 4.dp)
            )
            Box(Modifier
                .background(colorScheme.background)
                .size(20.dp)
                .padding(start = 4.dp)
            )
            Box(Modifier
                .background(colorScheme.onBackground)
                .size(20.dp)
                .padding(start = 4.dp)
            )
            Box(Modifier
                .background(colorScheme.secondary)
                .size(20.dp)
                .padding(start = 4.dp)
            )
            Box(Modifier
                .background(colorScheme.secondaryContainer)
                .size(20.dp)
                .padding(start = 4.dp)
            )
        }
    }

    @Composable
    private fun SecuritySettings(settingsViewModel: SettingsViewModel) {
        QuickComposables.Titlebar(stringResource(R.string.security_setting)) {
            settingsViewModel.openSecurity.value = false
        }

        Column(Modifier.padding(all = 16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = if (settingsViewModel.pinLastChanged.longValue != 0L) {
                        "${stringResource(R.string.pin_changed_ago)} ${DateTimeProcessor.convertToHumanReadable(settingsViewModel.pinLastChanged.longValue)}"
                    } else {
                        stringResource(R.string.pin_never_changed)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .widthIn(100.dp, 160.dp)
                )

                Button (
                    onClick = {
                        settingsViewModel.openPINChange.value = true
                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.inverseSurface,
                        disabledContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier
                        .widthIn(100.dp, 200.dp)
                ) {
                    Text(
                        text = stringResource(R.string.change_pin_btn),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.flabs_explanation_part_1),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .widthIn(60.dp, 140.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                FLABSDropdown(settingsViewModel = settingsViewModel)
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = stringResource(R.string.flabs_explanation_part_2),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .widthIn(60.dp, 140.dp)
                )
            }
        }
    }

    @Composable
    private fun FLABSDropdown(settingsViewModel: SettingsViewModel) {
        var isDropDownExpanded by remember { mutableStateOf(false) }

        val flabsVariants = listOf(10, 15, 20, 25, 30)
        var selectedFlabsIndex by remember { mutableIntStateOf(0) }

        val localctx = LocalContext.current
        LaunchedEffect(Unit) {
            val result = VaultHandler().getFlabsAndFlabsr(localctx)
            selectedFlabsIndex = flabsVariants.indexOf(result.first)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                isDropDownExpanded = true
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_drop_down_24px),
                tint = MaterialTheme.colorScheme.primaryContainer,
                contentDescription = "Dropdown arrow lol :3"
            )
            Text(
                text = flabsVariants[selectedFlabsIndex].toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            DropdownMenu(
                expanded = isDropDownExpanded,
                onDismissRequest = {
                    isDropDownExpanded = false
                }) {
                flabsVariants.forEachIndexed { index, variant ->
                    DropdownMenuItem(
                        text = {
                            Text(text = variant.toString())
                        },
                        onClick = {
                            isDropDownExpanded = false
                            selectedFlabsIndex = index
                            lifecycleScope.launch {
                                settingsViewModel._setSelectedFlabs(variant, localctx)
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun ChangePIN(settingsViewModel: SettingsViewModel) {
        CPINInfo(settingsViewModel)
        CPINDigits(settingsViewModel)
        CPINPad(settingsViewModel)
    }

    @Composable
    private fun CPINInfo(settingsViewModel: SettingsViewModel) {
        IconButton(
            onClick = {
                settingsViewModel.openPINChange.value = false
            },
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .size(36.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "An arrow facing backwards, damnit",
                tint = MaterialTheme.colorScheme.primaryContainer
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.change_pin_btn),
                modifier = Modifier.padding(bottom = 24.dp),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                painter = painterResource(R.drawable.pin_48px),
                contentDescription = "PIN material icon lol",
                modifier = Modifier
                    .size(128.dp)
                    .padding(bottom = 24.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(R.string.change_pin_desc),
                modifier = Modifier.padding(bottom = 24.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text =
                    when {
                        !settingsViewModel.currentPINConfirmed.value -> stringResource(R.string.enter_current_pin)
                        !settingsViewModel.firstPromptDone.value -> stringResource(R.string.pin_creation_enter_pin)
                        !settingsViewModel.secondPromptDone.value -> stringResource(R.string.pin_creation_enter_pin_again)
                        else -> "How the hell can this happen xd?"
                    },
                modifier = Modifier.padding(bottom = 24.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    private fun CPINDigits(settingsViewModel: SettingsViewModel) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                for (i in 0 until settingsViewModel.getCurrentlyEditedPINsLen()) {
                    Box(
                        modifier = Modifier
                            .size(22.dp) // Set the size of the circle
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }

    @Composable
    private fun CPINPad(settingsViewModel: SettingsViewModel) {
        val localctx = LocalContext.current
        val padElements = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", specialCharacters[SpecialCharNames.Backspace].toString(), "0", specialCharacters[SpecialCharNames.Tick].toString())
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(padElements.size) { index ->
                    val element = padElements[index]
                    Button(
                        onClick = {
                            lifecycleScope.launch {
                                settingsViewModel.onCPINPadClick(btnClicked = element, ctx = localctx)
                                } },
                        modifier = Modifier
                            .padding(6.dp)
                            .size(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = element,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ExportsSettings(settingsViewModel: SettingsViewModel) {
        QuickComposables.Titlebar(
            text = stringResource(R.string.export_backup_setting),
            onBackButtonClickAction = {settingsViewModel.openExports.value = false}
        )
        val localctx = LocalContext.current
        var backupData by remember { mutableStateOf<Triple<Uri, Long, Long>?>(null) }
        LaunchedEffect(Unit) {
            backupData = VaultHandler().getBackupInfo(localctx)
        }
        Column(
            Modifier.padding(all = 16.dp)
        ) {
            if (backupData == null) { // Loading state
                Text("Please wait...")
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BackupEverySetting(settingsViewModel = settingsViewModel, backupEveryValue = backupData!!.second)
                    PasswordDialog(settingsViewModel = settingsViewModel)
                    BackupFolderSetting(settingsViewModel = settingsViewModel, backupFolder = backupData!!.first)
                    LastBackupBackupNow(settingsViewModel = settingsViewModel, lastBackup = backupData!!.third)
                }
            }
        }
    }

    @Composable
    private fun LastBackupBackupNow(settingsViewModel: SettingsViewModel, lastBackup: Long) {
        val localctx = LocalContext.current
        var exportDone by remember { mutableStateOf(false) }

        var lastBackupForUI by remember { mutableStateOf(lastBackup) }
        LaunchedEffect(exportDone) {
            if (exportDone) {
                val newTimestamp = withContext(Dispatchers.IO) {
                    VaultHandler().getBackupInfo(localctx).third
                }
                lastBackupForUI = newTimestamp
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween, // Pushes items to ends
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "${stringResource(R.string.last_backup_date)}:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = DateTimeProcessor.convertToHumanReadable(lastBackupForUI),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            var showWaitDialog by remember { mutableStateOf(false) }
            if (showWaitDialog) {
                QuickComposables.WaitingDialog(
                    stringResource(R.string.backing_up_wait)
                )
            }
            Row {
                Button(
                    onClick = {
                        lifecycleScope.launch {
                            try {
                                showWaitDialog = true
                                withContext(Dispatchers.IO) {
                                    settingsViewModel.export(localctx)
                                }
                            } finally {
                                showWaitDialog = false
                                exportDone = !exportDone
                            }
                        }
                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.inverseSurface,
                        disabledContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(
                        text = stringResource(R.string.make_backup_now),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    private fun BackupFolderSetting(settingsViewModel: SettingsViewModel, backupFolder: Uri) {
        val localctx = LocalContext.current
        var bfolderChanged by remember { mutableStateOf(false) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree()
        ) { directoryUri: Uri? ->
            if (directoryUri == null) {
                return@rememberLauncherForActivityResult
            }

            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(directoryUri, flags)

            GlobalScope.launch(Dispatchers.IO) {
                VaultHandler().setBackupFolder(directoryUri, localctx)
            }
            settingsViewModel._backupFolder.value = directoryUri
            bfolderChanged = true
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!bfolderChanged) {
                settingsViewModel._backupFolder.value = backupFolder
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.backup_folder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val folderUri = settingsViewModel._backupFolder.value
                if (folderUri == "".toUri()) {
                    Text(
                        text = stringResource(R.string.backup_folder_never_set),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    // Aligns the folder name to the right
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        FolderNameFromUri(uri = folderUri)
                    }
                }
            }
            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ){
                Button (
                    onClick = {
                        launcher.launch(null)
                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.inverseSurface,
                        disabledContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                ) {
                    Text(
                        text = if (settingsViewModel._backupFolder.value == "".toUri()) {
                            stringResource(R.string.set) } else {
                            stringResource(R.string.change)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                /*
                if (backupFolder != "".toUri()) {
                    Button (
                        onClick = {
                            val treeUri: Uri = settingsViewModel._backupFolder.value
                            val treeDocumentId = DocumentsContract.getTreeDocumentId(treeUri)
                            val docUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, treeDocumentId)

                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(docUri, DocumentsContract.Document.MIME_TYPE_DIR)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)

                            if (intent.resolveActivity(localctx.packageManager) != null) {
                                localctx.startActivity(intent)
                            }
                        },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.inverseSurface,
                            disabledContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.open_in_finder),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
}
                 */
            }
        }
    }

    @Composable
    private fun BackupEverySetting(settingsViewModel: SettingsViewModel, backupEveryValue: Long) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, // This will push items to the ends
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.backup_every),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            BackupEveryDropdown(settingsViewModel = settingsViewModel, backupEveryValue = backupEveryValue)
        }
    }

    @Composable
    private fun BackupEveryDropdown(settingsViewModel: SettingsViewModel, backupEveryValue: Long) {
        var isDropDownExpanded by remember { mutableStateOf(false) }

        val timeVariantsMillisToText = mapOf(
            0L to stringResource(R.string.never),
            TimeInMillis.EightHours to stringResource(R.string.eight_hrs),
            TimeInMillis.TwelveHours to stringResource(R.string.twelve_hrs),
            TimeInMillis.Day to stringResource(R.string.day),
            TimeInMillis.ThreeDays to stringResource(R.string.three_days),
            TimeInMillis.Week to stringResource(R.string.week),
            TimeInMillis.Month to stringResource(R.string.month)
        )
        var selectedTVIndex by remember { mutableIntStateOf(0) }

        val localctx = LocalContext.current
        var tvChanged by remember { mutableStateOf(false) }
        if (!tvChanged) {
            selectedTVIndex = timeVariantsMillisToText.keys.indexOf(backupEveryValue)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                isDropDownExpanded = true
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_drop_down_24px),
                tint = MaterialTheme.colorScheme.primaryContainer,
                contentDescription = "Dropdown arrow lol :3"
            )
            if (selectedTVIndex == -1) {
                "loadin"
            } else {
                timeVariantsMillisToText[timeVariantsMillisToText.keys.elementAt(selectedTVIndex)]
            }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
            DropdownMenu(
                expanded = isDropDownExpanded,
                onDismissRequest = {
                    isDropDownExpanded = false
                }) {
                timeVariantsMillisToText.forEach { (millis, text) ->
                    DropdownMenuItem(
                        text = {
                            Text(text = text)
                        },
                        onClick = {
                            isDropDownExpanded = false
                            tvChanged = true
                            selectedTVIndex = timeVariantsMillisToText.keys.indexOf(millis)
                            lifecycleScope.launch {
                                settingsViewModel._setSelectedTV(millis, localctx)
                            }

                        }
                    )
                }
            }
        }
    }


    @Composable
    private fun PasswordDialog(settingsViewModel: SettingsViewModel) {
        var isDialogOpen by remember { mutableStateOf(false) }
        var textState by remember { mutableStateOf(TextFieldValue()) }

        val localctx = LocalContext.current

        Column {
            TextButton (
                onClick = {
                    isDialogOpen = true
                }
            )  {
                Text(
                    text =
                    if (SecurityPrefs.getBackupPassword(localctx).contentEquals(""))
                        stringResource(R.string.set_backup_password)
                    else
                        stringResource(R.string.change_backup_password),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )
            }
        }
        if (isDialogOpen) {
            var digitNumber = 0; var specialCharNumber = 0; var uppercaseNumber = 0
            AlertDialog(
                onDismissRequest = { isDialogOpen = false },
                title = { Text(stringResource(R.string.backup_password)) },
                text = {
                    Column {
                        Column {
                            TextField(
                                value = textState,
                                onValueChange = { textState = it
                                    val specialCharacters = listOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', ';', ':', '\'', '"', '\\', '|', ',', '<', '.', '>', '/', '?')
                                    specialCharNumber = 0; digitNumber = 0; uppercaseNumber = 0;
                                    for (char: Char in textState.text) {
                                        if (char.isDigit())
                                            digitNumber++
                                        if (specialCharacters.contains(char))
                                            specialCharNumber++
                                        if (char.isUpperCase())
                                            uppercaseNumber++
                                    }},
                                label = { Text(stringResource(R.string.enter_backup_password)) }
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = specialCharNumber >= MP_SPECCHARS_MINIMUM,
                                    onCheckedChange = null,
                                    enabled = false,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = MaterialTheme.colorScheme.secondary,
                                        checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                                        disabledCheckedColor = MaterialTheme.colorScheme.primary,
                                        disabledUncheckedColor = MaterialTheme.colorScheme.secondary
                                    )
                                )
                                Text(stringResource(R.string.mp_creation_checkbox_special_chars),  color = MaterialTheme.colorScheme.onBackground, fontSize =12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = digitNumber >= MP_DIGITS_MINIMUM,
                                    onCheckedChange = null,
                                    enabled = false,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = MaterialTheme.colorScheme.secondary,
                                        checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                                        disabledCheckedColor = MaterialTheme.colorScheme.primary,
                                        disabledUncheckedColor = MaterialTheme.colorScheme.secondary
                                    )
                                )
                                Text(stringResource(R.string.mp_creation_checkbox_digits), color = MaterialTheme.colorScheme.onBackground, fontSize =12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = uppercaseNumber >= MP_UPPERCASE_MINIMUM,
                                    onCheckedChange = null,
                                    enabled = false,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = MaterialTheme.colorScheme.secondary,
                                        checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                                        disabledCheckedColor = MaterialTheme.colorScheme.primary,
                                        disabledUncheckedColor = MaterialTheme.colorScheme.secondary
                                    )
                                )
                                Text(stringResource(R.string.mp_creation_checkbox_uppercase),  color = MaterialTheme.colorScheme.onBackground,  fontSize =12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = textState.text.length >= MP_LENGTH_MINIMUM,
                                    onCheckedChange = null,
                                    enabled = false,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = MaterialTheme.colorScheme.secondary,
                                        checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                                        disabledCheckedColor = MaterialTheme.colorScheme.primary,
                                        disabledUncheckedColor = MaterialTheme.colorScheme.secondary
                                    )
                                )
                                Text(stringResource(R.string.mp_creation_checkbox_length),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize =12.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        enabled = MasterPassword.verify(textState.text),
                        onClick = {
                            lifecycleScope.launch {
                                settingsViewModel.setBackupPassword(textState.text, localctx)
                            }
                            isDialogOpen = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { isDialogOpen = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }


    @Composable
    private fun BackupPassowrdSetting(settingsViewModel: SettingsViewModel) {


    }

    @Composable
    private fun ImportsSettings() {

    }

    @Composable
    private fun Info(settingsViewModel: SettingsViewModel) {
        QuickComposables.Titlebar(stringResource(R.string.info_setting)) {
            settingsViewModel.openInfo.value = false
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp),
        ) {
            Column {
                Text(
                    text = "Passhaven",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = stringResource(R.string.developed_by),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Column {
                Text(
                    text = stringResource(R.string.ph_desc),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Column {
                Text(
                    text = stringResource(R.string.ph_credits),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
                Column(Modifier.padding(start = 8.dp)) {
                    Text(
                        text = stringResource(R.string.aegis_respect),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.rossman_respect),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.andy_respect),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.fam_respect),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.me_respect),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        val uriHandler = LocalUriHandler.current
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = stringResource(R.string.source_code),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = {
                        uriHandler.openUri("https://codeberg.org/ztrixdev/PasshavenApp")
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.codeberg), contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
                IconButton(
                    onClick = {
                        uriHandler.openUri("https://github.com/ztrixdev/PasshavenApp")
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.github_light),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
                IconButton(
                    onClick = {
                        uriHandler.openUri("https://gitlab.com/Faulhaj/PasshavenApp")
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.gitlab), contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
            Text(
                text = stringResource(R.string.ztrix_socials),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = {
                        uriHandler.openUri("https://bsky.app/profile/ztrixvalo.bsky.social")
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.bluesky), contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
                IconButton(
                    onClick = {
                        uriHandler.openUri("https://t.me/ztrixdev")
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.telegram), contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
                IconButton(
                    onClick = {
                        uriHandler.openUri("https://www.youtube.com/@ztrix-eu")
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.youtube), contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }

}

