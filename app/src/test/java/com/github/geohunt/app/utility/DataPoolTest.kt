package com.github.geohunt.app.utility

import com.github.geohunt.app.model.DataPool
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test

class DataPoolTest {

    private var count = 0
    private lateinit var dataPool : DataPool<String, String>

    @Before
    fun setup() {
        count = 0
        dataPool = DataPool() {
            count += 1
            "value: $it"
        }
    }

    @Test
    fun testGetCallFactoryOnlyOnce() {
        assertThat(count, equalTo(0))
        assertThat(dataPool.get("test"), equalTo("value: test"))
        assertThat(count, equalTo(1))
        assertThat(dataPool.get("test"), equalTo("value: test"))
        assertThat(count, equalTo(1))
    }

    @Test
    fun testRegisterDoNotCallFactory() {
        assertThat(count, equalTo(0))
        dataPool.register("help", "pleh")
        assertThat(count, equalTo(0))
        assertThat(dataPool.get("help"), equalTo("pleh"))
        assertThat(count, equalTo(0))
    }

}