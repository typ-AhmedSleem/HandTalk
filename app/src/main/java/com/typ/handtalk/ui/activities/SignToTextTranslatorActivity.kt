package com.typ.handtalk.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.databinding.ActivitySignToTextTranslatorBinding

class SignToTextTranslatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignToTextTranslatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup binding
        binding = ActivitySignToTextTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Setup GestureRecognizer
        
        // Setup camera
    }

}