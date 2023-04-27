package com.github.geohunt.app.ui.components.statistics

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.ui.components.utils.Graph
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun Statistics(claims: List<Claim>) {

}

@Preview
@Composable
fun Test() {
    val now = LocalDate.now()
    val range = listOf<Long>(0, 3, 6, 9, 12, 15, 18)
    val dates = (0 until 7).reversed().map { now.minusDays(it.toLong()) }
    DateGraph(xDateValues = dates, DateGranularity.MONTH, yValues = range.toList().map { it*it })
}

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
            yValues = yValues,
            yBottom = yValues.first(),
            yTop = yValues.last(),
            yStrings = listOf())
}

enum class DateGranularity(private val displaySteps: Long,
                           private val displayUnit: ChronoUnit) {
    WEEK(7, ChronoUnit.DAYS),
    MONTH(4, ChronoUnit.WEEKS),
    YEAR(12, ChronoUnit.MONTHS);

    fun dateRange(until: LocalDate): List<LocalDate> {
        val range = (0 until displaySteps).reversed()
        return range.map { until.minus(it, displayUnit) }
    }
}