package com.yalnizfahrettin.azim

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
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
 * Marble, ink, wine and decoded legacy palettes share one readability floor.
 * High contrast is allowed; WCAG does not define a maximum contrast ratio.
 */
class KontrastTest {

    @Test fun `brand captions survive the decorative column overlay`() = tumSetler().forEach { (ad, r) ->
        kontrol(ad, r.markaUstu, r.marka, 7.0, "brand header")
        kontrol(ad, r.markaUstu, r.markaUstu.copy(alpha = .12f).compositeOver(r.marka), 4.5, "brand ornament")
    }

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
        kontrol(ad, r.accent, r.yuzey, 4.5, "accent/yüzey")
    }

    /** Labels also remain readable in elevated surfaces such as choice rows. */
    @Test
    fun `elevated surface text remains readable`() = tumSetler().forEach { (ad, r) ->
        kontrol(ad, r.metin, r.yuzeyYuksek, 4.5, "metin/yüksek yüzey")
        kontrol(ad, r.metinIkincil, r.yuzeyYuksek, 4.5, "ikincil/yüksek yüzey")
    }

    /** Decorative rules may be quiet; outlines identifying controls must meet 3:1. */
    @Test
    fun `interactive outlines remain visible on every opaque surface`() = tumSetler().forEach { (ad, r) ->
        listOf("paper" to r.zemin, "surface" to r.yuzey, "elevated" to r.yuzeyYuksek,
            "selected" to r.accentZemin).forEach { (surface, background) ->
            kontrol(ad, r.kenarlikGuclu, background, 3.0, "control outline/$surface")
        }
    }

    /** Chips, captions and plan summaries use opaque reading surfaces, never bare art. */
    @Test
    fun `secondary and accent labels meet AA on their reading surfaces`() = tumSetler().forEach { (ad, r) ->
        listOf("paper" to r.zemin, "surface" to r.yuzey, "elevated" to r.yuzeyYuksek,
            "selected" to r.accentZemin).forEach { (surface, background) ->
            kontrol(ad, r.metinIkincil, background, 4.5, "secondary label/$surface")
            kontrol(ad, r.accent, background, 4.5, "accent label/$surface")
        }
    }

    /** Stress the published two-layer art budget, independent of any particular bitmap. */
    @Test
    fun `primary ink remains readable over two worst case light art layers`() {
        // A single art layer retains at least .835 of white; this deliberately
        // conservative two-layer floor also covers anti-aliased overlapping edges.
        val floor = .835f * .835f
        val darkestAllowedArt = Color(floor, floor, floor)
        tumSetler().filterNot { it.second.karanlikMi }.forEach { (ad, r) ->
            kontrol(ad, r.metin, darkestAllowedArt, 4.5, "primary ink/two art layers")
        }
    }

    @Test
    fun `primary text remains readable under the dark art opacity budget`() {
        val brightestArt = Color.White.copy(alpha = .07f)
        tumSetler().filter { it.second.karanlikMi }.forEach { (ad, r) ->
            val twoLayers = brightestArt.compositeOver(brightestArt.compositeOver(r.zemin))
            kontrol(ad, r.metin, twoLayers, 4.5, "primary text/two dark-mode art layers")
        }
    }

    /** Kenarlık yüzeyden ayrışmalı, yoksa kartların sınırı kaybolur. */
    @Test
    fun `kenarlik yuzeyden ayrisiyor`() = tumSetler().forEach { (ad, r) ->
        kontrol(ad, r.kenarlik, r.yuzey, 1.15, "kenarlık/yüzey")
    }
}
