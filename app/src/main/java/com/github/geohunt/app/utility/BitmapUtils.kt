package com.github.geohunt.app.utility

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object BitmapUtils {
    /**
     * Save the bitmap to the file
     *
     * @param file the file where the bitmap will be saved to
     * @param format the format to use in order to save the bitmap
     * @param quality the quality to format the bitmap
     */
    suspend fun saveToFile(bitmap: Bitmap, file: File, format: CompressFormat, quality: Int) {
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use {
                bitmap.compress(format, quality, it)
                it.flush()
            }
        }
    }

    /**
     * Try loading a bitmap from the given file
     *
     * @param file the file where we should download the bitmap from
     */
    suspend fun loadFromFile(file: File) : Bitmap {
        return withContext(Dispatchers.IO) {
            FileInputStream(file).use {
                BitmapFactory.decodeStream(it)
            }
        }
    }


}
