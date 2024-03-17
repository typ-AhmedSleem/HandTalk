package com.typ.handtalk.core.repository

import com.typ.handtalk.core.models.HandSign

object Alphabet {

    private val alphabet = mapOf(
        "a" to HandSign("a"),
        "b" to HandSign("b"),
        "c" to HandSign("c"),
        "d" to HandSign("d"),
        "e" to HandSign("e"),
        "f" to HandSign("f"),
        "g" to HandSign("g"),
        "h" to HandSign("h"),
        "i" to HandSign("i"),
        "j" to HandSign("j"),
        "k" to HandSign("k"),
        "l" to HandSign("l"),
        "m" to HandSign("m"),
        "n" to HandSign("n"),
        "o" to HandSign("o"),
        "p" to HandSign("p"),
        "q" to HandSign("q"),
        "r" to HandSign("r"),
        "s" to HandSign("s"),
        "t" to HandSign("t"),
        "u" to HandSign("u"),
        "v" to HandSign("v"),
        "w" to HandSign("w"),
        "x" to HandSign("x"),
        "y" to HandSign("y"),
        "z" to HandSign("z"),
    )

    operator fun get(letter: String) = alphabet[letter]

}