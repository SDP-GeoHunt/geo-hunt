package com.github.geohunt.app.data.local

import android.net.Uri

/**
 * Represents a picture present on the local storage of the phone. This may either be taken by
 * the user, or be stored in his photo albums.
 *
 * This data class abstracts over regular files to ensure correctness at all times of the photo
 * files (e.g. extension, permission to read, etc.).
 *
 * @param uri The URI
 */
data class LocalPicture(
    val uri: Uri
)