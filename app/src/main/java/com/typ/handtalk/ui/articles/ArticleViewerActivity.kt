package com.typ.handtalk.ui.articles

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.typ.handtalk.core.articles.models.Article
import com.typ.handtalk.core.articles.models.Section
import com.typ.handtalk.databinding.ActivityArticleViewerBinding
import com.typ.handtalk.ui.articles.views.ImageSectionView
import com.typ.handtalk.ui.articles.views.TextSectionView
import com.typ.handtalk.ui.articles.views.TitleSectionView
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

    private val context: Context
        get() = this

    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get topic from intent
        val article = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(Utils.EXTRA_ARTICLE, Article::class.java) as Article
        } else intent.getSerializableExtra(Utils.EXTRA_ARTICLE) as Article

        Log.i(TAG, "Viewing: $article")
        // * Bind UI
        with(ActivityArticleViewerBinding.inflate(layoutInflater)) {
            setContentView(root)
            toolbar.apply {
                title = "" // article.title
                setNavigationOnClickListener {
                    finishAfterTransition()
                }
            }
            // Loading progress
            progress.apply { hide() }
            // Hide video card if article has no video
            if (TextUtils.isEmpty(article.videoPath)) {
                cardVideoPlayer.visibility = View.GONE
            } else {
                // * Video Player
                videoTopic.apply {
                    // Init video player
                    setMediaController(MediaController(this@ArticleViewerActivity))
                    if (article.videoPath != null) {
                        val vidFile = File(cacheDir, "video_${article.id}.mp4")
                        if (vidFile.exists().not()) {
                            GlobalScope.launch(Dispatchers.IO) {
                                // Show loading
                                withContext(Dispatchers.Main) {
                                    progress.show()
                                    Log.i(TAG, "Before: ${vidFile.path} | ${vidFile.exists()}")
                                }
                                // Cache video
                                var ins: InputStream? = null
                                var ots: FileOutputStream? = null
                                try {
                                    // Create in and out IO streams
                                    ins = assets.open(article.videoPath)
                                    ots = FileOutputStream(vidFile)
                                    Log.i(TAG, "Available: ${ins.available()} bytes.")
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
                                    Log.i(TAG, "After: ${vidFile.path} | ${vidFile.exists()}")
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
            // * Sections
            llSections.addView(
                TitleSectionView(context, article.title),
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            for (section in article.sections) {
                when (section) {
                    is Section.TextSection -> {
                        llSections.addView(
                            TextSectionView(context, section),
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    is Section.ImageSection -> {
                        llSections.addView(
                            ImageSectionView(context, section.src),
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            200f.dp2px(context)
                        )
                    }
                }
            }
        }
    }

    private fun Float.dp2px(context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    companion object {
        private const val TAG = "WA-ArticleViewer"
    }

}