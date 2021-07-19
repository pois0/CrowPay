package jp.pois.crowpay.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> decodeProtoBuf(bytes: ByteArray): T = ProtoBuf.decodeFromByteArray(bytes)

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> encodeToProtoBuf(content: T) = ProtoBuf.encodeToByteArray(content)
