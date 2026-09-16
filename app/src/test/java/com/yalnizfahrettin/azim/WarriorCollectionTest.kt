package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.AnaTemalar
import com.yalnizfahrettin.azim.ui.Atmosfer
import com.yalnizfahrettin.azim.ui.AtmosferGrubu
import org.junit.Assert.*
import org.junit.Test

class WarriorCollectionTest {
    @Test fun collectionIsCuratedAndRetiredThemesDoNotUnlockPro() {
        assertFalse(Atmosfer.CADI in Atmosfer.gallery)
        assertFalse(Atmosfer.DENIZ in Atmosfer.gallery)
        assertFalse(Atmosfer.ZIRVE in Atmosfer.gallery)
        assertTrue(Atmosfer.gallery.none { it.grup == AtmosferGrubu.DOKU })
        assertEquals(Atmosfer.gallery.size,Atmosfer.gallery.map { it.res }.distinct().size)
        assertEquals(setOf("white","black","roma","rider"),AnaTemalar.all.filter { !it.pro }.map { it.id }.toSet())
        assertEquals("black",AnaTemalar.allowed("sea",false).id)
        assertEquals("black",AnaTemalar.allowed("emperor",false).id)
        assertEquals("emperor",AnaTemalar.allowed("emperor",true).id)
        assertEquals(6,AnaTemalar.onboarding.size)
        assertTrue(Atmosfer.gallery.all { scene -> AnaTemalar.all.any { it.art == scene.res } })
    }
}
