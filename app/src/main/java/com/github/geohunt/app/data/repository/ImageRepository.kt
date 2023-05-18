package com.github.geohunt.app.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.text.style.BulletSpan
import com.github.geohunt.app.R
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.BitmapUtils.resizeBitmapToFit
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
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
        CLAIM_PHOTO,
        BOUNTY_CLAIM_PHOTO,
        CHALLENGE_PHOTO;
        override fun toString(): String = when(this) {
            PROFILE_PICTURE -> "profile"
            CHALLENGE_PHOTO -> "challenge"
            CLAIM_PHOTO -> "claim"
            BOUNTY_CLAIM_PHOTO -> "bounty-claim"
        }
    }


    /**
     * Preprocess the image at the given file
     */
    suspend fun preprocessImage(image: Bitmap, outputFileFactory: suspend (String) -> File) : File = withContext(ioDispatcher) {
        val bitmap = image
            .resizeBitmapToFit(R.integer.maximum_number_of_pixel_per_photo)

        val outputFile = outputFileFactory(".webp")

        BitmapUtils.saveToFile(bitmap,
            outputFile,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP,
            80)

        return@withContext outputFile
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
        val fileRef = storage.getReference("images/$type/$id.${IMAGE_EXTENSION}")

        val upload = fileRef.putFile(picture.uri)
        upload.await()

        return@withContext fileRef.downloadUrl.await()
    }

    suspend fun uploadProfilePicture(picture: LocalPicture, id: String) = uploadImage(picture, ImageType.PROFILE_PICTURE, id)
    suspend fun uploadChallengePhoto(photo: LocalPicture, coarseHash: String, id: String) =
        uploadImage(photo, ImageType.CHALLENGE_PHOTO, "$coarseHash/$id")

    fun getProfilePictureUrl(user: User): String? = user.profilePictureUrl

    suspend fun uploadClaimPhoto(photo: LocalPicture, id: String) =
        uploadImage(photo, ImageType.CLAIM_PHOTO, id)

    suspend fun uploadBountyClaimPhoto(photo: LocalPicture, id: String, bid: String) =
        uploadImage(photo, ImageType.BOUNTY_CLAIM_PHOTO, "$id-$bid")

    fun getChallengePhoto(challenge: Challenge): String = challenge.photoUrl

    companion object {
        private const val IMAGE_EXTENSION = "webp"
    }
}