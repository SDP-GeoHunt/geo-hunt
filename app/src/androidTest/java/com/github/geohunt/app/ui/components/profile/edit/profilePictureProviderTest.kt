package com.github.geohunt.app.ui.components.profile.edit

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class profilePictureProviderTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun triggersCorrectIntentWhenCalled() {
        var provider: (() -> Unit)? = null
        rule.setContent {
            provider = profilePictureProvider(onPick = { })
        }
        Intents.init()
        assert(provider != null)
        provider!!()
        intended(
            allOf(
                hasAction("android.provider.action.PICK_IMAGES")
            )
        )
        Intents.release()
    }

    @Test
    fun callbacksWhenPicked() {
        var provider: (() -> Unit)? = null
        val cf = CompletableFuture<Bitmap>()
        rule.setContent {
            provider = profilePictureProvider(onPick = { cf.complete(it) })
        }

        // Create the intent result
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val intent = Intent()
        intent.data = rule.activity.saveImage(bitmap)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        Intents.init()
        intending(
            allOf(
                hasAction("android.provider.action.PICK_IMAGES")
            )
        ).respondWith(result)

        assert(provider != null)
        provider!!()

        // Throws exception if not finished within 2s
        cf.get(15, TimeUnit.SECONDS)
        Intents.release()
    }

    @Test
    fun doesNotCallbackOnFail() {
        var provider: (() -> Unit)? = null
        val cf = CompletableFuture<Bitmap>()
        rule.setContent {
            provider = profilePictureProvider(onPick = { cf.complete(it) })
        }

        // Create the intent result
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val intent = Intent()
        intent.data = rule.activity.saveImage(bitmap)
        val result = Instrumentation.ActivityResult(Activity.RESULT_CANCELED, intent)

        Intents.init()
        intending(
            allOf(
                hasAction("android.provider.action.PICK_IMAGES")
            )
        ).respondWith(result)

        assert(provider != null)
        provider!!()
        Thread.sleep(2000) // I dont know how to wait for it to happen :(
        assert(!cf.isDone)
        Intents.release()
    }


    // From https://stackoverflow.com/questions/8295773/how-can-i-transform-a-bitmap-into-a-uri
    private fun Context.saveImage(bitmap: Bitmap): Uri? {
        var uri: Uri? = null
        try {
            val fileName = System.nanoTime().toString() + ".png"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                } else {
                    val directory =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    val file = File(directory, fileName)
                    put(MediaStore.MediaColumns.DATA, file.absolutePath)
                }
            }

            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                contentResolver.openOutputStream(it).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.apply {
                        clear()
                        put(MediaStore.Audio.Media.IS_PENDING, 0)
                    }
                    contentResolver.update(uri, values, null, null)
                }
            }
            return uri
        }catch (e: java.lang.Exception) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                contentResolver.delete(uri, null, null)
            }
            throw e
        }
    }
}