package ru.ztrixdev.projects.passhavenapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import ru.ztrixdev.projects.passhavenapp.QuickComposables
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.entryManagers.MFATriple
import ru.ztrixdev.projects.passhavenapp.entryManagers.SortingKeys
import ru.ztrixdev.projects.passhavenapp.handlers.SessionHandler
import ru.ztrixdev.projects.passhavenapp.preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.room.Account
import ru.ztrixdev.projects.passhavenapp.room.Card
import ru.ztrixdev.projects.passhavenapp.room.Folder
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme
import ru.ztrixdev.projects.passhavenapp.viewModels.VaultOverviewViewModel
import kotlin.uuid.Uuid


class VaultOverviewActivity: ComponentActivity() {
    val vaultOverviewViewModel: VaultOverviewViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        val isSessionExpd = SessionHandler.isSessionExpired(this.applicationContext)
        if (isSessionExpd) {
            startActivity(
                Intent(this@VaultOverviewActivity, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent()
        {
            PasshavenTheme(
                themeType = ThemePrefs.getSelectedTheme(LocalContext.current),
                darkTheme = ThemePrefs.getDarkThemeBool(LocalContext.current),
                dynamicColors = ThemePrefs.getDynamicColorsBool(LocalContext.current),
            ) {
                val localctx = LocalContext.current
                LaunchedEffect(Unit) {
                    vaultOverviewViewModel.fetchEntries(localctx)
                    vaultOverviewViewModel.showAll()
                }

                var isNewFabExpanded by remember { mutableStateOf(false) }
                Scaffold(
                    topBar = { VaultOverviewTitlebar() } ,
                    bottomBar = { BottomNavbar() },
                    floatingActionButton = {
                        NewFab(isExpanded = isNewFabExpanded, onFabClick = {isNewFabExpanded = !isNewFabExpanded})
                    }
                ) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
                        MainBody()
                    }

                }
            }
        }
    }

    @Composable
    private fun MainBody() {
        val viewMode = vaultOverviewViewModel.viewMode
        val columns = if (viewMode == VaultOverviewViewModel.ViewMode.Card) 2 else 1

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item(span = {GridItemSpan(maxLineSpan) }) {
                Column {
                    ViewModeToggle()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (vaultOverviewViewModel.currentView == VaultOverviewViewModel.Views.Overview)
                            SortingKeySelectionDropdown()
                        ReverseSortingCheckbox()
                    }
                }
            }

            val folders = vaultOverviewViewModel.getFolders()
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(folders.size) { index ->
                        FolderButton(folder = folders[index])
                    }
                }
            }

            if (vaultOverviewViewModel.currentView == VaultOverviewViewModel.Views.Overview) {
                items(
                    items = vaultOverviewViewModel.visibleEntries
                ) { entry ->
                    when (viewMode) {
                        VaultOverviewViewModel.ViewMode.Card -> {
                            EntryCard(entry)
                        }
                        VaultOverviewViewModel.ViewMode.Row -> {
                            EntryRow(entry)
                        }
                    }
                }
            }
            if (vaultOverviewViewModel.currentView == VaultOverviewViewModel.Views.MFA) {
                items(
                    items = vaultOverviewViewModel.visibleMFA
                ) { mfa ->
                    when (viewMode) {
                        VaultOverviewViewModel.ViewMode.Card -> {
                            MFACard(mfa)
                        }
                        VaultOverviewViewModel.ViewMode.Row -> {
                            MFARow(mfa)
                        }
                    }
                }
            }
        }
    }




    @Composable
    private fun SortingKeySelectionDropdown() {
        var isExpanded by remember { mutableStateOf(false) }
        val sortingKeyStringsToEntryTypes = mapOf(
            stringResource(R.string.sort_by_alphabet) to SortingKeys.ByAlphabet,
            stringResource(R.string.sort_by_date) to SortingKeys.ByDate,
            stringResource(R.string.sort_by_type) to SortingKeys.ByType
        )
        val selectedText = sortingKeyStringsToEntryTypes.entries.find { it.value == vaultOverviewViewModel.getSelectedSortingKey() }?.key ?: ""

        Box {
            Row(
                modifier = Modifier.clickable { isExpanded = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_drop_down_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(text = selectedText, color = MaterialTheme.colorScheme.primary)
            }
            DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                sortingKeyStringsToEntryTypes.forEach { (text, key) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            vaultOverviewViewModel.setSelectedSortingKey(key)
                            vaultOverviewViewModel.sortVisibles()
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun ReverseSortingCheckbox() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                vaultOverviewViewModel.toggleReverseSorting()
                vaultOverviewViewModel.sortVisibles()
            }
        ) {
            Checkbox(
                checked = vaultOverviewViewModel.reverseSorting,
                onCheckedChange = {
                    vaultOverviewViewModel.toggleReverseSorting()
                    vaultOverviewViewModel.sortVisibles()
                }
            )
            Text(text = stringResource(R.string.reverse_sorting))
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ViewModeToggle() {
        val modes = listOf(
            Triple(VaultOverviewViewModel.ViewMode.Card, stringResource(R.string.view_mode_cards), painterResource(R.drawable.cards_stack_24px)),
            Triple(VaultOverviewViewModel.ViewMode.Row, stringResource(R.string.view_mode_rows), painterResource(R.drawable.list_24px))
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            modes.forEachIndexed { index, (mode, label, iconRes) ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                    onClick = { vaultOverviewViewModel.viewMode = mode },
                    selected = vaultOverviewViewModel.viewMode == mode,
                    icon = {
                        SegmentedButtonDefaults.Icon(active = vaultOverviewViewModel.viewMode == mode) {
                            Icon(
                                painter = iconRes,
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                            )
                        }
                    }
                ) {
                    Text(label, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }

    @Composable
    private fun NewFab(isExpanded: Boolean, onFabClick: () -> Unit) {
        val rotation by animateFloatAsState(targetValue = if (isExpanded) 45f else 0f)

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedVisibility(visible = isExpanded) {
                SmallFloatingActionButton(
                    onClick = {
                        goToNewEntry()
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        painter = painterResource(R.drawable.cards_stack_24px),
                        contentDescription = "Create new entry"
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                SmallFloatingActionButton(
                    onClick = {
                        goToNewFolder()
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        painter = painterResource(R.drawable.folder_open_24px),
                        contentDescription = "Create new folder"
                    )
                }
            }

            FloatingActionButton(
                onClick = onFabClick,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_24px),
                    contentDescription = "Create new item",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }

    @Composable
    private fun MFACard(triple: MFATriple) {
        var code by remember { mutableStateOf(vaultOverviewViewModel.getTOTP(triple.secret).toString()) }
        val localctx = LocalContext.current
        Card(
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .clickable {
                    vaultOverviewViewModel.copy(code, localctx)
                },
            elevation = CardDefaults.elevatedCardElevation(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = triple.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = code.chunked(3).joinToString(" "),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                QuickComposables.ThirtySecondsProgressbar(fillMaxWidth = true) {
                    code = vaultOverviewViewModel.getTOTP(triple.secret).toString()
                }
            }
        }
    }

    @Composable
    private fun MFARow(triple: MFATriple) {
        var code by remember { mutableStateOf(vaultOverviewViewModel.getTOTP(triple.secret).toString()) }
        var username by remember { mutableStateOf("") }
        val localctx = LocalContext.current

        LaunchedEffect(triple.originalUuid) {
            username = vaultOverviewViewModel.getUsernameByUuid(triple.originalUuid, localctx)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                vaultOverviewViewModel.copy(text = code, context = localctx)
            },
            elevation = CardDefaults.elevatedCardElevation(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = triple.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = username,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // MFA specific Column (Code + Progress)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = code.chunked(3).joinToString(" "),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Box(modifier = Modifier.width(60.dp)) {
                        QuickComposables.ThirtySecondsProgressbar(fillMaxWidth = true) {
                            code = vaultOverviewViewModel.getTOTP(triple.secret).toString()
                        }
                    }
                }
            }
        }
    }


    @Composable
    private fun FolderButton(folder: Folder) {
        val isSelected = vaultOverviewViewModel.selectedFolderUuid == folder.uuid

        val colors = if (isSelected) {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedButton(
            onClick = {
                if (isSelected) {
                    // Deselect if clicking the same one
                    vaultOverviewViewModel.selectedFolderUuid = null
                    vaultOverviewViewModel.showAll()
                } else {
                    vaultOverviewViewModel.selectedFolderUuid = folder.uuid
                    vaultOverviewViewModel.showFolderContents(folder)
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = colors,
            border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.height(40.dp)
        ) {
            Text(
                text = folder.name,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        }
    }

    @Composable
    private fun EntryCard(entry: Any) {
        val entryId = when (entry) {
            is Card -> entry.uuid
            is Account -> entry.uuid
            else -> return
        }

        Card(
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .clickable {
                    goToViewEntry(entryId)
                },
            elevation = CardDefaults.elevatedCardElevation(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val name = when (entry) {
                    is Card -> entry.name
                    is Account -> entry.name
                    else -> "N/A"
                }
                val addInfo = when (entry) {
                    is Card -> entry.number
                    is Account -> entry.username
                    else -> "N/A"
                }
                val type = when (entry) {
                    is Card -> stringResource(R.string.card)
                    is Account -> stringResource(R.string.account)
                    else -> "N/A"
                }

                Text(text = name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = addInfo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = type, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    @Composable
    private fun EntryRow(entry: Any) {
        val entryId = when (entry) {
            is Card -> entry.uuid
            is Account -> entry.uuid
            else -> return
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                goToViewEntry(entryId)
            },
            elevation = CardDefaults.elevatedCardElevation(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
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
                    else -> R.drawable.browse_24px
                }
                Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = MaterialTheme.colorScheme.primary)

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    val name = when (entry) {
                        is Card -> entry.name
                        is Account -> entry.name
                        else -> "N/A"
                    }
                    val addInfo = when (entry) {
                        is Card -> entry.number
                        is Account -> entry.username
                        else -> "N/A"
                    }
                    Text(text = name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = addInfo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }


    @Composable
    private fun RowScope.BottomNavItem(
        selected: Boolean,
        icon: Int,
        label: String,
        onClick: () -> Unit
    ) {
        val selectedColor = MaterialTheme.colorScheme.primary
        val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable(onClick = onClick)
                .weight(1f)
                .padding(bottom = 12.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .padding(top = 8.dp, bottom = 4.dp),
                tint = if (selected) selectedColor else unselectedColor
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) selectedColor else unselectedColor
            )

            if (selected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .width(24.dp)
                        .background(
                            selectedColor,
                            RoundedCornerShape(50)
                        )
                )
            }
        }
    }

    @Composable
    private fun BottomNavbar() {
        val currentView = vaultOverviewViewModel.currentView

        Row(
            Modifier.background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
            ).fillMaxWidth()
        ) {
            Row(
                Modifier.padding(bottom = 12.dp)
            ) {
                BottomNavItem(
                    selected = currentView == VaultOverviewViewModel.Views.Overview,
                    icon = R.drawable.browse_24px,
                    label = stringResource(R.string.overview),
                    onClick = {
                        vaultOverviewViewModel.currentView =
                            VaultOverviewViewModel.Views.Overview
                    }
                )
                BottomNavItem(
                    selected = currentView == VaultOverviewViewModel.Views.MFA,
                    icon = R.drawable.shield_24px,
                    label = stringResource(R.string.mfa),
                    onClick = {
                        vaultOverviewViewModel.currentView =
                            VaultOverviewViewModel.Views.MFA
                    }
                )
            }
        }
    }

    @Composable
    private fun VaultOverviewTitlebar() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .statusBarsPadding()
                .padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = {
                    logOut()
                },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(36.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.lock_24px),
                    contentDescription = "A lock.",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.passhaven),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    goToSettings()
                },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(36.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.settings_24px),
                    contentDescription = "A gear.",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }



    private fun logOut() {
        startActivity(
            Intent(this@VaultOverviewActivity, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun goToSettings() {
        startActivity(
            Intent(this@VaultOverviewActivity, SettingsActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun goToNewEntry() {
        startActivity(
            Intent(this@VaultOverviewActivity, NewEntryActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun goToNewFolder() {
        startActivity(
            Intent(this@VaultOverviewActivity, NewFolderActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun goToViewEntry(uuid: Uuid) {
        startActivity(
            Intent(this@VaultOverviewActivity, ViewEntryActivity::class.java)
                .putExtra(EDIT_ENTRY_ACTIVITY_EXTRA_ENTRY_UUID_KEY, uuid.toString())
                .putExtra(EDIT_ENTRY_ACTIVITY_EXTRA_PREV_ACTIVITY_KEY, EDIT_ENTRY_ACTIVITY_EXTRA_PREV_ACTIVITY_VAULT_OVERVIEW_VALUE)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }


}
