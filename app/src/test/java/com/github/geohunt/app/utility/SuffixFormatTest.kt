package com.github.geohunt.app.utility

import com.github.geohunt.app.i18n.toSuffixedString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert
import org.junit.Test

class SuffixFormatTest {
    @Test
    fun testIntToSuffixedStringFailedOnNegativeValues() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            (-1).toSuffixedString()
        }

        Assert.assertThrows(IllegalArgumentException::class.java) {
            (-8945).toSuffixedString()
        }
    }

    @Test
    fun testIntToSuffixedStringSucceedForPositiveValues() {
        assertThat(4.toSuffixedString(), equalTo("4"))
        assertThat(987.toSuffixedString(), equalTo("987"))
        assertThat(53.toSuffixedString(), equalTo("53"))

        assertThat(1_452.toSuffixedString(), equalTo("1.5k"))
        assertThat(5_829.toSuffixedString(), equalTo("5.8k"))
        assertThat(56_461.toSuffixedString(), equalTo("56k"))
        assertThat(192_501.toSuffixedString(), equalTo("193k"))
        assertThat(192_499.toSuffixedString(), equalTo("192k"))

        assertThat(1_500_483.toSuffixedString(), equalTo("1.5M"))
        assertThat(8_590_483.toSuffixedString(), equalTo("8.6M"))
        assertThat(12_590_483.toSuffixedString(), equalTo("13M"))
        assertThat(12_500_001.toSuffixedString(), equalTo("13M"))
        assertThat(25_499_999.toSuffixedString(), equalTo("25M"))
        assertThat(295_499_999.toSuffixedString(), equalTo("295M"))
    }
}
