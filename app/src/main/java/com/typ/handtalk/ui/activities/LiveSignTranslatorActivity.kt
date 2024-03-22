package com.typ.handtalk.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.MainViewModel
import com.typ.handtalk.core.PermissionHelper
import com.typ.handtalk.databinding.ActivitySignToTextTranslatorBinding

class LiveSignTranslatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignToTextTranslatorBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup binding
        binding = ActivitySignToTextTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // * Check required permissions
        assert(PermissionHelper.hasPermissions(this)) {
            "Required permissions aren't all satisfied."
        }

        // Setup GestureRecognizer instance

        // Setup Camera instance
    }

}