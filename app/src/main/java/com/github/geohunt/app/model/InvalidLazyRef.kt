package com.github.geohunt.app.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class InvalidLazyRef<T>(private val exception: Exception) : BaseLazyRef<T>() {
    override fun fetchValue(): Task<T> {
        return Tasks.forException(exception)
    }

    override val id: String = "invalid-ref"
}