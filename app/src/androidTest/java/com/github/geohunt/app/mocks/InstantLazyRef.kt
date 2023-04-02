package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.BaseLazyRef
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class InstantLazyRef<T>(override val id: String, override var value: T?) :
    BaseLazyRef<T>() {
    override fun fetchValue(): Task<T> {
        return Tasks.forResult(value)
    }
}