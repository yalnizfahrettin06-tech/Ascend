package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test

class MomentFeedTest {
    @Test fun changingThisMomentCannotIntroduceLockedOrExcludedContent() {
        val profile = PersonalProfile().choose("format", "affirmation")
        val feed = PersonalPlan.feed(profile, setOf("ozsefkat"), Erisim.ucretsizKategoriler)
        assertTrue(MomentFeed.apply(feed, "action").isEmpty())
        assertTrue(MomentFeed.apply(feed, "calm").all { it in feed })
        assertEquals(feed, MomentFeed.apply(feed, null))
    }
    @Test fun aTemporaryFilterDoesNotChangeTheInputOrderOrOriginalText() {
        val feed = listOf(Sozler.kategoriden("marcus").first(), Sozler.kategoriden("motivasyon").first(),
            Sozler.kategoriden("azim").first())
        assertEquals(feed.drop(1), MomentFeed.apply(feed, "action"))
        assertEquals(feed.take(1), MomentFeed.apply(feed, "perspective"))
        assertEquals(3, feed.size)
    }
}
