package com.github.geohunt.app.ui.components.statistics

import androidx.compose.runtime.Composable
import com.github.geohunt.app.model.database.api.Claim
import java.time.LocalDate

@Composable
fun ClaimPointsGraph(claims: List<Claim>, dateGranularity: DateGranularity) {
    val (xDatesValues, yValues) = createEntries(claims, dateGranularity)

    DateGraph(xDateValues = xDatesValues, dateGranularity = dateGranularity, yValues = yValues)
}

fun createEntries(claims: List<Claim>, dateGranularity: DateGranularity): Pair<List<LocalDate>, List<Long>> {
    val sortedClaims = claims.sortedBy { it.time }

    //Split claims, undisplayed/displayed claims
    val now = LocalDate.now()
    val undisplayedLimit = dateGranularity.subtract(now)
    val (undisplayedClaims, displayedClaims) = partitionClaims(sortedClaims, undisplayedLimit)

    //Total of points of the claims that won't be displayed
    val basePoints = undisplayedClaims.sumOf { it.awardedPoints }

    //Convert claims to dates and points, if we have undisplayed claims
    //we add a base point to represent them
    val displayedDates = displayedClaims.map { it.time.toLocalDate() }
    val entryDates = if(undisplayedClaims.isEmpty()) displayedDates else
        listOf(undisplayedLimit.plusDays(1)) + displayedDates

    val displayedPoints = displayedClaims.map { it.awardedPoints }
    val awardedPoints = if(undisplayedClaims.isEmpty()) displayedPoints else
        listOf(0L) + displayedPoints

    //Eliminate date duplicates by combining them (adding up points of same dates)
    val (dates, regroupedPoints) = regroupDates(entryDates, awardedPoints)

    //Go from individual claim points to total points until this claim
    val points = regroupedPoints
            .runningFold(basePoints) {acc, elem -> acc + elem}
            .takeLast(regroupedPoints.size) //discard helper element added by runningFold

    return Pair(dates, points)
}

fun regroupDates(entryDates: List<LocalDate>, entryPoints: List<Long>): Pair<List<LocalDate>, List<Long>> {
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

fun partitionClaims(claims: List<Claim>, undisplayedLimit: LocalDate): Pair<List<Claim>, List<Claim>> {
    return claims.partition { it.time.toLocalDate().toEpochDay() <= undisplayedLimit.toEpochDay() }
}