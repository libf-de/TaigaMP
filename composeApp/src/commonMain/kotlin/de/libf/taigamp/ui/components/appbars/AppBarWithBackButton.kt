package de.libf.taigamp.ui.components.appbars

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.ic_arrow_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarWithBackButton(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    navigateBack: (() -> Unit)? = null
) = TopAppBar(
    title = title,
    navigationIcon = navigateBack?.let {
        {
            IconButton(onClick = it) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    } ?: {},
    actions = actions,
    modifier = modifier.safeDrawingPadding()
)
