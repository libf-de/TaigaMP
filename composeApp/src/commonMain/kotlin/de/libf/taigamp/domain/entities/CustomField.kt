package de.libf.taigamp.domain.entities

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
enum class CustomFieldType {
    @SerialName("text") Text,
    @SerialName("multiline") Multiline,
    @SerialName("richtext") RichText,
    @SerialName("date") Date,
    @SerialName("url") Url,
    @SerialName("dropdown") Dropdown,
    @SerialName("number") Number,
    @SerialName("checkbox") Checkbox
}

data class CustomField(
    val id: Long,
    val type: CustomFieldType,
    val name: String,
    val description: String?,
    val value: CustomFieldValue?,
    val options: List<String>? = null // for CustomFieldType.Dropdown
)

@JvmInline
value class CustomFieldValue(val value: Any) {
    init {
        require(
            value is String ||
            value is LocalDate ||
            value is Double ||
            value is Boolean
        )
    }

    val stringValue get() = value as? String ?: throw IllegalArgumentException("value is not String")
    val doubleValue get() = value as? Double ?: throw IllegalArgumentException("value is not Int")
    val dateValue get() = value as? LocalDate ?: throw IllegalArgumentException("value is not Date")
    val booleanValue get() = value as? Boolean ?: throw IllegalArgumentException("value is not Boolean")
}


data class CustomFields(
    val fields: List<CustomField>,
    val version: Int
)
