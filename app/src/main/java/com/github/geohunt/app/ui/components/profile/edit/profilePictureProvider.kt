package com.github.geohunt.app.ui.components.profile.edit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun profilePictureProvider(onPick: (Bitmap) -> Unit): () -> Unit {
    val ctx = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { it?.let {
            onPick(uriToBitMap(ctx, it))
        } } )

    return {
        imagePickerLauncher.launch(
            PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }
}

private fun uriToBitMap(ctx: Context, uri: Uri): Bitmap {
    val source = ImageDecoder.createSource(ctx.contentResolver, uri)

    return ImageDecoder.decodeBitmap(source)
}
