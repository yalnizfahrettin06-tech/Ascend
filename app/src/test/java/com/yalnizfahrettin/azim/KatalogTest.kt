package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.EskiSozler
import com.yalnizfahrettin.azim.data.Kategoriler
import com.yalnizfahrettin.azim.data.Sozler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** Content delivery contracts: coverage, saved IDs, language fallback and notification rotation. */
class KatalogTest {

    @Test
    fun `current catalogue covers all seventy categories with ten records each`() {
        val records = Sozler.tumu()
        val categories = Kategoriler.tumAltlar.map { it.anahtar }.toSet()
        val grouped = records.groupBy { it.kategori }

        assertEquals("The release must contain 700 current records", 700, records.size)
        assertEquals("The release must define 70 categories", 70, categories.size)
        assertEquals("Missing or unknown content categories", categories, grouped.keys)
        categories.forEach { category ->
            assertEquals("Incomplete category: $category", 10, grouped.getValue(category).size)
            assertEquals(grouped.getValue(category), Sozler.kategoriden(category))
        }
    }

    @Test
    fun `current IDs are unique explicit and stable through copy edits`() {
        val records = Sozler.tumu()
        assertEquals(records.size, records.map { it.kimlik }.toSet().size)

        records.groupBy { it.kategori }.forEach { (category, group) ->
            val expected = (1..10).map { "v5_${category}_${it.toString().padStart(2, '0')}" }.toSet()
            assertEquals("Unexpected IDs in $category", expected, group.map { it.kimlik }.toSet())
        }
        records.forEach { original ->
            assertNotNull("Missing explicit ID: ${original.kategori}", original.sabitKimlik)
            val edited = original.copy(
                tr = "Bu kaydın Türkçe metni editoryal incelemeden sonra değiştirildi.",
                en = "The English wording of this record changed during editorial review.",
            )
            assertEquals("Copy edits must not orphan a saved record", original.kimlik, edited.kimlik)
            assertSame("Saved IDs must resolve to the current record", original, Sozler.kimlikten(original.kimlik))
            assertTrue(Sozler.aktifKimlikMi(original.kimlik))
        }
    }

    @Test
    fun `all eighty two previous records remain resolvable as archives`() {
        val archived = EskiSozler.kimlikler
        assertEquals("Do not drop previously saved v4 IDs", 82, archived.size)
        archived.forEach { (id, oldRecord) ->
            val resolved = Sozler.kimlikten(id)
            assertNotNull("Missing archived ID: $id", resolved)
            assertEquals(oldRecord, resolved)
            assertEquals(id, resolved!!.kimlik)
            assertTrue("A previous record needs its archive label: $id", resolved.arsiv)
            assertFalse("Archive must not count as current: $id", Sozler.aktifKimlikMi(id))
        }
        // Fixed v4 examples independently protect bookmarks from the old text-hash scheme.
        assertEquals(
            "Bugün kendime daha nazik davranmayı seçiyorum.",
            Sozler.kimlikten("ozsefkat:-461031388")?.tr,
        )
        assertEquals(
            "I can focus on one small thing I can do right now.",
            Sozler.kimlikten("ic_huzur:-346997883")?.en,
        )
        assertNull(Sozler.kimlikten("a-saved-id-that-never-existed"))
    }

    @Test
    fun `archives never enter current feeds or notification pools`() {
        val selected = Kategoriler.tumAltlar.map { it.anahtar }.toSet()
        val currentIds = Sozler.tumu().map { it.kimlik }.toSet()
        val archiveIds = EskiSozler.kimlikler.keys

        assertTrue("Archive and current IDs must be disjoint", currentIds.intersect(archiveIds).isEmpty())
        assertTrue(Sozler.tumu().none { it.arsiv })
        listOf("tr", "en").forEach { language ->
            val pool = Sozler.bildirimHavuzu(selected, language)
            assertEquals(currentIds, pool.map { it.kimlik }.toSet())
            assertTrue(pool.none { it.arsiv })
        }
        listOf(selected, emptySet<String>()).forEach { selection ->
            val feed = Sozler.akis(selection, archiveIds)
            assertEquals(currentIds, feed.map { it.kimlik }.toSet())
            assertTrue(feed.none { it.arsiv })
            val random = Sozler.rastgele(selection, archiveIds)
            assertNotNull(random)
            assertTrue(random!!.kimlik in currentIds)
            assertFalse(random.arsiv)
        }
        selected.forEach { category ->
            assertTrue("Archive leaked into $category", Sozler.kategoriden(category).none { it.arsiv })
        }
    }

    @Test
    fun `unsupported content locales use the English source`() {
        val selected = setOf("motivasyon", "ozsefkat")
        val expectedPool = Sozler.bildirimHavuzu(selected, "en").map { it.kimlik }.toSet()
        listOf("de", "fr", "zz-ZZ", "").forEach { unsupported ->
            (Sozler.tumu() + EskiSozler.kimlikler.values).forEach { record ->
                assertEquals("Wrong fallback for ${record.kimlik}", record.en, record.metin(unsupported))
                assertEquals(record.imza("en"), record.imza(unsupported))
                assertEquals(record.sunumEtiketi("en"), record.sunumEtiketi(unsupported))
            }
            assertEquals(expectedPool, Sozler.bildirimHavuzu(selected, unsupported).map { it.kimlik }.toSet())
        }
    }

    @Test
    fun `notification selection uses the only unseen record before restarting`() {
        val selected = setOf("motivasyon", "marcus")
        val pool = Sozler.bildirimHavuzu(selected, "tr")
        val remaining = pool.first()
        val history = pool.drop(1).map { it.kimlik }.toSet() + EskiSozler.kimlikler.keys + "unknown-old-id"
        val result = requireNotNull(Sozler.bildirimSec(selected, "tr", history, pool.last().kimlik))

        assertEquals(remaining.kimlik, result.soz.kimlik)
        assertFalse("One unseen record remains; do not restart early", result.yeniTur)
    }

    @Test
    fun `notification rotation exhausts selected records in two complete cycles`() {
        val selected = setOf("motivasyon", "marcus")
        val expectedIds = Sozler.kategoriden("motivasyon").map { it.kimlik }.toSet() +
            Sozler.kategoriden("marcus").map { it.kimlik }
        assertEquals(20, expectedIds.size)
        val history = mutableSetOf<String>()
        var lastId: String? = null

        repeat(2) { cycle ->
            val seenInCycle = mutableSetOf<String>()
            repeat(expectedIds.size) { position ->
                val result = requireNotNull(Sozler.bildirimSec(selected, "en", history, lastId))
                assertEquals("Unexpected reset at cycle $cycle, position $position", cycle == 1 && position == 0, result.yeniTur)
                assertTrue("Unselected content delivered", result.soz.kimlik in expectedIds)
                assertNotEquals("Immediate repeat across a cycle boundary", lastId, result.soz.kimlik)
                assertTrue("A record repeated before exhaustion", seenInCycle.add(result.soz.kimlik))
                if (result.yeniTur) history.clear()
                history += result.soz.kimlik
                lastId = result.soz.kimlik
            }
            assertEquals("A selected record was skipped", expectedIds, seenInCycle)
        }
    }

    @Test
    fun `notification selection avoids the last record even without a history entry`() {
        val selected = setOf("motivasyon")
        val lastId = Sozler.kategoriden("motivasyon").first().kimlik
        // The invariant must hold for every random draw, without depending on draw order.
        repeat(20) {
            val result = requireNotNull(Sozler.bildirimSec(selected, "en", emptySet(), lastId))
            assertNotEquals(lastId, result.soz.kimlik)
            assertFalse(result.yeniTur)
        }
    }

    @Test
    fun `empty and invalid notification selections return no content`() {
        listOf(emptySet<String>(), setOf("unknown-category"), setOf("filozoflar")).forEach { invalid ->
            assertTrue(Sozler.bildirimHavuzu(invalid, "en").isEmpty())
            assertNull(Sozler.bildirimSec(invalid, "en", emptySet(), null))
        }
        val mixed = requireNotNull(
            Sozler.bildirimSec(setOf("unknown-category", "motivasyon"), "en", emptySet(), null),
        )
        assertEquals("An invalid key must not widen the pool", "motivasyon", mixed.soz.kategori)
    }
}
