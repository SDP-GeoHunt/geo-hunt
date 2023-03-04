package com.github.geohunt.app.sensor

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.github.geohunt.app.utility.attackTaskCompletionSourceToTask
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import kotlin.coroutines.cancellation.CancellationException

private fun hasPermission(context: Context, permission: String) : Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}



@Composable
private fun <I> GenericPermissionRequire(vararg permissions: String,
                                     callbackFailure: (String) -> Unit,
                                     callbackSuccess: (I?) -> Unit) : ((I) -> Unit)
{
    val activity = LocalContext.current as Activity
    val lateInput = remember {
        mutableStateOf<I?>(null)
    }
    val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
    ) { isGrantedMap ->
        var permissionFailed : String? = null
        isGrantedMap.entries
                .forEach {  entry ->
                    if (!entry.value) {
                        Log.w("GeoHunt", "The permission ${entry.key} was denied")
                        permissionFailed = entry.key
                    }
                    else {
                        Log.i("GeoHunt", "The permission ${entry.key} was granted")
                    }
                }

        if (permissionFailed != null) {
            callbackFailure(permissionFailed!!)
        }
        else {
            callbackSuccess(lateInput.value)
        }
    }

    return {
        lateInput.value = it
        var failedPermission : String? = null
        val launchedPermission = permissions.toList()
                .filter { permission ->
                    if (hasPermission(activity, permission)) {
                        Log.i("GeoHunt", "The user already had the permission $permission")
                        false
                    }
                    else if (shouldShowRequestPermissionRationale(activity, permission)) {
                        Log.i("GeoHunt", "Should show rationale for permission $permission")
                        failedPermission = permission
                        false
                    }
                    else {
                        true
                    }
                }
        if (failedPermission != null) {
            callbackFailure(failedPermission!!)
        }
        else if (launchedPermission.isNotEmpty()) {
            Log.i("GeoHunt", "Try retrieve permissions for $launchedPermission")
            launcher.launch(launchedPermission.toTypedArray())
        }
        else {
            callbackSuccess(it)
        }
    }
}

@Composable
fun <TResult> attachPermissionToTaskLaunch(vararg permissions: String, taskLauncher: () -> Task<TResult>) : () -> Task<TResult> {
    val taskCompletionSource = remember {
        mutableStateOf<TaskCompletionSource<TResult>?>(null)
    }
    val launcher = GenericPermissionRequire<Void?>(
            permissions = permissions,
            callbackFailure = {
                taskCompletionSource.value!!.setException(IllegalStateException("No sufficient permission for $it"))
            },
            callbackSuccess = {
                attackTaskCompletionSourceToTask(taskCompletionSource.value!!, taskLauncher())
            }
    )

    return {
        taskCompletionSource.value = TaskCompletionSource()
        launcher(null)
        taskCompletionSource.value!!.task
    }
}

@Composable
fun <I> attachPermissionToLaunch(vararg permissions: String, launcher: ActivityResultLauncher<I>) : ((I) -> Unit) {
    val activity = LocalContext.current as Activity
    val mutableInput = remember {
        mutableStateOf<I?>(null)
    }

    return GenericPermissionRequire<I>(
            permissions = permissions,
            callbackFailure = {})
    {
        launcher.launch(it)
    }
}

