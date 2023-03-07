package com.github.geohunt.app.model.database.api

import android.graphics.Bitmap
import java.util.concurrent.CompletableFuture

interface PictureImage {
    val iid : String
    val bitmap : Bitmap?

    fun load() : CompletableFuture<Bitmap>

    fun save() : CompletableFuture<Void>
}
