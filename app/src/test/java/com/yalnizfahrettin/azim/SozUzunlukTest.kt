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

    /*
     * POLİTİKA DEĞİŞTİ (bildirim raporu):
     * Uzun sözler artık havuzdan ELENMİYOR — bildirimde kısaltılıp tamamı
     * uygulamada açılıyor. Bu yüzden test artık "hiçbir söz uzun olmasın"
     * demiyor; "kısaltma her koşulda sınırın altında bir metin üretsin"
     * diyor. Asıl güvence bu.
     */
    @Test
    fun `bildirim metni her dilde siniri asmaz`() {
        val asanlar = Sozler.tumu().flatMap { s ->
            listOf("tr", "en").map { it to s.bildirimMetni(it) }
        }.filter { it.second.length > Sozler.BILDIRIM_SINIRI }
        assertTrue(
            "Kısaltmadan sonra bile sınırı aşanlar:\n" +
                asanlar.joinToString("\n") { "${it.first} ${it.second.length} kr — ${it.second}" },
            asanlar.isEmpty(),
        )
    }

    @Test
    fun `kisa sozler kisaltilmadan gecer`() {
        val kisa = Sozler.tumu().filter { !it.kisaltilirMi("tr") }
        assertTrue("Test için kısa söz bulunamadı", kisa.isNotEmpty())
        kisa.forEach { assertEquals(it.tr, it.bildirimMetni("tr")) }
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

    @Test
    fun `sozlerin cogu ideal sinirin altinda`() {
        val tum = Sozler.tumu()
        val idealde = tum.count { it.tr.length <= Sozler.IDEAL_SINIR }
        assertTrue(
            "En az %60'ı ${Sozler.IDEAL_SINIR} altında olmalı ($idealde/${tum.size})",
            idealde * 100 / tum.size >= 60,
        )
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
