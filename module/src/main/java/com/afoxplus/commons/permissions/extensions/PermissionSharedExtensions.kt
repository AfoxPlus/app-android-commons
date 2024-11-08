package com.afoxplus.commons.permissions.extensions

import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.PermissionChecker

sealed interface PermissionState
object GRANTED : PermissionState
object DENIED : PermissionState
object NEVER_ASK_AGAIN : PermissionState

typealias PermissionResult = MutableMap<String, PermissionState>

internal var MIN_SDK_PERMISSIONS: Map<String, Int> = mapOf(
    Pair("android.permission.READ_CALL_LOG", 16),
    Pair("android.permission.READ_EXTERNAL_STORAGE", 16),
    Pair("android.permission.SYSTEM_ALERT_WINDOW", 23),
    Pair("android.permission.WRITE_SETTINGS", 23),
    Pair("android.permission.ACCESS_BACKGROUND_LOCATION", 29),
    Pair("android.permission.POST_NOTIFICATIONS", 33)
)

/**
 * Callback class to be called when the user respond to permission asking
 * or when the app should show a rationale
 *
 */
interface PermissionFlowCallback {

    /**
     * Send to caller the result of the permission flow
     *
     * @param result current status of the permissions.
     */
    fun onFinishFlow(result: PermissionResult)

    /**
     * Indicate to the caller that a rationale message should be shown to let the user know
     * about the goal of those permissions
     *
     * @param callback callback instance to indicate to this flow the user's response to the rationale
     * @param result current status of the permissions.
     */
    fun onShowRationale(callback: RequestRationaleResult, result: PermissionResult)
}

interface RequestRationaleResult {
    fun onAccept()
    fun onCancel()
}

lateinit var requestPermissionLauncher : ActivityResultLauncher<Array<String>>


internal fun requestPermission(permissions: List<String>) {
    if (::requestPermissionLauncher.isInitialized) {
        requestPermissionLauncher.launch(permissions.toTypedArray())
    }
}

internal fun hasSelfPermissions(context: ComponentActivity, permissions: List<String>): Boolean {
    for (permission in permissions) {
        if (permissionExists(permission) && !hasSelfPermission(context, permission)) {
            return false
        }
    }
    return true
}

internal fun hasSelfPermission(context: Context, permission: String): Boolean {
    return try {
        PermissionChecker.checkSelfPermission(
            context,
            permission
        ) == PermissionChecker.PERMISSION_GRANTED
    } catch (t: RuntimeException) {
        false
    }
}

internal fun permissionExists(permission: String): Boolean {
    // Check if the permission could potentially be missing on this device
    val minVersion: Int? = MIN_SDK_PERMISSIONS[permission]
    // If null was returned from the above call, there is no need for a device API level check for the permission;
    // otherwise, we check if its minimum API level requirement is met
    return minVersion == null || Build.VERSION.SDK_INT >= minVersion
}