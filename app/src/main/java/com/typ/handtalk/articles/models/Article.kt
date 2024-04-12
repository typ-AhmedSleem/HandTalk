package com.typ.handtalk.articles.models

import java.io.Serializable

open class Article(
    val id: Int,
    val title: String,
    val videoPath: String? = null, // Video path in assets folder, e.g: /1/vid.mp4 (nullable).
    val sections: Array<Section> = emptyArray()
) : Serializable