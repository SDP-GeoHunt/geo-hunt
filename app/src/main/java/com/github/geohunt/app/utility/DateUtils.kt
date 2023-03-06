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

fun utcIso6801Now() : String {
    return utcIso6801FromLocal(LocalDateTime.now())
}

fun utcIso6801FromLocal(localDateTime: LocalDateTime) : String {
    return toIso8601(localToServerDate(localDateTime))
}

fun localFromUtcIso6801(value: String) : LocalDateTime {
    return serverToLocalDate(fromIso6801(value))
}


fun formatDateTime(date: LocalDateTime) : String {
    return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
}
