package com.github.geohunt.app.i18n

import android.icu.text.RelativeDateTimeFormatter
import android.icu.text.RelativeDateTimeFormatter.Direction
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R
import java.time.Duration
import java.time.LocalDate
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
        return formatDate(dateTime.toLocalDate())
    }

    /**
     * Formats given LocalDate into a string
     * Formats using the following pattern "day/month/year"
     * @param date date to format
     */
    fun formatDate(date: LocalDate): String {
        return dateFormatter.format(date)
    }

    @Composable
    private fun doTimeString(duration: Duration, formattingStringId: Int, direction: Direction) : String {
        val fmt = RelativeDateTimeFormatter.getInstance()

        val raw = when {
            duration.toDays() > 182 ->
                fmt.format((duration.toDays() + 182) / 365,
                    direction,
                    RelativeDateTimeFormatter.RelativeUnit.YEARS
                )
            duration.toDays() > 29 ->
                fmt.format((duration.toDays() + 15) / 30,
                    direction,
                    RelativeDateTimeFormatter.RelativeUnit.MONTHS
                )
            duration.toDays() > 0 ->
                fmt.format(duration.toDays(),
                    direction,
                    RelativeDateTimeFormatter.RelativeUnit.DAYS
                )
            duration.toHours() > 0 ->
                fmt.format(duration.toHours(),
                    direction,
                    RelativeDateTimeFormatter.RelativeUnit.HOURS
                )
            duration.toMinutes() > 3 ->
                fmt.format((duration.toMinutes() / 5) * 5,
                    direction,
                    RelativeDateTimeFormatter.RelativeUnit.MINUTES
                )
            else -> stringResource(id = R.string.just_now)
        }
        return stringResource(id = formattingStringId, (raw ?: "???"))
    }

    /**
     * Returns a human-readable string representation of the remaining time until a given date and
     * the current local date time, e.g., "in 2 days", "1 hour"
     *
     * @param dateTime The target date-time of the event to be compared to
     * @param formattingStringId The identifier of the resource to be used to format the resulting string.
     *                           e.g. "Expires %s" -> "Expires in 1 hour"
     * @param passedFormattingStringId Identifier of the string resources whenever the corresponding target date
     *                               is passed
     * @return A human-readable string of the remaining time
     */
    @Composable
    fun getRemainingTimeString(dateTime: LocalDateTime, formattingStringId: Int, passedFormattingStringId: Int) : String {
        val now = LocalDateTime.now()
        return if (dateTime < now) {
            doTimeString(
                duration = Duration.between(dateTime, now),
                formattingStringId = passedFormattingStringId,
                direction = Direction.LAST
            )
        }
        else {
            doTimeString(
                duration = Duration.between(now, dateTime),
                formattingStringId = formattingStringId,
                direction = Direction.NEXT
            )
        }
    }

    /**
     * Returns a human-readable string representing the elapsed time between the given date-time
     * and the current time, e.g., "2 days ago", "1 hour ago", "5 minutes ago", or "just now".
     *
     * @param dateTime The date-time to calculate the elapsed time from.
     * @param formattingStringId The identifier of the resource to be used to format the resulting string.
     *                           e.g. "Expired %s" -> "Expired 2 days ago"
     * @return A human-readable string representing the elapsed time.
     */
    @Composable
    fun getElapsedTimeString(dateTime: LocalDateTime, formattingStringId: Int) : String {
        return doTimeString(
            duration = Duration.between(dateTime, LocalDateTime.now()),
            formattingStringId = formattingStringId,
            direction = Direction.LAST
        )
    }

    /**
     * Formats remaining time from the current time (in the sense of LocalDateTime.now()) to the
     * time given as an argument
     * The output depends on the difference in time, it will first try to give the difference in days
     * and will give it in hours if less than a day is left. If less than an hour remains it will
     * simply return the string "Less than 1 hour"
     * If argument is null it returns the string "NEVER". Note that a null value
     * represents an unlimited duration.
     * @param to LocalDateTime to which the remaining time should be computed
     */
    @Deprecated("Should prefer the other method as this does not comply with i18n")
    fun formatRemainingTime(to: LocalDateTime?): String {
        when(to) {
            null -> return "Never"
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

    fun atEndOfDay(date: LocalDate?): LocalDateTime? {
        return date?.atTime(23,59, 59)
    }
}

private fun RelativeDateTimeFormatter.format(quantity: Long, direction: RelativeDateTimeFormatter.Direction, timeUnit: RelativeDateTimeFormatter.RelativeUnit): String? {
    return this.format(quantity.toDouble(), direction, timeUnit)
}
