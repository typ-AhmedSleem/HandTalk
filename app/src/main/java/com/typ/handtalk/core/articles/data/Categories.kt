package com.typ.handtalk.core.articles.data

import com.typ.handtalk.R
import com.typ.handtalk.core.articles.models.Category

object Categories {

    @JvmStatic
    fun getAll(): Array<Category> {
        return arrayOf(
            Category(
                1,
                R.string.cat1,
                R.drawable.ic_category1,
                articles = arrayOf(
                    Articles.Article_11,
                    Articles.Article_12,
                )
            ),
            Category(
                2,
                R.string.cat2,
                R.drawable.ic_category2,
                articles = arrayOf(
                    Articles.Article_21,
                    Articles.Article_22,
                    Articles.Article_23
                )
            ),
            Category(
                3,
                R.string.cat3,
                R.drawable.ic_category3,
                articles = arrayOf(
                    Articles.Article_31,
                    Articles.Article_32,
                    Articles.Article_33,
                )
            ),
//            Category(
//                4,
//                R.string.cat4,
//                R.drawable.ic_category4,
//                articles = arrayOf(
//                    Articles.Article_41,
//                    Articles.Article_42
//                )
//            ),
//            Category(
//                5,
//                R.string.cat5,
//                R.drawable.ic_category5,
//                articles = arrayOf(
//                    Articles.Article_51,
//                    Articles.Article_52,
//                    Articles.Article_53,
//                    Articles.Article_54,
//                    Articles.Article_55,
//                )
//            ),
//            Category(
//                6,
//                R.string.cat6,
//                R.drawable.ic_foundation
//            ),
//            Category(
//                7,
//                R.string.cat7,
//                R.drawable.ic_translate
//            )
        )
    }

    @JvmStatic
    fun getCombinedCategory(): Category {
        return Category(
            0,
            R.string.cat1,
            R.drawable.ic_category1,
            articles = arrayOf(
                Articles.Article_11,
                Articles.Article_12,
                Articles.Article_344,
                Articles.Article_343,
                Articles.Article_31,
                Articles.Article_341,
                Articles.Article_21,
                Articles.Article_345,
                Articles.Article_33,
                Articles.Article_23,
                Articles.Article_22,
                Articles.Article_342,
                Articles.Article_32,
            ).apply {
                shuffle()
            }.take((5 until 13).random()).toTypedArray()
        )
    }

}
