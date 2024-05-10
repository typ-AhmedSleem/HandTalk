package com.typ.handtalk.ui.articles

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView
import com.typ.handtalk.R
import com.typ.handtalk.core.articles.data.Translators
import com.typ.handtalk.core.articles.models.TranslatorProfile
import com.typ.handtalk.databinding.ActivityTranslatorsBinding
import com.typ.handtalk.databinding.BsTranslatorProfileDetailsBinding
import java.util.Locale

class TranslatorsActivity : AppCompatActivity() {

    private val translators = Translators.getAll()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = getColor(R.color.toolbar_color)
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        super.onCreate(savedInstanceState)
        with(ActivityTranslatorsBinding.inflate(layoutInflater)) {
            // * Bind UI
            setContentView(root)
            supportActionBar?.hide()
            toolbar.setNavigationOnClickListener { finishAfterTransition() }
            rvTranslators.apply {
                adapter = TranslatorsAdapter()
                itemAnimator = DefaultItemAnimator()
                layoutManager = GridLayoutManager(this@TranslatorsActivity, 2)
            }
        }
    }

    private fun showTranslatorProfile(translator: TranslatorProfile) {
        BottomSheetDialog(this).apply {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            with(BsTranslatorProfileDetailsBinding.bind(View.inflate(context, R.layout.bs_translator_profile_details, null))) {
                setContentView(root)
                tvTranslatorName.text = translator.name
                tvTranslatorBio.text = translator.bio
                tvTranslatorLoc.text = translator.homeCity
                btnCopyTranslatorNum.apply {
                    text = String.format(Locale("ar"), "%s '%s'", "نسخ", translator.phoneNumber)
                    setOnClickListener {
                        clipboard.setPrimaryClip(ClipData.newPlainText(translator.name, translator.phoneNumber))
                        Toast.makeText(context, "تم نسخ رقم الهاتف", Toast.LENGTH_SHORT).show()
                    }
                }
                ifvTranslatorAvatar.setImageDrawable(Drawable.createFromStream(this@TranslatorsActivity.assets.open(translator.photoPath), null))
                show()
            }
        }
    }

    private inner class TranslatorsAdapter : RecyclerView.Adapter<TranslatorsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(this@TranslatorsActivity).inflate(R.layout.item_translator, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(translators[position]) {
                holder.tvName.text = name
                holder.ifvAvatar.setImageDrawable(Drawable.createFromStream(this@TranslatorsActivity.assets.open(photoPath), null))
                if (!holder.itemView.hasOnClickListeners()) {
                    holder.itemView.setOnClickListener { showTranslatorProfile(this) }
                }
            }
        }

        override fun getItemCount(): Int = translators.size

        private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val ifvAvatar: ImageFilterView
            val tvName: MaterialTextView

            init {
                ifvAvatar = itemView.findViewById(R.id.ifv_translator_avatar)
                tvName = itemView.findViewById(R.id.tv_translator_name)
            }

        }

    }

}
