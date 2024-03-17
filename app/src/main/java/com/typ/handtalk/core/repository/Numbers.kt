package com.typ.handtalk.core.repository

import com.typ.handtalk.core.models.HandSign

object Numbers {

    private val numbers = mapOf(
        "1" to HandSign("1"),
        "2" to HandSign("2"),
        "3" to HandSign("3"),
        "4" to HandSign("4"),
        "5" to HandSign("5"),
        "6" to HandSign("6"),
        "7" to HandSign("7"),
        "8" to HandSign("8"),
        "9" to HandSign("9"),
        "0" to HandSign("0"),
    )

    operator fun get(number: String) = numbers[number]

}