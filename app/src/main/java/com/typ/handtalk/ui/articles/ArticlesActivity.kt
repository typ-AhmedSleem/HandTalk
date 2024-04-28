package com.typ.handtalk.ui.articles

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.android.material.transition.platform.MaterialElevationScale
import com.typ.handtalk.R
import com.typ.handtalk.articles.data.Articles
import com.typ.handtalk.articles.models.Article
import com.typ.handtalk.articles.models.Category
import com.typ.handtalk.databinding.ActivityArticlesBinding
import com.typ.handtalk.utils.Utils

class ArticlesActivity : AppCompatActivity() {

    // Runtime
    private lateinit var category: Category

    override fun onCreate(savedInstanceState: Bundle?) {
        with(window) {
            statusBarColor = getColor(R.color.toolbar_color)
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            findViewById<View>(android.R.id.content).transitionName = getString(R.string.transition_cat_to_topic)
            val transition = MaterialContainerTransform().apply {
                duration = 1500L
                isDrawDebugEnabled = true
                addTarget(android.R.id.content)
                scrimColor = getColor(R.color.color_bg)
            }
//            sharedElementEnterTransition = transition
//            sharedElementReenterTransition = transition
            enterTransition = MaterialElevationScale(true)
            reenterTransition = MaterialElevationScale(false)
        }
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        // * Get category from intent bundle
        category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(Utils.EXTRA_CATEGORY, Category::class.java) as Category
        } else intent.getSerializableExtra(Utils.EXTRA_CATEGORY) as Category
        // * Bind UI
        with(ActivityArticlesBinding.inflate(layoutInflater)) {
            setContentView(root)
            supportActionBar?.hide()

            toolbar.apply {
                title = getString(category.name)
                setNavigationOnClickListener { finish() }
            }
            rvTopics.apply {
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(this@ArticlesActivity)
                adapter = ArticlesAdapter(this@ArticlesActivity) { article, card ->
                    // Create activity transition
                    val opt = ActivityOptionsCompat.makeClipRevealAnimation(
                        card,
                        card.width - card.width / 2,
                        card.height - card.height / 2,
                        250,
                        250
                    )
                    // View the clicked article
                    startActivity(
                        if (article.id == 34) {
                            Intent(this@ArticlesActivity, ArticlesActivity::class.java).apply {
                                putExtra(
                                    Utils.EXTRA_CATEGORY, Category(
                                        id = 34,
                                        name = R.string.sub_cat_34,
                                        icon = R.drawable.ic_category3,
                                        articles = arrayOf(
                                            Articles.Article_341,
//                                        Articles.Article_342,
                                            Articles.Article_343,
//                                        Articles.Article_344,
                                            Articles.Article_345
                                        )
                                    )
                                )
                            }
                        } else {
                            Intent(this@ArticlesActivity, ArticleViewerActivity::class.java).apply {
                                putExtra(Utils.EXTRA_ARTICLE, article)
                            }
                        }, opt.toBundle()
                    )
                }
            }
        }
    }

    private inner class ArticlesAdapter(
        val context: Context,
        val callback: (Article, MaterialCardView) -> Unit
    ) : RecyclerView.Adapter<ArticleViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
            return ArticleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_article, parent, false))
        }

        override fun onBindViewHolder(holder: ArticleViewHolder, pos: Int) {
            val article = category.articles[pos]
            with(holder) {
                tvTopicTitle.text = article.title
                chipHasVideo.visibility = if (TextUtils.isEmpty(article.videoPath)) View.INVISIBLE else View.VISIBLE
                try {
                    val id = article.id.toString()
                    var thumbnail = ""
                    for (dir in id) thumbnail += "${dir}/"
                    thumbnail += "thumb.jpg"
                    ivTopicThumb.setImageDrawable(Drawable.createFromStream(assets.open(thumbnail), null))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (itemView.hasOnClickListeners().not()) {
                    itemView.setOnClickListener {
                        callback.invoke(article, card)
                    }
                }
            }
        }

        override fun getItemCount(): Int = category.articles.size

    }

    private inner class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val chipHasVideo: Chip = view.findViewById(R.id.chip_article_has_video)
        val card: MaterialCardView = view.findViewById(R.id.card_topic)
        val tvTopicTitle: MaterialTextView = view.findViewById(R.id.tv_topic_title)
        val ivTopicThumb: AppCompatImageView = view.findViewById(R.id.iv_topic_thumb)
    }

}