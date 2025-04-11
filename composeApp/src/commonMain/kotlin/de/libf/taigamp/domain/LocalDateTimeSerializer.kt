package de.libf.taigamp.domain

import kotlinx.datetime.*
import kotlinx.datetime.format.alternativeParsing
import kotlinx.datetime.format.char
import kotlinx.datetime.format.optional
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    val fmt = LocalDateTime.Format {
        year(); char('-'); monthNumber(); char('-'); dayOfMonth()

        alternativeParsing({ char('t') }) { char('T') }

        hour()
        char(':')
        minute()
        alternativeParsing({
            // intentionally empty
        }) {
            char(':')
            second()
            optional {
                char('.')
                secondFraction(1, 9)
            }
        }

        optional { alternativeParsing({ char('z') }) { char('Z') } }
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), fmt)
    }
}