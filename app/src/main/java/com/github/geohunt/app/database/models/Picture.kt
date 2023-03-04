package com.github.geohunt.app.database.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class Picture(val bitmap : Bitmap) {
    companion object {
        fun serialize(picture: Picture) : String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            picture.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val b = byteArrayOutputStream.toByteArray()
            return String(Base64.encode(b, Base64.DEFAULT))
        }

        fun deserialize(pictureString: String) : Picture {
            val data = Base64.decode(pictureString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            return Picture(bitmap)
        }
    }
}