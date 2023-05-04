package com.github.geohunt.app.utility

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.equalTo
import org.junit.Assert
import org.junit.Test

class MathUtilsTest {

    private val epsilon = 1e-5

    @Test
    fun quantizeCorrectlyWorkForValidInput() {
        assertThat(5.3.quantize(1.0), closeTo(5.0, epsilon))
        assertThat(5.3.quantize(0.5), closeTo(5.5, epsilon))
        assertThat((-0.001).quantize(0.01), closeTo(0.0, epsilon))
        assertThat((-0.00051).quantize(0.001), closeTo(-0.001, epsilon))
        assertThat(4.31.quantize(8.0), closeTo(8.0, epsilon))
        assertThat(3.95.quantize(8.0), closeTo(0.0, epsilon))
    }

    @Test
    fun quantizeToLongCorrectlyWorkForValidInput() {
        assertThat(5.3.quantizeToLong(1.0), equalTo(5))
        assertThat(5.3.quantizeToLong(0.5), equalTo(11))
        assertThat((-0.001).quantizeToLong(0.01), equalTo(0))
        assertThat((-0.00051).quantizeToLong(0.001), equalTo(-1))
        assertThat(4.31.quantizeToLong(8.0), equalTo(1))
        assertThat(3.95.quantizeToLong(8.0), equalTo(0))
        assertThat((-3.95).quantizeToLong(8.0), equalTo(0))
        assertThat((-4.31).quantizeToLong(8.0), equalTo(-1))
    }

    @Test
    fun quantizeThrowsWhenNullOrNegativeBinSize() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            0.95.quantize(0.0)
        }

        Assert.assertThrows(IllegalArgumentException::class.java) {
            0.95.quantize(-10.1)
        }

        Assert.assertThrows(IllegalArgumentException::class.java) {
            0.95.quantizeToLong(0.0)
        }

        Assert.assertThrows(IllegalArgumentException::class.java) {
            0.95.quantizeToLong(-15.3)
        }
    }

    @Test
    fun quantizeThrowsWhenResultingDoubleCannotBeRepresentedByLong() {
        Assert.assertThrows(UnsupportedOperationException::class.java) {
            (Long.MIN_VALUE / 2.0).quantizeToLong(0.2)
        }
        Assert.assertThrows(UnsupportedOperationException::class.java) {
            (Long.MIN_VALUE / 2.0).quantize(0.2)
        }
        Assert.assertThrows(UnsupportedOperationException::class.java) {
            (Long.MAX_VALUE / 2.0).quantizeToLong(0.2)
        }
        Assert.assertThrows(UnsupportedOperationException::class.java) {
            (Long.MAX_VALUE / 2.0).quantize(0.2)
        }
    }

}
