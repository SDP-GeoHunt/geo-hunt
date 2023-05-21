package com.github.geohunt.app.utility

import android.graphics.Bitmap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BitmapUtilsTest {

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun testResizeBitmapToFitSquareBitmapAsync() = runBlocking {
        val bitmap = Bitmap.createBitmap(2048, 2048, Bitmap.Config.ARGB_8888)
        val result = BitmapUtils.resizeBitmapToFitAsync(bitmap, 1024 * 1024).await()
        assertThat(result.width, equalTo(1024))
        assertThat(result.height, equalTo(1024))
    }

    @Test
    fun testResizeBitmapToFitRectangularBitmapAsync() = runBlocking {
        val bitmap = Bitmap.createBitmap(1024, 4096, Bitmap.Config.ARGB_8888)
        val result = BitmapUtils.resizeBitmapToFitAsync(bitmap, 1024 * 1024).await()
        assertThat(result.width, equalTo(512))
        assertThat(result.height, equalTo(2048))
    }

    @Test
    fun testResizeBitmapToFitSmallBitmapAsync() = runBlocking {
        val bitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
        val result = BitmapUtils.resizeBitmapToFitAsync(bitmap, 1024 * 1024).await()
        assertThat(result, `is`(bitmap))
    }
}