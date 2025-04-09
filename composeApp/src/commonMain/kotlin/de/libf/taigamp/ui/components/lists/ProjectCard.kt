package de.libf.taigamp.ui.components.lists

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import de.libf.taigamp.domain.entities.Project
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.default_avatar
import taigamultiplatform.composeapp.generated.resources.ic_favorite
import taigamultiplatform.composeapp.generated.resources.ic_key
import taigamultiplatform.composeapp.generated.resources.ic_team
import taigamultiplatform.composeapp.generated.resources.ic_watch
import taigamultiplatform.composeapp.generated.resources.project_admin
import taigamultiplatform.composeapp.generated.resources.project_member
import taigamultiplatform.composeapp.generated.resources.project_owner

@Composable
fun ProjectCard(
    project: Project,
    isCurrent: Boolean,
    onClick: () -> Unit = {}
) = Surface(
    shape = MaterialTheme.shapes.small,
    border = if (isCurrent) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    },
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = mainHorizontalScreenPadding, vertical = 4.dp)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = project.avatarUrl ?: Res.drawable.default_avatar,
//                    builder = {
//                        error(R.drawable.default_avatar)
//                        crossfade(true)
//                    },
                ),
                contentDescription = null,
                modifier = Modifier.size(46.dp)
            )

            Spacer(Modifier.width(8.dp))

            Column {
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    text = stringResource(
                        when {
                            project.isOwner -> Res.string.project_owner
                            project.isAdmin -> Res.string.project_admin
                            else -> Res.string.project_member
                        }
                    )
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = project.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        project.description?.let {
            Spacer(Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(8.dp))

        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.outline
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconSize = 18.dp
                val indicatorsSpacing = 8.dp

                @Composable
                fun Indicator(icon: DrawableResource, value: Int) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Indicator(Res.drawable.ic_favorite, project.fansCount)
                Spacer(Modifier.width(indicatorsSpacing))
                Indicator(Res.drawable.ic_watch, project.watchersCount)
                Spacer(Modifier.width(indicatorsSpacing))
                Indicator(Res.drawable.ic_team, project.members.size)

                if (project.isPrivate) {
                    Spacer(Modifier.width(indicatorsSpacing))
                    Icon(
                        painter = painterResource(Res.drawable.ic_key),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}