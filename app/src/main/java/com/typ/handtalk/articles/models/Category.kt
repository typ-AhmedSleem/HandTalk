package com.typ.handtalk.articles.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.io.Serializable

class Category(
    val id: Int,
    @field:StringRes val name: Int,
    @field:DrawableRes val icon: Int,
    val articles: Array<Article> = emptyArray()
) : Serializable