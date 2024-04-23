package com.typ.handtalk.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.R
import com.typ.handtalk.core.perms.PermissionHelper
import com.typ.handtalk.databinding.BsRequiredPermissionsBinding

class RequestPermissionsActivity : AppCompatActivity() {
    private val permissionsGranted: Boolean
        get() = PermissionHelper.requiredPermissionsGranted(this)

    private var lastToast: Toast? = null
    private lateinit var binding: BsRequiredPermissionsBinding

    private val reqPermLauncher = PermissionHelper.requestPermissionLauncher(this) {
        val granted = it.all { result -> result.value }
        if (granted) {
            toast(R.string.permissions_granted)
            setResult(RESULT_OK)
            finish()
            return@requestPermissionLauncher
        }
        toast(R.string.permissions_denied)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // * Init UI
        supportActionBar?.hide()
        binding = BsRequiredPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fabDismiss.setOnClickListener {
            toast(R.string.permissions_denied)
            setResult(RESULT_CANCELED)
            finish()
        }
        binding.btnRequestCameraPermission.setOnClickListener {
            // * Request camera permission
            reqPermLauncher.launch(PermissionHelper.REQUIRED_PERMISSIONS)
        }

        if (permissionsGranted) {
            toast(R.string.permissions_granted)
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun toast(resId: Int) {
        lastToast?.cancel()
        lastToast = Toast.makeText(this, resId, Toast.LENGTH_SHORT)
        lastToast?.show()
    }

}