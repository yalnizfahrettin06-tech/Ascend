package com.yalnizfahrettin.azim.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.paylas.*
import kotlinx.coroutines.*

private val ayarSaver = listSaver<PaylasimAyari, String>(save = { a ->
    listOf(a.format.name, a.yazi.name, when (val z = a.zemin) { is KartZemin.Sahne -> "s:${z.kaynak}"; is KartZemin.Duz -> "d:${z.renk}"; is KartZemin.Gradyan -> "g:${z.ust}:${z.alt}"; is KartZemin.Foto -> "f:${z.uri}" }, a.karartma.toString(), a.yaziOlcegi.toString(), a.hizalama.name, a.imzaGoster.toString())
}, restore = { a ->
    val z = a[2]
    PaylasimAyari(KartFormat.valueOf(a[0]), KartYazi.valueOf(a[1]), when {
        z.startsWith("s:") -> KartZemin.Sahne(z.substring(2).toInt())
        z.startsWith("d:") -> KartZemin.Duz(z.substring(2).toLong())
        z.startsWith("g:") -> KartZemin.Gradyan(z.split(':')[1].toLong(), z.split(':')[2].toLong())
        else -> KartZemin.Foto(Uri.parse(z.substring(2)))
    }, a[3].toFloat(), a[4].toFloat(), KartHizalama.valueOf(a[5]), a[6].toBoolean())
})

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PaylasimEkrani(soz: Soz, dil: String, geri: () -> Unit, pro: Boolean = false, proAc: () -> Unit = {}) {
    val ctx = LocalContext.current
    val kapsam = rememberCoroutineScope()
    var ayar by rememberSaveable(stateSaver = ayarSaver) { mutableStateOf(PaylasimAyari()) }
    var video by rememberSaveable { mutableStateOf(false) }
    var saniye by rememberSaveable { mutableIntStateOf(10) }
    var arac by rememberSaveable { mutableStateOf(false) }
    var zeminFiltresi by rememberSaveable { mutableStateOf(PaylasimZeminFiltresi.UCRETSIZ.name) }
    var onizleme by remember { mutableStateOf<Bitmap?>(null) }
    var hazirlaniyor by remember { mutableStateOf(false) }
    var ilerleme by remember { mutableFloatStateOf(0f) }
    var durum by remember { mutableStateOf<String?>(null) }
    var hata by remember { mutableStateOf<String?>(null) }
    var islem by remember { mutableStateOf<Job?>(null) }
    var bekleyenUri by rememberSaveable { mutableStateOf<String?>(null) }
    var bekleyenPro by rememberSaveable { mutableStateOf(false) }
    val guncelPro by rememberUpdatedState(pro)
    var oncekiPro by remember { mutableStateOf(pro) }
    val gorunenAyar = PaylasimErisimi.gorunenAyar(ayar, pro)
    val gorunenVideo = video && pro
    val zeminler = remember(dil, soz.kategori) { paylasimZeminleri(dil, soz.kategori) }
    val etkinZeminFiltresi = PaylasimZeminFiltresi.valueOf(zeminFiltresi)
    val gorunenZeminler = remember(zeminler, etkinZeminFiltresi) { zeminler.filter(etkinZeminFiltresi::kapsar) }
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    fun durdur() { islem?.cancel() }
    BackHandler { if (hazirlaniyor) durdur() else geri() }
    DisposableEffect(Unit) { onDispose { islem?.cancel() } }
    LaunchedEffect(Unit) { withContext(Dispatchers.IO) { MedyaDeposu.eskiDosyalariTemizle(ctx) } }
    LaunchedEffect(pro) {
        if (oncekiPro && !pro) {
            durdur()
            ayar = PaylasimErisimi.ucretsizAyar(ayar)
            video = false
            arac = false
            zeminFiltresi = PaylasimZeminFiltresi.UCRETSIZ.name
            bekleyenUri = null
            bekleyenPro = false
            durum = cevir(dil, "Ücretsiz paylaşım seçeneklerine döndün.", "Free sharing options are now selected.")
        }
        oncekiPro = pro
    }
    LaunchedEffect(gorunenAyar, soz, dil) {
        onizleme = null
        try {
            val bmp = withContext(Dispatchers.Default) { KartCizici.ciz(ctx, soz.metin(dil), soz.imza(dil), gorunenAyar, 480, (480 / gorunenAyar.format.oran).toInt()) }
            onizleme = bmp
            hata = null
        } catch (e: CancellationException) { throw e }
        catch (_: Exception) { hata = cevir(dil, "Bu fotoğraf açılamadı. Başka bir arka plan seç.", "Cannot open this photo. Choose another background.") }
    }
    fun dosyaSonucu(uri: Uri?) {
        val kaynak = bekleyenUri?.let(Uri::parse)
        val proDosya = bekleyenPro
        if (bekleyenPro && !guncelPro) {
            hata = cevir(dil, "Pro demosu kapalı. Üç ücretsiz arka planla görsel paylaşabilirsin.", "Pro demo is off. You can share images with the three free backgrounds.")
        } else if (uri != null && kaynak != null) {
            hazirlaniyor = true
            islem = kapsam.launch {
                try {
                    if (proDosya && !guncelPro) return@launch
                    MedyaDeposu.kopyala(ctx, kaynak, uri)
                    durum = cevir(dil, "Dosya kaydedildi", "File saved")
                } catch (e: CancellationException) { throw e }
                catch (_: Exception) { hata = cevir(dil, "Dosya kaydedilemedi. Yeniden dene.", "Could not save the file. Try again.") }
                finally { hazirlaniyor = false }
            }
        }
        bekleyenUri = null
        bekleyenPro = false
    }
    val pngDosyasi = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png"), ::dosyaSonucu)
    val mp4Dosyasi = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("video/mp4"), ::dosyaSonucu)
    val fotoSecici = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null && guncelPro) {
            runCatching { ctx.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            ayar = ayar.copy(zemin = KartZemin.Foto(uri))
        }
    }
    fun uret(galeri: Boolean) {
        if (hazirlaniyor) return
        val secim = gorunenAyar; val videoMu = gorunenVideo; val sure = saniye
        if (!PaylasimErisimi.izinVar(guncelPro, secim, videoMu)) { proAc(); return }
        hazirlaniyor = true; ilerleme = 0f; hata = null; durum = null
        islem = kapsam.launch {
            try {
                val uri = if (videoMu) {
                    val sonuc = VideoUretici.uret(ctx, soz.metin(dil), soz.imza(dil), secim, sure) { oran -> ilerleme = oran }
                    sonuc.uri ?: error(sonuc.hata ?: "Video export failed")
                } else MedyaDeposu.gorsel(ctx, soz.metin(dil), soz.imza(dil), secim)
                ensureActive()
                if (!PaylasimErisimi.izinVar(guncelPro, secim, videoMu)) {
                    hata = cevir(dil, "Pro demosu kapatıldı. Ücretsiz bir görselle yeniden dene.", "Pro demo was turned off. Try again with a free image.")
                    return@launch
                }
                if (galeri) {
                    if (Build.VERSION.SDK_INT >= 29) {
                        MedyaDeposu.galeriyeKaydet(ctx, uri, videoMu)
                        durum = cevir(dil, "Galeriye kaydedildi ✓", "Saved to gallery ✓")
                    } else {
                        bekleyenUri = uri.toString()
                        bekleyenPro = PaylasimErisimi.proGerekir(secim, videoMu)
                        if (videoMu) mp4Dosyasi.launch("Ascend.mp4") else pngDosyasi.launch("Ascend.png")
                    }
                } else MedyaDeposu.paylas(ctx, uri, videoMu, cevir(dil, "Ascend’den paylaş", "Share from Ascend"))
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { hata = cevir(dil, "${if (videoMu) "Video" else "Görsel"} hazırlanamadı veya kaydedilemedi. Alanı kontrol edip yeniden dene; başka bir arka plan da seçebilirsin.", "Could not export or save ${if (videoMu) "video" else "image"}. Check storage and try again, or choose another background.") }
            finally { hazirlaniyor = false }
        }
    }
    ModalBottomSheet(onDismissRequest = { if (hazirlaniyor) durdur() else geri() }, sheetState = sheet, containerColor = Renk.zemin, dragHandle = null) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(.94f)) {
            Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    KucukBaslik(cevir(dil, "Paylaşım stüdyosu", "Share studio"))
                    Text(cevir(dil, "Bir söz, senin dokunuşun.", "A quote, with your touch."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                }
                if (pro) TextButton(onClick = proAc, enabled = !hazirlaniyor) { ProRozeti(metin = "PRO DEMO") }
                IconButton(onClick = { if (hazirlaniyor) durdur() else geri() }) { Icon(AzimIkon.Kapat, cevir(dil, "Kapat", "Close"), tint = Renk.metin) }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(Modifier.height(if (gorunenAyar.format == KartFormat.STORY) 278.dp else 225.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    onizleme?.let { bmp -> Image(bmp.asImageBitmap(), cevir(dil, "Paylaşılacak kart önizlemesi", "Export card preview"), Modifier.fillMaxHeight().aspectRatio(gorunenAyar.format.oran).clip(RoundedCornerShape(18.dp)).border(1.dp, Renk.kenarlik, RoundedCornerShape(18.dp)).testTag("share-preview"), contentScale = ContentScale.Fit) } ?: CircularProgressIndicator()
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(cevir(dil, "Arka plan", "Background"), color = Renk.metin, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Text(cevir(dil, "3 ücretsiz seçenek", "3 free options"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }
                LazyRow(Modifier.fillMaxWidth().testTag("share-background-filters"), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PaylasimZeminFiltresi.entries, key = { it.name }) { f ->
                        FilterChip(
                            selected = etkinZeminFiltresi == f, onClick = { zeminFiltresi = f.name }, enabled = !hazirlaniyor,
                            label = { Text(f.ad(dil)) }, shape = RoundedCornerShape(50),
                            modifier = Modifier.heightIn(min = 48.dp).testTag("share-filter-${f.name.lowercase()}"),
                        )
                    }
                }
                key(zeminFiltresi) {
                    LazyRow(Modifier.fillMaxWidth().testTag("share-background-options"), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(gorunenZeminler, key = { it.anahtar }) { z ->
                            PaylasimZeminSecenegi(z, gorunenAyar.zemin == z.zemin, dil, hazirlaniyor) {
                                if (PaylasimErisimi.zeminProMu(z.zemin) && !pro) proAc()
                                else ayar = gorunenAyar.copy(zemin = z.zemin)
                            }
                        }
                    }
                }
                val seciliZeminAdi = zeminler.firstOrNull { it.zemin == gorunenAyar.zemin }?.ad ?: cevir(dil, "Kendi fotoğrafın", "Your photo")
                Text(cevir(dil, "Seçili: $seciliZeminAdi", "Selected: $seciliZeminAdi"), Modifier.fillMaxWidth().testTag("share-selected-background"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                OutlinedButton(
                    onClick = { if (pro) fotoSecici.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) else proAc() },
                    enabled = !hazirlaniyor, contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("share-background-photo").semantics { contentDescription = cevir(dil, "Fotoğraf seç", "Choose photo"); stateDescription = "Pro" },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("+", modifier = Modifier.padding(end = 10.dp))
                    Text(cevir(dil, "Kendi fotoğrafını seç", "Choose your own photo"), Modifier.weight(1f), textAlign = TextAlign.Start)
                    Spacer(Modifier.width(8.dp))
                    ProRozeti()
                }
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    listOf(false, true).forEachIndexed { index, v ->
                        SegmentedButton(
                            selected = gorunenVideo == v,
                            onClick = { if (v && !pro) proAc() else video = v },
                            enabled = !hazirlaniyor, shape = SegmentedButtonDefaults.itemShape(index, 2),
                            modifier = Modifier.heightIn(min = 50.dp).testTag(if (v) "share-video" else "share-image"),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(if (v) "Video" else cevir(dil, "Görsel", "Image"))
                                if (v) ProRozeti()
                            }
                        }
                    }
                }
                if (gorunenVideo) {
                    Text(cevir(dil, "Süre · Yazı yavaşça belirir · Sessiz", "Duration · Text reveal · Silent"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5, 10, 30, 45).forEach { s -> FilterChip(selected = saniye == s, onClick = { saniye = s }, enabled = !hazirlaniyor, label = { Text("${s}s") }, modifier = Modifier.weight(1f).testTag("share-duration-$s")) }
                    }
                }
                OutlinedButton(
                    onClick = { if (pro) arac = !arac else proAc() }, enabled = !hazirlaniyor,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp).testTag("share-advanced"),
                    shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Icon(AzimIkon.Ayarlar, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(cevir(dil, "Biçim ve yazı", "Format and type"), modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                    ProRozeti()
                    if (pro) Text(if (arac) "  −" else "  +")
                }
                if (arac && pro) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { KartFormat.entries.forEach { f -> FilterChip(selected = ayar.format == f, onClick = { ayar = ayar.copy(format = f) }, enabled = !hazirlaniyor, label = { Text(f.etiket(dil)) }) } }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { KartYazi.entries.forEach { y -> FilterChip(selected = ayar.yazi == y, onClick = { ayar = ayar.copy(yazi = y) }, enabled = !hazirlaniyor, label = { Text(y.etiket(dil)) }) } }
                    Text(cevir(dil, "Yazı boyutu", "Text size"), color = Renk.metin)
                    Slider(ayar.yaziOlcegi, { ayar = ayar.copy(yaziOlcegi = it) }, valueRange = .8f..1.3f, enabled = !hazirlaniyor, modifier = Modifier.semantics { contentDescription = cevir(dil, "Yazı boyutu", "Text size") })
                    if (ayar.zemin is KartZemin.Sahne || ayar.zemin is KartZemin.Foto) {
                        Text(cevir(dil, "Arka plan karartması", "Background dimming"), color = Renk.metin)
                        Slider(ayar.karartma, { ayar = ayar.copy(karartma = it) }, valueRange = .25f.. .75f, enabled = !hazirlaniyor, modifier = Modifier.semantics { contentDescription = cevir(dil, "Arka plan karartması", "Background dimming") })
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(cevir(dil, "Hizalama", "Alignment"), color = Renk.metin, modifier = Modifier.weight(1f))
                        KartHizalama.entries.forEach { h ->
                            FilterChip(selected = ayar.hizalama == h, onClick = { ayar = ayar.copy(hizalama = h) }, enabled = !hazirlaniyor,
                                label = { Text(if (h == KartHizalama.ORTA) cevir(dil, "Orta", "Center") else cevir(dil, "Sol", "Left")) })
                        }
                    }
                }
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                durum?.let { Text(it, color = Renk.accent, modifier = Modifier.padding(bottom = 8.dp).semantics { liveRegion = LiveRegionMode.Polite }) }
                hata?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp).semantics { liveRegion = LiveRegionMode.Assertive }) }
                if (hazirlaniyor) {
                    if (gorunenVideo) LinearProgressIndicator(progress = { ilerleme }, modifier = Modifier.fillMaxWidth())
                    else LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text(if (gorunenVideo) cevir(dil, "Video hazırlanıyor · ${(ilerleme * 100).toInt()}%", "Preparing video · ${(ilerleme * 100).toInt()}%") else cevir(dil, "Görsel hazırlanıyor", "Preparing image"), Modifier.padding(8.dp), color = Renk.metin)
                    TextButton(onClick = ::durdur) { Text(cevir(dil, "İptal", "Cancel")) }
                } else {
                    Button(onClick = { uret(false) }, enabled = onizleme != null, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp), shape = RoundedCornerShape(18.dp)) { Icon(AzimIkon.Paylas, null, Modifier.size(19.dp)); Spacer(Modifier.width(8.dp)); Text(cevir(dil, "Paylaş", "Share")) }
                    TextButton(onClick = { uret(true) }, enabled = onizleme != null, modifier = Modifier.heightIn(min = 48.dp)) { Text(if (Build.VERSION.SDK_INT >= 29) cevir(dil, "Galeriye kaydet", "Save to gallery") else cevir(dil, "Dosyaya kaydet", "Save to file")) }
                }
            }
        }
    }
}

private data class PaylasimZemini(val anahtar: String, val zemin: KartZemin, val ad: String, val grup: AtmosferGrubu? = null)

private enum class PaylasimZeminFiltresi(val tr: String, val en: String) {
    UCRETSIZ("Ücretsiz", "Free"),
    MANZARA("Manzaralar", "Scenery"),
    EFSANE("Efsaneler", "Legends"),
    DOKU("Dokular", "Textures"),
    RENKLER("Renkler", "Colors");

    fun ad(dil: String) = if (dil == "en") en else tr
    fun kapsar(zemin: PaylasimZemini): Boolean = when (this) {
        UCRETSIZ -> !PaylasimErisimi.zeminProMu(zemin.zemin)
        MANZARA -> zemin.grup == AtmosferGrubu.MANZARA
        EFSANE -> zemin.grup == AtmosferGrubu.EFSANE
        DOKU -> zemin.grup == AtmosferGrubu.DOKU
        RENKLER -> zemin.zemin is KartZemin.Duz || zemin.zemin is KartZemin.Gradyan
    }
}

// 37 curated backgrounds, the quote's own topic artwork and a separate photo picker.
private fun paylasimZeminleri(dil: String, kategori: String): List<PaylasimZemini> = buildList {
    add(PaylasimZemini("summit", PaylasimErisimi.ucretsizZeminler[0], cevir(dil, "Zirve", "Summit"), AtmosferGrubu.MANZARA))
    add(PaylasimZemini("night", PaylasimErisimi.ucretsizZeminler[1], cevir(dil, "Gece", "Night")))
    add(PaylasimZemini("paper", PaylasimErisimi.ucretsizZeminler[2], cevir(dil, "Kâğıt", "Paper")))
    val konuGorseli = KartZemin.Sahne(KategoriResimleri.kaynak(kategori))
    if (PaylasimErisimi.zeminProMu(konuGorseli) && Atmosfer.entries.none { it.res == konuGorseli.kaynak }) {
        add(PaylasimZemini("topic", konuGorseli, cevir(dil, "Konu görseli", "Topic artwork"), AtmosferGrubu.MANZARA))
    }
    Atmosfer.entries.filter { it != Atmosfer.ZIRVE }.forEach { a ->
        add(PaylasimZemini(a.name.lowercase(), KartZemin.Sahne(a.res), a.ad(dil), a.grup))
    }
    val renkAdlari = listOf("Grafit" to "Graphite", "Gece yarısı" to "Midnight", "Şarap" to "Wine", "Lacivert" to "Navy", "Yosun" to "Moss", "Kar" to "Snow")
    HazirZeminler.duzler.drop(1).dropLast(1).forEachIndexed { i, z ->
        add(PaylasimZemini("color-$i", z, cevir(dil, renkAdlari[i].first, renkAdlari[i].second)))
    }
    val gecisAdlari = listOf("Sis" to "Mist", "Akşam" to "Evening", "Okyanus" to "Ocean", "Yaprak" to "Leaf", "Gül" to "Rose", "Fildişi" to "Ivory")
    HazirZeminler.gradyanlar.forEachIndexed { i, z ->
        add(PaylasimZemini("gradient-$i", z, cevir(dil, gecisAdlari[i].first, gecisAdlari[i].second)))
    }
}

@Composable
private fun PaylasimZeminSecenegi(z: PaylasimZemini, secili: Boolean, dil: String, mesgul: Boolean, sec: () -> Unit) {
    val proSecenek = PaylasimErisimi.zeminProMu(z.zemin)
    Column(
        Modifier.width(66.dp).clip(RoundedCornerShape(15.dp))
            .clickable(enabled = !mesgul, role = Role.RadioButton, onClick = sec)
            .semantics {
                contentDescription = if (z.anahtar == "topic") cevir(dil, "Konu görseli · PRO", "Topic artwork · PRO") else z.ad
                selected = secili
                if (proSecenek) stateDescription = cevir(dil, "Pro arka planı", "Pro background")
            }
            .testTag("share-background-${z.anahtar}").padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(62.dp).clip(RoundedCornerShape(14.dp)).border(if (secili) 2.dp else 1.dp, if (secili) Renk.accent else Renk.kenarlik, RoundedCornerShape(14.dp))) {
            when (val zemin = z.zemin) {
                is KartZemin.Sahne -> Image(painterResource(zemin.kaynak), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
                is KartZemin.Duz -> Box(Modifier.matchParentSize().background(Color(zemin.renk)))
                is KartZemin.Gradyan -> Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color(zemin.ust), Color(zemin.alt)))))
                is KartZemin.Foto -> Unit
            }
            if (proSecenek) Surface(color = Renk.zemin.copy(alpha = .94f), shape = RoundedCornerShape(7.dp), modifier = Modifier.align(Alignment.BottomEnd).padding(3.dp)) { ProRozeti() }
        }
        Text(z.ad, color = if (secili) Renk.accent else Renk.metinIkincil, fontSize = 11.sp, lineHeight = 14.sp, textAlign = TextAlign.Center)
    }
}
