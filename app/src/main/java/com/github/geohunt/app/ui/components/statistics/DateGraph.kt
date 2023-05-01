package com.github.geohunt.app.ui.components.statistics

import androidx.compose.runtime.Composable
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.ui.components.utils.Graph
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * A graph that draws x/y points, where x is a date and y is a value
 * @param xDateValues The x values of our points represented by dates
 *  have to be in the range dateGranularity.dateRange(LocalDate.now())
 * @param dateGranularity the DateGranularity of the graph, gives the range of dates that will
 *  be shown on the graph (last week, month, year)
 * @param yValues the y values of our points represented by Long values
 */
@Composable
fun DateGraph(
        xDateValues: List<LocalDate>,
        dateGranularity: DateGranularity,
        yValues: List<Long>,
) {
    require(xDateValues.isNotEmpty())
    require(yValues.isNotEmpty())
    require(xDateValues.size == yValues.size)

    val dateRange = dateGranularity.dateRange(LocalDate.now())

    val xStrings = dateRange.map { DateFormatUtils.formatDate(it) }
    val integerDateValues = xDateValues.map { it.toEpochDay() }

    Graph(xValues = integerDateValues,
            xBottom = dateRange.first().toEpochDay(),
            xTop = dateRange.last().toEpochDay(),
            xStrings = xStrings,
            yValues = yValues)
}

/**
 * Used to represent the granularity of the dates we want to show
 * Contains 3 values representing a week, a month and a year
 */
enum class DateGranularity(private val displaySteps: Long,
                           private val displayUnit: ChronoUnit) {
    WEEK(7, ChronoUnit.DAYS),
    MONTH(4, ChronoUnit.WEEKS),
    YEAR(12, ChronoUnit.MONTHS);

    fun dateRange(until: LocalDate): List<LocalDate> {
        val range = (0 until displaySteps).reversed()
        return range.map { until.minus(it, displayUnit) }
    }

    fun subtract(from: LocalDate): LocalDate {
        return from.minus(displaySteps, displayUnit)
    }

    override fun toString(): String {
        return when(this) {
            WEEK -> "Last week"
            MONTH -> "Last month"
            YEAR -> "Last year"
        }
    }
}