package com.github.geohunt.app.utility

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateFormatUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun formatDate(dateTime: LocalDateTime): String {
        return dateFormatter.format(dateTime)
    }

    fun formatRemainingTime(to: LocalDateTime?): String {
        when(to) {
            null -> return "NEVER"
            else -> {
                val now = LocalDateTime.now()

                val days = now.until(to, ChronoUnit.DAYS)
                if (days != 0L) return "$days day(s)"

                val hours = now.until(to, ChronoUnit.HOURS)
                if (hours != 0L) return "$hours hour(s)"

                return "Less than 1 hour"
            }
        }
    }

}