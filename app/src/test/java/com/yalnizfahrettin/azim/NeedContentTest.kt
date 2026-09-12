package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test

class NeedContentTest {
    private val added = "dustukten_sonra kucuk_adim gelisimi_gormek beklemek niyetine_sadik hayir_demek sinirlar onay kiyas elestiri kendini_ifade kararsizlik kontrol fazla_dusunmek ofke pismanlik kendini_affet mukemmeliyet belirsizlik degisim vedalar tek_basina destek dinlemek onarim dinlenme beden is_sinir yaraticilik anlam".split(" ").toSet()

    @Test fun thirtyDistinctNeedsAreCompleteSearchableAndNeverLegacyGrants() {
        assertEquals(30, added.size)
        val legacy = Kategoriler.acikAltlar(Kategoriler.gruplar.map { it.anahtar }.toSet())
        assertEquals(70, legacy.size)
        added.forEach { key ->
            val category = requireNotNull(Kategoriler.bul(key))
            assertEquals(10, Sozler.kategoriden(key).size)
            assertFalse(key in legacy)
            assertFalse(key in Erisim.ucretsizKategoriler)
            listOf("en", "tr").forEach { language ->
                assertTrue(LibraryQuery.filter(category.ad(language), language).any { it.anahtar == key })
                assertEquals(10, Sozler.bildirimHavuzu(setOf(key), language).size)
            }
        }
    }

    @Test fun allNewNeedsCompleteOneCycleWithoutCrossCategoryOrImmediateRepeats() {
        listOf("tr", "en").forEach { language ->
            val history = mutableSetOf<String>()
            var last: String? = null
            repeat(300) {
                val next = requireNotNull(Sozler.bildirimSec(added, language, history, last))
                assertFalse(next.yeniTur)
                assertTrue(next.soz.kategori in added)
                assertTrue(history.add(next.soz.kimlik))
                last = next.soz.kimlik
            }
            val restart = requireNotNull(Sozler.bildirimSec(added, language, history, last))
            assertTrue(restart.yeniTur)
            assertNotEquals(last, restart.soz.kimlik)
        }
    }
}
