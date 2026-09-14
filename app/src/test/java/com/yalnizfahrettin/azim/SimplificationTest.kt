package com.yalnizfahrettin.azim
import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test
class SimplificationTest {
    @Test fun emptyCollectionsNeverAppearAndPopulatedTopicsRemainReachable() {
        assertTrue(LibraryQuery.visibleGroups(emptySet()).isEmpty())
        val groups=LibraryQuery.visibleGroups(setOf("motivasyon"))
        assertEquals(listOf("motivasyon"),groups.flatMap { it.altlar }.map { it.anahtar })
        assertEquals(Kategoriler.tumAltlar.map { it.anahtar }.toSet(), LibraryQuery.visibleGroups().flatMap { it.altlar }.map { it.anahtar }.toSet())
    }
    @Test fun inspiredWordsHaveAnHonestSourceInEveryLanguage() {
        Diller.kodlar.forEach { lang ->
            val author=Sozler.kategoriden("yunus").first().imza(lang)
            val original=Sozler.kategoriden("motivasyon").first().imza(lang)
            assertTrue(author.startsWith("Ascend"));assertNotEquals(author,original)
        }
    }
}
