package com.typ.handtalk.core.articles.models

import java.io.Serializable

open class Article(
    val id: Int,
    val title: String,
    val videoPath: String? = null, // Video path in assets folder, e.g: /1/vid.mp4 (nullable).
    val sections: Array<Section> = emptyArray()
) : Serializable {

    override fun toString(): String {
        return "Article(id=$id, title='$title', videoPath=$videoPath, sectionsCount=${sections.size})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Article) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }


}