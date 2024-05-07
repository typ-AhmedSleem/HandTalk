package com.typ.handtalk.core.a2s

import com.typ.handtalk.core.a2s.playables.A2SignPlayable
import com.typ.handtalk.core.a2s.playables.A2SignPlayableImage
import com.typ.handtalk.core.a2s.playables.A2SignPlayableVideo

object A2SPlayableRepository {

    private val IMAGES_REPO = emptyMap<String, A2SignPlayableImage>()

    private val VIDEOS_REPO = mapOf(
        "اتفضل" to A2SignPlayableVideo("اتفضل.mp4"),
        "تفضل" to A2SignPlayableVideo("اتفضل.mp4"),
        "اسبوع" to A2SignPlayableVideo("اسبوع.mp4"),
        "أسبوع" to A2SignPlayableVideo("اسبوع.mp4"),
        "التجاره" to A2SignPlayableVideo("التجارة.mp4"),
        "التجارة" to A2SignPlayableVideo("التجارة.mp4"),
        "بجانب" to A2SignPlayableVideo("بجانب.mp4"),
        "طوارئ" to A2SignPlayableVideo("طوارئ.mp4"),
        "علاج" to A2SignPlayableVideo("علاج.mp4"),
        "في" to A2SignPlayableVideo("في.mp4"),
        "فى" to A2SignPlayableVideo("في.mp4"),
        "كليه" to A2SignPlayableVideo("كليه.mp4"),
        "كلية" to A2SignPlayableVideo("كليه.mp4"),
        "لمده" to A2SignPlayableVideo("لمدة.mp4"),
        "لمدة" to A2SignPlayableVideo("لمدة.mp4"),
        "هكتبلك" to A2SignPlayableVideo("هكتبلك.mp4"),
        "هكتب لك" to A2SignPlayableVideo("هكتبلك.mp4"),
    )

    private val SENTENCES_REPO = mapOf(
        "حاسس بأيه" to A2SignPlayableVideo("حاسس بأيه.mp4"),
        "حاسس بايه" to A2SignPlayableVideo("حاسس بأيه.mp4"),
        "صباح الخير" to A2SignPlayableVideo("صباح الخير.mp4"),
        "عليكم السلام" to A2SignPlayableVideo("عليكم السلام.mp4"),
        "هكتب لك علاج لمده اسبوع" to A2SignPlayableVideo("هكتبلك علاج لمدة اسبوع.mp4"),
        "هكتبلك علاج لمده اسبوع" to A2SignPlayableVideo("هكتبلك علاج لمدة اسبوع.mp4"),
        "كليه التجاره" to A2SignPlayableVideo("كليةالتجارة.mp4"),
        "كلية التجاره" to A2SignPlayableVideo("كليةالتجارة.mp4"),
    )

    @JvmStatic
    fun getPlayableForWord(word: String): A2SignPlayable? {
        return when (val finalWord = word.lowercase().trim()) {
            in IMAGES_REPO -> IMAGES_REPO[finalWord]

            in SENTENCES_REPO -> SENTENCES_REPO[finalWord]

            in VIDEOS_REPO -> VIDEOS_REPO[finalWord]

            else -> null
        }
    }

    fun getPlayableForSentence(sentence: String) = SENTENCES_REPO[sentence]
}
