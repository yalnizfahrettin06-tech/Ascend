package com.yalnizfahrettin.azim.data

import kotlin.random.Random

object QuietFeed {
    const val RECENT_LIMIT = 30
    fun remember(recent: List<String>, id: String): List<String> =
        (listOf(id) + recent.filterNot { it == id }).take(RECENT_LIMIT)

    fun order(pool: List<Soz>, recent: List<String>, hidden: Set<String>, random: Random = Random.Default): List<Soz> {
        val allowed = pool.filterNot { it.kimlik in hidden }
        val fresh = allowed.filterNot { it.kimlik in recent }.shuffled(random)
        val repeated = allowed.filter { it.kimlik in recent }.sortedByDescending { recent.indexOf(it.kimlik) }
        return fresh + repeated
    }
}
