package de.libf.taigamp.ui.screens.wiki.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.libf.taigamp.ui.components.buttons.TextButton
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.create_new_page
import taigamultiplatform.composeapp.generated.resources.empty_wiki_dialog_subtitle
import taigamultiplatform.composeapp.generated.resources.empty_wiki_dialog_title

@Composable
fun EmptyWikiDialog(
    createNewPage: () -> Unit = {},
    isButtonAvailable: Boolean = true
) = Box (
    modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surface)
        .imePadding()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = mainHorizontalScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(Res.string.empty_wiki_dialog_title),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(Res.string.empty_wiki_dialog_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isButtonAvailable) {
            TextButton(
                text = stringResource(Res.string.create_new_page),
                onClick = createNewPage
            )
        }
    }
}

@Preview
@Composable
fun EmptyWikiDialogPreview() {
    EmptyWikiDialog()
}