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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.typ.handtalk.R
import com.typ.handtalk.articles.data.Foundations
import com.typ.handtalk.articles.models.Foundation

class FoundationsActivity : AppCompatActivity() {

    private val foundations = Foundations.getAll()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = getColor(R.color.toolbar_color)
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        // Init UI
        supportActionBar?.hide()
        setContentView(R.layout.activity_foundations)
        findViewById<MaterialToolbar>(R.id.toolbar).apply { setNavigationOnClickListener { finish() } }
        findViewById<RecyclerView>(R.id.rv_foundations).apply {
            adapter = FoundationsAdapter()
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this@FoundationsActivity)
            addItemDecoration(DividerItemDecoration(this@FoundationsActivity, VERTICAL))
        }
    }

    private fun showFoundationDetails(fnd: Foundation) {
        BottomSheetDialog(this).apply {
            setContentView(R.layout.bs_foundation_details)
            findViewById<MaterialTextView>(R.id.tv_fnd_name)?.text = fnd.name
            findViewById<MaterialTextView>(R.id.tv_fnd_bio)?.text = fnd.bio
            findViewById<MaterialButton>(R.id.btn_show_fnd_on_map)?.setOnClickListener {
                val mapUri = Uri.parse("geo:${fnd.loc.latitude},${fnd.loc.longitude}")
                val mapsIntent = Intent(Intent.ACTION_VIEW, mapUri)
                mapsIntent.resolveActivity(packageManager)?.let {
                    startActivity(mapsIntent)
                } ?: Toast.makeText(context, "تطبيق الخرائط غير مثبت", Toast.LENGTH_SHORT).show()
            }
            show()
//            findViewById<MaterialButton>(R.id.btn_copy_fnd_num)?.apply {
//                text = String.format(Locale("ar"), "%s '%s'", "نسخ", fnd.phoneNumber)
//                setOnClickListener {
//                    clipboard.setPrimaryClip(ClipData.newPlainText(fnd.name, fnd.phoneNumber))
//                    Toast.makeText(context, "تم نسخ رقم الهاتف", Toast.LENGTH_SHORT).show()
//                }
//            }
//            findViewById<ImageFilterView>(R.id.ifv_fnd_logo)?.setImageDrawable(Drawable.createFromStream(this@FoundationsActivity.assets.open("foundations/${fnd.logo}"), null))
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