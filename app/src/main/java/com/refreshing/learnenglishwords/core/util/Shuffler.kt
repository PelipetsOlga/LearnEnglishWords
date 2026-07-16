package com.refreshing.learnenglishwords.core.util

interface Shuffler {
    fun <T> shuffle(list: List<T>): List<T>
}

object RandomShuffler : Shuffler {
    override fun <T> shuffle(list: List<T>): List<T> = list.shuffled()
}
