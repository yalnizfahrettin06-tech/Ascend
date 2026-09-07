package com.yalnizfahrettin.azim

import androidx.compose.ui.graphics.Color
import com.yalnizfahrettin.azim.core.AzimRenkleri
import com.yalnizfahrettin.azim.core.Palet
import com.yalnizfahrettin.azim.core.kontrastOrani
import com.yalnizfahrettin.azim.core.paletiCozTest
import org.junit.Assert.assertTrue
import org.junit.Test

/*
 * KONTRAST BEKÇİSİ (WCAG 2.1)
 *
 * Palet değerleri göz kararıyla seçilmedi, ölçülerek seçildi. Bu test
 * eşikleri koruyor: biri renk değerlerini "biraz daha güzel dursun" diye
 * değiştirirse ve okunabilirlik düşerse derleme kırılır.
 *
 * Hedef "orta kontrast": okunaklı ama bağırmayan.
 */
class KontrastTest {

    private fun tumSetler(): List<Pair<String, AzimRenkleri>> =
        Palet.entries.flatMap { p ->
            listOf(
                "${p.name}-karanlık" to paletiCozTest(p, karanlik = true, oled = false),
                "${p.name}-aydınlık" to paletiCozTest(p, karanlik = false, oled = false),
                "${p.name}-oled" to paletiCozTest(p, karanlik = true, oled = true),
            )
        }

    private fun kontrol(ad: String, on: Color, arka: Color, esik: Double, alan: String) {
        val oran = kontrastOrani(on, arka)
        assertTrue(
            "$ad · $alan kontrastı %.2f:1, en az %.1f:1 olmalı".format(oran, esik),
            oran >= esik,
        )
    }

    /** Ana metin her zemin üzerinde rahat okunmalı. */
    @Test
    fun `ana metin yeterince kontrastli`() = tumSetler().forEach { (ad, r) ->
        kontrol(ad, r.metin, r.zemin, 10.0, "metin/zemin")
        kontrol(ad, r.metin, r.yuzey, 9.0, "metin/yüzey")
    }

    /** İkincil metin WCAG AA normal metin eşiğini geçmeli. */
    @Test
    fun `ikincil metin AA esigini gecer`() = tumSetler().forEach { (ad, r) ->
        kontrol(ad, r.metinIkincil, r.zemin, 4.5, "ikincil/zemin")
        kontrol(ad, r.metinIkincil, r.yuzey, 4.5, "ikincil/yüzey")
    }

    /** Small labels need the normal-text contrast threshold. */
    @Test
    fun `sonuk metin UI esigini gecer`() = tumSetler().forEach { (ad, r) ->
        kontrol(ad, r.metinSonuk, r.zemin, 4.5, "sönük/zemin")
        kontrol(ad, r.metinSonuk, r.yuzey, 4.5, "sönük/yüzey")
        val onPrimary = if (r.karanlikMi) Color(0xFF121416) else Color.White
        kontrol(ad, onPrimary, r.accent, 4.5, "button label")
    }

    /** Accent ikon ve metin olarak kullanılıyor, okunabilir olmalı. */
    @Test
    fun `accent okunabilir`() = tumSetler().forEach { (ad, r) ->
        kontrol(ad, r.accent, r.zemin, 4.5, "accent/zemin")
        kontrol(ad, r.accent, r.yuzey, 4.0, "accent/yüzey")
    }

    /**
     * Kontrast YETERLİ olmalı ama AŞIRI da olmamalı — saf beyaz/siyah
     * (21:1) göz yorar. İstenen "orta" bandın üst sınırı.
     */
    @Test
    fun `kontrast asiri degil`() = tumSetler().forEach { (ad, r) ->
        val oran = kontrastOrani(r.metin, r.zemin)
        assertTrue(
            "$ad · metin kontrastı %.2f:1 — 16:1 üstü keskin, kısılmalı".format(oran),
            oran <= 16.0,
        )
    }

    /** Kenarlık yüzeyden ayrışmalı, yoksa kartların sınırı kaybolur. */
    @Test
    fun `kenarlik yuzeyden ayrisiyor`() = tumSetler().forEach { (ad, r) ->
        kontrol(ad, r.kenarlik, r.yuzey, 1.15, "kenarlık/yüzey")
    }
}
