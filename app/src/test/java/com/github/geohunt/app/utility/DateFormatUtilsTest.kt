package com.github.geohunt.app.utility

import org.junit.Assert.*

import org.junit.Test
import java.time.LocalDateTime

/**
 * Unit Test for utility methods in DateFormatUtils
 */
class DateFormatUtilsTest{
    @Test
    fun formatDateCorrectlyFormats() {
        val date = LocalDateTime.of(2001, 7, 24, 20, 50)
        assertEquals("24/07/2001", DateFormatUtils.formatDate(date))

        val date2 = LocalDateTime.of(2003, 11, 5, 15, 15)
        assertEquals("05/11/2003", DateFormatUtils.formatDate(date2))
    }

    @Test
    fun formatRemainingTimeWorksOnNullInput() {
        assertEquals("NEVER", DateFormatUtils.formatRemainingTime(null))
    }

    @Test
    fun formatRemainingTimeCorrectlyReturnsHours() {
        val now = LocalDateTime.now()
        //add one minute to take into account the fact that DateFormatUtils also uses .now()
        // but slightly later in the execution of the Test
        val later = now.plusHours(3).plusMinutes(1)

        assertEquals("3 hours", DateFormatUtils.formatRemainingTime(later))
    }

    @Test
    fun formatRemainingTimeCorrectlyExpressesSingularHours() {
        val now = LocalDateTime.now()
        //add one minute to take into account the fact that DateFormatUtils also uses .now()
        // but slightly later in the execution of the Test
        val later = now.plusHours(1).plusMinutes(1)

        assertEquals("1 hour", DateFormatUtils.formatRemainingTime(later))
    }

    @Test
    fun formatRemainingTimeCorrectlyReturnsDays() {
        val now = LocalDateTime.now()
        //add one hour to take into account the fact that DateFormatUtils also uses .now()
        // but slightly later in the execution of the Test
        val later = now.plusDays(5).plusHours(1)

        assertEquals("5 days", DateFormatUtils.formatRemainingTime(later))
    }

    @Test
    fun formatRemainingTimeCorrectlyExpressesSingularDays() {
        val now = LocalDateTime.now()
        //add one minute to take into account the fact that DateFormatUtils also uses .now()
        // but slightly later in the execution of the Test
        val later = now.plusDays(1).plusHours(1)

        assertEquals("1 day", DateFormatUtils.formatRemainingTime(later))
    }

    @Test
    fun formatRemainingTimeCorrectlyReturnsLessThanOneHour() {
        val now = LocalDateTime.now()
        val later = now.plusMinutes(45)

        assertEquals("Less than 1 hour", DateFormatUtils.formatRemainingTime(later))
    }

}