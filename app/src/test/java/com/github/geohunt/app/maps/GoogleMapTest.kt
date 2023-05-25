package com.github.geohunt.app.maps

import com.github.geohunt.app.maps.marker.getExpiryString
import org.junit.Test
import java.time.LocalDateTime

class GoogleMapTest {
    @Test
    fun testExpiryStringIsReturnedCorrectly() {
        val mockCurrentDate = LocalDateTime.of(2025, 1, 1, 0, 0)

        val mockExpiresInMinutes = LocalDateTime.of(2025, 1, 1, 0, 30)
        assert(getExpiryString(mockExpiresInMinutes, mockCurrentDate) == "Expires in 30 minute(s)")

        val mockExpiresInHours = LocalDateTime.of(2025, 1, 1, 1, 0)
        assert(getExpiryString(mockExpiresInHours, mockCurrentDate) == "Expires in 1 hour(s)")

        val mockExpiresInDays = LocalDateTime.of(2025, 1, 2, 0, 0)
        assert(getExpiryString(mockExpiresInDays, mockCurrentDate) == "Expires in 1 day(s)")

        val mockExpiresInMonths = LocalDateTime.of(2025, 2, 8, 0, 0)
        assert(getExpiryString(mockExpiresInMonths, mockCurrentDate) == "Expires in 1 month(s)")

        val mockExpiresInYears = LocalDateTime.of(2026, 1, 1, 0, 0)
        assert(getExpiryString(mockExpiresInYears, mockCurrentDate) == "Expires in 1 year(s)")
    }
}