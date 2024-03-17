package com.typ.handtalk.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.MainActivity
import com.typ.handtalk.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(ActivityWelcomeBinding.inflate(layoutInflater)) {
            setContentView(root)
            btnStartApp.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                finish()
            }
        }
    }

}