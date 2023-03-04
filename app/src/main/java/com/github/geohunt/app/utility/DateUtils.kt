package com.github.geohunt.app.utility

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun toIso8601(iso6801: Instant) : String {
    val formatter = DateTimeFormatter.ISO_INSTANT
            .withZone(ZoneOffset.UTC)
    return formatter.format(iso6801)
}

fun fromIso6801(iso6801: String) : Instant {
    val formatter = DateTimeFormatter.ISO_INSTANT
            .withZone(ZoneOffset.UTC)
    return Instant.from(formatter.parse(iso6801))
}

fun localToServerDate(date: LocalDateTime) : Instant {
    return date.toInstant(ZoneOffset.UTC)
}

fun serverToLocalDate(date: Instant) : LocalDateTime {
    return LocalDateTime.ofInstant(date, ZoneOffset.UTC)
}

fun serverNow() : Instant {
    return localToServerDate(LocalDateTime.now())
}

fun formatDateTime(date: LocalDateTime) : String {
    return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
}
