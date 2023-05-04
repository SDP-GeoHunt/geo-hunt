package com.github.geohunt.app.mocks

import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

object MockProfilePicture : LazyRef<Bitmap> {
    override val id: String = "0"
    override var value: Bitmap? = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    override fun fetch(): Task<Bitmap> = Tasks.forResult(value)
}