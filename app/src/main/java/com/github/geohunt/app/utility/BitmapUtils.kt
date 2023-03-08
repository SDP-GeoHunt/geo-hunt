package com.github.geohunt.app.utility

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.asTask
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
    fun saveToFile(bitmap: Bitmap, file: File, format: CompressFormat, quality: Int) : Task<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            withContext(Dispatchers.IO) {
                FileOutputStream(file).use {
                    bitmap.compress(format, quality, it)
                    it.flush()
                }
            }
        }.asTask()
    }

    /**
     * Start a task to load a specific file as a bitmap
     *
     * @param file the file to be loaded in memory
     */
    fun loadFromFile(file: File) : Task<Bitmap> {
        return CoroutineScope(Dispatchers.IO).async {
            withContext(Dispatchers.IO) {
                FileInputStream(file).use {
                    BitmapFactory.decodeStream(it)
                }
            }
        }.asTask()
    }


}
