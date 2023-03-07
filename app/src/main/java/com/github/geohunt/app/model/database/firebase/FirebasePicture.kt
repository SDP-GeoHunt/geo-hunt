package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.model.database.api.PictureImage
import com.google.firebase.storage.StorageReference
import java.util.concurrent.CompletableFuture

internal class FirebasePicture(override val iid: String, val storageRef: StorageReference) :
    PictureImage {
    override var bitmap : Bitmap ?= null

    override fun load(): CompletableFuture<Bitmap> {
        throw NotImplementedError()
    }

    override fun save(): CompletableFuture<Void> {
        throw NotImplementedError()
    }
}

