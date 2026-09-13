package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test

class LocaleTest {
    @Test fun allFiveLanguagesCoverEveryCurrentQuoteWithoutChangingIds() {
        listOf("pt", "de", "fr", "it", "ru").forEach { lang ->
            val all = Sozler.tumu()
            assertEquals(all.size, LocaleCatalog.quotes(lang).size)
            all.forEach { quote ->
                assertNotNull("$lang ${quote.kimlik}", Diller.soz(lang, quote.kimlik))
                assertNotEquals(quote.en, quote.metin(lang))
                assertNotEquals(quote.tr, quote.metin(lang))
                assertTrue(quote.metin(lang).length in 20..300)
                assertSame(quote, Sozler.kimlikten(quote.kimlik))
            }
            assertEquals(all.map { it.kimlik }.toSet(), Sozler.bildirimHavuzu(Kategoriler.tumAltlar.map { it.anahtar }.toSet(),lang).map { it.kimlik }.toSet())
        }
    }
    @Test fun reviewedActionsAndDynamicValuesResolveInEveryLanguage() {
        val expected = mapOf("pt" to "Continuar", "de" to "Weiter", "fr" to "Continuer", "it" to "Continua", "ru" to "Продолжить")
        expected.forEach { (lang, label) ->
            assertEquals(label, Diller.metin(lang,"Devam","Continue"))
            val text = Diller.metin(lang,"","3 topics selected")
            assertTrue(text.contains("3"))
            assertFalse(text.contains("{0}"))
            assertFalse(text.contains("topics"))
            assertTrue(Sozler.tumu().first().imza(lang).startsWith("Ascend"))
        }
        assertEquals("Выбрано тем: 3", Diller.metin("ru","","3 topics selected"))
        assertEquals("pt",Diller.normalize("pt-BR"))
        assertEquals("de",Diller.normalize("de-DE"))
        assertEquals("en",Diller.normalize("zz-ZZ"))
    }
    @Test fun translatedCategoriesRemainSearchableAndDoNotChangeAccess() {
        listOf("pt","de","fr","it","ru").forEach { lang ->
            val topic=Kategoriler.bul("merak")!!
            assertTrue(LibraryQuery.filter(topic.ad(lang),lang).any { it.anahtar==topic.anahtar })
            assertTrue(LibraryQuery.filter(topic.ad(lang),lang,unlockedOnly=true,unlocked=emptySet()).isEmpty())
        }
    }
}
