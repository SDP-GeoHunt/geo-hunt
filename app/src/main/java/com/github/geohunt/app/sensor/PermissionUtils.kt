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
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.github.geohunt.app.ui.findActivity
import com.github.geohunt.app.utility.attackTaskCompletionSourceToTask
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import java.util.concurrent.CompletableFuture

/**
 * Define the two possible status for any permission, can be
 * either Granted or Denied
 */
sealed interface PermissionStatus {
    object Granted : PermissionStatus
    data class Denied(override val shouldShowRationale : Boolean) : PermissionStatus

    val shouldShowRationale : Boolean
        get() = false

    val isGranted : Boolean
        get() = this is Granted

    val isDenied : Boolean
        get() = this is Denied
}

/**
 * Define a permission name along with its status
 */
sealed interface Permission {
    val name : String
    val status : PermissionStatus
}

interface MultiplePermissionState
{
    /**
     * A list of all the permission that are requested by this state
     */
    val permissions : List<Permission>

    /**
     * Are all permission granted
     */
    val allAreGranted : Boolean
        get() = permissions.none { it.status.isDenied }

    /**
     * Ask the user for the required permission, once done update the permission list
     * of this object. The returned future will be completed upon the success of the operation
     * and will hold an PermissionDeniedException upon failure. Notice that this function
     * must not be called from the context of a Composable function an should only be called
     * from a side-effect context
     */
    fun launchPermissionRequest() : CompletableFuture<Void>
}

/**
 * Specialized exception for permission denied handling (used in CompletableFuture)
 */
class PermissionDeniedException(permissions: List<String>) : RuntimeException("The following permission was denied $permissions")

/**
 * Create a state used to request permission from a composable object. Notice the user must
 * then manually call the launchPermissionRequest on the resulting object
 */
@Composable
fun rememberPermissionsState(vararg permissions : String) : MultiplePermissionState
{
    val context = LocalContext.current
    val mutableMultiplePermissionState = remember {
        MutableMultiplePermissionState(permissions.toList().map {
            MutablePermission(it).updateStatus(context)
        })
    }

    mutableMultiplePermissionState.launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        // First update the state of the mutable permission state
        mutableMultiplePermissionState.mutablePermission.forEach {
            if (result.getOrElse(it.name) { true }) {
                it.status = PermissionStatus.Granted
            }
            else {
                it.status = PermissionStatus.Denied(true)
            }
        }

        // Secondly make the future either succeed or failed
        val deniedPermissions = mutableMultiplePermissionState.permissions
            .filter { it.status.isDenied }
            .map { it.name }

        if (deniedPermissions.isEmpty()) {
            mutableMultiplePermissionState.future?.complete(null)
        }
        else {
            Log.e("GeoHunt", "The following permissions: $deniedPermissions were denied by the user")
            mutableMultiplePermissionState.future?.completeExceptionally(PermissionDeniedException(deniedPermissions))
        }
    }



    return mutableMultiplePermissionState
}


private class MutablePermission(
    override var name: String,
    override var status: PermissionStatus = PermissionStatus.Denied(false)
) : Permission {
    fun updateStatus(context : Context) : MutablePermission {
        status = when {
            ContextCompat.checkSelfPermission(context, name) == PackageManager.PERMISSION_GRANTED
            -> PermissionStatus.Granted

            ActivityCompat.shouldShowRequestPermissionRationale(context.findActivity(), name)
            -> PermissionStatus.Denied(true)

            else
            -> PermissionStatus.Denied(false)
        }
        return this
    }
}

private class MutableMultiplePermissionState(val mutablePermission : List<MutablePermission>) : MultiplePermissionState
{
    override val permissions : List<Permission> = mutablePermission

    internal var launcher : ActivityResultLauncher<Array<String>>? = null

    var future : CompletableFuture<Void>? = null

    override fun launchPermissionRequest(): CompletableFuture<Void> {
        if (future != null) {
            return future!!
        }

        return if (allAreGranted) {
            CompletableFuture.completedFuture(null)
        } else {
            future = CompletableFuture()

            launcher!!.launch(
                permissions.filter { it.status.isDenied }.map { it.name }.toTypedArray()
            )

            future!!
        }
    }
}
