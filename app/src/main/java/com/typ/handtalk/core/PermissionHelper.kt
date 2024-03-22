package com.typ.handtalk.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

object PermissionHelper {

    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    @JvmStatic
    fun requestPermissionLauncher(activity: AppCompatActivity, callback: (Boolean) -> Unit) {
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission(), callback)
    }

    @JvmStatic
    fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

}