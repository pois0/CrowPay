package jp.pois.crowpay.utils

import android.app.Activity
import android.content.Context
import jp.pois.crowpay.data.EndpointIdentifier
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.*
import kotlin.IllegalStateException

val Activity.endpointIdentifier: EndpointIdentifier
    get() {
        val pref = getSharedPreferences(IDENTIFIER_KEY, Context.MODE_PRIVATE)
        val name = pref.getString(NAME_KEY, null)
        val most = pref.getLong(UUID_MOST_KEY, 0)
        val least = pref.getLong(UUID_LEAST_KEY, 0)
        if (name == null && most == 0L && least == 0L) throw IllegalStateException()
        return EndpointIdentifier(UUID(most, least), name!!)
    }

@OptIn(ExperimentalSerializationApi::class)
val Activity.endpointIdentifierBytes: ByteArray
    get() = ProtoBuf.encodeToByteArray(endpointIdentifier)

val Activity.isEndpointIdentifierInitialized: Boolean
    get() = try {
        endpointIdentifier
        true
    } catch (e: IllegalStateException) {
        false
    } catch (e: Exception) {
        throw e
    }

fun Activity.initializeEndpointIdentifier(name: String) {
    val uuid = UUID.randomUUID()
    val pref = getSharedPreferences(IDENTIFIER_KEY, Context.MODE_PRIVATE)
    pref.edit().apply {
        putString(NAME_KEY, name)
        putLong(UUID_MOST_KEY, uuid.mostSignificantBits)
        putLong(UUID_LEAST_KEY, uuid.leastSignificantBits)
    }.apply()
}

private const val IDENTIFIER_KEY = "jp.pois.crowpay.IDENTIFIER"
private const val NAME_KEY = "name"
private const val UUID_MOST_KEY = "uuid_most"
private const val UUID_LEAST_KEY = "uuid_least"
