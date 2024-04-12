package com.typ.handtalk.articles.models

import java.io.Serializable

sealed class Section : Serializable {

    class TextSection(
        val question: String,
        val answer: String
    ) : Section()

    class ImageSection(
        /** Image path in assets folder of parent article*/
        val src: String
    ) : Section()

}
