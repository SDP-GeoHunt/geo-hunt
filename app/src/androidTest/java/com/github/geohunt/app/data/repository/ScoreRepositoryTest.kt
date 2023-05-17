package com.github.geohunt.app.data.repository

import com.github.geohunt.app.model.database.FirebaseEmulator
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ScoreRepositoryTest {
    private lateinit var database: FirebaseDatabase

    @Before
    fun setupEmulator() {
        database = FirebaseEmulator.getEmulatedFirebase()
    }

    @After
    fun deleteScores() {
        database.getReference("scores").removeValue()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun defaultScoreIsZero() = runTest {
        val scoreRepository = ScoreRepository(database)
        val score = scoreRepository.getScore("any")
        Assert.assertEquals(0, score)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun incrementingScoresCorrectlyIncrements() = runTest {
        val scoreRepository = ScoreRepository(database)

        scoreRepository.incrementUserScore("1", 10L)
        val score1 = scoreRepository.getScore("1")
        Assert.assertEquals(10L, score1)

        scoreRepository.incrementUserScore("1", 100L)
        val score2 = scoreRepository.getScore("1")
        Assert.assertEquals(110L, score2)

        scoreRepository.incrementUserScore("2", 50L)
        val score3 = scoreRepository.getScore("2")
        Assert.assertEquals(50L, score3)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getTopNReturnsCorrectList() = runTest {
        val scoreRepository = ScoreRepository(database)

        for(i in 1..10) {
            scoreRepository.incrementUserScore(i.toString(), i.toLong())
        }

        val expected = (10 downTo 1).map { Pair(it.toString(), it.toLong()) }

        val getAll = scoreRepository.getTopNUsers(100)
        Assert.assertEquals(expected, getAll)

        val getExact = scoreRepository.getTopNUsers(expected.size)
        Assert.assertEquals(expected, getExact)

        val get5 = scoreRepository.getTopNUsers(5)
        Assert.assertEquals(expected.take(5), get5)
    }
}