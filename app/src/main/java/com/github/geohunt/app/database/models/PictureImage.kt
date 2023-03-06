package com.github.geohunt.app.database.models

import android.graphics.Bitmap
import java.util.concurrent.CompletableFuture

interface PictureImage {
    val iid : String
    val bitmap : Bitmap?

    fun load() : CompletableFuture<Bitmap>

    fun save() : CompletableFuture<Void>
}
