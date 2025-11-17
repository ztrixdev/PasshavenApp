package ru.ztrixdev.projects.passhavenapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object QuickComposables {
    @Composable
    fun Titlebar(text: String, onBackButtonClickAction: () -> Unit) {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .padding(all = 10.dp)
        ) {
            IconButton(
                onClick = onBackButtonClickAction,
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
                text = text,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(start = 40.dp)
            )
        }
    }


}