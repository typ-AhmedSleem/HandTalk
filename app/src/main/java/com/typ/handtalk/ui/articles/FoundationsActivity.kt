package com.typ.handtalk.ui.articles

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView
import com.typ.handtalk.R
import com.typ.handtalk.articles.data.Foundations
import com.typ.handtalk.articles.models.Foundation
import com.typ.handtalk.databinding.ActivityFoundationsBinding
import com.typ.handtalk.databinding.BsFoundationDetailsBinding

class FoundationsActivity : AppCompatActivity() {

    private val foundations = Foundations.getAll()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = getColor(R.color.toolbar_color)
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        // Init UI
        supportActionBar?.hide()
        with(ActivityFoundationsBinding.inflate(layoutInflater)) {
            setContentView(root)
            toolbar.apply { setNavigationOnClickListener { finish() } }
            rvFoundations.apply {
                adapter = FoundationsAdapter()
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(this@FoundationsActivity)
                addItemDecoration(DividerItemDecoration(this@FoundationsActivity, VERTICAL))
            }
        }
    }

    private fun showFoundationDetails(fnd: Foundation) {
        BottomSheetDialog(this).apply {
            with(BsFoundationDetailsBinding.bind(View.inflate(context, R.layout.bs_foundation_details, null))) {
                setContentView(root)
                tvFndName.text = fnd.name
                tvFndBio.text = fnd.bio
                btnShowFndOnMap.setOnClickListener {
                    val mapUri = Uri.parse("geo:${fnd.loc.latitude},${fnd.loc.longitude}")
                    val mapsIntent = Intent(Intent.ACTION_VIEW, mapUri)
                    // ! Double check [mapsIntent.resolveActivity] again ensuring if it requires <queries> in manifest or not.
                    mapsIntent.resolveActivity(packageManager)?.let {
                        startActivity(mapsIntent)
                    } ?: Toast.makeText(context, "تطبيق الخرائط غير مثبت", Toast.LENGTH_SHORT).show()
                }
                show()
            }
        }
    }

    private inner class FoundationsAdapter : RecyclerView.Adapter<FoundationsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(this@FoundationsActivity).inflate(R.layout.item_foundation, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(foundations[position]) {
                holder.tvName.text = name
                holder.tvAddress.text = bio
//                holder.ifvLogo.setImageDrawable(Drawable.createFromStream(this@FoundationsActivity.assets.open("foundations/$logo"), null))
                if (!holder.itemView.hasOnClickListeners()) {
                    holder.itemView.setOnClickListener { showFoundationDetails(this) }
                }
            }
        }

        override fun getItemCount(): Int = foundations.size

        private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            //            val ifvLogo: ImageFilterView
            val tvName: MaterialTextView
            val tvAddress: MaterialTextView

            init {
//                ifvLogo = itemView.findViewById(R.id.ifv_fnd_logo)
                tvName = itemView.findViewById(R.id.tv_fnd_name)
                tvAddress = itemView.findViewById(R.id.tv_fnd_bio)
            }

        }

    }

}