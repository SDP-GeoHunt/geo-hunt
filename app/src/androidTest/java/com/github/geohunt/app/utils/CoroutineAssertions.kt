package com.github.geohunt.app.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.fail
import kotlin.time.Duration

/**
 * Runs the given block with the specified timeout and throws a [TimeoutCancellationException]
 * if the timeout was exceeded.
 *
 * Contrary to [withTimeout], this function's timeout is not virtually sped up by Android tests,
 * and thus ensures that at most [timeout] real seconds are spent inside the test. This may be useful
 * to wait for a costly operation to finish with the timeout eagerly being triggered, e.g. for large
 * database or network transactions.
 *
 * @param timeout The timeout duration.
 * @param block The suspendable block to run.
 */
suspend fun withRealTimeout(
    timeout: Duration, block: suspend CoroutineScope.() -> Unit
) {
    // Changing dispatcher forces the test to disable virtual acceleration
    // of delays/timeout and hence require the real timeout to be spent
    return withContext(Dispatchers.IO) {
        withTimeout(timeout, block)
    }
}

/**
 * Asserts that the given block times out without any exception thrown.
 *
 * @param timeout The timeout duration.
 * @param block The suspendable block to run.
 */
suspend fun assertTimesOut(
    timeout: Duration, block: suspend CoroutineScope.() -> Unit
) {
    try {
        withRealTimeout(timeout, block)
        fail("Execution finished before timing out.")
    } catch(_: TimeoutCancellationException) {
        return
    } catch(e: Exception) {
        fail("Execution failed with error $e.")
    }
}

/**
 * Asserts that the given block finishes execution before the timeout.
 *
 * @param timeout The maximum time for the block to execute.
 * @param block The suspendable block to run.
 */
suspend fun assertFinishes(
    timeout: Duration, block: suspend CoroutineScope.() -> Unit
) {
    try {
        return withRealTimeout(timeout, block)
    } catch(e: TimeoutCancellationException) {
        fail("Execution did not finish before timeout.")
    } catch (e: Exception) {
        fail("Execution failed with error $e")
    }
}