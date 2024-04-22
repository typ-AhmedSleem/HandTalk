package com.typ.handtalk.ui.permissions

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.typ.handtalk.R
import com.typ.handtalk.core.PermissionHelper
import com.typ.handtalk.databinding.BsRequiredPermissionsBinding

class RequestPermissionsBottomSheet(
    private val activity: AppCompatActivity,
    private val callback: (granted: Boolean) -> Unit
) {
    private val permissionsGranted: Boolean
        get() = PermissionHelper.requiredPermissionsGranted(activity)

    private val bs: BottomSheetDialog = BottomSheetDialog(activity)
    private val binding = BsRequiredPermissionsBinding.bind(
        View.inflate(
            activity,
            R.layout.bs_required_permissions,
            null
        )
    )

    init {
        // * Init bs
        bs.apply {
            setCancelable(false)
            setContentView(binding.root)
            behavior.apply {
                isDraggable = false
                state = BottomSheetBehavior.STATE_EXPANDED
                isFitToContents = false
            }
        }
        // * Init UI
        binding.fabDismiss.setOnClickListener {
            bs.dismiss()
        }
        binding.btnRequestCameraPermission.setOnClickListener {
            // * Request camera permission
            PermissionHelper.requestPermissionLauncher(activity, callback)
        }
    }

    fun requestPermissionsIfNeeded() {
        // * Immediately fire callback with true if
        // * all permissions are granted
        if (permissionsGranted) {
            callback.invoke(true)
            return
        }
        // Show the bs
        bs.show()
    }

    fun dismiss() {
        if (bs.isShowing) {
            bs.dismiss()
        }
    }

}