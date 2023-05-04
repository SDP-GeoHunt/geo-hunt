package com.github.geohunt.app.utility

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import java.time.ZoneId

@RunWith(MockitoJUnitRunner::class) // Unresolved reference MockitoJUnitRunner
class DateUtilsTest {

    private val now = LocalDateTime.of(2023, 1, 1, 11, 59, 45)

    private fun <T> mockedTime(fn: () -> T): T {
        var v: T? = null
        Mockito.mockStatic(LocalDateTime::class.java, Mockito.CALLS_REAL_METHODS)
            .use { mockedStatic ->
                mockedStatic.`when`<LocalDateTime> { LocalDateTime.now() }
                    .thenReturn(now)
                v = fn()
            }
        return v!!
    }

    private fun <T> mockedZone(zoneId: ZoneId, fn: () -> T): T {
        var v: T? = null

        Mockito.mockStatic(DateUtils::class.java, Mockito.CALLS_REAL_METHODS)
            .use { mockedStatic ->
                mockedStatic.`when`<ZoneId> { DateUtils.getSystemZoneId() }
                    .thenReturn(zoneId)
                v = fn()
            }
        return v!!
    }

    @Test
    fun testUtcIso6801Now() = mockedTime {
        assertThat(
            mockedZone(zoneId = ZoneId.of("UTC")) { DateUtils.utcIso8601Now() },
            startsWith("2023-01-01T11:59:45")
        )

        assertThat(
            mockedZone(zoneId = ZoneId.of("America/Catamarca")) { DateUtils.utcIso8601Now() },
            startsWith("2023-01-01T14:59:45")
        )

        assertThat(
            mockedZone(zoneId = ZoneId.of("Asia/Qatar")) { DateUtils.utcIso8601Now() },
            startsWith("2023-01-01T08:59:45")
        )
    }

    @Test
    fun testUtcIso6801FromLocal() {
        assertThat(
            mockedZone(zoneId = ZoneId.of("UTC")) { DateUtils.utcIso8601FromLocal(now) },
            startsWith("2023-01-01T11:59:45")
        )

        assertThat(
            mockedZone(zoneId = ZoneId.of("America/Catamarca")) { DateUtils.utcIso8601FromLocal(now) },
            startsWith("2023-01-01T14:59:45")
        )

        assertThat(
            mockedZone(zoneId = ZoneId.of("Asia/Qatar")) { DateUtils.utcIso8601FromLocal(now) },
            startsWith("2023-01-01T08:59:45")
        )
    }

    @Test
    fun testUtcIso6801FromLocalNullable() {
        assertThat(
            mockedZone(zoneId = ZoneId.of("UTC")) { DateUtils.utcIso8601FromLocalNullable(now) },
            startsWith("2023-01-01T11:59:45")
        )

        assertThat(
            mockedZone(zoneId = ZoneId.of("America/Catamarca")) { DateUtils.utcIso8601FromLocalNullable(now) },
            startsWith("2023-01-01T14:59:45")
        )

        assertThat(
            mockedZone(zoneId = ZoneId.of("Asia/Qatar")) { DateUtils.utcIso8601FromLocalNullable(now) },
            startsWith("2023-01-01T08:59:45")
        )

        assertThat(DateUtils.utcIso8601FromLocalNullable(null), equalTo(null))
    }

    @Test
    fun testLocalFromUtcIso6801() {
        val time = mockedZone(zoneId = ZoneId.of("Asia/Istanbul")) {
            DateUtils.localFromUtcIso8601("2023-01-01T11:59:45Z")
        }

        assertThat(time.year, equalTo(now.year))
        assertThat(time.month, equalTo(now.month))
        assertThat(time.dayOfMonth, equalTo(now.dayOfMonth))
        assertThat(time.hour, equalTo(now.hour + 3))
        assertThat(time.minute, equalTo(now.minute))
        assertThat(time.second, equalTo(now.second))
    }

    @Test
    fun testLocalFromUtcIso6801Nullable() {
        val time = mockedZone(zoneId = ZoneId.of("Asia/Istanbul")) {
            DateUtils.localNullableFromUtcIso8601("2023-01-01T11:59:45Z")!!
        }

        assertThat(time.year, equalTo(now.year))
        assertThat(time.month, equalTo(now.month))
        assertThat(time.dayOfMonth, equalTo(now.dayOfMonth))
        assertThat(time.hour, equalTo(now.hour + 3))
        assertThat(time.minute, equalTo(now.minute))
        assertThat(time.second, equalTo(now.second))

        assertThat(DateUtils.localNullableFromUtcIso8601(null), nullValue())
    }
}
