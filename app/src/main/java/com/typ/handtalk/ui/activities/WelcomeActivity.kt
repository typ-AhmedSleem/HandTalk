package com.typ.handtalk.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.databinding.ActivityWelcomeBinding
import com.typ.handtalk.ui.a2s.Arabic2SignTranslatorActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(ActivityWelcomeBinding.inflate(layoutInflater)) {
            setContentView(root)
            btnStartApp.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, LiveSignTranslatorActivity::class.java))
//                finish()
            }
            btnStartApp.setOnLongClickListener {
                startActivity(Intent(this@WelcomeActivity, Arabic2SignTranslatorActivity::class.java))
                true
            }
        }
    }

}