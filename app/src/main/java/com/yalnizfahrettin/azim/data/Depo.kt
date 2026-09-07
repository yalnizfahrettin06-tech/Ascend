package com.yalnizfahrettin.azim.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yalnizfahrettin.azim.core.Palet
import com.yalnizfahrettin.azim.core.TemaModu
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.ds by preferencesDataStore("azim")

/** Tek veri kapısı. Ekranlar DataStore'u doğrudan görmez. */
class Depo(private val ctx: Context) {

    private object K {
        val SECILI = stringSetPreferencesKey("secili_kategoriler")
        val ACIK_GRUP = stringSetPreferencesKey("acik_gruplar")
        val FAVORI = stringSetPreferencesKey("favoriler")
        val GECMIS = stringSetPreferencesKey("gosterilen_gecmis")
        val GUNLUK = intPreferencesKey("gunluk_adet")
        val BASLANGIC = intPreferencesKey("baslangic_saati")
        val BITIS = intPreferencesKey("bitis_saati")
        val TEMA = stringPreferencesKey("tema")
        val DINAMIK = booleanPreferencesKey("dinamik_renk")
        val HAPTIK = booleanPreferencesKey("haptik")
        val DIL = stringPreferencesKey("dil")
        val ONBOARDING = booleanPreferencesKey("onboarding_bitti")
        val HATIRLATICI = booleanPreferencesKey("hatirlatici_acik")
        val OKUNAN = stringSetPreferencesKey("okunan_gunluk")
        val SERI = intPreferencesKey("seri_gun")
        val REKOR = intPreferencesKey("rekor_seri")
        val SON_GUN = stringPreferencesKey("son_acilis")
        val GORULEN = intPreferencesKey("gorulen_toplam")
        val KUTLANAN = intPreferencesKey("kutlanan_kilometre")
        val DEGERLENDIRME = booleanPreferencesKey("degerlendirme_soruldu")
        val AKTIF_GUNLER = stringSetPreferencesKey("aktif_gunler")
        val BUGUN_GELEN = stringSetPreferencesKey("bugun_gelen")
        val PALET = stringPreferencesKey("palet")
        val BUGUN_GORULEN = stringPreferencesKey("bugun_gorulen")   // "tarih|adet"
        val PLANLI_SAATLER = stringSetPreferencesKey("planli_saatler")
        val IPUCU_KAPATILDI = stringPreferencesKey("ipucu_kapatildi")
    }

    /** Geçmişte kaç söz hatırlanacak — havuzun yarısı kadarı yeterli. */
    private val gecmisTavani get() = (Sozler.tumu().size / 2).coerceAtLeast(5)

    /** Seçili alt kategoriler. Eski düz anahtarlar okunurken göçürülür. */
    val secili: Flow<Set<String>> = ctx.ds.data.map { p ->
        val kayitli = p[K.SECILI]
        if (kayitli.isNullOrEmpty()) Kategoriler.varsayilanSecili
        else Kategoriler.gocur(kayitli).ifEmpty { Kategoriler.varsayilanSecili }
    }

    /** Açık gruplar — ücretsizler her zaman dahil. */
    val acikGruplar: Flow<Set<String>> = ctx.ds.data.map {
        (it[K.ACIK_GRUP] ?: emptySet()) + Kategoriler.ucretsizGruplar
    }

    /** Açık alt kategoriler — gruptan türetilir. */
    val acik: Flow<Set<String>> = acikGruplar.map { Kategoriler.acikAltlar(it) }
    val favoriler: Flow<Set<String>> = ctx.ds.data.map { it[K.FAVORI] ?: emptySet() }
    val gecmis: Flow<Set<String>> = ctx.ds.data.map { it[K.GECMIS] ?: emptySet() }
    val gunlukAdet: Flow<Int> = ctx.ds.data.map { it[K.GUNLUK] ?: 3 }
    val baslangicSaati: Flow<Int> = ctx.ds.data.map { it[K.BASLANGIC] ?: 10 }
    val bitisSaati: Flow<Int> = ctx.ds.data.map { it[K.BITIS] ?: 23 }
    val tema: Flow<TemaModu> = ctx.ds.data.map {
        runCatching { TemaModu.valueOf(it[K.TEMA] ?: "SISTEM") }.getOrDefault(TemaModu.SISTEM)
    }
    val dinamikRenk: Flow<Boolean> = ctx.ds.data.map { it[K.DINAMIK] ?: false }
    val haptikAcik: Flow<Boolean> = ctx.ds.data.map { it[K.HAPTIK] ?: true }
    val dil: Flow<String> = ctx.ds.data.map { it[K.DIL] ?: "tr" }
    val hatirlaticiAcik: Flow<Boolean> = ctx.ds.data.map { it[K.HATIRLATICI] ?: (it[K.ONBOARDING] ?: false) }
    val onboardingBitti: Flow<Boolean> = ctx.ds.data.map { it[K.ONBOARDING] ?: false }
    val seri: Flow<Int> = ctx.ds.data.map { it[K.SERI] ?: 0 }
    val rekor: Flow<Int> = ctx.ds.data.map { it[K.REKOR] ?: 0 }
    val gorulenToplam: Flow<Int> = ctx.ds.data.map { it[K.GORULEN] ?: 0 }
    val kutlananKilometre: Flow<Int> = ctx.ds.data.map { it[K.KUTLANAN] ?: 0 }
    val palet: Flow<Palet> = ctx.ds.data.map {
        runCatching { Palet.valueOf(it[K.PALET] ?: "KUM") }.getOrDefault(Palet.KUM)
    }

    /** Son 7 günün aktiflik durumu — pazartesiden bugüne. */
    val haftalikAktiflik: Flow<List<Boolean>> = ctx.ds.data.map { p ->
        val gunler = p[K.AKTIF_GUNLER] ?: emptySet()
        val bugun = LocalDate.now()
        (6 downTo 0).map { bugun.minusDays(it.toLong()).toString() in gunler }
    }

    /**
     * Bugün gelen sözler (rapor: ana ekran derinliği).
     * "tarih|kimlik" biçiminde saklanır; okurken bugüne göre süzülür,
     * böylece ayrı bir temizlik işine gerek kalmaz.
     */
    val bugunGelenler: Flow<List<String>> = ctx.ds.data.map { p ->
        val bugun = LocalDate.now().toString()
        (p[K.BUGUN_GELEN] ?: emptySet())
            .filter { it.startsWith("$bugun|") }
            .map { it.substringAfter("|") }
    }

    /**
     * Son 7 günün gelen sözleri, tarihe göre gruplanmış.
     * Haftalık şeritte bir güne dokununca o günün sözlerini göstermek için.
     */
    val gunlukGelenler: Flow<Map<String, List<String>>> = ctx.ds.data.map { p ->
        (p[K.BUGUN_GELEN] ?: emptySet())
            .mapNotNull { kayit ->
                val tarih = kayit.substringBefore("|")
                val kimlik = kayit.substringAfter("|")
                if (tarih.isBlank() || kimlik.isBlank()) null else tarih to kimlik
            }
            .groupBy({ it.first }, { it.second })
    }

    /** Bugün kaç söz görüldü — günün ilerleme halkası için. */
    val bugunGorulen: Flow<Int> = ctx.ds.data.map { p ->
        val kayit = p[K.BUGUN_GORULEN] ?: return@map 0
        val bugun = LocalDate.now().toString()
        if (kayit.substringBefore("|") == bugun) {
            kayit.substringAfter("|").toIntOrNull() ?: 0
        } else 0
    }

    /**
     * Sıradaki bildirimin saati (HH:mm), yoksa null.
     * Planlayici her planlama turunda saatleri buraya yazar; ekran
     * yalnızca gelecekte kalan ilkini okur.
     */
    val sonrakiBildirim: Flow<String?> = ctx.ds.data.map { p ->
        val simdi = java.time.LocalDateTime.now()
        (p[K.PLANLI_SAATLER] ?: emptySet())
            .mapNotNull { runCatching { java.time.LocalDateTime.parse(it) }.getOrNull() }
            .filter { it.isAfter(simdi) }
            .minOrNull()
            ?.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
    }

    /** Keşif ipucu bugün kapatıldı mı? */
    val ipucuKapatildi: Flow<Boolean> = ctx.ds.data.map {
        it[K.IPUCU_KAPATILDI] == LocalDate.now().toString()
    }

    suspend fun planliSaatleriYaz(saatler: List<java.time.LocalDateTime>) = ctx.ds.edit { p ->
        p[K.PLANLI_SAATLER] = saatler.map { it.toString() }.toSet()
    }

    suspend fun ipucunuKapat() = ctx.ds.edit {
        it[K.IPUCU_KAPATILDI] = LocalDate.now().toString()
    }

    suspend fun paletAyarla(pl: Palet) = ctx.ds.edit { it[K.PALET] = pl.name }

    /** Bildirimle gelen sözü günün listesine yazar. */
    suspend fun bugunGeldi(kimlik: String) = ctx.ds.edit { p ->
        val bugun = LocalDate.now()
        val esik = bugun.minusDays(6)
        // Son 7 günü sakla — haftalık şeride dokunma bu veriyi okuyor.
        val mevcut = (p[K.BUGUN_GELEN] ?: emptySet()).filter { kayit ->
            runCatching { LocalDate.parse(kayit.substringBefore("|")) >= esik }
                .getOrDefault(false)
        }
        p[K.BUGUN_GELEN] = (mevcut + "$bugun|$kimlik").toSet()
    }

    suspend fun kategoriSec(anahtar: String) = ctx.ds.edit { p ->
        val s = Kategoriler.gocur(p[K.SECILI] ?: Kategoriler.varsayilanSecili).toMutableSet()
        if (!s.add(anahtar)) s.remove(anahtar)
        // En az bir kategori kalmalı, yoksa bildirim havuzu boşalır.
        if (s.isNotEmpty()) p[K.SECILI] = s
    }

    /** Grup kilidini açar ve altlarını seçime ekler. */
    suspend fun grupAc(grupAnahtari: String) = ctx.ds.edit { p ->
        p[K.ACIK_GRUP] = (p[K.ACIK_GRUP] ?: emptySet()) + grupAnahtari
        val altlar = Kategoriler.grupBul(grupAnahtari)?.altlar?.map { it.anahtar } ?: emptyList()
        p[K.SECILI] = (p[K.SECILI] ?: emptySet()) + altlar
    }

    suspend fun favoriDegistir(kimlik: String) = ctx.ds.edit { p ->
        val f = (p[K.FAVORI] ?: emptySet()).toMutableSet()
        if (!f.add(kimlik)) f.remove(kimlik)
        p[K.FAVORI] = f
    }

    /** Gösterilen sözü geçmişe yazar; tavan aşılırsa geçmişi sıfırlar. */
    suspend fun gosterildi(kimlik: String) = ctx.ds.edit { p ->
        val tarih = LocalDate.now().toString()
        val okunan = (p[K.OKUNAN] ?: emptySet()).filter { it.startsWith("$tarih|") }.toSet()
        if ("$tarih|$kimlik" in okunan) return@edit
        p[K.OKUNAN] = okunan + "$tarih|$kimlik"
        val g = (p[K.GECMIS] ?: emptySet()).toMutableSet()
        g.add(kimlik)
        p[K.GECMIS] = if (g.size > gecmisTavani) setOf(kimlik) else g
        p[K.GORULEN] = (p[K.GORULEN] ?: 0) + 1

        // Günün ilerleme halkası için: tarih değiştiyse sıfırdan başla.
        val bugun = LocalDate.now().toString()
        val onceki = p[K.BUGUN_GORULEN]
        val sayi = if (onceki?.substringBefore("|") == bugun) {
            (onceki.substringAfter("|").toIntOrNull() ?: 0) + 1
        } else 1
        p[K.BUGUN_GORULEN] = "$bugun|$sayi"
    }

    suspend fun gunlukAdetAyarla(n: Int) = ctx.ds.edit { it[K.GUNLUK] = n.coerceIn(1, 7) }
    suspend fun saatAraligiAyarla(bas: Int, bit: Int) = ctx.ds.edit {
        it[K.BASLANGIC] = bas.coerceIn(0, 23); it[K.BITIS] = bit.coerceIn(bas.coerceIn(0, 23) + 1, 24)
    }
    suspend fun temaAyarla(t: TemaModu) = ctx.ds.edit { it[K.TEMA] = t.name }
    suspend fun dinamikRenkAyarla(a: Boolean) = ctx.ds.edit { it[K.DINAMIK] = a }
    suspend fun haptikAyarla(a: Boolean) = ctx.ds.edit { it[K.HAPTIK] = a }
    suspend fun dilAyarla(d: String) = ctx.ds.edit { it[K.DIL] = d }
    suspend fun onboardingKaydet(secili: Set<String>, adet: Int, bas: Int, bit: Int, hatirlat: Boolean) = ctx.ds.edit {
        it[K.SECILI] = Baslangic.dogrula(secili)
        it[K.GUNLUK] = adet.coerceIn(1, 7)
        it[K.BASLANGIC] = bas.coerceIn(0, 23)
        it[K.BITIS] = bit.coerceIn(bas.coerceIn(0, 23) + 1, 24)
        it[K.HATIRLATICI] = hatirlat
        it[K.ONBOARDING] = true
    }
    suspend fun hatirlaticiAyarla(acik: Boolean) = ctx.ds.edit { it[K.HATIRLATICI] = acik }
    suspend fun bildirimGecmisineEkle(kimlik: String) = ctx.ds.edit {
        it[K.GECMIS] = ((it[K.GECMIS] ?: emptySet()) + kimlik).toList().takeLast(gecmisTavani).toSet()
    }
    suspend fun kategorileriAyarla(s: Set<String>) = ctx.ds.edit {
        if (s.isNotEmpty()) it[K.SECILI] = s
    }
    suspend fun kilometreKutlandi(gun: Int) = ctx.ds.edit { it[K.KUTLANAN] = gun }
    suspend fun degerlendirmeSoruldu() = ctx.ds.edit { it[K.DEGERLENDIRME] = true }
    suspend fun degerlendirmeSorulduMu() = ctx.ds.data.first()[K.DEGERLENDIRME] ?: false

    /** Gün başına bir kez; seriyi ilerletir veya sıfırlar. */
    suspend fun seriyiTazele() = ctx.ds.edit { p ->
        val bugun = LocalDate.now()
        val son = p[K.SON_GUN]?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val yeni = when {
            son == null -> 1
            son == bugun -> p[K.SERI] ?: 1
            son.plusDays(1) == bugun -> (p[K.SERI] ?: 0) + 1
            else -> 1
        }
        p[K.SERI] = yeni
        p[K.REKOR] = maxOf(p[K.REKOR] ?: 0, yeni)

        p[K.SON_GUN] = bugun.toString()

        // Haftalık şerit için: son 7 günden eskisini at, bugünü ekle.
        val esik = bugun.minusDays(6)
        val gunler = (p[K.AKTIF_GUNLER] ?: emptySet())
            .filter { g -> runCatching { LocalDate.parse(g) >= esik }.getOrDefault(false) }
        p[K.AKTIF_GUNLER] = (gunler + bugun.toString()).toSet()
    }

}
