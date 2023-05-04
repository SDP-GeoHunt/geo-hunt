package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.BaseLazyRef
import com.google.android.gms.tasks.Task

class MockLazyRef<T>(override val id: String, val fetchCallback: () -> Task<T>) :
    BaseLazyRef<T>() {
    override fun fetchValue(): Task<T> {
        return fetchCallback()
    }
}

