/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.typ.handtalk.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.tasks.components.containers.Category
import com.typ.handtalk.core.models.Hand
import com.typ.handtalk.core.models.HandSign
import com.typ.handtalk.core.tts.TextSpeaker
import com.typ.handtalk.databinding.ItemGestureRecognizerResultBinding
import java.util.Locale
import kotlin.math.min

class GestureRecognizerResultsAdapter(context: Context) : RecyclerView.Adapter<GestureRecognizerResultsAdapter.ViewHolder>() {
    companion object {
        private const val NO_VALUE = "--"
    }

    private var adapterSize: Int = 0
    private var adapterCategories: MutableList<Category?> = mutableListOf()

    private var lastSign: String? = null
    private var hands: Array<Hand> = emptyArray()
    private val textSpeaker = TextSpeaker(context)


    @SuppressLint("NotifyDataSetChanged")
    fun updateResults(categories: List<Category>?) {
        adapterCategories = MutableList(adapterSize) { null }
        if (categories != null) {
            val sortedCategories = categories.sortedByDescending { it.score() }
            val min = min(sortedCategories.size, adapterCategories.size)
            for (i in 0 until min) {
                adapterCategories[i] = sortedCategories[i]
            }
            adapterCategories.sortedBy { it?.index() }
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setResults(hands: Array<Hand>) {
        this.hands = hands
        notifyDataSetChanged()
    }

    fun updateAdapterSize(size: Int) {
        adapterSize = size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGestureRecognizerResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        adapterCategories[position].let { category ->
//            holder.bind(category?.categoryName(), category?.score())
//        }
        holder.bind(hands[position])
    }

    override fun getItemCount(): Int = hands.size

    inner class ViewHolder(private val binding: ItemGestureRecognizerResultBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hand: Hand) {
            with(binding) {
                tvLeftHandSignLabel.text = hand.sign?.label ?: NO_VALUE
                tvLeftHandSignScore.text = String.format(
                    Locale.US,
                    "%.2f",
                    hand.sign?.score ?: NO_VALUE
                )
            }
            // Speak the sign
            if (hands.size >= 2) return
            hand.sign?.label?.let {
                if (lastSign != hand.sign?.label && hand.sign?.label != "None") {
                    textSpeaker.speak(it)
                    lastSign = hand.sign?.label
                }
            }
        }

        fun bind(label: String?, score: Float?) {
//            with(binding) {
//                tvLabel.text = label ?: NO_VALUE
//                tvScore.text = if (score != null) String.format(
//                    Locale.US,
//                    "%.2f",
//                    score
//                ) else NO_VALUE
//            }
        }
    }
}
