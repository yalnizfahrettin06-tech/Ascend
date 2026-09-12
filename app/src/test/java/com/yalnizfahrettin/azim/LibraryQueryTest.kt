package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test

class LibraryQueryTest {
    @Test fun turkishDottedAndDotlessSearchUseTheCorrectLocale() {
        assertTrue(LibraryQuery.filter("İNANÇ", "tr").isEmpty()) // group names are not topic names
        assertTrue(LibraryQuery.filter("İNCİL", "tr").any { it.anahtar == "incil" })
        assertTrue(LibraryQuery.filter("SABIR", "tr").any { it.anahtar == "zorluk_sabir" })
        assertTrue(LibraryQuery.filter("GİRİŞİM", "tr").any { it.anahtar == "girisimcilik" })
        assertTrue(LibraryQuery.filter("ŞÜKÜR", "tr").any { it.anahtar == "sukur" })
    }
    @Test fun aQuerySearchesOutsideTheCurrentCollectionAndSelection() {
        assertEquals(listOf("marcus"), LibraryQuery.filter("Marcus", "tr", group = "olumlamalar",
            selectedOnly = true, selected = setOf("ozsefkat")).map { it.anahtar })
        assertEquals(4, LibraryQuery.filter("", "tr", group = "olumlamalar").size)
        assertEquals(listOf("ozsefkat"), LibraryQuery.filter("", "tr", selectedOnly = true,
            selected = setOf("ozsefkat")).map { it.anahtar })
    }
    @Test fun accessFiltersNeverGrantAccessAndDataIdsAreUnchanged() {
        assertTrue(LibraryQuery.filter("Marcus", "en", unlockedOnly = true, unlocked = emptySet()).isEmpty())
        assertEquals(120, LibraryQuery.filter("", "tr").size)
        assertEquals(Kategoriler.tumAltlar.map { it.anahtar }, LibraryQuery.filter("", "en").map { it.anahtar })
    }
    @Test fun thinkersHaveOneDiscoveryEntryWithoutChangingRealGroups() {
        val entry = Kategoriler.kesfetGruplari.single { it.anahtar == Kategoriler.DUSUNURLER }
        assertEquals(32, entry.altlar.size)
        assertEquals("Ünlü düşünürler", entry.ad("tr"))
        assertEquals(entry.altlar, LibraryQuery.filter("", "tr", group = entry.anahtar))
        assertTrue(entry.altlar.any { it.anahtar == "mevlana" })
        assertTrue(entry.altlar.any { it.anahtar == "marcus" })
        assertEquals("tasavvuf", Kategoriler.bul("mevlana")!!.grup)
        assertEquals("filozoflar", Kategoriler.bul("marcus")!!.grup)
        assertEquals(listOf("zen"), LibraryQuery.filter("", "tr", group = "dogu_gelenegi").map { it.anahtar })
        val displayed = Kategoriler.kesfetGruplari.flatMap { it.altlar }.map { it.anahtar }
        assertEquals(Kategoriler.tumAltlar.map { it.anahtar }.toSet(), displayed.toSet())
        assertEquals(displayed.size, displayed.toSet().size)
        entry.altlar.forEach { person ->
            assertTrue(Sozler.kategoriden(person.anahtar).size in 10..20)
        }
    }

    @Test fun newThinkersAreSearchableButDoNotBypassPro() {
        assertFalse("epikuros" in Kategoriler.acikAltlar(setOf("filozoflar")))
        assertTrue("seneca" in Kategoriler.acikAltlar(setOf("filozoflar")))
        assertFalse("attar" in Kategoriler.acikAltlar(setOf("tasavvuf")))
        assertTrue("mevlana" in Kategoriler.acikAltlar(setOf("tasavvuf")))
        assertEquals(listOf("kierkegaard"), LibraryQuery.filter("Kierkegaard", "en").map { it.anahtar })
        assertTrue(LibraryQuery.filter("Kierkegaard", "en", unlockedOnly = true, unlocked = emptySet()).isEmpty())
        assertEquals(listOf("attar"), LibraryQuery.filter("Attâr", "tr", group = "olumlamalar").map { it.anahtar })
    }
}

