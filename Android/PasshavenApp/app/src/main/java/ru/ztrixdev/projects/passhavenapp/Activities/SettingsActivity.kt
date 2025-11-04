package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import ru.ztrixdev.projects.passhavenapp.Preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.ViewModels.SettingsViewModel
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
        enableEdgeToEdge()
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
                    if (settingsViewModel.openAppearance.value) {
                        AppearanceSettings(
                            settingsViewModel = settingsViewModel,
                            themeChanged = {newTheme -> selectedTheme = newTheme},
                            darkBoolChanged = {newBool -> darkTheme = newBool}
                        )
                    } else if (settingsViewModel.openSecurity.value) {
                        SecuritySettings()
                    } else if (settingsViewModel.openExports.value) {
                        ExportsSettings()
                    } else if (settingsViewModel.openImports.value) {
                        ImportsSettings()
                    } else if (settingsViewModel.openInfo.value) {
                        Info()
                    } else {
                        SettingsTitlebar()
                        Spacer(
                            Modifier.height(16.dp)
                        )
                        SettingsList(settingsViewModel)
                    }
                }
            }
        }
    }


    @Composable
    private fun SettingsTitlebar() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .padding(all = 10.dp)
        ) {
            IconButton(
                onClick = {
                    val intent = Intent(this@SettingsActivity, VaultOverviewActivity::class.java)
                    this@SettingsActivity.startActivity(intent)
                },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "An arrow facing backwards, damnit",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = stringResource(R.string.settings_titlebar),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(start = 40.dp)
            )
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
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
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
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
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
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
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
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
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
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
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
        // Appearance titlebar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .padding(all = 10.dp)
        ) {
            IconButton(
                onClick = {
                    settingsViewModel.openAppearance.value = false
                },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "An arrow facing backwards, damnit",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = stringResource(R.string.appearance_setting),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(start = 40.dp)
            )
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
    private fun SecuritySettings() {

    }

    @Composable
    private fun ExportsSettings() {

    }

    @Composable
    private fun ImportsSettings() {

    }

    @Composable
    private fun Info() {

    }

}

