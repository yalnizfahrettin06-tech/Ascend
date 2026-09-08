package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.Baslangic
import com.yalnizfahrettin.azim.data.Erisim
import com.yalnizfahrettin.azim.data.Kategoriler
import org.junit.Assert.*
import org.junit.Test

class ErisimTest {
    @Test fun `new access offers six complete and individually distinct starter topics`() {
        assertEquals(Baslangic.konular.toSet(), Erisim.acikKategoriler(emptySet(), false))
        assertEquals(6, Erisim.ucretsizKategoriler.size)
        assertTrue("Motivation and persistence are separate free choices", "azim" in Erisim.ucretsizKategoriler)
        assertFalse("A related topic must not unlock its whole family", "pes" in Erisim.ucretsizKategoriler)
        assertEquals(Erisim.ucretsizKategoriler, Kategoriler.acikAltlar(emptySet()))
    }

    @Test fun `legacy free and earned groups expand only during migration`() {
        val granted = Erisim.eskiKazanilanlar(setOf("cesaret", "unknown"), setOf("odak", "stoacilik"))
        val accessible = Erisim.acikKategoriler(granted, false)
        assertEquals(30, accessible.size)
        assertTrue(accessible.containsAll(setOf("pes", "seneca", "ozguven", "epiktetos", "derin_odak")))
        assertFalse("An unrelated locked family stays locked", "kuran" in accessible)
        assertFalse("Unknown IDs never become grants", "unknown" in accessible)
    }

    @Test fun `Pro grants everything while a single reward cannot expand siblings`() {
        val reward = Erisim.acikKategoriler(setOf("ozguven", "unknown"), false)
        assertEquals(Erisim.ucretsizKategoriler + "ozguven", reward)
        assertFalse("korku" in reward)
        assertEquals(70, Erisim.acikKategoriler(emptySet(), true).size)
    }

    @Test fun `revocation discards inaccessible selections and uses a usable fallback`() {
        assertEquals(setOf("motivasyon"), Erisim.guvenliSecim(setOf("motivasyon", "kuran", "unknown"), Erisim.ucretsizKategoriler))
        assertEquals(Kategoriler.varsayilanSecili, Erisim.guvenliSecim(setOf("kuran"), Erisim.ucretsizKategoriler))
        assertEquals(setOf("derin_odak"), Erisim.guvenliSecim(setOf("odak"), Erisim.ucretsizKategoriler))
        assertEquals(Baslangic.varsayilan, Baslangic.dogrula(setOf("pes", "kuran")))
    }
}
