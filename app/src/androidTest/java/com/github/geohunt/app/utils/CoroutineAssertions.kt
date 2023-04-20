package com.github.geohunt.app.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.junit.Assert.fail
import kotlin.time.Duration

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
        withTimeout(timeout, block)
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
        return withTimeout(timeout, block)
    } catch(e: TimeoutCancellationException) {
        fail("Execution did not finish before timeout.")
    } catch (e: Exception) {
        fail("Execution failed with error $e")
    }
}