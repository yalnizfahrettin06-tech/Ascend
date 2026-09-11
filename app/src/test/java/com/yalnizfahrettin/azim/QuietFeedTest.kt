package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class QuietFeedTest {
    @Test fun newSessionStartsWithUnseenAndKeepsMostRecentLast() {
        val pool = Sozler.kategoriden("motivasyon")
        val recent = listOf(pool[0].kimlik, pool[1].kimlik)
        val ordered = QuietFeed.order(pool, recent, emptySet(), Random(42))
        assertFalse(ordered.first().kimlik in recent)
        assertEquals(pool[0], ordered.last())
        assertEquals(pool.toSet(), ordered.toSet())
    }
    @Test fun fullCycleUsesOldestFirstAndHiddenNeverReturnsAsFallback() {
        val pool = Sozler.kategoriden("motivasyon").take(3)
        val ordered = QuietFeed.order(pool, pool.map { it.kimlik }, setOf(pool[1].kimlik))
        assertEquals(listOf(pool[2], pool[0]), ordered)
        assertTrue(QuietFeed.order(pool, emptyList(), pool.map { it.kimlik }.toSet()).isEmpty())
    }
    @Test fun boundedRecentOrderDeduplicatesBeforeTrimming() {
        val recent = (0..40).map(Int::toString)
        val updated = QuietFeed.remember(recent, "4")
        assertEquals(30, updated.size)
        assertEquals("4", updated.first())
        assertEquals(1, updated.count { it == "4" })
    }
    @Test fun remindersDoNotFallBackToHiddenQuotes() {
        val access = setOf("motivasyon")
        val hidden = Sozler.kategoriden("motivasyon").map { it.kimlik }.toSet()
        assertNull(PersonalPlan.notification(null, access, access, "tr", emptySet(), null, hidden))
    }
}
