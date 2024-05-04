package com.typ.handtalk.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import com.typ.handtalk.R
import com.typ.handtalk.core.a2s.A2SPlayableRepository
import com.typ.handtalk.core.a2s.playables.A2SignPlayableVideo
import com.typ.handtalk.core.perms.PermissionHelper
import com.typ.handtalk.core.perms.RequestRequiredPermissionsContract
import com.typ.handtalk.databinding.ActivityWelcomeBinding
import com.typ.handtalk.ui.a2s.Arabic2SignTranslationHistoryActivity
import com.typ.handtalk.ui.articles.ArticlesActivity
import com.typ.handtalk.ui.s2a.LiveSignTranslatorActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private var isShowingNormalLayout = false

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isShowingNormalLayout) {
                isShowingNormalLayout = false
                showNormalOrDeafLayout()
            } else finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        // * Initialize the contract
        val reqPermsLauncher = registerForActivityResult(RequestRequiredPermissionsContract()) {
            if (it) startActivity(Intent(this, LiveSignTranslatorActivity::class.java))
        }
        // * Init UI
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)
            showNormalOrDeafLayout()
            // * Init click listeners
            btnSign2Arabic.setOnClickListener {
                if (PermissionHelper.requiredPermissionsGranted(this@WelcomeActivity)) {
                    startActivity(Intent(this@WelcomeActivity, LiveSignTranslatorActivity::class.java))
                    return@setOnClickListener
                }
                reqPermsLauncher.launch(Unit)
            }
            btnArabic2Sign.setOnClickListener {
                if (isShowingNormalLayout) {
                    startActivity(Intent(this@WelcomeActivity, Arabic2SignTranslationHistoryActivity::class.java))
                } else {
                    binding.btnArticles.visibility = View.VISIBLE
                    binding.btnArabic2Sign.setText(R.string.a2s_translator)
                    binding.btnSign2Arabic.apply {
                        setText(R.string.sign_to_arabic_translator)
                        setTextColor(ResourcesCompat.getColor(resources, R.color.colorOnPrimary, null))
                        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
                    }
                    isShowingNormalLayout = true
                }
            }
            btnArticles.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, ArticlesActivity::class.java))
            }

            GlobalScope.launch(Dispatchers.IO) {
                (A2SPlayableRepository.getPlayableForWord("احمد") as A2SignPlayableVideo).let {
                    val copied = it.copyToCache(this@WelcomeActivity)
                    if (copied) {
                        withContext(Dispatchers.Main){
                            videoTutorial.setVideoPath(it.getVideoPath(cacheDir))
                            videoTutorial.start()
                        }
                    }
                }
            }

        }

        // Register on-back-pressed callback
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun showNormalOrDeafLayout() {
        binding.btnArticles.visibility = View.INVISIBLE
        binding.btnSign2Arabic.apply {
            setText(R.string.deaf_user)
            setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorParent, null))
        }
        binding.btnArabic2Sign.setText(R.string.normal_user)
        isShowingNormalLayout = false
    }

    override fun onResume() {
        super.onResume()
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onPause() {
        super.onPause()
        onBackPressedCallback.remove()
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }

}