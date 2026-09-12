package com.yalnizfahrettin.azim.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yalnizfahrettin.azim.core.Palet
import com.yalnizfahrettin.azim.core.TemaModu
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.ds by preferencesDataStore("azim")

/** Tek veri kapısı. Ekranlar DataStore'u doğrudan görmez. */
class Depo(ctx: Context, private val store: DataStore<Preferences> = ctx.ds) {

    private object K {
        val SECILI = stringSetPreferencesKey("secili_kategoriler")
        val ACIK_GRUP = stringSetPreferencesKey("acik_gruplar")
        val ACIK_KATEGORI = stringSetPreferencesKey("kazanilan_kategoriler")
        val ERISIM_SURUMU = intPreferencesKey("erisim_surumu")
        val PRO_DEMO = booleanPreferencesKey("pro_demo_acik")
        val ARKA_PLAN = stringPreferencesKey("ana_arka_plan")
        val FAVORI = stringSetPreferencesKey("favoriler")
        val RECENT = stringPreferencesKey("recent_quote_order")
        val HIDDEN = stringSetPreferencesKey("hidden_quotes")
        val PAUSED_UNTIL = longPreferencesKey("reminders_paused_until")
        val GECMIS = stringSetPreferencesKey("gosterilen_gecmis")
        val SON_BILDIRIM = stringPreferencesKey("son_bildirim_kimlik")
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
        val PERSONAL_PROFILE = stringPreferencesKey("personal_profile_v1")
        val ONBOARDING_DRAFT = stringPreferencesKey("onboarding_draft_v1")
        val VISUAL_V7 = booleanPreferencesKey("visual_v7_applied")
        val VISUAL_V8 = booleanPreferencesKey("visual_v8_applied")
    }

    /** Every entitlement read begins after the atomic, idempotent migration. */
    private val erisimVerisi: Flow<Preferences> = flow {
        store.edit { erisimiGocur(it); gorseliGocur(it) }
        emitAll(store.data)
    }

    val secili: Flow<Set<String>> = erisimVerisi.map { p ->
        Erisim.guvenliSecim(p[K.SECILI] ?: Kategoriler.varsayilanSecili, etkinErisim(p))
    }

    /** Effective category access, including the explicitly enabled Pro demo. */
    val acik: Flow<Set<String>> = erisimVerisi.map(::etkinErisim)
    val proDemo: Flow<Boolean> = erisimVerisi.map { it[K.PRO_DEMO] ?: false }
    val personalProfile: Flow<PersonalProfile?> = store.data.map { p -> p[K.PERSONAL_PROFILE]?.let(PersonalProfile::decode) }
    val onboardingDraft: Flow<PersonalProfile> = store.data.map { PersonalProfile.decode(it[K.ONBOARDING_DRAFT]) }

    suspend fun saveOnboardingDraft(profile: PersonalProfile) = store.edit { it[K.ONBOARDING_DRAFT] = profile.encode() }

    /** One atomic commit: answers change recommendations, never paid access. */
    suspend fun completePersonalPlan(profile: PersonalProfile, reminders: Boolean, preserveTopics: Boolean = false) = store.edit { p ->
        erisimiGocur(p)
        gorseliGocur(p)
        val safe = PersonalProfile.decode(profile.encode()).copy(step = 0)
        val chosenTheme = AnaTemalar.allowed(safe.answer("theme").firstOrNull(), p[K.PRO_DEMO] ?: false)
        p[K.ARKA_PLAN] = chosenTheme.id
        p[K.TEMA] = if (chosenTheme.dark) TemaModu.KARANLIK.name else TemaModu.AYDINLIK.name
        p[K.DIL] = safe.answer("language").firstOrNull()?.takeIf { it in setOf("tr", "en") } ?: "tr"
        p[K.PERSONAL_PROFILE] = safe.encode()
        p[K.SECILI] = if (preserveTopics) p[K.SECILI].orEmpty().intersect(etkinErisim(p)).ifEmpty { PersonalPlan.initialCategories(safe, etkinErisim(p)) }
            else PersonalPlan.initialCategories(safe, etkinErisim(p))
        p[K.GUNLUK] = safe.dailyCount
        p[K.BASLANGIC] = safe.startHour
        p[K.BITIS] = safe.endHour
        p[K.HATIRLATICI] = reminders
        p[K.ONBOARDING] = true
        p.remove(K.ONBOARDING_DRAFT)
    }

    /** Update only content controls; never reset topics, rhythm or notification consent. */
    suspend fun contentPreferences(answers: Map<String, Set<String>>) = store.edit { p ->
        val current = PersonalProfile.decode(p[K.PERSONAL_PROFILE])
        val keys = setOf("avoid", "spirituality", "format", "discovery")
        p[K.PERSONAL_PROFILE] = current.copy(answers = current.answers + answers.filterKeys { it in keys }).encode()
    }

    suspend fun reminderRhythm(count: Int, start: Int, end: Int, enabled: Boolean) = store.edit { p ->
        val safe = PersonalProfile.decode(PersonalProfile(dailyCount = count, startHour = start, endHour = end).encode())
        p[K.GUNLUK] = safe.dailyCount
        p[K.BASLANGIC] = safe.startHour
        p[K.BITIS] = safe.endHour
        p[K.HATIRLATICI] = enabled
        p[K.PERSONAL_PROFILE]?.let { raw ->
            p[K.PERSONAL_PROFILE] = PersonalProfile.decode(raw).copy(dailyCount = safe.dailyCount,
                startHour = safe.startHour, endHour = safe.endHour).encode()
        }
    }

    private fun gorseliGocur(p: MutablePreferences) {
        if (p[K.VISUAL_V8] == true) return
        val previous = p[K.PALET]?.let { runCatching { Palet.valueOf(it) }.getOrNull() }
        // New paper default; explicit monochrome/wine and night choices survive.
        p[K.PALET] = when (previous) {
            Palet.MONO, Palet.BORDO -> previous.name
            else -> Palet.MERMER.name
        }
        if (p[K.TEMA] == null) p[K.TEMA] = TemaModu.AYDINLIK.name
        p[K.DINAMIK] = false
        p.remove(K.ARKA_PLAN)
        p[K.VISUAL_V7] = true
        p[K.VISUAL_V8] = true
    }
    val arkaPlan: Flow<String?> = erisimVerisi.map { AnaTemalar.allowed(it[K.ARKA_PLAN], it[K.PRO_DEMO] ?: false).id }

    suspend fun arkaPlanAyarla(ad: String?) = store.edit { p ->
        val theme = AnaTemalar.allowed(ad, p[K.PRO_DEMO] ?: false)
        p[K.ARKA_PLAN] = theme.id
        p[K.TEMA] = if (theme.dark) TemaModu.KARANLIK.name else TemaModu.AYDINLIK.name
    }

    /** Compatibility view only: a group is open when every category in it is open. */
    val acikGruplar: Flow<Set<String>> = acik.map { kategoriler ->
        Kategoriler.gruplar.filter { grup -> grup.altlar.all { it.anahtar in kategoriler } }
            .map { it.anahtar }.toSet()
    }

    private fun etkinErisim(p: Preferences): Set<String> =
        Erisim.acikKategoriler(p[K.ACIK_KATEGORI] ?: emptySet(), p[K.PRO_DEMO] ?: false)

    private fun erisimiGocur(p: MutablePreferences) {
        if ((p[K.ERISIM_SURUMU] ?: 0) >= Erisim.SURUM) return
        val eskiSecim = p[K.SECILI] ?: Kategoriler.varsayilanSecili
        // Persist before onboarding is first completed, so a new v6 install is never
        // mistaken for an existing v5 user by a later collector or process restart.
        p[K.ACIK_KATEGORI] = if (p[K.ONBOARDING] == true) {
            Erisim.eskiKazanilanlar(p[K.ACIK_GRUP] ?: emptySet(), eskiSecim)
        } else emptySet()
        p[K.PRO_DEMO] = false
        p[K.SECILI] = Erisim.guvenliSecim(eskiSecim, etkinErisim(p))
        p[K.ERISIM_SURUMU] = Erisim.SURUM
    }
    val favoriler: Flow<Set<String>> = store.data.map { it[K.FAVORI] ?: emptySet() }
    val sonBildirimKimlik: Flow<String?> = store.data.map { it[K.SON_BILDIRIM] }
    val recentQuotes = store.data.map { p -> p[K.RECENT].orEmpty().split('|').filter(Sozler::aktifKimlikMi).take(QuietFeed.RECENT_LIMIT) }
    val hiddenQuotes = store.data.map { it[K.HIDDEN].orEmpty() }
    val pausedUntil = store.data.map { it[K.PAUSED_UNTIL] ?: 0L }
    suspend fun hideQuote(id: String, hidden: Boolean) = store.edit { p ->
        p[K.HIDDEN] = if(hidden) p[K.HIDDEN].orEmpty() + id else p[K.HIDDEN].orEmpty() - id
    }
    suspend fun restoreHiddenQuotes() = store.edit { it.remove(K.HIDDEN) }
    suspend fun pauseReminders(until: Long) = store.edit { it[K.PAUSED_UNTIL] = until }

    val gecmis: Flow<Set<String>> = store.data.map { (it[K.GECMIS] ?: emptySet()).filter(Sozler::aktifKimlikMi).toSet() }
    val gunlukAdet: Flow<Int> = store.data.map { it[K.GUNLUK] ?: 3 }
    val baslangicSaati: Flow<Int> = store.data.map { it[K.BASLANGIC] ?: 10 }
    val bitisSaati: Flow<Int> = store.data.map { it[K.BITIS] ?: 23 }
    val tema: Flow<TemaModu> = erisimVerisi.map {
        runCatching { TemaModu.valueOf(it[K.TEMA] ?: "AYDINLIK") }.getOrDefault(TemaModu.AYDINLIK)
    }
    val dinamikRenk: Flow<Boolean> = store.data.map { it[K.DINAMIK] ?: false }
    val haptikAcik: Flow<Boolean> = store.data.map { it[K.HAPTIK] ?: true }
    val dil: Flow<String> = store.data.map { it[K.DIL] ?: "tr" }
    val hatirlaticiAcik: Flow<Boolean> = store.data.map { it[K.HATIRLATICI] ?: (it[K.ONBOARDING] ?: false) }
    val onboardingBitti: Flow<Boolean> = store.data.map { it[K.ONBOARDING] ?: false }
    val seri: Flow<Int> = store.data.map { it[K.SERI] ?: 0 }
    val rekor: Flow<Int> = store.data.map { it[K.REKOR] ?: 0 }
    val gorulenToplam: Flow<Int> = store.data.map { it[K.GORULEN] ?: 0 }
    val kutlananKilometre: Flow<Int> = store.data.map { it[K.KUTLANAN] ?: 0 }
    val palet: Flow<Palet> = erisimVerisi.map {
        runCatching { Palet.valueOf(it[K.PALET] ?: "MERMER") }.getOrDefault(Palet.MERMER)
    }

    /** Son 7 günün aktiflik durumu — pazartesiden bugüne. */
    val haftalikAktiflik: Flow<List<Boolean>> = store.data.map { p ->
        val gunler = p[K.AKTIF_GUNLER] ?: emptySet()
        val bugun = LocalDate.now()
        (6 downTo 0).map { bugun.minusDays(it.toLong()).toString() in gunler }
    }

    /**
     * Bugün gelen sözler (rapor: ana ekran derinliği).
     * "tarih|kimlik" biçiminde saklanır; okurken bugüne göre süzülür,
     * böylece ayrı bir temizlik işine gerek kalmaz.
     */
    val bugunGelenler: Flow<List<String>> = store.data.map { p ->
        val bugun = LocalDate.now().toString()
        (p[K.BUGUN_GELEN] ?: emptySet())
            .filter { it.startsWith("$bugun|") }
            .map { it.substringAfter("|") }
    }

    /**
     * Son 7 günün gelen sözleri, tarihe göre gruplanmış.
     * Haftalık şeritte bir güne dokununca o günün sözlerini göstermek için.
     */
    val gunlukGelenler: Flow<Map<String, List<String>>> = store.data.map { p ->
        (p[K.BUGUN_GELEN] ?: emptySet())
            .mapNotNull { kayit ->
                val tarih = kayit.substringBefore("|")
                val kimlik = kayit.substringAfter("|")
                if (tarih.isBlank() || kimlik.isBlank()) null else tarih to kimlik
            }
            .groupBy({ it.first }, { it.second })
    }

    /** Bugün kaç söz görüldü — günün ilerleme halkası için. */
    val bugunGorulen: Flow<Int> = store.data.map { p ->
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
    val sonrakiBildirim: Flow<String?> = store.data.map { p ->
        val simdi = java.time.LocalDateTime.now()
        (p[K.PLANLI_SAATLER] ?: emptySet())
            .mapNotNull { runCatching { java.time.LocalDateTime.parse(it) }.getOrNull() }
            .filter { it.isAfter(simdi) }
            .minOrNull()
            ?.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
    }

    /** Keşif ipucu bugün kapatıldı mı? */
    val ipucuKapatildi: Flow<Boolean> = store.data.map {
        it[K.IPUCU_KAPATILDI] == LocalDate.now().toString()
    }

    suspend fun planliSaatleriYaz(saatler: List<java.time.LocalDateTime>) = store.edit { p ->
        p[K.PLANLI_SAATLER] = saatler.map { it.toString() }.toSet()
    }

    suspend fun ipucunuKapat() = store.edit {
        it[K.IPUCU_KAPATILDI] = LocalDate.now().toString()
    }

    suspend fun paletAyarla(pl: Palet) = store.edit { it[K.PALET] = pl.name }

    /** Bildirimle gelen sözü günün listesine yazar. */
    suspend fun bugunGeldi(kimlik: String) = store.edit { p ->
        val bugun = LocalDate.now()
        val esik = bugun.minusDays(6)
        // Son 7 günü sakla — haftalık şeride dokunma bu veriyi okuyor.
        val mevcut = (p[K.BUGUN_GELEN] ?: emptySet()).filter { kayit ->
            runCatching { LocalDate.parse(kayit.substringBefore("|")) >= esik }
                .getOrDefault(false)
        }
        p[K.BUGUN_GELEN] = (mevcut + "$bugun|$kimlik").toSet()
    }

    suspend fun kategoriSec(anahtar: String) = store.edit { p ->
        erisimiGocur(p)
        val acik = etkinErisim(p)
        if (anahtar !in acik) return@edit
        val s = Erisim.guvenliSecim(p[K.SECILI] ?: emptySet(), acik).toMutableSet()
        if (!s.add(anahtar)) s.remove(anahtar)
        // En az bir kategori kalmalı, yoksa bildirim havuzu boşalır.
        if (s.isNotEmpty()) p[K.SECILI] = s
    }

    /** Access can be granted without changing the notification selection. */
    suspend fun kategoriAc(anahtar: String, bildirimlereEkle: Boolean = true) = store.edit { p ->
        erisimiGocur(p)
        if (Kategoriler.bul(anahtar) == null) return@edit
        p[K.ACIK_KATEGORI] = (p[K.ACIK_KATEGORI] ?: emptySet()) + anahtar
        if (bildirimlereEkle) p[K.SECILI] = Erisim.guvenliSecim((p[K.SECILI] ?: emptySet()) + anahtar, etkinErisim(p))
    }

    /** Demo only: no purchase, payment or subscription state is represented here. */
    suspend fun proDemoAyarla(acik: Boolean) = store.edit { p ->
        erisimiGocur(p)
        p[K.PRO_DEMO] = acik
        val theme = AnaTemalar.allowed(p[K.ARKA_PLAN], acik)
        p[K.ARKA_PLAN] = theme.id
        p[K.TEMA] = if (theme.dark) TemaModu.KARANLIK.name else TemaModu.AYDINLIK.name
        // Enabling never subscribes the user to additional notification topics.
        // Revoking preserves individual rewards and grandfathered v5 access.
        p[K.SECILI] = Erisim.guvenliSecim(p[K.SECILI] ?: emptySet(), etkinErisim(p))
    }

    suspend fun favoriDegistir(kimlik: String) = store.edit { p ->
        val f = (p[K.FAVORI] ?: emptySet()).toMutableSet()
        if (!f.add(kimlik)) f.remove(kimlik)
        p[K.FAVORI] = f
    }

    /** Cycle history and daily read counts have separate lifetimes. */
    suspend fun gosterildi(kimlik: String) = store.edit { p ->
        if (Sozler.aktifKimlikMi(kimlik)) p[K.RECENT] = QuietFeed.remember(p[K.RECENT].orEmpty().split('|').filter(Sozler::aktifKimlikMi), kimlik).joinToString("|")
        val g = (p[K.GECMIS] ?: emptySet()).filter(Sozler::aktifKimlikMi).toMutableSet()
        if (Sozler.aktifKimlikMi(kimlik)) g.add(kimlik)
        p[K.GECMIS] = g
        val tarih = LocalDate.now().toString()
        val okunan = (p[K.OKUNAN] ?: emptySet()).filter { it.startsWith("$tarih|") }.toSet()
        if ("$tarih|$kimlik" in okunan) return@edit
        p[K.OKUNAN] = okunan + "$tarih|$kimlik"
        p[K.GORULEN] = (p[K.GORULEN] ?: 0) + 1

        // Günün ilerleme halkası için: tarih değiştiyse sıfırdan başla.
        val bugun = LocalDate.now().toString()
        val onceki = p[K.BUGUN_GORULEN]
        val sayi = if (onceki?.substringBefore("|") == bugun) {
            (onceki.substringAfter("|").toIntOrNull() ?: 0) + 1
        } else 1
        p[K.BUGUN_GORULEN] = "$bugun|$sayi"
    }

    suspend fun gunlukAdetAyarla(n: Int) = store.edit { it[K.GUNLUK] = n.coerceIn(1, 7) }
    suspend fun saatAraligiAyarla(bas: Int, bit: Int) = store.edit {
        it[K.BASLANGIC] = bas.coerceIn(0, 23); it[K.BITIS] = bit.coerceIn(bas.coerceIn(0, 23) + 1, 24)
    }
    suspend fun temaAyarla(t: TemaModu) = store.edit {
        it[K.TEMA] = t.name
        it[K.ARKA_PLAN] = if (t == TemaModu.KARANLIK || t == TemaModu.OLED) "black" else "white"
    }
    suspend fun dinamikRenkAyarla(a: Boolean) = store.edit { it[K.DINAMIK] = a }
    suspend fun haptikAyarla(a: Boolean) = store.edit { it[K.HAPTIK] = a }
    suspend fun dilAyarla(d: String) = store.edit { it[K.DIL] = d }
    suspend fun onboardingKaydet(secili: Set<String>, adet: Int, bas: Int, bit: Int, hatirlat: Boolean) = store.edit {
        erisimiGocur(it)
        it[K.SECILI] = Baslangic.dogrula(secili)
        it[K.GUNLUK] = adet.coerceIn(1, 7)
        it[K.BASLANGIC] = bas.coerceIn(0, 23)
        it[K.BITIS] = bit.coerceIn(bas.coerceIn(0, 23) + 1, 24)
        it[K.HATIRLATICI] = hatirlat
        it[K.ONBOARDING] = true
    }
    suspend fun hatirlaticiAyarla(acik: Boolean) = store.edit { it[K.HATIRLATICI] = acik }
    suspend fun bildirimGecmisineEkle(kimlik: String, yeniTurKategorileri: Set<String> = emptySet()) = store.edit {
        val onceki = (it[K.GECMIS] ?: emptySet()).filter(Sozler::aktifKimlikMi)
            .filterNot { id -> Sozler.kimlikten(id)?.kategori in yeniTurKategorileri }
        it[K.GECMIS] = (onceki + kimlik).filter(Sozler::aktifKimlikMi).toSet()
        it[K.SON_BILDIRIM] = kimlik
    }
    suspend fun kategorileriAyarla(s: Set<String>) = store.edit {
        erisimiGocur(it)
        val izinli = Kategoriler.gocur(s).intersect(etkinErisim(it))
        if (izinli.isNotEmpty()) it[K.SECILI] = izinli
    }
    suspend fun kilometreKutlandi(gun: Int) = store.edit { it[K.KUTLANAN] = gun }
    suspend fun degerlendirmeSoruldu() = store.edit { it[K.DEGERLENDIRME] = true }
    suspend fun degerlendirmeSorulduMu() = store.data.first()[K.DEGERLENDIRME] ?: false

    /** Gün başına bir kez; seriyi ilerletir veya sıfırlar. */
    suspend fun seriyiTazele() = store.edit { p ->
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
