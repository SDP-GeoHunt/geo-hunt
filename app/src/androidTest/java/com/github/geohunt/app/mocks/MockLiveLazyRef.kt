package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.LiveLazyRefListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class MockLiveLazyRef<T>(override val id: String, defaultValue: T? = null) : LiveLazyRef<T>() {
    var listeners: List<(T) -> Any?> = listOf()

    override fun addListener(callback: (T) -> Any?): LiveLazyRefListener {
        listeners = listeners + callback
        return object: LiveLazyRefListener() {
            override fun stop() {
                listeners = listeners - callback
            }
        }
    }

    override var value = defaultValue

    override fun fetchValue(): Task<T> {
        return Tasks.forResult(value)
    }

    fun updateValue(newValue: T) {
        value = newValue
        listeners.map { it(newValue) }
    }
}