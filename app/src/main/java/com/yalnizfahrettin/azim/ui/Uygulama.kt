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
    val acik by depo.acik.collectAsStateWithLifecycle(emptySet())
    val acikGruplar by depo.acikGruplar.collectAsStateWithLifecycle(emptySet())
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
    val tema by depo.tema.collectAsStateWithLifecycle(TemaModu.SISTEM)
    val dinamik by depo.dinamikRenk.collectAsStateWithLifecycle(false)
    val haptik by depo.haptikAcik.collectAsStateWithLifecycle(true)
    val palet by depo.palet.collectAsStateWithLifecycle(com.yalnizfahrettin.azim.core.Palet.KUM)
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
    var kilitGrup by remember { mutableStateOf<com.yalnizfahrettin.azim.data.KategoriGrubu?>(null) }
    var acilacakGrup by remember { mutableStateOf<String?>(null) }
    var paylasilanKimlik by rememberSaveable { mutableStateOf<String?>(null) }
    val paylasilanSoz = paylasilanKimlik?.let { Sozler.kimlikten(it) }
    // Akış: pager'ın gezineceği söz listesi
    var akis by remember { mutableStateOf<List<Soz>>(emptyList()) }
    var indeks by remember { mutableIntStateOf(0) }
    var kutlamaGunu by remember { mutableStateOf<Int?>(null) }
    val snackbar = remember { SnackbarHostState() }

    var kaydediliyor by remember { mutableStateOf(false) }
    var kayitHatasi by remember { mutableStateOf<String?>(null) }
    val hataMetni = stringResource(R.string.asc_kayit_hata)
    BackHandler(ayarlardaMi || paylasilanSoz != null || sekme != Sekme.ANA) {
        when { paylasilanSoz != null -> paylasilanKimlik = null; ayarlardaMi -> ayarlardaMi = false; else -> sekme = Sekme.ANA }
    }
    LaunchedEffect(acilisSekmesi) { acilisSekmesi?.let { sekme = it } }
    if (onboardingBitti == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    // Onboarding is committed atomically before any system permission request.
    if (onboardingBitti == false) {
        Onboarding(dil = dil, kaydediliyor = kaydediliyor, hata = kayitHatasi,
            bildirimIzni = bildirimIzni, izinIste = izinIste,
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
    LaunchedEffect(secili, onboardingBitti) {
        if (secili.isNotEmpty()) {
            val mevcut = akis.getOrNull(indeks)
            val yeni = Sozler.akis(secili, gecmis)
            akis = if (mevcut != null && mevcut.kategori in secili) listOf(mevcut) + yeni.filterNot { it.kimlik == mevcut.kimlik } else yeni
            indeks = 0
        }
        depo.seriyiTazele()
    }

    // Bildirimden gelindiyse o sözü akışın başına al.
    LaunchedEffect(acilistakiKimlik) {
        val hedef = acilistakiKimlik?.let { Sozler.kimlikten(it) } ?: return@LaunchedEffect
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
                when (s) {
                    Sekme.ANA -> AnaEkran(
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
                            if (grup.anahtar !in acikGruplar) kilitGrup = grup
                        },
                        ipucunuKapat = { kapsam.launch { depo.ipucunuKapat() } },
                        ayarlaraGit = { ayarlardaMi = true },
                    )

                    Sekme.KATEGORI -> KategorilerEkrani(
                        secili = secili, acikGruplar = acikGruplar, dil = dil,
                        sec = { kapsam.launch { depo.kategoriSec(it) } },
                        kilidiAc = { kilitGrup = it },
                        acilacakGrup = acilacakGrup,
                    )

                    Sekme.FAVORI -> FavorilerEkrani(
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
                    )
                }
            }
            SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
        }
        AltNav(sekme) { sekme = it }
    }

    paylasilanSoz?.let { soz -> PaylasimEkrani(soz, dil, geri = { paylasilanKimlik = null }) }

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
                    depo.grupAc(grupAnahtari)
                    snackbar.showSnackbar(cevir(dil, "Demo tamamlandı. Seçtiğin koleksiyon açıldı.", "Demo complete. Your selected collection is unlocked."))
                } catch (_: java.io.IOException) {
                    snackbar.showSnackbar(cevir(dil, "Koleksiyon kaydedilemedi. Lütfen yeniden dene.", "The collection could not be saved. Please try again."))
                }
            }
        },
        hata = { demoHatasi = cevir(dil, "Tarayıcı açılamadı. Koleksiyon kilitli kaldı; yeniden deneyebilirsin.", "The browser could not open. The collection is still locked; you can try again.") },
    )
    val acilanGrup = kilitGrup
    if (acilanGrup != null) {
        LaunchedEffect(acilanGrup.anahtar) { demoHatasi = null }
        KilitDialog(
            grup = acilanGrup, dil = dil,
            kapat = { kilitGrup = null; demoHatasi = null },
            hata = demoHatasi,
            demoAc = {
                demoHatasi = null
                if (demoBaslat(acilanGrup.anahtar)) kilitGrup = null
            },
        )
    }
}
