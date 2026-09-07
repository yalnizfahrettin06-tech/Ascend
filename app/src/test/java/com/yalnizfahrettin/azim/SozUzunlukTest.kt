package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.Kategoriler
import com.yalnizfahrettin.azim.data.Soz
import com.yalnizfahrettin.azim.data.Sozler
import com.yalnizfahrettin.azim.ui.Kilometre
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Bildirim okunabilirliğinin bekçisi — her dil için ayrı ayrı. */
class SozUzunlukTest {

    // The v5 catalogue must fit completely in both languages. Truncation remains
    // a legacy helper, not a way to make oversized current content pass validation.
    @Test
    fun `guncel metinler her dilde kirk ile yuz yirmi karakter arasinda`() {
        val asanlar = Sozler.tumu().flatMap { s ->
            listOf("tr", "en").map { Triple(s.kimlik, it, s.metin(it)) }
        }.filter { it.third.length !in 40..120 }
        assertTrue(
            "Güncel katalogda uzunluk sınırını aşan metinler:\n" +
                asanlar.joinToString("\n") { "${it.first} ${it.second} ${it.third.length} kr — ${it.third}" },
            asanlar.isEmpty(),
        )
    }

    @Test
    fun `yedi yuz guncel sozun tamami her iki dilde kisaltilmadan gecer`() {
        val tumu = Sozler.tumu()
        assertEquals("Güncel katalog eksik", 700, tumu.size)
        tumu.forEach { soz ->
            listOf("tr", "en").forEach { dil ->
                assertTrue("${soz.kimlik} $dil bildirime uygun değil", soz.bildirimeUygun(dil))
                assertFalse("${soz.kimlik} $dil kısaltma istiyor", soz.kisaltilirMi(dil))
                assertEquals("${soz.kimlik} $dil metni kesildi", soz.metin(dil), soz.bildirimMetni(dil))
            }
        }
    }

    @Test
    fun `uzun soz kelime ortasindan bolunmez`() {
        val uzun = Soz(
            tr = "Bu cümle bilerek çok uzun tutulmuş bir denemedir ve kısaltma " +
                "işleminin kelimeleri ortasından bölüp bölmediğini sınamak için " +
                "yazılmıştır efendim.",
            en = "This sentence is deliberately long enough to be truncated by " +
                "the notification shortening logic under test right here now.",
            yazar = "Test", kategori = "motivasyon",
        )
        listOf("tr", "en").forEach { dil ->
            val kisa = uzun.bildirimMetni(dil)
            assertTrue("Sınırı aşıyor: ${kisa.length}", kisa.length <= Sozler.BILDIRIM_SINIRI)
            assertTrue("Üç nokta yok", kisa.endsWith("…"))
            // Kesilen gövde, tam metnin bir kelime sınırı öneki olmalı
            val govde = kisa.dropLast(1)
            assertTrue("Kelime ortasından bölünmüş", uzun.metin(dil).startsWith(govde))
            assertFalse("Boşlukla bitiyor", govde.endsWith(" "))
        }
    }

    @Test
    fun `hicbir soz havuz tavanini asmaz`() {
        val asanlar = Sozler.tumu().filter {
            it.tr.length > Sozler.HAVUZ_TAVANI || it.en.length > Sozler.HAVUZ_TAVANI
        }
        assertTrue("Havuz tavanını (${Sozler.HAVUZ_TAVANI}) aşan söz var", asanlar.isEmpty())
    }

    /** Rapor 5.1: İngilizce arayüz + Türkçe içerik çelişkisi geri gelmesin. */
    @Test
    fun `her sozun her iki dilde metni var`() {
        val eksik = Sozler.tumu().filter { it.tr.isBlank() || it.en.isBlank() }
        assertTrue("Dili eksik söz: ${eksik.map { it.kategori }}", eksik.isEmpty())
    }

    @Test
    fun `ceviriler birbirinin kopyasi degil`() {
        val kopya = Sozler.tumu().filter { it.tr == it.en }
        assertTrue("Çevrilmemiş söz: ${kopya.map { it.tr }}", kopya.isEmpty())
    }

    @Test
    fun `her sozun kategorisi tanimli`() {
        val bilinmeyen = Sozler.tumu().filter { Kategoriler.bul(it.kategori) == null }
        assertTrue("Tanımsız kategori: $bilinmeyen", bilinmeyen.isEmpty())
    }

    @Test
    fun `soz kimlikleri benzersiz`() {
        val k = Sozler.tumu().map { it.kimlik }
        assertEquals("Çakışan kimlik var", k.size, k.toSet().size)
    }

    @Test
    fun `kategori anahtarlari benzersiz`() {
        val a = Kategoriler.tumAltlar.map { it.anahtar }
        assertEquals("Çakışan alt kategori anahtarı", a.size, a.toSet().size)
    }

    @Test
    fun `grup anahtarlari benzersiz`() {
        val g = Kategoriler.gruplar.map { it.anahtar }
        assertEquals("Çakışan grup anahtarı", g.size, g.toSet().size)
    }

    /** Ücretsiz gruplar boş olmamalı — ilk deneyim buna bağlı. */
    @Test
    fun `ucretsiz gruplarda yeterli soz var`() {
        Kategoriler.gruplar.filter { it.ucretsiz }.forEach { grup ->
            val adet = grup.altlar.sumOf { Sozler.kategoriden(it.anahtar).size }
            assertTrue("${grup.anahtar} grubunda yalnız $adet söz var", adet >= 10)
        }
    }

    /** Varsayılan seçili kategorilerin hepsi tanımlı ve ücretsiz olmalı. */
    @Test
    fun `varsayilan secili gecerli`() {
        val ucretsizAltlar = Kategoriler.acikAltlar(emptySet())
        Kategoriler.varsayilanSecili.forEach {
            assertTrue("$it tanımsız", Kategoriler.bul(it) != null)
            assertTrue("$it ücretsiz değil", it in ucretsizAltlar)
        }
    }

    /** Eski anahtarlar yeni yapıya kayıpsız göçmeli. */
    @Test
    fun `eski anahtarlar gocuruluyor`() {
        val eski = setOf("sabir", "odak", "stoacilik", "marcus_aurelius", "bilinmeyen")
        val yeni = Kategoriler.gocur(eski)
        assertTrue("zorluk_sabir yok", "zorluk_sabir" in yeni)
        assertTrue("derin_odak yok", "derin_odak" in yeni)
        assertTrue("epiktetos yok", "epiktetos" in yeni)
        assertTrue("marcus yok", "marcus" in yeni)
        assertTrue("bilinmeyen düşmedi", "bilinmeyen" !in yeni)
    }


}

/** Kilometre taşı mantığı (rapor 4.1). */
class KilometreTest {

    @Test
    fun `esik gecilince kutlama tetiklenir`() {
        assertEquals(7, Kilometre.yeniEsik(seri = 7, kutlanan = 3))
    }

    @Test
    fun `ayni esik iki kez kutlanmaz`() {
        assertNull(Kilometre.yeniEsik(seri = 7, kutlanan = 7))
    }

    @Test
    fun `esik altinda kutlama yok`() {
        assertNull(Kilometre.yeniEsik(seri = 2, kutlanan = 0))
    }

    @Test
    fun `atlanan esikler icin en yuksegi secilir`() {
        assertEquals(30, Kilometre.yeniEsik(seri = 35, kutlanan = 3))
    }
}
