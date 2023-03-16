package com.github.geohunt.app.utility

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateUtils {
    @JvmStatic
    fun getSystemZoneId() : ZoneId {
        return ZoneOffset.systemDefault()
    }

    private fun toIso8601(iso8601: Instant) : String {
        val formatter = DateTimeFormatter.ISO_INSTANT
            .withZone(ZoneOffset.UTC)
        return formatter.format(iso8601)
    }

    private fun fromIso8601(iso8601: String) : Instant {
        val formatter = DateTimeFormatter.ISO_INSTANT
            .withZone(ZoneOffset.UTC)
        return Instant.from(formatter.parse(iso8601))
    }

    private fun localToServerDate(date: LocalDateTime) : Instant {
        val zoneOffset = getSystemZoneId().rules.getOffset(date)
        return date.atOffset(zoneOffset).atZoneSameInstant(ZoneId.of("UTC")).toInstant()
    }

    private fun serverToLocalDate(date: Instant) : LocalDateTime {
        return date.atOffset(ZoneOffset.UTC).atZoneSameInstant(getSystemZoneId()).toLocalDateTime()
    }

    /**
     * Convert the current local time to a ISO 8601 compliant String of the corresponding time at UTC
     */
    fun utcIso8601Now() : String {
        return utcIso8601FromLocal(LocalDateTime.now())
    }

    /**
     * Convert the given localtime to a ISO 8601 compliant String of the corresponding time at UTC
     *
     * @param localDateTime the datetime to be converted
     */

    fun utcIso8601FromLocal(localDateTime: LocalDateTime) : String {
        return toIso8601(localToServerDate(localDateTime))
    }

    /**
     * Convert the given nullable localtime to a ISO 8601 compliant String of the corresponding time at
     * UTC. The string will be "null" when no localDateTime was provided
     *
     * @param localDateTime the datetime to be converted
     */
    fun utcIso8601FromLocalNullable(localDateTime: LocalDateTime?) : String {
        return when (localDateTime) {
            null -> "null"
            else -> utcIso8601FromLocal(localDateTime)
        }
    }

    /**
     * Convert a ISO 8601 compliant String at UTC to the corresponding local date time
     *
     * @param value the ISO 8601 complaint String to be converted
     */
    fun localFromUtcIso8601(value: String) : LocalDateTime {
        return serverToLocalDate(fromIso8601(value))
    }

    /**
     * Convert a ISO 8601 compliant String at UTC to the corresponding nullable local date time
     *
     * @param value the ISO 8601 complaint String to be converted, also can be "null"
     */
    fun localNullableFromUtcIso8601(value: String) : LocalDateTime? {
        return when (value) {
            "null" -> null
            else -> localFromUtcIso8601(value)
        }
    }
}


