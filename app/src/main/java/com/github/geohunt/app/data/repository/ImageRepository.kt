package com.github.geohunt.app.data.repository

import android.net.Uri
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException

/**
 * Contains methods related to local and remote images retrieval, including uploading to
 * [Firebase Storage](https://firebase.google.com/docs/storage/).
 *
 * As this contains very broad methods that can be unrelated to the use case, avoid in most cases
 * using methods of this repository alone, and rather use the methods provided by the specialized
 * repositories. For example, to fetch the profile picture, prefer using [UserRepository.getProfilePictureUrl]
 * instead of [ImageRepository.getProfilePictureUrl] (even though the former uses the latter internally)
 * because it makes the intention clearer and avoids injecting the whole repository for a single
 * method.
 */
class ImageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    enum class ImageType {
        PROFILE_PICTURE,
        CHALLENGE_PHOTO;

        override fun toString(): String = when(this) {
            PROFILE_PICTURE -> "profile"
            CHALLENGE_PHOTO -> "challenge"
        }
    }

    /**
     * Uploads the given picture to Firebase Storage.
     *
     * The given [ImageType] determines the location
     */
    private suspend fun uploadImage(
        picture: LocalPicture,
        type: ImageType,
        id: String
    ): Uri = withContext(ioDispatcher) {
        if (!picture.isValid()) {
            throw IllegalStateException("Could not read image.")
        }

        val fileRef = storage.getReference("images/$type/$id.${IMAGE_EXTENSION}")

        val upload = fileRef.putFile(picture.uri)
        upload.await()

        return@withContext fileRef.downloadUrl.await()
    }

    suspend fun uploadProfilePicture(picture: LocalPicture, id: String) = uploadImage(picture, ImageType.PROFILE_PICTURE, id)
    suspend fun uploadChallengePhoto(photo: LocalPicture, coarseHash: String, id: String) =
        uploadImage(photo, ImageType.CHALLENGE_PHOTO, "$coarseHash/$id")

    fun getProfilePictureUrl(user: User): String = user.profilePictureUrl

    fun getChallengePhoto(challenge: Challenge): String = challenge.photoUrl

    companion object {
        private const val IMAGE_EXTENSION = "webp"
    }
}