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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import ru.ztrixdev.projects.passhavenapp.entryManagers.MFATriple
import ru.ztrixdev.projects.passhavenapp.handlers.SessionHandler
import ru.ztrixdev.projects.passhavenapp.preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.QuickComposables
import ru.ztrixdev.projects.passhavenapp.R
import ru.ztrixdev.projects.passhavenapp.viewModels.VaultOverviewViewModel
import ru.ztrixdev.projects.passhavenapp.ui.theme.PasshavenTheme


class VaultOverviewActivity: ComponentActivity() {
    val vaultOverviewViewModel: VaultOverviewViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        val isSessionExpd = SessionHandler.isSessionExpired(this.applicationContext)
        if (isSessionExpd) {
            this.applicationContext.startActivity(
                Intent(this.applicationContext, LoginActivity::class.java)
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
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                    }

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
            shape = CardDefaults.elevatedShape,
            colors = CardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.inverseSurface,
                disabledContentColor = MaterialTheme.colorScheme.inverseOnSurface,
            ),
            modifier = Modifier
                .clickable(enabled = true,
                    onClick = {
                        vaultOverviewViewModel.copy(code, localctx)
                    }),
            border = BorderStroke(
                width = 1.dp,
                brush = radialGradient(colors = listOf(
                    MaterialTheme.colorScheme.outline,
                    MaterialTheme.colorScheme.outlineVariant
                )),
            )
        ) {
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .height(100.dp)
                    .padding(8.dp)
            ) {
                Text(
                    text = triple.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = code,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                )
                QuickComposables.ThirtySecondsProgressbar(fillMaxWidth = false) {
                    code = vaultOverviewViewModel.getTOTP(triple.secret).toString()
                }
            }

        }
    }

    @Composable
    private fun MFARow(triple: MFATriple) {
        var code by remember {
            mutableStateOf(
                vaultOverviewViewModel.getTOTP(triple.secret).toString()
            )
        }
        var username by remember { mutableStateOf("") }
        val localctx = LocalContext.current

        LaunchedEffect(Unit) {
            username = vaultOverviewViewModel.getUsernameByUuid(triple.originalUuid, localctx)
        }
        Box(
            Modifier.border(
                border = BorderStroke(
                    width = 1.dp,
                    brush = radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.colorScheme.outlineVariant
                        )
                    ),
                )
            )
        ) {
            Column(
                Modifier
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .clickable(
                        enabled = true,
                        onClick = {
                            vaultOverviewViewModel.copy(text = code, context = localctx)
                        }
                    )
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = triple.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!username.isEmpty()) {
                            Text(
                                text = username,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        Text(
                            text = code,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                        )
                    }
                }
                QuickComposables.ThirtySecondsProgressbar(fillMaxWidth = true) {
                    code = vaultOverviewViewModel.getTOTP(triple.secret).toString()
                }
            }
        }
    }

    @Composable
    private fun BottomNavbar() {
        Row(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            vaultOverviewViewModel.currentView =
                                VaultOverviewViewModel.Views.Overview
                        }
                    )
                    .weight(1f)
                    .padding(bottom = 12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.browse_24px),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(top = 8.dp, bottom = 4.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.overview),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            vaultOverviewViewModel.currentView = VaultOverviewViewModel.Views.MFA
                        }
                    )
                    .weight(1f)
                    .padding(bottom = 12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.shield_24px),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(top = 8.dp, bottom = 4.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.mfa),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            vaultOverviewViewModel.currentView =
                                VaultOverviewViewModel.Views.Generator
                        }
                    )
                    .weight(1f)
                    .padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.autorenew_24px),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(top = 8.dp, bottom = 4.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.generator),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
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
        this.applicationContext.startActivity(
            Intent(this.applicationContext, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun goToSettings() {
        this.applicationContext.startActivity(
            Intent(this.applicationContext, SettingsActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun goToNewEntry() {
        this.applicationContext.startActivity(
            Intent(this.applicationContext, NewEntryActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun goToNewFolder() {
        this.applicationContext.startActivity(
            Intent(this.applicationContext, NewFolderActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }


}
