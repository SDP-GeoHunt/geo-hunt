package com.github.geohunt.app.ui.components.statistics

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class DateGraphTest {
    @get:Rule
    val testRule = createComposeRule()

    private fun setupComposable(dates: List<LocalDate>, granularity: DateGranularity, points: List<Long>) {
        testRule.setContent {
            DateGraph(xDateValues = dates, dateGranularity = granularity, yValues = points)
        }
    }

    @Test
    fun dateRangeGivesCorrectRange() {
        val base = LocalDate.of(2023, 4, 30)
        val expectedWeek = listOf(base.minusDays(6),
                base.minusDays(5),
                base.minusDays(4),
                base.minusDays(3),
                base.minusDays(2),
                base.minusDays(1),
                base)

        Assert.assertEquals(expectedWeek, DateGranularity.WEEK.dateRange(base))

        val expectedMonth = listOf(base.minusWeeks(3),
                base.minusWeeks(2),
                base.minusWeeks(1),
                base)

        Assert.assertEquals(expectedMonth, DateGranularity.MONTH.dateRange(base))
    }

    @Test
    fun subtractRemovesCorrectAmount() {
        val base = LocalDate.of(2023, 4, 30)
        Assert.assertEquals(base.minusDays(7), DateGranularity.WEEK.subtract(base))
        Assert.assertEquals(base.minusWeeks(4), DateGranularity.MONTH.subtract(base))
        Assert.assertEquals(base.minusMonths(12), DateGranularity.YEAR.subtract(base))
    }

    @Test
    fun dateGraphThrowsOnEmptyInputs() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            setupComposable(listOf(), DateGranularity.YEAR, listOf())
        }
    }

    @Test
    fun dateGraphThrowsOnUnrelatedInputs() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            setupComposable(listOf(LocalDate.now()), DateGranularity.YEAR, listOf(3, 4))
        }
    }

}