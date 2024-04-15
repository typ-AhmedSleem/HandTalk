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
        )
    }

}
