package com.github.geohunt.app.utility

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/**
 * Combines a list of [Flow] that all emit lists into a single [Flow] source that emits the
 * concatenation of all lists.
 */
fun <T> List<Flow<List<T>>>.aggregateFlows(): Flow<List<T>> =
    if (isEmpty()) flowOf(emptyList()) else reduce {
        leftFlow, rightFlow -> leftFlow.combine(rightFlow) {
            leftList, rightList -> leftList + rightList
        }
    }
