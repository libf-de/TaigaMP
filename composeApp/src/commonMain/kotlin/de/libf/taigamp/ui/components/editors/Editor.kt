package de.libf.taigamp.ui.components.editors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.libf.taigamp.ui.components.appbars.AppBarWithBackButton
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import de.libf.taigamp.ui.utils.onBackPressed
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.description_hint
import taigamultiplatform.composeapp.generated.resources.ic_save
import taigamultiplatform.composeapp.generated.resources.title_hint

@Composable
fun Editor(
    toolbarText: String,
    title: String = "",
    description: String = "",
    showTitle: Boolean = true,
    onSaveClick: (title: String, description: String) -> Unit = { _, _ -> },
    navigateBack: () -> Unit = {}
) {
    var titleInput by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(title)) }
    var descriptionInput by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(description)) }

    fun save() {
        titleInput.text.trim().takeIf { it.isNotEmpty() }?.let {
            onSaveClick(it, descriptionInput.text.trim())
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .imePadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { save() },
            ) {
                Icon(painterResource(Res.drawable.ic_save), "Save task")
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            onBackPressed(navigateBack)

            AppBarWithBackButton(
                title = { Text(toolbarText) },
                actions = {
//            IconButton(
//                onClick = { save() }
//            ) {
//                Icon(
//                    painter = painterResource(Res.drawable.ic_save),
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
                },
                navigateBack = navigateBack
            )



            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = mainHorizontalScreenPadding)
            ) {

                Spacer(Modifier.height(8.dp))

                if (showTitle) {
                    TextFieldWithHint(
                        hintId = Res.string.title_hint,
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.height(16.dp))
                }

                TextFieldWithHint(
                    hintId = Res.string.description_hint,
                    value = descriptionInput,
                    onValueChange = { descriptionInput = it },
                )

                Spacer(
                    Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                        .padding(bottom = 8.dp)
                )
            }

        }
    }
}

@Preview
@Composable
fun TaskEditorPreview() = TaigaMobileTheme {
    Editor("Edit")
}
