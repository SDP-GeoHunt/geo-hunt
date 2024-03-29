package com.github.geohunt.app.utility

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Searches up the context hierarchy to find the [Activity] associated with this [Context].
 *
 * @return the [Activity] associated with this [Context]
 * @throws IllegalStateException if this method is called in the context of a non-Activity context
 */
fun Context.findActivity() : Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Context.findActivity should be called in the context of an Activity")
}

fun Context.createImageFile(suffix: String = ".jpg"): File {
    // See https://stackoverflow.com/a/27023535 for explanation of Location.US
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFileName = "GENERATED_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        suffix, /* suffix */
        externalCacheDir /* directory */
    )
}