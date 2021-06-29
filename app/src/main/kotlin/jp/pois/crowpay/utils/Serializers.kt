package jp.pois.crowpay.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.builtins.LongAsStringSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.util.*

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.time.LocalDate", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeLong(value.toEpochDay())
    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.ofEpochDay(decoder.decodeLong())
}

object UUIDSerializer : KSerializer<UUID> {
    private const val MOST_INDEX = 0
    private const val LEAST_INDEX = 1

    override val descriptor: SerialDescriptor = LongPairDescriptor
    override fun deserialize(decoder: Decoder): UUID {
        val composite = decoder.beginStructure(longDescriptor)
        val most = composite.decodeLongElement(longDescriptor, MOST_INDEX)
        val least = composite.decodeLongElement(longDescriptor, LEAST_INDEX)
        composite.endStructure(longDescriptor)
        return UUID(most, least)
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.beginCollection(longDescriptor, 2).apply {
            encodeLongElement(longDescriptor, MOST_INDEX, value.mostSignificantBits)
            encodeLongElement(longDescriptor, LEAST_INDEX, value.leastSignificantBits)
        }.endStructure(longDescriptor)
    }

    private val longDescriptor
        get() = Long.serializer().descriptor

    private object LongPairDescriptor: SerialDescriptor {
        @ExperimentalSerializationApi
        override val elementsCount: Int
            get() = 2

        @ExperimentalSerializationApi
        override val kind: SerialKind
            get() = StructureKind.LIST

        @ExperimentalSerializationApi
        override val serialName: String = "java.util.UUID"

        @ExperimentalSerializationApi
        override fun getElementAnnotations(index: Int): List<Annotation> {
            require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
            return emptyList()
        }

        @ExperimentalSerializationApi
        override fun getElementDescriptor(index: Int): SerialDescriptor {
            require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
            return Long.serializer().descriptor
        }

        @ExperimentalSerializationApi
        override fun getElementIndex(name: String): Int = name.toInt()

        @ExperimentalSerializationApi
        override fun getElementName(index: Int): String = index.toString()

        @ExperimentalSerializationApi
        override fun isElementOptional(index: Int): Boolean {
            require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
            return false
        }
    }
}

object UUIDArraySerializer : KSerializer<Array<UUID>> {
    @OptIn(ExperimentalSerializationApi::class)
    private val serializer = ArraySerializer(UUIDSerializer)

    override fun deserialize(decoder: Decoder): Array<UUID> = serializer.deserialize(decoder)

    override val descriptor: SerialDescriptor
        get() = serializer.descriptor

    override fun serialize(encoder: Encoder, value: Array<UUID>) = serializer.serialize(encoder, value)
}
