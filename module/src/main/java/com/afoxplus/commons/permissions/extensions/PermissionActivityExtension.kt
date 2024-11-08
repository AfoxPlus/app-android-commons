package com.afoxplus.commons.permissions.extensions

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.afoxplus.commons.permissions.delivery.BasePermissionMVVMActivity

/**
 * Method to allow permission's framework to initialize activities callback in order to receive
 * user's response in permissions asking.
 *
 * This method should be called during or before [Activity.onCreate]
 */
fun BasePermissionMVVMActivity.initPermissionFlow(listener: PermissionFlowCallback) {
    requestPermissionLauncher = activityResultLauncher(listener)
}


private fun ComponentActivity.activityResultLauncher(listener: PermissionFlowCallback) =
    this.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { requestResponse: Map<String, Boolean> ->
        listener.onFinishFlow(hashMapOf<String, PermissionState>().also {
            requestResponse.forEach { permission ->
                it[permission.key] = getPermissionStatus(permission.key, permission.value)
            }
        })
    }

/**
 * Initialize framework and start a new flow to ask permissions to the user
 * This method should be use when the permission is asked during Fragment initialization
 *
 * This flow should be called in [Activity.onCreate] because, in some cases, an activity callback
 * is register to receive user's response
 *
 * @param permissions List of permissions to be asked to the user
 * @param callback Callback to be invoked once the flow finishes or a rationale is required to be shown
 */
fun BasePermissionMVVMActivity.initAndStartPermissionFlow(
    permissions: List<String>,
    listener: PermissionFlowCallback
) {
    initPermissionFlow(listener)
    startPermissionsFlow(permissions, listener)
}

/**
 * Start the flow to ask permissions to the user. In order to run this flow, [ComponentActivity.initPermissionFlow]
 * should be called before of this call
 * This method should be use when the permission is asked due a user's action or an app's event
 *
 * @param permissions List of permissions to be asked to the user
 * @param callback Callback to be invoked once the flow finishes or a rationale is required to be shown
 */
fun BasePermissionMVVMActivity.startPermissionsFlow(
    permissions: List<String>,
    callback: PermissionFlowCallback
) {
    val result = hashMapOf<String, PermissionState>()
    if (hasSelfPermissions(this, permissions)) {
        permissions.forEach { result[it] = GRANTED }
        callback.onFinishFlow(result)
    } else {
        if (isRationaleRequired(this, permissions)) callback.onShowRationale(object :
            RequestRationaleResult {
            override fun onAccept() {
                requestPermission(permissions)
            }

            override fun onCancel() {
                permissions.forEach { result[it] = DENIED }
                callback.onFinishFlow(result)
            }
        }, hashMapOf<String, PermissionState>().also {
            permissions.forEach { permission ->
                it[permission] =
                    getPermissionStatus(permission, hasSelfPermission(this, permission))
            }
        }) else requestPermission(permissions)
    }
}

/**
 * Map current permission's status to [PermissionState]
 *
 * @param permission Permission asked to the user
 * @param askResult True is user accepted the requested permission, false if the permission was rejected
 */
internal fun ComponentActivity.getPermissionStatus(
    permission: String,
    askResult: Boolean
): PermissionState {
    return when (askResult) {
        true -> GRANTED
        false -> when (shouldShowRequestPermissionRationale(permission)) {
            true -> DENIED
            false -> NEVER_ASK_AGAIN
        }
    }
}


/**
 * Added method to check the current status of a list of permissions
 *
 * @param permissions List of permissions to be checked
 */
fun ComponentActivity.getPermissionsStatus(permissions: List<String>): Map<String, PermissionState> {
    return hashMapOf<String, PermissionState>().also {
        permissions.forEach { permission ->
            it[permission] = getPermissionStatus(
                permission,
                !permissionExists(permission) || hasSelfPermission(this, permission)
            )
        }
    }
}

internal fun isRationaleRequired(activity: ComponentActivity, permissions: List<String>): Boolean {
    for (permission in permissions) {
        if (activity.shouldShowRequestPermissionRationale(permission)) {
            return true
        }
    }
    return false
}
