package com.github.geohunt.app.utility.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class TestBaseLazyRef {
    @Test
    fun baseLazyRefFetchValueWasCalledOnce() {
        var invoked = 0

        val lazyRef = object : BaseLazyRef<Int>() {
            override fun fetchValue(): Task<Int> {
                invoked += 1
                return Tasks.forResult(20)
            }

            override val id: String
                get() = TODO("Not yet implemented")
        }

        assertThat(lazyRef.fetch().result, equalTo(20))
        assertThat(invoked, equalTo(1))
    }

    @Test
    fun baseLazyRefFetchValueWasNotCalledMoreThanOnce() {
        var invoked = 0

        val lazyRef = object : BaseLazyRef<Int>() {
            override fun fetchValue(): Task<Int> {
                invoked += 1
                return Tasks.forCanceled()
            }

            override val id: String
                get() = TODO("Not yet implemented")
        }

        lazyRef.fetch()
        lazyRef.fetch()
        lazyRef.fetch()
        assertThat(invoked, equalTo(1))
    }

}