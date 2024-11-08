package com.afoxplus.commons.permissions.delivery

import androidx.activity.result.ActivityResultLauncher
import com.afoxplus.uikit.activities.UIKitBaseActivity

abstract class BasePermissionMVVMActivity : UIKitBaseActivity() {
    var requestPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

    fun requestPermission(permissions: List<String>) {
        requestPermissionLauncher?.launch(permissions.toTypedArray())
    }
}