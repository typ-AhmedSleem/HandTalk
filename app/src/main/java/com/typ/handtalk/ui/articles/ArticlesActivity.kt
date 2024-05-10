package com.typ.handtalk.ui.articles

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.textview.MaterialTextView
import com.typ.handtalk.R
import com.typ.handtalk.core.articles.data.Categories
import com.typ.handtalk.core.articles.models.Article
import com.typ.handtalk.databinding.ActivityArticlesBinding
import com.typ.handtalk.utils.Utils

class ArticlesActivity : AppCompatActivity() {

    // Runtime
    private val category = Categories.getCombinedCategory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // * Bind UI
        with(ActivityArticlesBinding.inflate(layoutInflater)) {
            setContentView(root)
            supportActionBar?.hide()

            toolbar.apply {
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
                        Intent(this@ArticlesActivity, ArticleViewerActivity::class.java).apply {
                            putExtra(Utils.EXTRA_ARTICLE, article)
                        },
                        opt.toBundle()
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