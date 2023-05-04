package com.github.geohunt.app.ui.components.statistics

import com.github.geohunt.app.mocks.MockClaim
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate

class ClaimPointsGraphTest {
    @Test
    fun createEntriesCorrectlyGroupsDates() {
        val now = LocalDate.now()
        val d1 = now.minusDays(2)
        val d2 = now.minusDays(4)
        val testDates = listOf(d1, d1, d2, d2)
        val claims = testDates.map { MockClaim(claimDate = it.atTime(12, 12), awardedPoints = 5L) }
        val (dates, points) = createEntries(claims, DateGranularity.WEEK)

        //Check if dates got correctly grouped
        //We have to add a third date since we don't use now as a value (and it gets added automatically)
        Assert.assertEquals(3, points.size)
        Assert.assertEquals(3, dates.size)

        //Check if points got correctly added together
        Assert.assertEquals(10L, points[0])
        Assert.assertEquals(20L, points[1])
    }

    @Test
    fun undisplayedDatesAreConsidered() {
        val now = LocalDate.now()
        val d1 = now.minusDays(2)
        val d2 = now.minusDays(4)
        val undisplayed1 = now.minusDays(12)
        val undisplayed2 = now.minusDays(14)

        val claims = listOf(undisplayed1, undisplayed2, d1, d2)
                .map { MockClaim(claimDate = it.atTime(12, 12), awardedPoints = 10L) }

        val (dates, points) = createEntries(claims, DateGranularity.WEEK)

        //We need four entries (one representing undisplayed dates
        // and one representing LocalDate.now())
        Assert.assertEquals(4, points.size)
        Assert.assertEquals(4, dates.size)

        //Check if point representing undisplayed dates got added correctly
        Assert.assertEquals(20, points[0])
        Assert.assertEquals(now.minusDays(6), dates[0])
    }
}