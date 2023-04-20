package com.github.geohunt.app.model

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class TestInvalidLazyRef {
    @Test
    fun invalidLazyRefFetchDoesNotThrow() {
        val invalidRef = InvalidLazyRef<Int>(RuntimeException("Invalid Ref Exception"))
        invalidRef.fetch()
    }

    @Test
    fun invalidLazyRefReturnTaskCompletedWithError() {
        val invalidRef = InvalidLazyRef<Int>(RuntimeException("Invalid Ref Exception"))
        val task = invalidRef.fetch()
        assertThat(task.isComplete, equalTo(true))
        assertThat(task.isSuccessful, equalTo(false))
    }
}