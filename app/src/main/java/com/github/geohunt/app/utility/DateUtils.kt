package com.github.geohunt.app.utility

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private fun toIso8601(iso6801: Instant) : String {
    val formatter = DateTimeFormatter.ISO_INSTANT
            .withZone(ZoneOffset.UTC)
    return formatter.format(iso6801)
}

private fun fromIso6801(iso6801: String) : Instant {
    val formatter = DateTimeFormatter.ISO_INSTANT
            .withZone(ZoneOffset.UTC)
    return Instant.from(formatter.parse(iso6801))
}

private fun localToServerDate(date: LocalDateTime) : Instant {
    return date.toInstant(ZoneOffset.UTC)
}

private fun serverToLocalDate(date: Instant) : LocalDateTime {
    return LocalDateTime.ofInstant(date, ZoneOffset.UTC)
}

/**
 * Convert the current local time to a ISO 6801 compliant String of the corresponding time at UTC
 */
fun utcIso6801Now() : String {
    return utcIso6801FromLocal(LocalDateTime.now())
}

/**
 * Convert the given localtime to a ISO 6801 compliant String of the corresponding time at UTC
 *
 * @param localDateTime the datetime to be converted
 */

fun utcIso6801FromLocal(localDateTime: LocalDateTime) : String {
    return toIso8601(localToServerDate(localDateTime))
}

/**
 * Convert the given nullable localtime to a ISO 6801 compliant String of the corresponding time at
 * UTC. The string will be "null" when no localDateTime was provided
 *
 * @param localDateTime the datetime to be converted
 */
fun utcIso6801FromLocalNullable(localDateTime: LocalDateTime?) : String {
    return when (localDateTime) {
        null -> "null"
        else -> utcIso6801FromLocal(localDateTime)
    }
}

/**
 * Convert a ISO 6801 compliant String at UTC to the corresponding local date time
 *
 * @param value the ISO 6801 complaint String to be converted
 */
fun localFromUtcIso6801(value: String) : LocalDateTime {
    return serverToLocalDate(fromIso6801(value))
}

/**
 * Convert a ISO 6801 compliant String at UTC to the corresponding nullable local date time
 *
 * @param value the ISO 6801 complaint String to be converted, also can be "null"
 */
fun localNullableFromUtcIso6801(value: String) : LocalDateTime? {
    return when (value) {
        "null" -> null
        else -> localFromUtcIso6801(value)
    }
}
