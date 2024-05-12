package com.typ.handtalk.ui.articles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.android.material.transition.platform.MaterialElevationScale
import com.typ.handtalk.R
import com.typ.handtalk.core.articles.data.Categories
import com.typ.handtalk.databinding.ActivityCategoriesBinding
import com.typ.handtalk.utils.Utils

class ArticlesHubActivity : AppCompatActivity() {

    private val categories = Categories.getAll()

    override fun onCreate(savedInstanceState: Bundle?) {
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            val transition = MaterialContainerTransform().apply {
                addTarget(android.R.id.content)
                pathMotion = MaterialArcMotion()
                scrimColor = getColor(R.color.color_bg)
            }
            sharedElementEnterTransition = transition
            sharedElementReenterTransition = transition
            statusBarColor = getColor(R.color.toolbar_color)
            exitTransition = MaterialElevationScale(false)
            reenterTransition = MaterialElevationScale(true)
        }
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        with(ActivityCategoriesBinding.inflate(layoutInflater)) {
            setContentView(root)
            toolbar.setNavigationOnClickListener { finish() }
            rvCategories.apply {
                itemAnimator = DefaultItemAnimator()
                adapter = CategoriesAdapter(this@ArticlesHubActivity)
                layoutManager = GridLayoutManager(this@ArticlesHubActivity, 2)
            }
        }
    }

    private inner class CategoriesAdapter(val context: Context) : RecyclerView.Adapter<CategoryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            return CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false))
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, pos: Int) {
            val category = categories[pos]
            with(holder) {
                tvCatName.setText(category.name)
                ifvCatIcon.setImageResource(category.icon)
                // Not all categories has articles under it
                if (itemView.hasOnClickListeners().not()) {
                    itemView.setOnClickListener {
                        when (category.id) {
                            in 1..5 -> {
                                // Show articles for selected category
                                val opt = ActivityOptionsCompat.makeClipRevealAnimation(
                                    card,
                                    card.width / 2,
                                    card.height / 2,
                                    250,
                                    250
                                )
                                startActivity(
                                    Intent(this@ArticlesHubActivity, ArticlesActivity::class.java).apply {
                                        putExtra(Utils.EXTRA_CATEGORY, category)
                                    }, opt.toBundle()
                                )
                            }

//                            6 -> {
//                                // Start FoundationsActivity
//                                val opt = ActivityOptionsCompat.makeClipRevealAnimation(
//                                    card,
//                                    card.width / 2,
//                                    card.height / 2,
//                                    250,
//                                    250
//                                )
//                                startActivity(Intent(this@ArticlesHubActivity, FoundationsActivity::class.java), opt.toBundle())
//                            }

                            7 -> {
                                // Start TranslatorsActivity
                                val opt = ActivityOptionsCompat.makeClipRevealAnimation(
                                    card,
                                    card.width / 2,
                                    card.height / 2,
                                    250,
                                    250
                                )
                                startActivity(Intent(this@ArticlesHubActivity, TranslatorsActivity::class.java), opt.toBundle())
                            }
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int = categories.size

    }

    private inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val card: MaterialCardView = view.findViewById(R.id.card_category)
        val ifvCatIcon: ImageFilterView = view.findViewById(R.id.ifv_category_icon)
        val tvCatName: MaterialTextView = view.findViewById(R.id.tv_category_name)

    }
}