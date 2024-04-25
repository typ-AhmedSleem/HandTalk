package com.typ.handtalk.core.a2s

import com.typ.handtalk.core.a2s.playables.A2SignPlayable

class Arabic2SignTranslator {

    /**
     * Translate the prompt to sign language
     * and result a map of with key is the word
     * and value is (array or A2SPlayableImage) or (A2SPlayableVideo).
     *
     * @return map<String, A2SPlayable?> or map<String, Array<A2SPlayableImage>?>
     */
    fun translate(sentence: String): Map<String, A2SignPlayable?> {
        // * Create an empty map
        val playableSentence = mutableMapOf<String, A2SignPlayable?>()
        // * Split the sentence into words separated by space
        val words = sentence.split(SPACE)
        // * Get the playable object for each word from the A2SPlayableRepository
        words.forEach { word ->
            playableSentence[word] = A2SPlayableRepository.getPlayableForWord(word)
        }
        // * Return the map
        return playableSentence
    }

    companion object {
        private const val SPACE = ' '
    }

}
