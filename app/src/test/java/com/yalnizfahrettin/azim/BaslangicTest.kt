package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.notif.BildirimZamanlari
import java.time.LocalDateTime
import org.junit.Assert.*
import org.junit.Test

class BaslangicTest {
    @Test fun `each onboarding intention has free content in both languages`() {
        Baslangic.konular.forEach { key ->
            assertTrue(key in Kategoriler.acikAltlar(emptySet()))
            listOf("tr", "en").forEach { assertTrue(Sozler.bildirimHavuzu(setOf(key), it).isNotEmpty()) }
        }
    }
    @Test fun `default feed contains affirmations and has no attribution errors`() {
        val feed = Sozler.akis(Baslangic.varsayilan)
        assertTrue(feed.size >= 12)
        assertTrue(feed.all { it.yazar == "Ascend" })
        assertTrue(Olumlamalar.tumu.all { it.bildirimeUygun("tr") && it.bildirimeUygun("en") })
    }
    @Test fun `selection allows replacing all defaults and sanitizes invalid keys`() {
        val empty = Baslangic.varsayilan.fold(Baslangic.varsayilan) { selection, key -> Baslangic.secimiDegistir(selection, key) }
        assertTrue(empty.isEmpty())
        assertEquals(setOf("umut"), Baslangic.secimiDegistir(empty, "umut"))
        assertEquals(Baslangic.varsayilan, Baslangic.dogrula(setOf("unknown", "marcus")))
    }
    @Test fun `seven reminders in one hour stay inside the window and are distinct`() {
        val now = LocalDateTime.of(2026, 9, 7, 8, 0)
        val times = BildirimZamanlari.hesapla(now, 7, 9, 10)
        assertEquals(7, times.toSet().size)
        assertTrue(times.all { it.hour == 9 })
        assertEquals(times.sorted(), times)
    }
    @Test fun `past slots are skipped and midnight end is supported`() {
        val now = LocalDateTime.of(2026, 9, 7, 23, 40)
        assertTrue(BildirimZamanlari.hesapla(now, 1, 9, 21).isEmpty())
        val times = BildirimZamanlari.hesapla(now, 7, 23, 24)
        assertTrue(times.isNotEmpty())
        assertTrue(times.all { it.isAfter(now) && it.isBefore(now.toLocalDate().plusDays(1).atStartOfDay()) })
    }
    @Test(expected = IllegalArgumentException::class) fun `invalid ranges are rejected`() {
        BildirimZamanlari.hesapla(LocalDateTime.now(), 1, 22, 9)
    }
}
