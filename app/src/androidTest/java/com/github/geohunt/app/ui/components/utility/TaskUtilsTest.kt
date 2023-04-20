package com.github.geohunt.app.ui.components.utility

import com.github.geohunt.app.utility.thenDo
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Tasks
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class TaskUtilsTest {
    @Test
    fun testTaskThenDoOnSucceededTask() {
        var called = 0
        val succeededTask = Tasks.forResult(5)
        val resultTask = succeededTask.thenDo {
            called++
            Tasks.forResult(it + 10)
        }

        Tasks.await(resultTask)

        assertThat(called, equalTo(1))
        assertThat("Assert the task succeeded", resultTask.isSuccessful)
        assertThat("Assert the task was completed successfully", resultTask.isComplete)
        assertThat(resultTask.result, equalTo(15))
    }

    @Test
    fun testTaskThenDoOnFailedTask() {
        var called = 0
        val exception = IllegalStateException()
        val failedTask = Tasks.forException<Int>(exception)
        val resultTask = failedTask.thenDo {
            called++
            Tasks.forResult(it + 10)
        }

        try {
            Tasks.await(resultTask)
        } catch (_: Exception) {

        }

        assertThat(called, equalTo(0))
        assertThat("Assert the task completed with failure", resultTask.isComplete && !resultTask.isSuccessful)
        assertThat(resultTask.exception, not(nullValue()))
    }

    @Test
    fun testTaskThenDoFailedAfterSucceed() {
        var called = 0
        val exception = IllegalStateException()
        val succeededTask = Tasks.forResult(5)
        val resultTask = succeededTask.thenDo {
            called++
            Tasks.forException<Float>(exception)
        }

        try {
            Tasks.await(resultTask)
        } catch (_: Exception) {

        }

        assertThat(called, equalTo(1))
        assertThat("Assert the task completed with failure", resultTask.isComplete && !resultTask.isSuccessful)
        assertThat(resultTask.exception, not(nullValue()))
    }

    @Test
    fun testTaskThenMapAfterSucceed() {
        var called = 0
        val resultTask = Tasks.forResult(5).thenMap {
            called++
            it.toDouble() + 10.0
        }

        Tasks.await(resultTask)

        assertThat(called, equalTo(1))
        assertThat("Assert the task succeeded", resultTask.isSuccessful)
        assertThat("Assert the task was completed successfully", resultTask.isComplete)
        assertThat(resultTask.result, instanceOf(Double::class.java))
        assertThat(resultTask.result, closeTo(15.0, 1e-3))
    }

    @Test
    fun testTaskThenMapWithExceptionThrown() {
        var called = 0
        val resultTask = Tasks.forResult(5).thenMap {
            called++
            throw IllegalArgumentException()
            it.toDouble() + 10.0
        }

        try {
            Tasks.await(resultTask)
        } catch (_: Exception) {
        }

        assertThat(called, equalTo(1))
        assertThat("Assert the task completed with failure", resultTask.isComplete && !resultTask.isSuccessful)
        assertThat(resultTask.exception, not(nullValue()))
    }

    @Test
    fun testTaskThenMapAfterFailure() {
        var called = 0
        val resultTask = Tasks.forException<Int>(IllegalStateException()).thenMap {
            called++
            it.toDouble() + 10.0
        }

        try {
            Tasks.await(resultTask)
        } catch (_: Exception) {
        }

        assertThat(called, equalTo(0))
        assertThat("Assert the task completed with failure", resultTask.isComplete && !resultTask.isSuccessful)
        assertThat(resultTask.exception, not(nullValue()))
    }
}