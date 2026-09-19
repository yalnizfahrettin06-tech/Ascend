package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import com.yalnizfahrettin.azim.data.QuietFeed
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
import com.yalnizfahrettin.azim.data.*
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
    acilistakiKimlik: String? = null, acilisIstegi: Long = 0L,
    acilisSekmesi: Sekme? = null,
    izinIste: () -> Unit,
    bildirimIzni: Boolean,
) {
    val ctx = LocalContext.current
    val kapsam = rememberCoroutineScope()

    val onboardingBitti by remember(depo) { depo.onboardingBitti.map { it as Boolean? } }.collectAsStateWithLifecycle(null)
    val secili by depo.secili.collectAsStateWithLifecycle(emptySet())
    val profileState by remember(depo) { depo.personalProfile.map { true to it } }.collectAsStateWithLifecycle(false to null)
    val profil = profileState.second
    val taslak by remember(depo) { depo.onboardingDraft.map { it as PersonalProfile? } }.collectAsStateWithLifecycle(null)
    val acik by depo.acik.collectAsStateWithLifecycle(emptySet())
    val homeCategories = remember(profil, acik) { PersonalPlan.homeCategories(profil, acik) }
    val acikGruplar by depo.acikGruplar.collectAsStateWithLifecycle(emptySet())
    val arkaPlan by depo.arkaPlan.collectAsStateWithLifecycle(null)
    val proDemo by remember(depo) { depo.proDemo.map { it as Boolean? } }.collectAsStateWithLifecycle(null)
    val favoriler by depo.favoriler.collectAsStateWithLifecycle(emptySet())
    val hidden by remember(depo) { depo.hiddenQuotes.map { it as Set<String>? } }.collectAsStateWithLifecycle(null)
    val pausedUntil by depo.pausedUntil.collectAsStateWithLifecycle(0L)
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
    val seriesProgress by depo.seriesProgress.collectAsStateWithLifecycle(emptyMap())
    var notificationTopicsOpen by rememberSaveable { mutableStateOf(false) }
    var personalPage by rememberSaveable { mutableStateOf("") }
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
    var readerId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedRequest by rememberSaveable { mutableIntStateOf(0) }
    var ihtiyac by rememberSaveable { mutableStateOf<String?>(null) }
    var proGoster by rememberSaveable { mutableStateOf(false) }
    var encodedOffer by rememberSaveable { mutableStateOf(ProOffer().encode()) }
    var offerDismissals by rememberSaveable { mutableIntStateOf(0) }
    val currentOffer = ProOffer.decode(encodedOffer)
    var proKaydediliyor by remember { mutableStateOf(false) }
    var proHata by remember { mutableStateOf<String?>(null) }
    var acilacakGrup by rememberSaveable { mutableStateOf<String?>(null) }
    var paylasilanKimlik by rememberSaveable { mutableStateOf<String?>(null) }
    val paylasilanSoz = paylasilanKimlik?.let { Sozler.kimlikten(it) }
    // Akış: pager'ın gezineceği söz listesi
    var akis by remember { mutableStateOf<List<Soz>>(emptyList()) }
    var indeks by remember { mutableIntStateOf(0) }
    fun openOffer(offer: ProOffer) {
        encodedOffer = offer.copy(quoteId = offer.quoteId.ifBlank { akis.getOrNull(indeks)?.kimlik.orEmpty() }).encode()
        proHata = null; proGoster = true
    }
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
    if (onboardingBitti == null || proDemo == null || !profileState.first || hidden == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    // Drafts resume locally; completing setup commits answers and the reminder choice atomically.
    if (onboardingBitti == false) {
        if (taslak == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return
        }
        Onboarding(dil = dil, kaydediliyor = kaydediliyor, hata = kayitHatasi,
            bildirimIzni = bildirimIzni, izinIste = izinIste,
            initialDraft = taslak!!, previewAccess = acik, pro = proDemo == true, proOpen = { proGoster = true },
            languageChanged = { kapsam.launch { depo.dilAyarla(it) } },
            startTrial = { result -> kapsam.launch {
                try { depo.proDemoAyarla(true); ProductSignals.record(ctx,ProductSignals.Event.DEMO_ENABLED,ProSource.ONBOARDING); result(true) }
                catch (_: java.io.IOException) { result(false) }
            } },
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
                            depo.completePersonalPlan(yeni, hatirlat)
                            ProductSignals.record(ctx,ProductSignals.Event.SETUP_COMPLETED,ProSource.ONBOARDING)
                            ihtiyac = null
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
        if (proGoster) ProEkrani(dil, proDemo == true, proKaydediliyor, proHata,
            kapat = { proGoster = false }, degistir = { enabled ->
                proKaydediliyor = true
                kapsam.launch {
                    try { depo.proDemoAyarla(enabled); proGoster = false }
                    catch (_: java.io.IOException) { proHata = hataMetni }
                    finally { proKaydediliyor = false }
                }
            })
        return
    }

    // Akışı bir kez kur: seçili kategorilerden karıştırılmış liste.
    LaunchedEffect(homeCategories, onboardingBitti, hidden) {
        if (onboardingBitti == true) {
            val yeni = QuietFeed.order(Sozler.tumu().filter { it.kategori in homeCategories }, depo.recentQuotes.first(), hidden.orEmpty())
            akis = yeni
            indeks = 0
        }
        depo.seriyiTazele()
    }

    // Consume this launch request once; changing entitlements must not replay it.
    var acilisTuketildi by rememberSaveable(acilistakiKimlik, acilisIstegi) { mutableStateOf(false) }
    LaunchedEffect(acilistakiKimlik, acilisIstegi, acik, homeCategories, favoriler, gunlukGelenler) {
        if (acilisTuketildi || acik.isEmpty()) return@LaunchedEffect
        val hedef = acilistakiKimlik?.let { Sozler.kimlikten(it) } ?: return@LaunchedEffect
        val arsiv = hedef.kimlik in favoriler || gunlukGelenler.values.any { hedef.kimlik in it }
        if (hedef.kategori !in acik && !arsiv) return@LaunchedEffect
        acilisTuketildi = true
        // A notification always opens its own reader; feed refresh cannot replace it.
        readerId = hedef.kimlik

    }

    // Personal space focuses on saved words and delivered reminders; no streak prompts.

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
            remindersOpen = { ayarlardaMi = false; notificationTopicsOpen = true },
            appearanceOpen = { ayarlardaMi = false; sekme = Sekme.GORUNUM },
        )
        return
    }

    var discoverySeries by rememberSaveable { mutableStateOf(false) }
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
                        gizle = { quote -> kapsam.launch {
                            depo.hideQuote(quote.kimlik, true); Planlayici.yenidenKur(ctx); AzimWidget.tazele(ctx)
                            val action = snackbar.showSnackbar(cevir(dil, "Bu söz akışta ve bildirimlerde gösterilmeyecek.", "This quote will no longer appear in your feed or reminders."), cevir(dil, "Geri al", "Undo"),
                                duration = androidx.compose.material3.SnackbarDuration.Long)
                            if(action == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                                depo.hideQuote(quote.kimlik, false); Planlayici.yenidenKur(ctx); AzimWidget.tazele(ctx)
                            }
                        } },
                        pro = proDemo == true, proAc = { openOffer(ProOffer()) }, kullaniciAdi = profil?.name.orEmpty(), planAc = { planGoster = true },
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
                        seciliKonular = secili, haptikAcik = haptik, konulariDuzenle = { notificationTopicsOpen = true },
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
                        motionActive = !proGoster && !notificationTopicsOpen && !planGoster && readerId == null && paylasilanKimlik == null,
                    )

                    Sekme.GORUNUM -> GorunumEkrani(dil, arkaPlan, proDemo == true, { proGoster = true },
                        { id -> kapsam.launch { depo.arkaPlanAyarla(id) } }, offerOpen = ::openOffer, offerDismissals = offerDismissals)
                    Sekme.KATEGORI -> KesifMerkezi(dil,showHeading = !discoverySeries) {
                        if (discoverySeries) KisaSerilerEkrani(dil, seriesProgress, favoriler, { discoverySeries = false },
                            { depo.startSeries(it) }, { depo.completeSeriesDay(it) },
                            { quote -> kapsam.launch { depo.favoriDegistir(quote.kimlik) } }, { paylasilanKimlik = it.kimlik }, insets = false, pro = proDemo == true, proOpen = { openOffer(ProOffer(ProSource.SERIES,"restart")) })
                        else
                        KategorilerEkrani(
                        secili = secili, acik = acik, dil = dil, pro = proDemo == true, proAc = { proGoster = true },
                        sec = { kapsam.launch { depo.kategoriSec(it); Planlayici.yenidenKur(ctx); AzimWidget.tazele(ctx) } },
                        kilidiAc = { openOffer(ProOffer(ProSource.TOPIC,it.anahtar)) },
                        acilacakGrup = acilacakGrup, bildirimAcik = hatirlaticiAcik && bildirimIzni,
                        oku = { readerId = it.kimlik }, selectedRequest = selectedRequest, insets = false,
                        seriesProgress = seriesProgress, offerOpen = ::openOffer,
                        series = { discoverySeries = true }, remindersOpen = { notificationTopicsOpen = true },
                    )
                    }

                    Sekme.FAVORI -> FavorilerEkrani(
                        onBack = { sekme = Sekme.ISTATISTIK },
                        favoriler = favoriler.mapNotNull { Sozler.kimlikten(it) },
                        dil = dil,
                        cikar = { kapsam.launch { depo.favoriDegistir(it) } },
                        oku = { soz -> readerId = soz.kimlik },
                        kesfet = { sekme = Sekme.ANA },
                        paylas = { paylasilanKimlik = it.kimlik },
                    )

                    Sekme.ISTATISTIK -> SeninBolumleri(dil, personalPage, { personalPage = it }, { ayarlardaMi = true }) { when(personalPage) {
                        "history" -> BildirimGecmisiEkrani(dil, gunlukGelenler, favoriler, { personalPage = "" },
                            { quote -> kapsam.launch { depo.favoriDegistir(quote.kimlik) } }, { paylasilanKimlik = it.kimlik }, embedded = true)
                        else -> FavorilerEkrani(favoriler.mapNotNull(Sozler::kimlikten), dil,
                            cikar = { kapsam.launch { depo.favoriDegistir(it) } },
                            oku = { readerId = it.kimlik }, kesfet = { sekme = Sekme.ANA },
                            paylas = { paylasilanKimlik = it.kimlik }, embedded = true)

                    } }
                }
                }
            }
            SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
        }
        AltNav(sekme) { sekme = it }
    }

    if(notificationTopicsOpen) BildirimKonulariPaneli(dil,secili,close = { notificationTopicsOpen = false },
        settings = { notificationTopicsOpen = false; planGoster = true },
        status = cevir(dil, if (!bildirimIzni) "Bildirim izni kapalı" else if (!hatirlaticiAcik) "Bildirimler kapalı" else if(pausedUntil > System.currentTimeMillis()) "Yarına kadar ara verildi" else "Bildirimler açık",
            if (!bildirimIzni) "Notification permission is off" else if (!hatirlaticiAcik) "Reminders off" else if(pausedUntil > System.currentTimeMillis()) "Paused until tomorrow" else "Reminders on"),
        toggle = { key -> kapsam.launch { depo.kategoriSec(key); Planlayici.yenidenKur(ctx); AzimWidget.tazele(ctx) } },
        discover = { notificationTopicsOpen = false; acilacakGrup = null; selectedRequest = -kotlin.math.abs(selectedRequest) - 1; sekme = Sekme.KATEGORI })

    val readerQuote = readerId?.let(Sozler::kimlikten)
    LaunchedEffect(readerId) { readerQuote?.let { depo.gosterildi(it.kimlik) } }
    readerQuote?.let { quote -> SozOkuyucu(quote, dil, quote.kimlik in favoriler,
        close = { readerId = null }, save = { kapsam.launch { depo.favoriDegistir(quote.kimlik) } },
        share = { paylasilanKimlik = quote.kimlik }) }
    paylasilanSoz?.let { soz -> PaylasimEkrani(soz = soz, dil = dil, geri = { paylasilanKimlik = null }, pro = proDemo == true, proAc = { openOffer(ProOffer()) }, offerOpen = ::openOffer, offerDismissals = offerDismissals) }
    if (planGoster) KisiselPlanPaneli(
        profil = profil, secili = secili, acik = acik, dil = dil,
        adet = gunlukAdet, bas = bas, bit = bit, bildirimAcik = hatirlaticiAcik,
        kapat = { planGoster = false },
        konular = { planGoster = false; notificationTopicsOpen = true },
        permission = bildirimIzni, requestPermission = izinIste,
        pausedUntil = pausedUntil, hiddenCount = hidden.orEmpty().size,
        restoreHidden = { depo.restoreHiddenQuotes(); Planlayici.yenidenKur(ctx); AzimWidget.tazele(ctx) },
        pause = { shouldPause ->
            val until = if(shouldPause) java.time.LocalDate.now().plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() else 0L
            depo.pauseReminders(until); Planlayici.yenidenKur(ctx)
        },
        saveContent = { answers -> depo.contentPreferences(answers); Planlayici.yenidenKur(ctx); AzimWidget.tazele(ctx) },
        saveRhythm = { count, start, end, enabled ->
            depo.reminderRhythm(count, start, end, enabled)
            Planlayici.yenidenKur(ctx)
            if(enabled && !bildirimIzni) izinIste()
        },
    )

    kutlamaGunu?.let { gun ->
        KilometreKutlamasi(gun) {
            kutlamaGunu = null
            kapsam.launch { depo.kilometreKutlandi(gun) }
        }
    }

    if (proGoster) ProEkrani(
        dil = dil, acik = proDemo == true, kaydediliyor = proKaydediliyor, hata = proHata, offer = currentOffer,
        kapat = { proGoster = false; proHata = null; offerDismissals++ },
        degistir = { etkin ->
            if (!proKaydediliyor) {
                proKaydediliyor = true; proHata = null
                kapsam.launch {
                    try {
                        depo.proDemoAyarla(etkin)
                        if(etkin) {
                            ProductSignals.record(ctx,ProductSignals.Event.DEMO_ENABLED,currentOffer.source)
                        }
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
