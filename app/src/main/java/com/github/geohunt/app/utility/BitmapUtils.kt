package com.github.geohunt.app.utility

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.math.sqrt

object BitmapUtils {
    /**
     * Save the bitmap to the file
     *
     * @param file the file where the bitmap will be saved to
     * @param format the format to use in order to save the bitmap
     * @param quality the quality to format the bitmap
     */
    fun saveToFileAsync(bitmap: Bitmap, file: File, format: CompressFormat, quality: Int) : Deferred<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            withContext(Dispatchers.IO) {
                FileOutputStream(file).use {
                    bitmap.compress(format, quality, it)
                    it.flush()
                }
            }
        }
    }

    /**
     * Start a task to load a specific file as a bitmap
     *
     * @param file the file to be loaded in memory
     */
    fun loadFromFileAsync(file: File) : Deferred<Bitmap> {
        return CoroutineScope(Dispatchers.IO).async {
            withContext(Dispatchers.IO) {
                FileInputStream(file).use {
                    BitmapFactory.decodeStream(it)
                }
            }
        }
    }

    /**
     * Resizes the given bitmap to fit within the specified maximum number of pixels, while preserving aspect ratio.
     * If the original bitmap already has less pixels than the specified maximum, it is returned unmodified.
     *
     * @param bitmap the bitmap to resize
     * @param maxPixels the maximum number of pixels in the resized bitmap
     * @throws IllegalArgumentException if the maximum number of pixels is not strictly positive
     * @return a [Task] containing the resized bitmap
     */
    fun resizeBitmapToFitAsync(bitmap: Bitmap, maxPixels: Int) : Deferred<Bitmap> {
        require(maxPixels > 0)

        return CoroutineScope(Dispatchers.Main).async {
            // If the bitmap is already well sized
            if (bitmap.width * bitmap.height <= maxPixels) {
                return@async bitmap
            }

            // Compute the scaling factor to fit within the max pixel
            val originalPixels = bitmap.width * bitmap.height
            val scale = sqrt(maxPixels.toDouble() / originalPixels)

            // Calculate the new width and height based on the scaling factor
            val newWidth = (bitmap.width * scale).toInt()
            val newHeight = (bitmap.height * scale).toInt()

            // Create the resized bitmap
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }
    }


}
