package de.libf.taigamp.ui.screens.commontask.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.libf.taigamp.domain.entities.CustomField
import de.libf.taigamp.domain.entities.CustomFieldValue
import de.libf.taigamp.ui.components.loaders.DotsLoader
import de.libf.taigamp.ui.components.texts.SectionTitle
import de.libf.taigamp.ui.screens.commontask.EditActions
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.custom_fields

@Suppress("FunctionName")
fun LazyListScope.CommonTaskCustomFields(
    customFields: List<CustomField>,
    customFieldsValues: Map<Long, CustomFieldValue?>,
    onValueChange: (Long, CustomFieldValue?) -> Unit,
    editActions: EditActions
) {
    item {
        SectionTitle(text = stringResource(Res.string.custom_fields))
    }

    itemsIndexed(customFields) { index, item ->
        CustomField(
            customField = item,
            value = customFieldsValues[item.id],
            onValueChange = { onValueChange(item.id, it) },
            onSaveClick = { editActions.editCustomField.select(Pair(item, customFieldsValues[item.id])) }
        )

        if (index < customFields.lastIndex) {
            Divider(
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    item {
        if (editActions.editCustomField.isLoading) {
            Spacer(Modifier.height(8.dp))
            DotsLoader()
        }
    }
}
