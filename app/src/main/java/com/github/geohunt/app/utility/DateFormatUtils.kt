package com.github.geohunt.app.utility

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Utility class that proposes static method for different formatting of LocalDateTimes
 */
object DateFormatUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /**
     * Formats given LocalDateTime into a string
     * Only takes year, month and day into account
     * Formats using the following pattern "day/month/year"
     * @param dateTime dateTime to format
     */
    fun formatDate(dateTime: LocalDateTime): String {
        return dateFormatter.format(dateTime)
    }

    /**
     * Formats remaining time from the current time (in the sense of LocalDateTime.now()) to the
     * time given as an argument
     * The output depends on the difference in time, it will first try to give the difference in days
     * and will give it in hours if less than a day is left. If less than an hour remains it will
     * simply return the string "Less than 1 hour"
     * If argument is null it returns the string "NEVER"
     * @param to LocalDateTime to which the remaining time should be computed
     */
    fun formatRemainingTime(to: LocalDateTime?): String {
        when(to) {
            null -> return "NEVER"
            else -> {
                val now = LocalDateTime.now()

                val days = now.until(to, ChronoUnit.DAYS)
                if (days > 0L) return if (days > 1L) "$days days" else "1 day"

                val hours = now.until(to, ChronoUnit.HOURS)
                if (hours > 0L) return if (hours > 1L) "$hours hours" else "1 hour"


                return "Less than 1 hour"
            }
        }
    }

}