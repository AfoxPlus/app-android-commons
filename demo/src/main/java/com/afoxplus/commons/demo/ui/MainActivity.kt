package com.afoxplus.commons.demo.ui

import android.Manifest
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.afoxplus.commons.permissions.delivery.BasePermissionMVVMActivity
import com.afoxplus.commons.permissions.extensions.PermissionFlowCallback
import com.afoxplus.commons.permissions.extensions.PermissionResult
import com.afoxplus.commons.permissions.extensions.RequestRationaleResult
import com.afoxplus.commons.permissions.extensions.getPermissionsStatus
import com.afoxplus.commons.permissions.extensions.initPermissionFlow
import com.afoxplus.commons.permissions.extensions.startPermissionsFlow
import com.afoxplus.uikit.designsystem.atoms.UIKitButtonOutlineLarge
import com.afoxplus.uikit.designsystem.atoms.UIKitButtonPrimaryLarge
import com.afoxplus.uikit.designsystem.atoms.UIKitText
import com.afoxplus.uikit.designsystem.foundations.UIKitTheme
import com.afoxplus.uikit.designsystem.foundations.UIKitTypographyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BasePermissionMVVMActivity() {

    private val permissionFlowCallback: PermissionFlowCallback = object : PermissionFlowCallback {
        override fun onFinishFlow(result: PermissionResult) {
            Log.d("Permission", "Finish Result: $result")
            //result permission
        }

        override fun onShowRationale(callback: RequestRationaleResult, result: PermissionResult) {
            Log.d("Permission", "Show Rationale: $result")
            // show rationale
        }
    }


    override fun setMainView() {
        initPermissionFlow(permissionFlowCallback)
        setContent {
            UIKitTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(UIKitTheme.spacing.spacing06)
                ) {
                    UIKitText(text = "Permissions", style = UIKitTypographyTheme.title01)
                    UIKitButtonOutlineLarge(
                        modifier = Modifier.padding(horizontal = UIKitTheme.spacing.spacing12),
                        text = "Get Location Permission"
                    ) {
                        handleGetLocationPermission()
                    }
                    UIKitButtonOutlineLarge(
                        modifier = Modifier.padding(horizontal = UIKitTheme.spacing.spacing12),
                        text = "Get Notification Permission"
                    ) {
                        handleGetNotificationPermission()
                    }

                    UIKitButtonPrimaryLarge(
                        modifier = Modifier.padding(horizontal = UIKitTheme.spacing.spacing12),
                        text = "Permission Status"
                    ) {
                        val statusPermission = getPermissionsStatus(
                            listOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        )
                        statusPermission.forEach { (permission, status) ->
                            Log.d("Permission", "$permission : $status")
                        }
                    }

                }

            }
        }
    }

    private fun handleGetLocationPermission() {
        startPermissionsFlow(
            listOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionFlowCallback
        )
    }

    private fun handleGetNotificationPermission() {
        startPermissionsFlow(
            listOf(Manifest.permission.POST_NOTIFICATIONS), permissionFlowCallback
        )
    }

    override fun setUpView() {

    }

}