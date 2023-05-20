package com.github.geohunt.app.sensor

import android.content.Context
import android.content.pm.PackageManager
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
import com.github.geohunt.app.utility.findActivity
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
    val allAreGranted: MutableState<Boolean>

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
@Deprecated("Prefer the use of view model and PermissionsUtils to this mess")
@Composable
fun rememberPermissionsState(vararg permissions: String): MultiplePermissionState {
    val mutableMultiplePermissionState = remember {
        MutableMultiplePermissionState()
    }
    mutableMultiplePermissionState.Initialize(permissions.asList())
    return mutableMultiplePermissionState
}


private class MutableMultiplePermissionState : MultiplePermissionState {

    override lateinit var allAreGranted: MutableState<Boolean>
    override lateinit var permissions: MutableState<List<Permission>>
    var launcher: ActivityResultLauncher<Array<String>>? = null
    var future: CompletableFuture<Void>? = null

    private val pAreAllGranted : Boolean
        get() = permissions.value.none { it.status.isDenied }

    @Composable
    fun Initialize(perms : List<String>) {
        val context = LocalContext.current

        permissions = remember {
            mutableStateOf(perms.map {
                Permission(it).updated(context)
            })
        }

        allAreGranted = remember {
            mutableStateOf(pAreAllGranted)
        }

        launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            updatePermissions(result)
        }

        if (pAreAllGranted) {
            future = CompletableFuture.completedFuture(null)
        }
    }

    private fun updatePermissions(permissionsUpdated: Map<String, Boolean>) {
        // First update list of the mutable permission state
        permissions.value =
            permissions.value.map {
                if (permissionsUpdated.getOrElse(it.name) { true }) {
                    it.updated(Granted)
                } else {
                    it.updated(Denied(true))
                }
            }

        // Complete the future (either succeed or failed)
        val deniedPermissions = permissions.value.filter { it.status.isDenied }.map { it.name }

        // Either succeed or deny the future
        if (deniedPermissions.isEmpty()) {
            allAreGranted.value = true
            future?.complete(null)
        } else {
            allAreGranted.value = false
            future?.completeExceptionally(PermissionDeniedException(deniedPermissions))
        }
    }

    override fun requestPermissions(): CompletableFuture<Void> {
        if (future != null) {
            return future!!
        }

        return if (pAreAllGranted) {
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
