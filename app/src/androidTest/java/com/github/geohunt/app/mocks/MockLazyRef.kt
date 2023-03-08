package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.BaseLazyRef
import java.util.concurrent.CompletableFuture

class MockLazyRef<T>(override val id: String, val fetchCallback: () -> CompletableFuture<T>) :
    BaseLazyRef<T>() {
    override fun fetchValue(): CompletableFuture<T> {
        return fetchCallback()
    }
}

