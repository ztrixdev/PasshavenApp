package ru.ztrixdev.projects.passhavenapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.ztrixdev.projects.passhavenapp.DateTimeProcessor
import ru.ztrixdev.projects.passhavenapp.QuickComposables
import ru.ztrixdev.projects.passhavenapp.QuickComposables.FolderNameFromUri
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.SpecialCharNames
import ru.ztrixdev.projects.passhavenapp.TimeInMillis
import ru.ztrixdev.projects.passhavenapp.Utils
import ru.ztrixdev.projects.passhavenapp.handlers.ExportsHandler
import ru.ztrixdev.projects.passhavenapp.handlers.ExportsHandler.isNotEmpty
import ru.ztrixdev.projects.passhavenapp.handlers.SessionHandler
import ru.ztrixdev.projects.passhavenapp.handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_DIGITS_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_LENGTH_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_SPECCHARS_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MP_UPPERCASE_MINIMUM
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword
import ru.ztrixdev.projects.passhavenapp.preferences.SecurityPrefs
import ru.ztrixdev.projects.passhavenapp.preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.preferences.VaultPrefs
import ru.ztrixdev.projects.passhavenapp.room.Account
import ru.ztrixdev.projects.passhavenapp.room.Card
import ru.ztrixdev.projects.passhavenapp.room.Folder
import ru.ztrixdev.projects.passhavenapp.specialCharacters
import ru.ztrixdev.projects.passhavenapp.ui.theme.AppThemeType
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.amoled.amoledDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.amoled.amoledLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.aqua.aquaDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.aqua.aquaLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.codered.codeRedDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.codered.codeRedLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.hotpink.hotpinkDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.hotpink.hotpinkLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.monochroma.monochromaDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.monochroma.monochromaLightScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.peppermint.peppermintDarkScheme
import ru.ztrixdev.projects.passhavenapp.ui.theme.peppermint.peppermintLightScheme
import ru.ztrixdev.projects.passhavenapp.viewModels.SettingsViewModel


val SETTINGS_ACTIVITY_EXTRA_CHANGE_BACKUP_PASSWORD_KEY = "change_backup_password"

class SettingsActivity : ComponentActivity() {
    val settingsViewModel: SettingsViewModel by viewModels()
    override fun onResume() {
        super.onResume()
        val isSessionExpd = SessionHandler.isSessionExpired(this.applicationContext)
        if (isSessionExpd) {
            startActivity(
                Intent(this.applicationContext, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
    var isPasswordDialogOpen by mutableStateOf(false)

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val localctx = LocalContext.current
            var selectedTheme by remember {
                mutableStateOf(ThemePrefs.getSelectedTheme(localctx))
            }
            var darkTheme by remember {
                mutableStateOf(ThemePrefs.getDarkThemeBool(localctx))
            }
            var dynamicColors by remember {
                mutableStateOf(ThemePrefs.getDynamicColorsBool(localctx))
            }

            PasshavenTheme(themeType = selectedTheme, darkTheme = darkTheme,  dynamicColors = dynamicColors) {
                Column(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxHeight()
                ) {
                    when {
                        settingsViewModel.openAppearance.value -> {
                            AppearanceSettings(
                                themeChanged = { newTheme -> selectedTheme = newTheme },
                                darkBoolChanged = { newBool -> darkTheme = newBool },
                                dynamicColorsChanged = { newBool -> dynamicColors = newBool }
                            )
                        }

                        settingsViewModel.openSecurity.value -> {
                            if (settingsViewModel.openPINChange.value) {
                                ChangePIN()
                            }
                            else {
                                settingsViewModel.onSecurityOpened(context = localctx)
                                SecuritySettings()
                            }
                        }

                        intent.getBooleanExtra(SETTINGS_ACTIVITY_EXTRA_CHANGE_BACKUP_PASSWORD_KEY, false) -> {
                            ExportsSettings()
                            isPasswordDialogOpen = true
                        }


                        settingsViewModel.openExports.value -> {
                            ExportsSettings()
                        }

                        settingsViewModel.openImports.value -> {
                            ImportsSettings()
                        }

                        settingsViewModel.openInfo.value -> {
                            Info()
                        }

                        else -> {
                            QuickComposables.BackButtonTitlebar(stringResource(R.string.settings_titlebar)) {
                                val intent = Intent(this@SettingsActivity, VaultOverviewActivity::class.java)
                                this@SettingsActivity.startActivity(intent)
                            }
                            Spacer(Modifier.height(16.dp))
                            SettingsList()
                        }
                    }
                }
            }
        }
    }


    @Composable
    private fun SettingsList() {
        val paletteIcon = painterResource(R.drawable.palette_24px)
        val securityIcon = painterResource(R.drawable.security_24px)
        val exportIcon = painterResource(R.drawable.upload_24px)
        val importIcon = painterResource(R.drawable.download_24px)
        val infoIcon = painterResource(R.drawable.info_24px)


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                SettingsListItem(
                    title = stringResource(R.string.appearance_setting),
                    icon = paletteIcon,
                    onClick = { settingsViewModel.openAppearance.value = true }
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.security_setting),
                    icon = securityIcon,
                    onClick = { settingsViewModel.openSecurity.value = true }
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.export_backup_setting),
                    icon = exportIcon,
                    onClick = { settingsViewModel.openExports.value = true }
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.import_setting),
                    icon = importIcon,
                    onClick = { settingsViewModel.openImports.value = true }
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.info_setting),
                    icon = infoIcon,
                    onClick = { settingsViewModel.openInfo.value = true }
                )
            }
        }
    }

    @Composable
    private fun SettingsListItem(
        title: String,
        icon: Painter,
        onClick: () -> Unit
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            leadingContent = {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.clickable(onClick = onClick)
        )
    }

    @Composable
    private fun AppearanceSettings(
        themeChanged: (AppThemeType) -> Unit,
        darkBoolChanged: (Boolean) -> Unit,
        dynamicColorsChanged: (Boolean) -> Unit
    ) {
        QuickComposables.BackButtonTitlebar(stringResource(R.string.appearance_setting)) {
            settingsViewModel.openAppearance.value = false
        }

        val localctx = LocalContext.current
        val currentTheme = ThemePrefs.getSelectedTheme(localctx)

        var selectedTheme by remember { mutableStateOf(currentTheme) }

        LaunchedEffect(selectedTheme) {
            ThemePrefs.saveSelectedTheme(localctx, selectedTheme)
            themeChanged(selectedTheme)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                var isDarkTheme by remember { mutableStateOf(ThemePrefs.getDarkThemeBool(localctx)) }
                var useDynamicColors by remember { mutableStateOf(ThemePrefs.getDynamicColorsBool(localctx)) }

                LaunchedEffect(isDarkTheme) {
                    ThemePrefs.saveDarkThemeBool(localctx, isDarkTheme)
                    darkBoolChanged(isDarkTheme)
                }
                LaunchedEffect(useDynamicColors) {
                    ThemePrefs.saveDynamicColorsBool(localctx, useDynamicColors)
                    dynamicColorsChanged(useDynamicColors)
                }

                Surface(shape = MaterialTheme.shapes.large, tonalElevation = 2.dp) {
                    Column(Modifier.padding(vertical = 8.dp)) {
                        SettingsSwitch(
                            title = stringResource(R.string.dark_theme),
                            icon = painterResource(R.drawable.dark_mode_24px),
                            checked = isDarkTheme,
                            onCheckedChange = { isDarkTheme = it }
                        )

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsSwitch(
                                title = stringResource(R.string.dynamic_colors),
                                icon = painterResource(R.drawable.palette_24px),
                                checked = useDynamicColors,
                                onCheckedChange = { useDynamicColors = it }
                            )
                        }
                    }
                }
            }
            item {
                Text(
                    text = stringResource(R.string.theme),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            val themes = listOf(
                (AppThemeType.Amoled to R.string.theme_name_amoled) to (amoledDarkScheme to amoledLightScheme),
                (AppThemeType.Peppermint to R.string.theme_name_mint) to (peppermintDarkScheme to peppermintLightScheme),
                (AppThemeType.Aqua to R.string.theme_name_aqua) to (aquaDarkScheme to aquaLightScheme),
                (AppThemeType.HotPink to R.string.theme_name_hotpink) to (hotpinkDarkScheme to hotpinkLightScheme),
                (AppThemeType.Monochroma to R.string.theme_name_monochroma) to (monochromaDarkScheme to monochromaLightScheme),
                (AppThemeType.CodeRED to R.string.theme_name_codered) to (codeRedDarkScheme to codeRedLightScheme)
            )

            items(themes) { themeInfo ->
                val (themeType, nameRes) = themeInfo.first
                val (darkScheme, lightScheme) = themeInfo.second

                ThemeCard(
                    name = stringResource(nameRes),
                    darkScheme = darkScheme,
                    lightScheme = lightScheme,
                    isSelected = selectedTheme == themeType,
                    onClick = { selectedTheme = themeType }
                )
            }
        }
    }

    @Composable
    private fun SettingsSwitch(
        title: String,
        icon: Painter,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        ListItem(
            headlineContent = { Text(title, style = MaterialTheme.typography.bodyLarge) },
            leadingContent = {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = {
                Switch(checked = checked, onCheckedChange = null)
            },
            modifier = Modifier.toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
        )
    }

    @Composable
    private fun SettingsSwitch(
        title: String,
        icon: ImageVector,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        ListItem(
            headlineContent = { Text(title, style = MaterialTheme.typography.bodyLarge) },
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = {
                Switch(checked = checked, onCheckedChange = null)
            },
            modifier = Modifier.toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
        )
    }

    @Composable
    private fun ThemeCard(
        name: String,
        darkScheme: ColorScheme,
        lightScheme: ColorScheme,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    RadioButton(
                        selected = isSelected,
                        onClick = null
                    )
                }

                ThemeColorsShowcase(icon = painterResource(R.drawable.dark_mode_24px), colorScheme = darkScheme)
                ThemeColorsShowcase(icon = painterResource(R.drawable.light_mode_24px), colorScheme = lightScheme)
            }
        }
    }

    @Composable
    private fun ThemeColorsShowcase(icon: Painter, colorScheme: ColorScheme) {
        val colors = listOf(
            colorScheme.primary,
            colorScheme.secondary,
            colorScheme.tertiary,
            colorScheme.surface,
            colorScheme.surfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )
            Canvas(modifier = Modifier
                .height(20.dp)
                .fillMaxWidth()) {
                val boxWidth = size.width / colors.size
                colors.forEachIndexed { index, color ->
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x = index * boxWidth, y = 0f),
                        size = Size(width = boxWidth - 4.dp.toPx(), height = size.height),
                        cornerRadius = CornerRadius(x = 4.dp.toPx(), y = 4.dp.toPx())
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SecuritySettings() {
        QuickComposables.BackButtonTitlebar(stringResource(R.string.security_setting)) {
            settingsViewModel.openSecurity.value = false
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (settingsViewModel.pinLastChanged.longValue != 0L) {
                                "${stringResource(R.string.pin_changed_ago)} ${
                                    DateTimeProcessor.convertToHumanReadable(settingsViewModel.pinLastChanged.longValue)
                                }"
                            } else {
                                stringResource(R.string.pin_never_changed)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { settingsViewModel.openPINChange.value = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.password_40px),
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = stringResource(R.string.change_pin_btn))
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.flabs_explanation_part_1),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                FLABSDropdown()
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.flabs_explanation_part_2),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FLABSDropdown() {
        var isDropDownExpanded by remember { mutableStateOf(false) }
        val flabsVariants = listOf(10, 15, 20, 25, 30)
        var selectedFlabsValue by remember { mutableIntStateOf(flabsVariants.first()) }
        val localctx = LocalContext.current

        LaunchedEffect(Unit) {
            selectedFlabsValue = VaultPrefs.getFlabs(localctx)
        }

        ExposedDropdownMenuBox(
            expanded = isDropDownExpanded,
            onExpandedChange = { isDropDownExpanded = !isDropDownExpanded },
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                value = selectedFlabsValue.toString(),
                onValueChange = {},
                readOnly = true,
                textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropDownExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                expanded = isDropDownExpanded,
                onDismissRequest = { isDropDownExpanded = false }
            ) {
                flabsVariants.forEach { variant ->
                    DropdownMenuItem(
                        text = { Text(variant.toString()) },
                        onClick = {
                            selectedFlabsValue = variant
                            isDropDownExpanded = false
                            lifecycleScope.launch {
                                settingsViewModel._setSelectedFlabs(variant, localctx)
                            }
                        }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangePIN() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.change_pin_btn)) },
                    navigationIcon = {
                        IconButton(onClick = { settingsViewModel.openPINChange.value = false }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { CPINInfo() }
                item { CPINDigits() }
                item { CPINPad() }
            }
        }
    }

    @Composable
    private fun CPINInfo() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Icon(
                painter = painterResource(R.drawable.pin_48px),
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .padding(bottom = 24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.change_pin_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = when {
                    !settingsViewModel.currentPINConfirmed.value -> stringResource(R.string.enter_current_pin)
                    !settingsViewModel.firstPromptDone.value -> stringResource(R.string.pin_creation_enter_pin)
                    !settingsViewModel.secondPromptDone.value -> stringResource(R.string.pin_creation_enter_pin_again)
                    else -> ""
                },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }

    @Composable
    private fun CPINDigits() {
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
    private fun CPINPad() {
        val localctx = LocalContext.current
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .width(240.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Row 1
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumpadButton(text = "1") {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick("1", localctx) }
                }
                NumpadButton(text = "2") {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick("2", localctx) }
                }
                NumpadButton(text = "3") {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick("3", localctx) }
                }
            }
            // Row 2
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumpadButton(text = "4") {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick("4", localctx) }
                }
                NumpadButton(text = "5") {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick("5", localctx) }
                }
                NumpadButton(text = "6") {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick("6", localctx) }
                }
            }
            // Row 3
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumpadButton(text = "7") {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick("7", localctx) }
                }
                NumpadButton(text = "8") {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick("8", localctx) }
                }
                NumpadButton(text = "9") {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick("9", localctx) }
                }
            }
            // Row 4
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumpadButton(icon = Icons.AutoMirrored.Filled.ArrowBack) {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick(specialCharacters[SpecialCharNames.Backspace].toString(), localctx) }
                }
                NumpadButton(text = "0") {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick("0", localctx) }
                }
                NumpadButton(icon = Icons.Default.Check) {
                    lifecycleScope.launch { settingsViewModel.onCPINPadClick(specialCharacters[SpecialCharNames.Tick].toString(), localctx) }
                }
            }
        }
    }


    @Composable
    private fun NumpadButton(
        text: String? = null,
        icon: ImageVector? = null,
        onClick: () -> Unit
    ) {
        FilledTonalButton(
            onClick = onClick,
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color.Transparent
            )
        ) {
            if (text != null) {
                Text(text = text, style = MaterialTheme.typography.headlineMedium)
            } else if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(32.dp))
            }
        }
    }

    @Composable
    private fun ExportsSettings() {
        QuickComposables.BackButtonTitlebar(
            text = stringResource(R.string.export_backup_setting),
            onBackButtonClickAction = { settingsViewModel.openExports.value = false }
        )

        val localctx = LocalContext.current
        var backupData by remember { mutableStateOf<Triple<Uri, Long, Long>?>(null) }
        var isInitialLoad by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            backupData = VaultHandler.getBackupInfo(localctx)
            isInitialLoad = false
        }

        if (isInitialLoad) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            backupData?.let { data ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        BackupEverySetting(
                            backupEveryValue = data.second,
                            onBackupEveryChanged = { newBackupValue ->
                                backupData = backupData?.copy(second = newBackupValue)
                                lifecycleScope.launch {
                                    settingsViewModel._setSelectedTV(newBackupValue, localctx)
                                }
                            }
                        )
                    }
                    item {
                        PasswordDialog()
                    }
                    item {
                        BackupFolderSetting(
                            backupFolder = data.first,
                            onFolderChanged = { newUri ->
                                backupData = backupData?.copy(first = newUri)
                                VaultHandler.setBackupFolder(newUri, localctx)
                            }
                        )
                    }
                    item {
                        LastBackupBackupNow(
                            lastBackup = data.third,
                            onBackupComplete = { newTimestamp ->
                                backupData = backupData?.copy(third = newTimestamp)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun LastBackupBackupNow(lastBackup: Long, onBackupComplete: (Long) -> Unit) {
        val localctx = LocalContext.current
        var showWaitDialog by remember { mutableStateOf(false) }

        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.last_backup_date)) },
                    trailingContent = {
                        Text(
                            text = DateTimeProcessor.convertToHumanReadable(lastBackup),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.End
                        )
                    }
                )

                if (showWaitDialog) {
                    QuickComposables.WaitingDialog(stringResource(R.string.backing_up_wait))
                }

                Button(
                    onClick = {
                        lifecycleScope.launch {
                            try {
                                showWaitDialog = true
                                settingsViewModel.export(localctx)
                                val newTimestamp = withContext(Dispatchers.IO) {
                                    VaultHandler.getBackupInfo(localctx).third
                                }
                                onBackupComplete(newTimestamp)
                            } finally {
                                showWaitDialog = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(R.string.make_backup_now))
                }
            }
        }
    }

    @Composable
    private fun BackupFolderSetting(backupFolder: Uri, onFolderChanged: (Uri) -> Unit) {
        val localctx = LocalContext.current

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree()
        ) { directoryUri: Uri? ->
            directoryUri?.let {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                localctx.contentResolver.takePersistableUriPermission(it, flags)
                onFolderChanged(it)
            }
        }

        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.backup_folder)) },
                    trailingContent = {
                        if (backupFolder == "".toUri()) {
                            Text(
                                text = stringResource(R.string.backup_folder_never_set),
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            FolderNameFromUri(uri = backupFolder)
                        }
                    }
                )

                FilledTonalButton(
                    onClick = { launcher.launch(null) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.folder_open_24px),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = if (backupFolder == "".toUri()) {
                            stringResource(R.string.set)
                        } else {
                            stringResource(R.string.change)
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun BackupEverySetting(backupEveryValue: Long, onBackupEveryChanged: (Long) -> Unit) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.backup_every),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.widthIn(100.dp, 240.dp)
            )
            BackupEveryDropdown(
                backupEveryValue = backupEveryValue,
                onBackupEveryChanged = onBackupEveryChanged
            )
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BackupEveryDropdown(backupEveryValue: Long, onBackupEveryChanged: (Long) -> Unit) {
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
        val selectedText = timeVariantsMillisToText[backupEveryValue] ?: ""

        ExposedDropdownMenuBox(
            expanded = isDropDownExpanded,
            onExpandedChange = { isDropDownExpanded = !isDropDownExpanded },
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = selectedText,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropDownExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
            ExposedDropdownMenu(
                expanded = isDropDownExpanded,
                onDismissRequest = { isDropDownExpanded = false },
            ) {
                timeVariantsMillisToText.forEach { (millis, text) ->
                    DropdownMenuItem(
                        text = { Text(text = text) },
                        onClick = {
                            onBackupEveryChanged(millis)
                            isDropDownExpanded = false
                        }
                    )
                }
            }
        }
    }


    @Composable
    private fun PasswordDialog() {
        var textState by remember { mutableStateOf(TextFieldValue()) }

        val localctx = LocalContext.current

        Column {
            TextButton (
                onClick = {
                    isPasswordDialogOpen = true
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
        if (isPasswordDialogOpen) {
            var digitNumber by remember(textState) { mutableIntStateOf(0) }
            var specialCharNumber by remember(textState) { mutableIntStateOf(0) }
            var uppercaseNumber by remember(textState) { mutableIntStateOf(0) }

            AlertDialog(
                onDismissRequest = { isPasswordDialogOpen = false },
                title = { Text(stringResource(R.string.backup_password)) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = textState,
                            onValueChange = {
                                textState = it
                                val specialCharacters = listOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', ';', ':', '\'', '"', '\\', '|', ',', '<', '.', '>', '/', '?')
                                // Recalculate counts on each change
                                digitNumber = it.text.count { char -> char.isDigit() }
                                specialCharNumber = it.text.count { char -> specialCharacters.contains(char) }
                                uppercaseNumber = it.text.count { char -> char.isUpperCase() }
                            },
                            label = { Text(stringResource(R.string.enter_backup_password)) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password criteria checks
                        PasswordCriteriaRow(
                            isMet = specialCharNumber >= MP_SPECCHARS_MINIMUM,
                            text = stringResource(R.string.mp_creation_checkbox_special_chars)
                        )
                        PasswordCriteriaRow(
                            isMet = digitNumber >= MP_DIGITS_MINIMUM,
                            text = stringResource(R.string.mp_creation_checkbox_digits)
                        )
                        PasswordCriteriaRow(
                            isMet = uppercaseNumber >= MP_UPPERCASE_MINIMUM,
                            text = stringResource(R.string.mp_creation_checkbox_uppercase)
                        )
                        PasswordCriteriaRow(
                            isMet = textState.text.length >= MP_LENGTH_MINIMUM,
                            text = stringResource(R.string.mp_creation_checkbox_length)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        enabled = MasterPassword.verify(textState.text),
                        onClick = {
                            lifecycleScope.launch {
                                settingsViewModel.setBackupPassword(textState.text, localctx)
                                SecurityPrefs.setLastBPChange(localctx, System.currentTimeMillis())
                            }
                            isPasswordDialogOpen = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isPasswordDialogOpen = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }

    @Composable
    private fun PasswordCriteriaRow(isMet: Boolean, text: String) {
        val icon = if (isMet) Icons.Filled.Check else Icons.Filled.Close
        val tint = if (isMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // The text provides context
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }




    private val importPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            val content = Utils.readFile(uri, contentResolver)
            val canImport = settingsViewModel.checkImportability(content)
            when (canImport) {
                settingsViewModel.IMPORTABLE_SIGNAL -> {
                    settingsViewModel.importFileContents = content
                }
                settingsViewModel.CORRUPTED_SIGNAL -> {
                    settingsViewModel.importBackToMain()
                    Toast.makeText(
                        this.applicationContext,
                        R.string.corrupted_file,
                        Toast.LENGTH_LONG
                    ).show()
                }
                settingsViewModel.ENCRYPTED_SIGNAL -> {
                    settingsViewModel.importFileContents = content
                    settingsViewModel.currentImportStage = SettingsViewModel.ImportStages.CheckPassword
                }
                Utils.IO_EXCEPTION_SIGNAL -> {
                    settingsViewModel.importBackToMain()
                    Toast.makeText(
                        this.applicationContext,
                        R.string.corrupted_file,
                        Toast.LENGTH_LONG
                    ).show()
                }
                Utils.FILE_NOT_FOUND_SIGNAL -> {
                    settingsViewModel.importBackToMain()
                    Toast.makeText(
                        this.applicationContext,
                        R.string.file_not_found,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }




    @Composable
    private fun MainImport() {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Column() {
                            Text(
                                text = stringResource(R.string.import_file_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.import_file_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
                                    importPicker.launch(arrayOf("application/octet-stream", "*/*"))
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.download_24px),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(stringResource(R.string.select_file_button))
                            }
                        }
                    }
                }
            }
        }
    }

    private var checkImportFinishButton by mutableStateOf(false)

    @Composable
    private fun SelectEntries() {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item() {
                val localctx = LocalContext.current
                var isButtonEnabled by remember { mutableStateOf(settingsViewModel.includedImportEntries.isNotEmpty()) }
                LaunchedEffect(checkImportFinishButton) {
                    isButtonEnabled = settingsViewModel.includedImportEntries.isNotEmpty()
                    checkImportFinishButton = false
                }
                Button(
                    onClick = {
                        lifecycleScope.launch(Dispatchers.IO) {
                            settingsViewModel.finishImport(localctx)
                            settingsViewModel.clearImport()
                        }
                        Toast.makeText(
                            this@SettingsActivity,
                            R.string.successful_import,
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    enabled = isButtonEnabled
                ) {
                    if (isButtonEnabled) {
                        Text(stringResource(R.string.import_selected))
                        Icon(
                            painter = painterResource(id = R.drawable.download_24px),
                            contentDescription = null
                        )
                    } else {
                        Text(stringResource(R.string.no_entries_selected))
                    }
                }
            }
            items(
                items = settingsViewModel.getEntryFlattenedList(),
                key = { entry ->
                    when (entry) {
                        is Card -> entry.uuid
                        is Account -> entry.uuid
                        is Folder -> entry.uuid
                        else -> entry.hashCode()
                    }
                }
            ) { entry ->
                EntryRow(entry = entry)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    @Composable
    private fun EntryRow(entry: Any) {
        val entryId = when (entry) {
            is Card -> entry.uuid
            is Account -> entry.uuid
            is Folder -> entry.uuid
            else -> return
        }

        var isChecked by remember(settingsViewModel.includedImportEntries) {
            mutableStateOf(
                settingsViewModel.includedImportEntries.Card?.any { it.uuid == entryId } == true ||
                        settingsViewModel.includedImportEntries.Account?.any { it.uuid == entryId } == true ||
                        settingsViewModel.includedImportEntries.Folder?.any { it.uuid == entryId } == true
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                isChecked = !isChecked
                checkImportFinishButton = true
                if (isChecked) {
                    settingsViewModel.includeImportEntry(entry)
                } else {
                    settingsViewModel.excludeImportEntry(entry)
                }
            },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconRes = when (entry) {
                    is Card -> R.drawable.credit_card_24px
                    is Account -> R.drawable.person_24px
                    is Folder -> R.drawable.folder_open_24px
                    else -> R.drawable.browse_24px
                }
                Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = MaterialTheme.colorScheme.primary)

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    val name = when (entry) {
                        is Card -> entry.name
                        is Account -> entry.name
                        is Folder -> entry.name
                        else -> "N/A"
                    }
                    val type = when (entry) {
                        is Card -> stringResource(R.string.card)
                        is Account -> stringResource(R.string.account)
                        is Folder -> stringResource(R.string.folder)
                        else -> "N/A"
                    }
                    Text(text = name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = type, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Checkbox(
                    checked = isChecked,
                    onCheckedChange = null
                )
            }
        }
    }

    @Composable
    private fun BackupPasswordImportDialog(export: String) {
        var textState by remember { mutableStateOf(TextFieldValue()) }
        var attemptsLeft by remember { mutableIntStateOf(4) }

        if (settingsViewModel.currentImportStage == SettingsViewModel.ImportStages.CheckPassword) {
            AlertDialog(
                onDismissRequest = { settingsViewModel.importBackToMain() },
                title = { Text(stringResource(R.string.backup_password_import)) },
                text = {
                    Column {
                        Text(
                            text = stringResource(R.string.backup_password_import_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = textState,
                            onValueChange = {
                                textState = it
                            },
                            isError = attemptsLeft < 4,
                            supportingText = {
                                if (attemptsLeft < 4) {
                                    Text(stringResource(R.string.incorrect_password_attempts_left) + " " + attemptsLeft.toString())
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                    }
                },
                confirmButton = {
                    Button(
                        enabled = MasterPassword.verify(textState.text),
                        onClick = {
                            lifecycleScope.launch {
                                var export: String = ""
                                try {
                                    export = ExportsHandler.getProtectedExport(settingsViewModel.importFileContents, textState.text)
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                    textState = TextFieldValue()
                                    attemptsLeft--
                                }

                                if (export.isNotBlank()) {
                                    settingsViewModel.importFileContents = export
                                    settingsViewModel.fetchImportFileEntries()
                                    settingsViewModel.currentImportStage = SettingsViewModel.ImportStages.EntrySelect
                                }
                                if (attemptsLeft==0) {
                                    settingsViewModel.importBackToMain()
                                }
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { settingsViewModel.importBackToMain() }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }

    @Composable
    private fun ImportsSettings() {
        QuickComposables.BackButtonTitlebar(stringResource(R.string.import_setting)) {
            settingsViewModel.importBackToMain()
            settingsViewModel.openImports.value = false
        }

        when (settingsViewModel.currentImportStage) {
            SettingsViewModel.ImportStages.Main -> MainImport()
            SettingsViewModel.ImportStages.CheckPassword -> BackupPasswordImportDialog(export = settingsViewModel.importFileContents)
            SettingsViewModel.ImportStages.EntrySelect -> SelectEntries()
            else -> {}
        }
    }

    @Composable
    private fun Info() {
        QuickComposables.BackButtonTitlebar(stringResource(R.string.info_setting)) {
            settingsViewModel.openInfo.value = false
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
        ) {
            item {
                Text(
                    text = "Passhaven",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.developed_by),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.version_number),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.ph_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                SectionTitle(stringResource(R.string.ph_credits))
            }

            val creditResourceIds = listOf(
                R.string.aegis_respect,
                R.string.rossman_respect,
            )

            items(creditResourceIds) { resId ->
                CreditItem(text = stringResource(resId))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp)) // Spacing before the next section
            }

            item {
                SectionTitle(stringResource(R.string.source_code))
                val sourceLinks = listOf(
                    SocialLink(R.drawable.codeberg, "https://codeberg.org/ztrixdev/PasshavenApp"),
                    SocialLink(R.drawable.github_light, "https://github.com/ztrixdev/PasshavenApp"),
                )
                SocialIconRow(links = sourceLinks)
                Spacer(modifier = Modifier.height(32.dp))
            }
            /*
            item {
                SectionTitle(stringResource(R.string.ztrix_socials))
                val socialLinks = listOf(
                    SocialLink(R.drawable.bluesky, "https://bsky.app/profile/ztrixvalo.bsky.social"),
                    SocialLink(R.drawable.telegram, "https://t.me/amcmaniachoe"),
                    SocialLink(R.drawable.youtube, "https://www.youtube.com/@ztrix-eu")
                )
                SocialIconRow(links = socialLinks)
            }

             */
        }
    }

    private data class SocialLink(@DrawableRes val iconRes: Int, val url: String?)

    @Composable
    private fun SectionTitle(text: String) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start
        )
    }

    @Composable
    private fun CreditItem(text: String) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            textAlign = TextAlign.Start
        )
    }

    @Composable
    private fun SocialIconRow(links: List<SocialLink>) {
        val uriHandler = LocalUriHandler.current
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            links.forEach { link ->
                IconButton(
                    onClick = { link.url?.let { uriHandler.openUri(it) } },
                    enabled = link.url != null
                ) {
                    Icon(
                        painter = painterResource(link.iconRes),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
