package com.typ.handtalk.ui.articles

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.typ.handtalk.R
import com.typ.handtalk.articles.models.Article
import com.typ.handtalk.articles.models.Section
import com.typ.handtalk.ui.articles.views.ImageSectionView
import com.typ.handtalk.ui.articles.views.TextSectionView
import com.typ.handtalk.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class ArticleViewerActivity : AppCompatActivity() {

    private val mTag = "ArticleViewer"

    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        findViewById<View>(android.R.id.content).transitionName = Utils.TRANS_CARD2ACT
        // Setup activity transition
        val transition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            scrimColor = getColor(R.color.color_bg)
        }
        supportActionBar?.hide()
        window.sharedElementEnterTransition = transition
        window.sharedElementReenterTransition = transition
        window.statusBarColor = getColor(R.color.toolbar_color)
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_viewer)
        // Get topic from intent
        val article = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(Utils.EXTRA_ARTICLE, Article::class.java) as Article
        } else intent.getSerializableExtra(Utils.EXTRA_ARTICLE) as Article
        // Init UI
        findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = article.title
            setNavigationOnClickListener {
                finishAfterTransition()
            }
        }
        // Loading progress
        val progress = findViewById<CircularProgressIndicator>(R.id.progress).apply { hide() }
        // Hide video card if article has no video
        if (TextUtils.isEmpty(article.videoPath)) {
            findViewById<MaterialCardView>(R.id.card_video_player).visibility = View.GONE
        } else {
            // Init Video Player
            findViewById<VideoView>(R.id.video_topic).apply {
                // Init video player
                setMediaController(MediaController(this@ArticleViewerActivity))
                if (article.videoPath != null) {
                    val vidFile = File(cacheDir, "video_${article.id}.mp4")
                    if (vidFile.exists().not()) {
                        GlobalScope.launch(Dispatchers.IO) {
                            // Show loading
                            withContext(Dispatchers.Main) {
                                progress.show()
                                Log.i(mTag, "Before: ${vidFile.path} | ${vidFile.exists()}")
                            }
                            // Cache video
                            var ins: InputStream? = null
                            var ots: FileOutputStream? = null
                            try {
                                // Create in and out IO streams
                                ins = assets.open(article.videoPath)
                                ots = FileOutputStream(vidFile)
                                Log.i(mTag, "Available: ${ins.available()} bytes.")
                                // Copy video file from in to out
                                var read: Int
                                val buffer = ByteArray(1024)
                                while ((ins.read(buffer).also { read = it }) != -1) {
                                    ots.write(buffer, 0, read)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {
                                try {
                                    ins?.close()
                                } catch (_: IOException) {
                                }
                                try {
                                    ots?.flush()
                                    ots?.close()
                                } catch (_: IOException) {
                                }
                            }
                            delay(2000)
                            // Hide loading
                            withContext(Dispatchers.Main) {
                                Log.i(mTag, "After: ${vidFile.path} | ${vidFile.exists()}")
                                progress.hide()
                                // Play video
                                setVideoURI(Uri.fromFile(vidFile))
                                start()
                            }
                        }
                    } else {
                        // Play video
                        setVideoURI(Uri.fromFile(vidFile))
                        start()
                    }
                }
            }
        }
        // Sections
        val llSections = findViewById<LinearLayout>(R.id.ll_sections)
        for (section in article.sections) {
            when (section) {
                is Section.TextSection -> {
                    llSections.addView(
                        TextSectionView(this, section),
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                is Section.ImageSection -> {
                    llSections.addView(
                        ImageSectionView(this, section.src),
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        200f.dp2px(this)
                    )
                }
            }
        }
    }

    private fun Float.dp2px(context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

}