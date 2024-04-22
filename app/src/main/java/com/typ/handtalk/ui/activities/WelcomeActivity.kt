package com.typ.handtalk.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.R
import com.typ.handtalk.databinding.ActivityWelcomeBinding
import com.typ.handtalk.ui.a2s.Arabic2SignTranslatorActivity
import com.typ.handtalk.ui.articles.ArticlesHubActivity
import com.typ.handtalk.ui.permissions.RequestPermissionsBottomSheet

class WelcomeActivity : AppCompatActivity() {

    private var lastToast: Toast? = null
    private lateinit var permissionsBS: RequestPermissionsBottomSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        // * Initialize the PermissionsBottomSheet instance
        permissionsBS = RequestPermissionsBottomSheet(this) { granted ->
            if (granted) {
                permissionsBS.dismiss()
                toast(R.string.permissions_granted)
                startActivity(Intent(this@WelcomeActivity, LiveSignTranslatorActivity::class.java))
                return@RequestPermissionsBottomSheet
            }
            toast(R.string.permissions_denied)
        }
        with(ActivityWelcomeBinding.inflate(layoutInflater)) {
            setContentView(root)
            // * Init click listeners
            btnStartApp.setOnClickListener {
                // We should ensure that permissions are granted
                permissionsBS.requestPermissionsIfNeeded()
            }
            btnArabic2Sign.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, Arabic2SignTranslatorActivity::class.java))
            }
            btnArticles.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, ArticlesHubActivity::class.java))
            }
        }
    }

    private fun toast(resId: Int) {
        lastToast?.cancel()
        lastToast = Toast.makeText(this, resId, Toast.LENGTH_SHORT)
        lastToast?.show()
    }

}