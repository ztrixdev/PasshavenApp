package ru.ztrixdev.projects.passhavenapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Database
import androidx.room.util.TableInfo
import androidx.sqlite.throwSQLiteException
import ru.ztrixdev.projects.passhavenapp.EntryManagers.AccountManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.EntryManager
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.Dao.AccountDao
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.decrypt

class VaultOverviewActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent()
        {
            // this some raw shii, don't mind it, its really ugly
            MaterialTheme {
                var gotoNEA by remember { mutableStateOf(false) }
                val ctx = LocalContext.current
                LaunchedEffect(gotoNEA) {
                    if (gotoNEA) {
                        ctx .startActivity(
                            Intent(ctx, NewEntryActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                }
                Column(
                    Modifier.verticalScroll(rememberScrollState())
                ){
                    val accounts = DatabaseProvider.getDatabase(ctx).accountDao().getALl()
                    accounts.forEach { it.decrypt(
                        VaultHandler().getEncryptionKey(ctx)) }
                    Text(
                        modifier = Modifier.padding(all = 32.dp),
                        color = Color.White,
                        text = accounts.toString(),
                        fontSize = 18.sp
                    )
                    val cards = DatabaseProvider.getDatabase(ctx).cardDao().getALl()
                    cards.forEach { it.decrypt(
                        VaultHandler().getEncryptionKey(ctx)) }
                    Text(
                        modifier = Modifier.padding(all = 32.dp),
                        color = Color.White,
                        text = cards.toString(),
                        fontSize = 18.sp
                    )
                    Button(
                        onClick = {
                            gotoNEA = true
                        },
                        modifier = Modifier.padding(all = 30.dp)
                    ) {
                        Text("Go to New Entry Activity")
                    }
                }
            }
        }
    }
}
