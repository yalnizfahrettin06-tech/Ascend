package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import kotlinx.coroutines.flow.map
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.TemaModu
import com.yalnizfahrettin.azim.data.Depo
import com.yalnizfahrettin.azim.data.Soz
import com.yalnizfahrettin.azim.data.Sozler
import com.yalnizfahrettin.azim.data.PersonalProfile
import com.yalnizfahrettin.azim.data.PersonalPlan
import com.yalnizfahrettin.azim.data.MomentFeed
import com.yalnizfahrettin.azim.notif.Planlayici
import com.yalnizfahrettin.azim.paylas.PaylasimKarti
import com.yalnizfahrettin.azim.widget.AzimWidget
import kotlinx.coroutines.launch

/** Kök bileşen: sekme durumu, veri akışları ve eylemler burada bağlanır. */
@Composable
fun Uygulama(
    depo: Depo,
    reklam: ReklamKapisi,
    acilistakiKimlik: String? = null,
    acilisSekmesi: Sekme? = null,
    izinIste: () -> Unit,
    bildirimIzni: Boolean,
) {
    val ctx = LocalContext.current
    val kapsam = rememberCoroutineScope()

    val onboardingBitti by remember(depo) { depo.onboardingBitti.map { it as Boolean? } }.collectAsStateWithLifecycle(null)
    val secili by depo.secili.collectAsStateWithLifecycle(emptySet())
    val profil by depo.personalProfile.collectAsStateWithLifecycle(null)
    val taslak by remember(depo) { depo.onboardingDraft.map { it as PersonalProfile? } }.collectAsStateWithLifecycle(null)
    val acik by depo.acik.collectAsStateWithLifecycle(emptySet())
    val acikGruplar by depo.acikGruplar.collectAsStateWithLifecycle(emptySet())
    val arkaPlan by depo.arkaPlan.collectAsStateWithLifecycle(null)
    val proDemo by remember(depo) { depo.proDemo.map { it as Boolean? } }.collectAsStateWithLifecycle(null)
    val favoriler by depo.favoriler.collectAsStateWithLifecycle(emptySet())
    val gecmis by depo.gecmis.collectAsStateWithLifecycle(emptySet())
    val seri by depo.seri.collectAsStateWithLifecycle(0)
    val rekor by depo.rekor.collectAsStateWithLifecycle(0)
    val gorulen by depo.gorulenToplam.collectAsStateWithLifecycle(0)
    val kutlanan by depo.kutlananKilometre.collectAsStateWithLifecycle(0)
    val dil by depo.dil.collectAsStateWithLifecycle("tr")
    val hatirlaticiAcik by depo.hatirlaticiAcik.collectAsStateWithLifecycle(false)
    val gunlukAdet by depo.gunlukAdet.collectAsStateWithLifecycle(3)
    val bas by depo.baslangicSaati.collectAsStateWithLifecycle(9)
    val bit by depo.bitisSaati.collectAsStateWithLifecycle(22)
    val tema by depo.tema.collectAsStateWithLifecycle(TemaModu.AYDINLIK)
    val dinamik by depo.dinamikRenk.collectAsStateWithLifecycle(false)
    val haptik by depo.haptikAcik.collectAsStateWithLifecycle(true)
    val palet by depo.palet.collectAsStateWithLifecycle(com.yalnizfahrettin.azim.core.Palet.MERMER)
    val haftalik by depo.haftalikAktiflik.collectAsStateWithLifecycle(List(7) { false })
    val bugunGelenler by depo.bugunGelenler.collectAsStateWithLifecycle(emptyList())
    val gunlukGelenler by depo.gunlukGelenler.collectAsStateWithLifecycle(emptyMap())
    val bugunGorulen by depo.bugunGorulen.collectAsStateWithLifecycle(0)
    val sonrakiBildirim by depo.sonrakiBildirim.collectAsStateWithLifecycle(null)
    val ipucuKapali by depo.ipucuKapatildi.collectAsStateWithLifecycle(true)

    // Keşif ipucu: günde bir kilitli kategori öner. Kategoriler sekmesine
    // hiç girmeyen kullanıcı kilitlerin varlığından böyle haberdar oluyor.
    val oneri = remember(acikGruplar, ipucuKapali, favoriler, secili, gunlukAdet) {
        if (ipucuKapali) null
        else KesifMotoru.oner(secili, acikGruplar, favoriler, gunlukAdet)
    }

    var sekme by rememberSaveable { mutableStateOf(acilisSekmesi ?: Sekme.ANA) }
    var ayarlardaMi by rememberSaveable { mutableStateOf(false) }
    var planGoster by rememberSaveable { mutableStateOf(false) }
    var planDuzenle by rememberSaveable { mutableStateOf(false) }
    var duzenlemeBaslangici by remember { mutableStateOf<PersonalProfile?>(null) }
    var ihtiyac by rememberSaveable { mutableStateOf<String?>(null) }
    var kilitKategori by remember { mutableStateOf<com.yalnizfahrettin.azim.data.Kategori?>(null) }
    var proGoster by rememberSaveable { mutableStateOf(false) }
    var proKaydediliyor by remember { mutableStateOf(false) }
    var proHata by remember { mutableStateOf<String?>(null) }
    var acilacakGrup by rememberSaveable { mutableStateOf<String?>(null) }
    var paylasilanKimlik by rememberSaveable { mutableStateOf<String?>(null) }
    val paylasilanSoz = paylasilanKimlik?.let { Sozler.kimlikten(it) }
    // Akış: pager'ın gezineceği söz listesi
    var akis by remember { mutableStateOf<List<Soz>>(emptyList()) }
    var indeks by remember { mutableIntStateOf(0) }
    var kutlamaGunu by remember { mutableStateOf<Int?>(null) }
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(dil) { snackbar.currentSnackbarData?.dismiss() }

    var kaydediliyor by remember { mutableStateOf(false) }
    var kayitHatasi by remember { mutableStateOf<String?>(null) }
    val hataMetni = stringResource(R.string.asc_kayit_hata)
    BackHandler(ayarlardaMi || paylasilanSoz != null || sekme != Sekme.ANA) {
        when { paylasilanSoz != null -> paylasilanKimlik = null; ayarlardaMi -> ayarlardaMi = false; sekme == Sekme.FAVORI -> sekme = Sekme.ISTATISTIK; else -> sekme = Sekme.ANA }
    }
    LaunchedEffect(acilisSekmesi) { acilisSekmesi?.let { sekme = it } }
    if (onboardingBitti == null || proDemo == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    // Drafts resume locally; completing setup commits answers and the reminder choice atomically.
    if (onboardingBitti == false || planDuzenle) {
        if (taslak == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return
        }
        Onboarding(dil = dil, kaydediliyor = kaydediliyor, hata = kayitHatasi,
            bildirimIzni = bildirimIzni, izinIste = izinIste,
            initialDraft = if (planDuzenle) duzenlemeBaslangici ?: taslak!! else taslak!!,
            editing = planDuzenle, previewAccess = acik,
            onCancel = { planDuzenle = false; duzenlemeBaslangici = null; kayitHatasi = null },
            draftChanged = { yeni ->
                kapsam.launch {
                    try { depo.saveOnboardingDraft(yeni) }
                    catch (_: java.io.IOException) { kayitHatasi = hataMetni }
                }
            },
            finishProfile = { yeni, hatirlat ->
                if (!kaydediliyor) {
                    kaydediliyor = true; kayitHatasi = null
                    kapsam.launch {
                        try {
                            depo.completePersonalPlan(yeni, hatirlat, preserveTopics = planDuzenle)
                            planDuzenle = false; duzenlemeBaslangici = null; ihtiyac = null
                            Planlayici.yenidenKur(ctx)
                            AzimWidget.tazele(ctx)
                        } catch (_: java.io.IOException) { kayitHatasi = hataMetni }
                        finally { kaydediliyor = false }
                    }
                }
            },
            bitir = { secilenler, adet, b, bt, hatirlat ->
                if (!kaydediliyor) {
                    kaydediliyor = true
                    kayitHatasi = null
                    kapsam.launch {
                        try {
                            depo.onboardingKaydet(secilenler, adet, b, bt, hatirlat)
                            Planlayici.yenidenKur(ctx)
                            if (hatirlat) izinIste()
                        } catch (e: java.io.IOException) {
                            kayitHatasi = hataMetni
                        } finally { kaydediliyor = false }
                    }
                }
            })
        return
    }

    // Akışı bir kez kur: seçili kategorilerden karıştırılmış liste.
    LaunchedEffect(acik, onboardingBitti) {
        if (onboardingBitti == true) {
            val yeni = Sozler.tumu().filter { it.kategori in acik }.shuffled()
            akis = yeni
            indeks = 0
        }
        depo.seriyiTazele()
    }

    // Consume this launch request once; changing entitlements must not replay it.
    var acilisTuketildi by rememberSaveable(acilistakiKimlik) { mutableStateOf(false) }
    LaunchedEffect(acilistakiKimlik, acik, favoriler, gunlukGelenler) {
        if (acilisTuketildi || acik.isEmpty()) return@LaunchedEffect
        val hedef = acilistakiKimlik?.let { Sozler.kimlikten(it) } ?: return@LaunchedEffect
        val arsiv = hedef.kimlik in favoriler || gunlukGelenler.values.any { hedef.kimlik in it }
        if (hedef.kategori !in acik && !arsiv) return@LaunchedEffect
        acilisTuketildi = true
        val yer = akis.indexOfFirst { it.kimlik == hedef.kimlik }
        if (yer >= 0) indeks = yer else {
            akis = listOf(hedef) + akis
            indeks = 0
        }
        sekme = Sekme.ANA
    }

    // Kilometre taşı kontrolü (rapor 4.1)
    LaunchedEffect(seri, kutlanan) {
        Kilometre.yeniEsik(seri, kutlanan)?.let { kutlamaGunu = it }
    }

    if (ayarlardaMi) {
        AyarlarEkrani(
            tema = tema, dil = dil, gunlukAdet = gunlukAdet, baslangic = bas, bitis = bit,
            haptik = haptik, dinamikRenk = dinamik, palet = palet,
            hatirlaticiAcik = hatirlaticiAcik, bildirimIzni = bildirimIzni,
            hatirlaticiSec = { ac -> kapsam.launch { depo.hatirlaticiAyarla(ac); Planlayici.yenidenKur(ctx); if (ac) izinIste() } },
            seciliKategoriSayisi = secili.size,
            temaSec = { kapsam.launch { depo.temaAyarla(it) } },
            dilSec = { kapsam.launch { depo.dilAyarla(it); AzimWidget.tazele(ctx) } },
            adetSec = { kapsam.launch { depo.gunlukAdetAyarla(it); Planlayici.yenidenKur(ctx) } },
            saatSec = { b, s -> kapsam.launch { depo.saatAraligiAyarla(b, s); Planlayici.yenidenKur(ctx) } },
            haptikSec = { kapsam.launch { depo.haptikAyarla(it) } },
            dinamikSec = { kapsam.launch { depo.dinamikRenkAyarla(it) } },
            paletSec = { kapsam.launch { depo.paletAyarla(it) } },
            geri = { ayarlardaMi = false },
        )
        return
    }

    val tabState = androidx.compose.runtime.saveable.rememberSaveableStateHolder()
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            // Sekme geçişleri artık yönlü kayma + soluklaşma (rapor 2.3)
            AnimatedContent(
                targetState = sekme,
                transitionSpec = {
                    val ileri = targetState.ordinal > initialState.ordinal
                    val yon = if (ileri) 1 else -1
                    (slideInHorizontally(tween(240)) { yon * it / 8 } + fadeIn(tween(240)))
                        .togetherWith(
                            slideOutHorizontally(tween(240)) { -yon * it / 8 } + fadeOut(tween(160))
                        )
                },
                label = "sekme",
            ) { s ->
                tabState.SaveableStateProvider(s.rota) {
                when (s) {
                    Sekme.ANA -> AnaEkran(
                        kullaniciAdi = profil?.name.orEmpty(), planAc = { planGoster = true },
                        ihtiyac = ihtiyac, ihtiyacSec = { ihtiyac = it },
                        secilenAtmosfer = arkaPlan, atmosferSec = { ad -> kapsam.launch { depo.arkaPlanAyarla(ad) } },
                        sozler = akis,
                        aktifIndeks = indeks,
                        favoriler = favoriler,
                        seri = seri,
                        haftalik = haftalik,
                        bugunGelenler = bugunGelenler.mapNotNull { Sozler.kimlikten(it) },
                        gunlukGelenler = gunlukGelenler.mapValues { (_, v) ->
                            v.mapNotNull { Sozler.kimlikten(it) }
                        },
                        bugunGorulen = bugunGorulen,
                        gunlukHedef = gunlukAdet,
                        sonrakiBildirim = sonrakiBildirim,
                        hatirlaticiAcik = hatirlaticiAcik, bildirimIzni = bildirimIzni,
                        seciliKonular = secili, haptikAcik = haptik, konulariDuzenle = { acilacakGrup = null; sekme = Sekme.KATEGORI },
                        oneri = oneri,
                        bugunPlanlanan = gunlukAdet,
                        dil = dil,
                        indeksDegisti = { yeni ->
                            indeks = yeni
                            akis.getOrNull(yeni)?.let { s2 ->
                                kapsam.launch {
                                    depo.gosterildi(s2.kimlik)
                                    AzimWidget.tazele(ctx)
                                }
                            }
                        },
                        favoriDegistir = { s2 -> kapsam.launch { depo.favoriDegistir(s2.kimlik) } },
                        paylas = { s2 -> paylasilanKimlik = s2.kimlik },
                        sozSecildi = { s2 ->
                            val yer = akis.indexOfFirst { it.kimlik == s2.kimlik }
                            if (yer >= 0) indeks = yer else {
                                akis = listOf(s2) + akis; indeks = 0
                            }
                        },
                        kesfeGit = { grup ->
                            sekme = Sekme.KATEGORI
                            acilacakGrup = grup.anahtar
                            // Families only filter discovery; categories unlock individually.
                        },
                        ipucunuKapat = { kapsam.launch { depo.ipucunuKapat() } },
                        ayarlaraGit = { ayarlardaMi = true },
                    )

                    Sekme.KATEGORI -> KategorilerEkrani(
                        secili = secili, acik = acik, dil = dil, pro = proDemo == true, proAc = { proGoster = true },
                        sec = { kapsam.launch { depo.kategoriSec(it); Planlayici.yenidenKur(ctx); AzimWidget.tazele(ctx) } },
                        kilidiAc = { kilitKategori = it },
                        acilacakGrup = acilacakGrup, bildirimAcik = hatirlaticiAcik && bildirimIzni,
                    )

                    Sekme.FAVORI -> FavorilerEkrani(
                        onBack = { sekme = Sekme.ISTATISTIK },
                        favoriler = favoriler.mapNotNull { Sozler.kimlikten(it) },
                        dil = dil,
                        cikar = { kapsam.launch { depo.favoriDegistir(it) } },
                        oku = { soz -> akis = listOf(soz) + akis.filterNot { it.kimlik == soz.kimlik }; indeks = 0; sekme = Sekme.ANA },
                        kesfet = { sekme = Sekme.ANA },
                        paylas = { paylasilanKimlik = it.kimlik },
                    )

                    Sekme.ISTATISTIK -> IstatistikEkrani(
                        seri = seri, rekor = rekor, gorulen = gorulen,
                        favoriSayisi = favoriler.size, acikKategori = acik.size, haftalik = haftalik,
                        name = profil?.name.orEmpty(),
                        planOzeti = profil?.let { PersonalPlan.summary(it, dil).take(2).joinToString(" · ") }.orEmpty(),
                        onFavoriler = { sekme = Sekme.FAVORI }, onPlan = { planGoster = true },
                        onSettings = { ayarlardaMi = true },
                    )
                }
                }
            }
            SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
        }
        AltNav(sekme) { sekme = it }
    }

    paylasilanSoz?.let { soz -> PaylasimEkrani(soz = soz, dil = dil, geri = { paylasilanKimlik = null }, pro = proDemo == true, proAc = { proGoster = true }) }
    if (planGoster) KisiselPlanPaneli(
        profil = profil, secili = secili, acik = acik, dil = dil,
        adet = gunlukAdet, bas = bas, bit = bit, bildirimAcik = hatirlaticiAcik && bildirimIzni,
        kapat = { planGoster = false },
        duzenle = {
            val yeni = (profil ?: PersonalProfile()).copy(step = 0, dailyCount = gunlukAdet, startHour = bas, endHour = bit)
            duzenlemeBaslangici = yeni
            kapsam.launch {
                try { depo.saveOnboardingDraft(yeni) }
                catch (_: java.io.IOException) { kayitHatasi = hataMetni }
            }
            planGoster = false; planDuzenle = true
        },
        konular = { planGoster = false; acilacakGrup = null; sekme = Sekme.KATEGORI },
        ayarlar = { planGoster = false; ayarlardaMi = true },
        proAc = { planGoster = false; proGoster = true },
    )

    kutlamaGunu?.let { gun ->
        KilometreKutlamasi(gun) {
            kutlamaGunu = null
            kapsam.launch { depo.kilometreKutlandi(gun) }
        }
    }

    var demoHatasi by rememberSaveable { mutableStateOf<String?>(null) }
    val demoBaslat = rememberDemoReklam(
        acildi = { grupAnahtari ->
            kapsam.launch {
                try {
                    depo.kategoriAc(grupAnahtari, bildirimlereEkle = false)
                    Planlayici.yenidenKur(ctx)
                    AzimWidget.tazele(ctx)
                    snackbar.showSnackbar(cevir(dil, "Demo tamamlandı. Seçtiğin kategori açıldı.", "Demo complete. Your selected topic is unlocked."))
                } catch (_: java.io.IOException) {
                    snackbar.showSnackbar(cevir(dil, "Kategori kaydedilemedi. Lütfen yeniden dene.", "The topic could not be saved. Please try again."))
                }
            }
        },
        hata = { demoHatasi = cevir(dil, "Tarayıcı açılamadı. Kategori kilitli kaldı; yeniden deneyebilirsin.", "The browser could not open. The topic is still locked; you can try again.") },
    )
    val acilanGrup = kilitKategori
    if (acilanGrup != null) {
        LaunchedEffect(acilanGrup.anahtar) { demoHatasi = null }
        KilitDialog(
            kategori = acilanGrup, dil = dil,
            proAc = { kilitKategori = null; proGoster = true },
            kapat = { kilitKategori = null; demoHatasi = null },
            hata = demoHatasi,
            demoAc = {
                demoHatasi = null
                if (demoBaslat(acilanGrup.anahtar)) kilitKategori = null
            },
        )
    }
    if (proGoster) ProEkrani(
        dil = dil, acik = proDemo == true, kaydediliyor = proKaydediliyor, hata = proHata,
        kapat = { proGoster = false; proHata = null },
        degistir = { etkin ->
            if (!proKaydediliyor) {
                proKaydediliyor = true; proHata = null
                kapsam.launch {
                    try {
                        depo.proDemoAyarla(etkin)
                        Planlayici.yenidenKur(ctx)
                        AzimWidget.tazele(ctx)
                        proGoster = false
                    } catch (_: java.io.IOException) {
                        proHata = cevir(dil, "Değişiklik kaydedilemedi. Yeniden dene.", "Could not save this change. Try again.")
                    } finally { proKaydediliyor = false }
                }
            }
        },
    )

}
