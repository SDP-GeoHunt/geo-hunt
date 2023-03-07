package com.github.geohunt.app.model.database.firebase

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.exceptionallyCompose
import com.github.geohunt.app.utility.toCompletableFuture
import com.google.firebase.storage.UploadTask.TaskSnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.asTask
import java.io.File
import java.util.concurrent.CompletableFuture

internal class FirebaseBitmapRef(
    override val id: String,
    private val database: FirebaseDatabase
) : LazyRef<Bitmap> {
    override var value : Bitmap? = null

    override fun load(): CompletableFuture<Bitmap> {
        if (isLoaded) {
            val future = CompletableFuture<Bitmap>()
            future.complete(value)
            return future
        }

        // Launch the async process
        // TODO: Move away from GlobalScope has it may be dangerous
        val readFileFuture = GlobalScope.async {
            val file = File(database.localImageFolder.absolutePath, id)
            BitmapUtils.loadFromFile(file)
        }.asTask().toCompletableFuture(database.activity)

        // Fetch the database if local storage failed
        return readFileFuture.exceptionallyCompose {
            val file = File(database.localImageFolder.absolutePath, id)

            database.storageImagesRef.child(id)
                .getFile(file)
                .toCompletableFuture(database.activity)
                .thenCompose {
                    GlobalScope.async {
                        BitmapUtils.loadFromFile(file)
                    }.asTask().toCompletableFuture(database.activity)
                }
        }
    }

    internal fun saveToLocalStorageThenSubmit(activity: Activity) : CompletableFuture<TaskSnapshot> {
        if (!isLoaded) {
            throw IllegalArgumentException("saveToLocalStorageThenSubmit suppose the value is already loaded")
        }
        val bitmap = value!!

        // Launch the write file process
        // TODO: Move away from GlobalScope has it may be dangerous
        val writeFileFuture = GlobalScope.async {
            val file = File(database.localImageFolder.absolutePath, id)
            BitmapUtils.saveToFile(bitmap, file, Bitmap.CompressFormat.PNG, 100)
            file
        }.asTask().toCompletableFuture(activity)

        return writeFileFuture.thenCompose { file ->
            val uri = Uri.fromFile(file)
            val imageRef = database.storageImagesRef.child(id)
            val uploadTask = imageRef.putFile(uri)

            uploadTask.toCompletableFuture(activity)
        }
    }

    companion object {
        internal fun getImageIdFromChallengeId(cid: String) : String {
            return "challenges-$cid.png"
        }
    }
}

