package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.LiveLazyRefListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class InstantLazyRef<T>(override val id: String, override var value: T?) :
    LiveLazyRef<T>() {
    override fun addListener(callback: (T) -> Any?): LiveLazyRefListener {
        return object: LiveLazyRefListener() {
            override fun stop() {

            }

        }
    }

    override fun fetchValue(): Task<T> {
        return Tasks.forResult(value)
    }
}