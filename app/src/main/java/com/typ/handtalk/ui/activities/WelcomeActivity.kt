package com.typ.handtalk.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.typ.handtalk.core.perms.PermissionHelper
import com.typ.handtalk.core.perms.RequestRequiredPermissionsContract
import com.typ.handtalk.databinding.ActivityWelcomeBinding
import com.typ.handtalk.ui.a2s.Arabic2SignTranslationHistoryActivity
import com.typ.handtalk.ui.articles.ArticlesActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        // * Initialize the contract
        val reqPermsLauncher = registerForActivityResult(RequestRequiredPermissionsContract()) {
            if (it) startActivity(Intent(this, LiveSignTranslatorActivity::class.java))
        }
        with(ActivityWelcomeBinding.inflate(layoutInflater)) {
            setContentView(root)
            // * Init click listeners
            btnStartApp.setOnClickListener {
                if (PermissionHelper.requiredPermissionsGranted(this@WelcomeActivity)) {
                    startActivity(Intent(this@WelcomeActivity, LiveSignTranslatorActivity::class.java))
                    return@setOnClickListener
                }
                reqPermsLauncher.launch(Unit)
            }
            btnArabic2Sign.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, Arabic2SignTranslationHistoryActivity::class.java))
            }
            btnArticles.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, ArticlesActivity::class.java))
            }
        }
    }

}