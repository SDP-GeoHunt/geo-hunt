package com.github.geohunt.app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.github.geohunt.app.ComposeActivity

private fun hasPermission(context: Context, permission: String) : Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun <I> rememberLauncherForActivityWithPermission(launcher: ActivityResultLauncher<I>, permission: String) : ((I) -> Unit) {
    val activity = LocalContext.current as Activity
    val mutableInput = remember {
        mutableStateOf<I?>(null)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("GeoHunt", "The permission $permission was granted")
            launcher.launch(mutableInput.value)
        }
        else {
            Log.w("GeoHunt", "The permission $permission was denied")
        }
    }

    return { input : I ->
        mutableInput.value = input
        when {
            hasPermission(activity, permission) -> {
                Log.i("GeoHunt", "The user already had the permission $permission")
                launcher.launch(mutableInput.value)
            }
            shouldShowRequestPermissionRationale(activity, permission) -> {
                Log.i("GeoHunt", "Should show rationale for permission $permission")
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

}

