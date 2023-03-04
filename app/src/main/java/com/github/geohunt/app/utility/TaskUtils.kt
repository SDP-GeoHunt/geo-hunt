package com.github.geohunt.app.utility

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import kotlin.coroutines.cancellation.CancellationException

fun <TResult> attackTaskCompletionSourceToTask(taskCompletionSource: TaskCompletionSource<TResult>,
                                               task: Task<TResult>) {
    task
            .addOnSuccessListener { result ->
                taskCompletionSource.setResult(result)
            }
            .addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }
            .addOnCanceledListener {
                taskCompletionSource.setException(CancellationException())
            }
}

fun <TResult, U> Task<TResult>.map(fn: (TResult) -> U) : Task<U> {
    return this.onSuccessTask {
        Tasks.forResult(fn(it))
    }
}

fun <TResult, U> Task<TResult>.flatMap(fn: (TResult) -> Task<U>) : Task<U> {
    return this.onSuccessTask {
        fn(it)
    }
}
