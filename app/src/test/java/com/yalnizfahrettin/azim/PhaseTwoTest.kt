package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test

class PhaseTwoTest {
    private val access = Kategoriler.tumAltlar.map { it.anahtar }.toSet()
    @Test fun homeAndRemindersRespectLegacyExclusions() {
        val profile = PersonalProfile.decode("v=1&a.avoid=work&a.spirituality=no&a.discovery=wide")
        val home = PersonalPlan.homeCategories(profile, access)
        assertFalse("kariyer" in home)
        assertFalse("para" in home)
        assertTrue(Kategoriler.tumAltlar.filter { it.grup in setOf("inanc", "tasavvuf") }.none { it.anahtar in home })
        repeat(100) {
            val reminder = PersonalPlan.notification(profile, setOf("kariyer", "motivasyon"), access, "tr", emptySet(), null)
            assertNotNull(reminder)
            assertTrue(reminder!!.soz.kategori in home)
        }
    }
    @Test fun noFallbackToExcludedContentAndNoRankingInHomePool() {
        val profile = PersonalProfile().choose("avoid", "work", true)
        assertTrue(PersonalPlan.homeCategories(profile, setOf("kariyer", "para")).isEmpty())
        assertEquals(PersonalPlan.homeCategories(profile, access),
            PersonalPlan.homeCategories(profile.choose("goal", "calm").choose("discovery", "none"), access))
    }
    @Test fun savedSearchHandlesTurkishCaseWhitespaceAndNoMatches() {
        val quotes = Sozler.tumu()
        val quote = quotes.first { it.metin("tr").contains("i") }
        val uppercase = quote.metin("tr").uppercase(java.util.Locale.forLanguageTag("tr"))
        assertTrue(quote in LibraryQuery.saved(quotes, "  $uppercase  ", "tr"))
        assertEquals(quotes, LibraryQuery.saved(quotes, "  ", "tr"))
        assertTrue(LibraryQuery.saved(quotes, "zxqv-does-not-exist", "tr").isEmpty())
    }
}
