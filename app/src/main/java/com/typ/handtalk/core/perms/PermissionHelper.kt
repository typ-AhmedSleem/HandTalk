package com.typ.handtalk.core.perms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

object PermissionHelper {

    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    @JvmStatic
    fun requestPermissionLauncher(activity: AppCompatActivity, callback: (Map<String, Boolean>) -> Unit): ActivityResultLauncher<Array<String>> {
        return activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), callback)
    }

    @JvmStatic
    fun requiredPermissionsGranted(context: Context) = arePermissionsGranted(context, REQUIRED_PERMISSIONS)

    @JvmStatic
    fun arePermissionsGranted(context: Context, permissions: Array<String>) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

}