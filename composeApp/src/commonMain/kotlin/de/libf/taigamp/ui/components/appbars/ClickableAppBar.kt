package de.libf.taigamp.ui.components.appbars

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import de.libf.taigamp.ui.utils.clickableUnindicated
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.choose_project_title
import taigamultiplatform.composeapp.generated.resources.ic_arrow_down

@Composable
fun ClickableAppBar(
    projectName: String,
    onTitleClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    navigateBack: (() -> Unit)? = null
) = AppBarWithBackButton(
    title = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickableUnindicated(onClick = onTitleClick)
        ) {
            Text(
                text = projectName.takeIf { it.isNotEmpty() }
                    ?: stringResource(Res.string.choose_project_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )

            Icon(
                painter = painterResource(Res.drawable.ic_arrow_down),
                contentDescription = null
            )
        }
    },
    actions = actions,
    navigateBack = navigateBack
)