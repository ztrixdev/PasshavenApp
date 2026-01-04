package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import ru.ztrixdev.projects.passhavenapp.EntryManagers.MFATriple
import ru.ztrixdev.projects.passhavenapp.Handlers.SessionHandler
import ru.ztrixdev.projects.passhavenapp.Preferences.ThemePrefs
import ru.ztrixdev.projects.passhavenapp.QuickComposables
import ru.ztrixdev.projects.passhavenapp.ViewModels.VaultOverviewViewModel
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
        // enableEdgeToEdge()
        setContent()
        {
            // this some raw shii, don't mind it, its really ugly
            PasshavenTheme(
                themeType = ThemePrefs.getSelectedTheme(LocalContext.current),
                darkTheme = ThemePrefs.getDarkThemeBool(LocalContext.current),
                dynamicColors = ThemePrefs.getDynamicColorsBool(LocalContext.current),
            )
                {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {

                }

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
                                color = MaterialTheme.colorScheme.surfaceDim
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

}
