package com.github.geohunt.app.model.database.firebase

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.databinding.BaseObservable
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.exceptionallyCompose
import com.github.geohunt.app.utility.toCompletableFuture
import com.google.firebase.storage.UploadTask.TaskSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.asTask
import java.io.File
import java.util.concurrent.CompletableFuture

internal class FirebaseBitmapRef(
    override val id: String,
    private val database: FirebaseDatabase
) : BaseLazyRef<Bitmap>() {

    override fun fetchValue(): CompletableFuture<Bitmap> {
        // Launch the async process
        val file = File(database.localImageFolder.absolutePath, id)

        val readFileFuture = BitmapUtils.loadFromFile(file).toCompletableFuture(database.activity)

        // Fetch the database if local storage failed
        return readFileFuture.exceptionallyCompose {
            database.storageImagesRef.child(id)
                .getFile(file)
                .toCompletableFuture(database.activity)
                .thenCompose {
                    BitmapUtils.loadFromFile(file).toCompletableFuture(database.activity)
                }
        }
    }

    internal fun saveToLocalStorageThenSubmit(activity: Activity) : CompletableFuture<TaskSnapshot> {
        if (!isLoaded) {
            throw IllegalArgumentException("saveToLocalStorageThenSubmit suppose the value is already loaded")
        }
        val bitmap = value!!

        // Launch the write file process
        val writeFileFuture = CoroutineScope(Dispatchers.IO).async {
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

