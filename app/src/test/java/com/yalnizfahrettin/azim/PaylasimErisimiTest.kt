package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.paylas.*
import com.yalnizfahrettin.azim.ui.Atmosfer
import org.junit.Assert.*
import org.junit.Test

class PaylasimErisimiTest {
    @Test
    fun `free export has marble night and paper backgrounds`() {
        val expected = listOf(KartZemin.Sahne(R.drawable.art_roman_home_v9), HazirZeminler.duzler.first(), HazirZeminler.duzler.last())
        assertEquals(expected, PaylasimErisimi.ucretsizZeminler)
        expected.forEach { background ->
            assertTrue(PaylasimErisimi.izinVar(false, PaylasimAyari(zemin = background)))
        }
    }

    @Test
    fun `previously free summit is preserved after upgrade`() {
        assertTrue(PaylasimErisimi.izinVar(false, PaylasimAyari(zemin = KartZemin.Sahne(R.drawable.scene_summit))))
    }

    @Test
    fun `all other built in backgrounds require Pro`() {
        val backgrounds = Atmosfer.entries.filter { it != Atmosfer.ZIRVE }.map { KartZemin.Sahne(it.res) } +
            KartZemin.Sahne(Int.MAX_VALUE) + HazirZeminler.duzler.drop(1).dropLast(1) + HazirZeminler.gradyanlar
        backgrounds.forEach { background ->
            assertFalse("Unexpected free background: $background", PaylasimErisimi.izinVar(false, PaylasimAyari(zemin = background)))
            assertTrue(PaylasimErisimi.izinVar(true, PaylasimAyari(zemin = background)))
        }
    }

    @Test
    fun `video cannot export without Pro even with a free background`() {
        PaylasimErisimi.ucretsizZeminler.forEach { background ->
            assertFalse(PaylasimErisimi.izinVar(false, PaylasimAyari(zemin = background), video = true))
            assertTrue(PaylasimErisimi.izinVar(true, PaylasimAyari(zemin = background), video = true))
        }
    }

    @Test
    fun `every advanced setting is checked at export rather than only in the UI`() {
        val default = PaylasimAyari()
        val advanced = listOf(
            default.copy(format = KartFormat.KARE), default.copy(format = KartFormat.YATAY),
            default.copy(yazi = KartYazi.SERIF), default.copy(yazi = KartYazi.SANS),
            default.copy(yazi = KartYazi.MONO), default.copy(karartma = .6f),
            default.copy(yaziOlcegi = 1.2f), default.copy(hizalama = KartHizalama.SOL),
            default.copy(imzaGoster = false),
        )
        advanced.forEach { settings ->
            assertFalse("Unexpected free advanced setting: $settings", PaylasimErisimi.izinVar(false, settings))
            assertTrue(PaylasimErisimi.izinVar(true, settings))
        }
    }

    @Test
    fun `revocation keeps chosen free background and resets all premium settings`() {
        PaylasimErisimi.ucretsizZeminler.forEach { background ->
            val advanced = PaylasimAyari(KartFormat.KARE, KartYazi.MONO, background, .7f, 1.3f, KartHizalama.SOL, false)
            val free = PaylasimErisimi.ucretsizAyar(advanced)
            assertEquals(PaylasimAyari(zemin = background), free)
            assertTrue(PaylasimErisimi.izinVar(false, free))
        }
    }

    @Test
    fun `restored Pro background is replaced with marble for a free preview`() {
        val restored = PaylasimAyari(zemin = KartZemin.Sahne(R.drawable.scene_forest), format = KartFormat.YATAY)
        assertEquals(PaylasimAyari(), PaylasimErisimi.gorunenAyar(restored, pro = false))
        assertSame(restored, PaylasimErisimi.gorunenAyar(restored, pro = true))
    }
}
