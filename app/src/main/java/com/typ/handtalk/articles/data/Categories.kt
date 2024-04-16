package com.typ.handtalk.articles.data

import com.typ.handtalk.R
import com.typ.handtalk.articles.models.Category

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
                    Articles.Article_34
                )
            ),
            Category(
                4,
                R.string.cat4,
                R.drawable.ic_category4,
                articles = arrayOf(
                    Articles.Article_41,
                    Articles.Article_42
                )
            ),
        )
    }

}
