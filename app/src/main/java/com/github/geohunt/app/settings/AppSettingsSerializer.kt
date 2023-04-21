package com.github.geohunt.app.settings

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppSettingsSerializer: Serializer<AppSettings> {

    override val defaultValue: AppSettings
        get() = AppSettings()

    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            Json.decodeFromString(
                string = input.readBytes().decodeToString(),
                deserializer = AppSettings.serializer()
            )
        } catch (e: SerializationException) {
            Log.e("WARN", "Could not parse app settings.")
            return AppSettings()
        }
    }

    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(
                    serializer = AppSettings.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}