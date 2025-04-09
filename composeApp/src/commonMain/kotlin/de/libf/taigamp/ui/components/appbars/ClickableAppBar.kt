package de.libf.taigamp.ui.components.appbars

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.libf.taigamp.R
import de.libf.taigamp.ui.utils.clickableUnindicated

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
                    ?: stringResource(R.string.choose_project_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )

            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null
            )
        }
    },
    actions = actions,
    navigateBack = navigateBack
)