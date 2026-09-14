package com.yalnizfahrettin.azim
import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test
class SimplificationTest {
    @Test fun quoteTerminologyNeverMeansPriceOrPunctuation() {
        assertEquals("Réafficher 4 citations masquées",Diller.metin("fr","","Restore 4 hidden quotes"))
        assertEquals("4 ausgeblendete Sprüche wieder anzeigen",Diller.metin("de","","Restore 4 hidden quotes"))
        assertEquals("Compartilhar frase",Diller.metin("pt","","Share quote"))
        listOf("pt","de","fr","it","ru").forEach { language ->
            val source="Never show this quote again"
            assertNotEquals(source,Diller.metin(language,"",source))
        }
    }
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
