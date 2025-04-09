package de.libf.taigamp.ui.components.texts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.nothing_to_see

/**
 * Common nothing to see here text
 */
@Composable
fun NothingToSeeHereText() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.nothing_to_see),
            color = MaterialTheme.colorScheme.outline
        )
    }
}
