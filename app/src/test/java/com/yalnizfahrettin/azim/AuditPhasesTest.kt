package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class AuditPhasesTest {
    @Test fun widgetExcludesAvoidedLockedAndHiddenContent() {
        val profile = PersonalProfile().choose("avoid", "work").choose("discovery", "none")
        val access = setOf("motivasyon", "kariyer", "ozsefkat")
        val hidden = Sozler.tumu().first { it.kategori == "motivasyon" }.kimlik
        val pool = PersonalPlan.widgetPool(profile, setOf("kariyer", "motivasyon"), access, setOf(hidden))
        assertTrue(pool.isNotEmpty())
        assertTrue(pool.all { it.kategori == "motivasyon" && it.kimlik != hidden })
        assertEquals(emptyList<Soz>(), PersonalPlan.widgetPool(profile, emptySet(), emptySet(), emptySet()))
    }
    @Test fun continuationDoesNotDependOnMapOrderAndIgnoresFinishedSeries() {
        val old = SeriesProgress("kindness", 2, LocalDate.of(2026, 9, 17))
        val latest = SeriesProgress("steps", 3, LocalDate.of(2026, 9, 19))
        val done = SeriesProgress("focus", 7, LocalDate.of(2026, 9, 20))
        assertEquals(latest, ShortSeries.active(linkedMapOf(old.id to old, latest.id to latest, done.id to done)))
        assertEquals(latest, ShortSeries.active(linkedMapOf(done.id to done, latest.id to latest, old.id to old)))
        assertNull(ShortSeries.active(mapOf(done.id to done)))
    }
    @Test fun cropNeverExposesBlankEdgesAcrossPortraitSquareAndWide() {
        listOf(.58f, 1f, 2f).forEach { aspect ->
            val w = 1080f; val h = w / aspect
            val scale = maxOf(w / 1024, h / 1536)
            val sw = 1024 * scale; val sh = 1536 * scale
            val focus = ArtworkFocus(.72f, .24f)
            assertTrue(focus.left(w, sw) <= .01f)
            assertTrue(focus.top(h, sh) <= .01f)
            assertTrue(focus.left(w, sw) + sw >= w - .01f)
            assertTrue(focus.top(h, sh) + sh >= h - .01f)
        }
    }
}
