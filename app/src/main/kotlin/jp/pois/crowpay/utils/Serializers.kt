package jp.pois.crowpay.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.builtins.LongAsStringSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.protobuf.ProtoIntegerType
import kotlinx.serialization.protobuf.ProtoType
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

    override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
        var most = 0L
        var least = 0L
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                MOST_INDEX -> most = decodeLongElement(descriptor, MOST_INDEX)
                LEAST_INDEX -> least = decodeLongElement(descriptor, LEAST_INDEX)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        require(most != 0L && least != 0L)
        UUID(most, least)
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.beginCollection(descriptor, 2).apply {
            encodeLongElement(longDescriptor, MOST_INDEX, value.mostSignificantBits)
            encodeLongElement(longDescriptor, LEAST_INDEX, value.leastSignificantBits)
        }.endStructure(descriptor)
    }

    private val longDescriptor
        get() = Long.serializer().descriptor

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = object : SerialDescriptor {
        override val elementsCount: Int
            get() = 2

        override val kind: SerialKind
            get() = StructureKind.LIST

        override val serialName: String = "java.util.UUID"

        override fun getElementAnnotations(index: Int): List<Annotation> {
            require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}

            return emptyList()
        }

        override fun getElementDescriptor(index: Int): SerialDescriptor {
            require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
            return Long.serializer().descriptor
        }

        override fun getElementIndex(name: String): Int = name.toInt()

        override fun getElementName(index: Int): String = index.toString()

        override fun isElementOptional(index: Int): Boolean {
            require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
            return false
        }
    }
}
