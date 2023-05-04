package com.github.geohunt.app.ui.components.statistics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.Claim
import java.time.LocalDate

/**
 * Represents a Graph that show the evolution of the points of claims
 * Mostly calls the createEntries method which converts a list of claims to a pair
 * of lists (dates, points) that will represent the points in the graph we want to draw
 * @param claims The full list of claims we want to represent of the graph
 * @param dateGranularity the amount of claims we want to show on the graph (last week/month/year)
 */
@Composable
fun ClaimPointsGraph(claims: List<Claim>, dateGranularity: DateGranularity) {
    require(claims.size > 1) {"Claims has to contain at least two elements"}
    val (xDatesValues, yValues) = createEntries(claims, dateGranularity)

    Box(modifier = Modifier.fillMaxSize().padding(5.dp)) {
        DateGraph(xDateValues = xDatesValues, dateGranularity = dateGranularity, yValues = yValues)
    }
}

fun createEntries(claims: List<Claim>, dateGranularity: DateGranularity): Pair<List<LocalDate>, List<Long>> {
    val sortedClaims = claims.sortedBy { it.claimDate }

    //Split claims, undisplayed/displayed claims
    val now = LocalDate.now()
    val undisplayedLimit = dateGranularity.subtract(now)
    val (undisplayedClaims, displayedClaims) = partitionClaims(sortedClaims, undisplayedLimit)

    //Total of points of the claims that won't be displayed
    val basePoints = undisplayedClaims.sumOf { it.awardedPoints }

    //Convert claims to dates and points, if we have undisplayed claims
    //we add a base point to represent them
    //We also add an empty point at the current date to make the graph go to the end
    val (entryDates, awardedPoints) = separateClaims(displayedClaims, now, undisplayedClaims, undisplayedLimit)

    //Eliminate date duplicates by combining them (adding up points of same dates)
    val (dates, regroupedPoints) = regroupDates(entryDates, awardedPoints)

    //Go from individual claim points to total points until this claim
    val points = regroupedPoints
            .runningFold(basePoints) {acc, elem -> acc + elem}
            .takeLast(regroupedPoints.size) //discard helper element added by runningFold

    return Pair(dates, points)
}

private fun partitionClaims(claims: List<Claim>, undisplayedLimit: LocalDate): Pair<List<Claim>, List<Claim>> {
    return claims.partition { it.claimDate.toLocalDate().toEpochDay() <= undisplayedLimit.toEpochDay() }
}

private fun separateClaims(displayedClaims: List<Claim>, now: LocalDate, undisplayedClaims: List<Claim>, undisplayedLimit: LocalDate): Pair<List<LocalDate>, List<Long>> {
    val displayedDates = displayedClaims.map { it.claimDate.toLocalDate() } + listOf(now)
    val entryDates = if (undisplayedClaims.isEmpty()) displayedDates else
        listOf(undisplayedLimit.plusDays(1)) + displayedDates

    val displayedPoints = displayedClaims.map { it.awardedPoints } + listOf(0L)
    val awardedPoints = if (undisplayedClaims.isEmpty()) displayedPoints else
        listOf(0L) + displayedPoints

    return Pair(entryDates, awardedPoints)
}

private fun regroupDates(entryDates: List<LocalDate>, entryPoints: List<Long>): Pair<List<LocalDate>, List<Long>> {
    val pairedList = entryDates.zip(entryPoints)
    val groupedMap = pairedList.groupBy { it.first }
    val groupedPairs = groupedMap
            .map { entry -> Pair(entry.key, entry.value.sumOf { it.second }) }
            .sortedBy { it.first.toEpochDay() }

    //We now have a list of pairs (Date, Points), we convert it to a pair of list to be able to return it
    val dates = groupedPairs.map { it.first }
    val points = groupedPairs.map { it.second }
    return Pair(dates, points)
}
