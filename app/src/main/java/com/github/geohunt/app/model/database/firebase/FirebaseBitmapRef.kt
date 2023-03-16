package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.thenDo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.UploadTask.TaskSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.asTask
import java.io.File

internal class FirebaseBitmapRef(
    override val id: String, private val database: FirebaseDatabase
) : BaseLazyRef<Bitmap>() {

    /**
     * Fetches the value of the referenced object by first attempting to load it from local storage.
     * If local storage retrieval fails, the object will be fetched from the remote database and stored locally for future
     * use.
     *
     * @return A task representing the loading of the object.
     */
    override fun fetchValue(): Task<Bitmap> {
        // Attempt to load the object from local storage
        val file = File(database.localImageFolder.absolutePath, id)

        // If local storage retrieval fails, fetch the object from the remote database and store it locally
        return BitmapUtils.loadFromFileAsync(file).asTask().continueWithTask {
            if (it.isSuccessful) {
                // If the object was successfully loaded from local storage, return it
                Tasks.forResult(it.result)
            } else {
                // If local storage retrieval failed, fetch the object from the remote database and store it locally
                database.storageImagesRef.child(id).getFile(file).thenDo {
                        BitmapUtils.loadFromFileAsync(file).asTask()
                    }
            }
        }
    }

    /**
     * Saves the referenced object to local storage and then submits it to the remote database.
     * Throws an IllegalArgumentException if the object is not yet loaded.
     *
     * @return A task representing the submission of the object to the remote database.
     * @throws IllegalArgumentException If the value has not yet been sets.
     */
    internal fun saveToLocalStorageThenSubmit(): Task<TaskSnapshot> {
        if (!isLoaded) {
            throw IllegalArgumentException("saveToLocalStorageThenSubmit suppose the value is already loaded")
        }
        val bitmap = value!!
        val file = File(database.localImageFolder.absolutePath, id)

        // Write the object to a file on disk
        val writeFileFuture =
            BitmapUtils.saveToFileAsync(bitmap, file, Bitmap.CompressFormat.PNG, 100).asTask()

        // Submit the object to the remote database
        return writeFileFuture.thenDo {
            val uri = Uri.fromFile(file)
            val imageRef = database.storageImagesRef.child(id)
            imageRef.putFile(uri)
        }
            .addOnFailureListener {
                Log.e("GeoHunt", "$it")
            }
    }

    companion object {
        /**
         * Returns the image ID corresponding to the given challenge ID.
         *
         * @param cid The ID of the challenge.
         * @return The image ID for the challenge.
         */
        internal fun getImageIdFromChallengeId(cid: String): String {
            return "challenges-$cid.png"
        }
    }
}

