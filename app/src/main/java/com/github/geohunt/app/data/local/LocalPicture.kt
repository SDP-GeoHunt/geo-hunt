package com.github.geohunt.app.data.local

import android.net.Uri
import java.io.File

/**
 * Represents a picture present on the local storage of the phone. This may either be taken by
 * the user, or be stored in his photo albums.
 *
 * This data class abstracts over regular files to ensure correctness at all times of the photo
 * files (e.g. extension, permission to read, etc.).
 *
 * @param file The underlying file that represents the image.
 */
data class LocalPicture(
    val file: File
) {
    val uri: Uri = Uri.fromFile(file)

    /**
     * Returns true if the picture is valid, i.e. it can be safely read as an image.
     * This function should be called before any read or upload.
     */
    fun isValid(): Boolean = file.isFile && file.canRead()

    init {
        require(isValid())
    }
}