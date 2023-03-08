package com.github.geohunt.app.sensor

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.geohunt.app.sensor.PermissionStatus.Denied
import com.github.geohunt.app.sensor.PermissionStatus.Granted
import com.github.geohunt.app.ui.findActivity
import java.util.concurrent.CompletableFuture

/**
 * Defines the two possible status for any permission,
 * can be either [Granted] or [Denied]
 */
sealed interface PermissionStatus {
    /**
     * Defines any permission that has been accepted by the users
     */
    object Granted : PermissionStatus

    /**
     * Defines any permission that has yet to be accepted or been refused by the users
     */
    data class Denied(override val shouldShowRationale: Boolean) : PermissionStatus

    /**
     * Whether we should show a descriptive rationale to explain why the application
     * needs this specific permission
     */
    val shouldShowRationale: Boolean
        get() = false

    /**
     * Check whether the current permission is granted
     */

    val isGranted: Boolean
        get() = this is Granted

    /**
     * Check whether the current permission is denied
     */
    val isDenied: Boolean
        get() = this is Denied
}

/**
 * Define a permission as a 2-Tuple composed of a name and a status
 */
class Permission(
    val name: String, val status: PermissionStatus = Denied(false)
) {
    /**
     * Return a permission corresponding with the actual permission state of the provided context
     */
    fun updated(context: Context): Permission {
        val status = when {
            ContextCompat.checkSelfPermission(context, name) == PackageManager.PERMISSION_GRANTED
            -> Granted

            ActivityCompat.shouldShowRequestPermissionRationale(context.findActivity(), name)
            -> Denied(true)

            else
            -> Denied(false)
        }
        return Permission(name, status)
    }

    /**
     * Update the permission status with a given value
     */
    fun updated(status: PermissionStatus): Permission {
        return Permission(name, status)
    }
}

/**
 * Defines a state representing the state of the permissions that was asked to track
 */
interface MultiplePermissionState {
    /**
     * A list of all the permission that are requested by this state
     */
    val permissions: MutableState<List<Permission>>

    /**
     * Are all permission granted
     */
    val allAreGranted: Boolean
        get() = permissions.value.none { it.status.isDenied }

    /**
     * Ask the user for the required permission, once done update the permission list
     * of this object. The returned future will be completed upon the success of the operation
     * and will hold an PermissionDeniedException upon failure. Notice that this function
     * must not be called from the context of a Composable function an should only be called
     * from a side-effect context
     */
    fun requestPermissions(): CompletableFuture<Void>
}

/**
 * Specialized exception for permission denied handling (using [CompletableFuture])
 */
class PermissionDeniedException(permissions: List<String>) :
    RuntimeException("The following permission was denied $permissions")

/**
 * Create a state used to request permission from a composable object. Notice the user must
 * then manually call the launchPermissionRequest on the resulting object
 */
@Composable
fun rememberPermissionsState(vararg permissions: String): MultiplePermissionState {
    val context = LocalContext.current
    val mutableMultiplePermissionState = remember {
        MutableMultiplePermissionState()
    }

    // Create permission list
    mutableMultiplePermissionState.permissions = remember {
        mutableStateOf(permissions.asList().map {
            Permission(it).updated(context)
        })
    }

    mutableMultiplePermissionState.launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        // First update the state of the mutable permission state
        mutableMultiplePermissionState.permissions.value =
            mutableMultiplePermissionState.permissions.value.map {
                if (result.getOrElse(it.name) { true }) {
                    it.updated(Granted)
                } else {
                    it.updated(Denied(true))
                }
            }

        // Secondly make the future either succeed or failed
        val deniedPermissions = mutableMultiplePermissionState.permissions
            .value
            .filter { it.status.isDenied }
            .map { it.name }

        if (deniedPermissions.isEmpty()) {
            mutableMultiplePermissionState.future?.complete(null)
        } else {
            Log.e(
                "GeoHunt",
                "The following permissions: $deniedPermissions were denied by the user"
            )
            mutableMultiplePermissionState.future?.completeExceptionally(
                PermissionDeniedException(
                    deniedPermissions
                )
            )
        }
    }

    return mutableMultiplePermissionState
}


private class MutableMultiplePermissionState() : MultiplePermissionState {
    override lateinit var permissions: MutableState<List<Permission>>

    internal var launcher: ActivityResultLauncher<Array<String>>? = null

    var future: CompletableFuture<Void>? = null

    override fun requestPermissions(): CompletableFuture<Void> {
        if (future != null) {
            return future!!
        }

        return if (allAreGranted) {
            CompletableFuture.completedFuture(null)
        } else {
            future = CompletableFuture()

            launcher!!.launch(
                permissions.value.filter { it.status.isDenied }.map { it.name }.toTypedArray()
            )

            future!!
        }
    }
}
