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
        assertEquals(3, LibraryQuery.filter("", "tr", group = "olumlamalar").size)
        assertEquals(listOf("ozsefkat"), LibraryQuery.filter("", "tr", selectedOnly = true,
            selected = setOf("ozsefkat")).map { it.anahtar })
    }
    @Test fun accessFiltersNeverGrantAccessAndDataIdsAreUnchanged() {
        assertTrue(LibraryQuery.filter("Marcus", "en", unlockedOnly = true, unlocked = emptySet()).isEmpty())
        assertEquals(70, LibraryQuery.filter("", "tr").size)
        assertEquals(Kategoriler.tumAltlar.map { it.anahtar }, LibraryQuery.filter("", "en").map { it.anahtar })
    }
}

