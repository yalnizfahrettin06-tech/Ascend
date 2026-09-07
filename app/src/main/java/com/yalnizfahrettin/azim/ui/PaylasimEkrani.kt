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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
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
fun PaylasimEkrani(soz: Soz, dil: String, geri: () -> Unit) {
    val ctx = LocalContext.current
    val kapsam = rememberCoroutineScope()
    var ayar by rememberSaveable(stateSaver = ayarSaver) { mutableStateOf(PaylasimAyari(zemin = KartZemin.Sahne(Atmosfer.grup(Kategoriler.bul(soz.kategori)?.grup).res))) }
    var video by rememberSaveable { mutableStateOf(false) }
    var saniye by rememberSaveable { mutableIntStateOf(10) }
    var arac by rememberSaveable { mutableStateOf(false) }
    var onizleme by remember { mutableStateOf<Bitmap?>(null) }
    var hazirlaniyor by remember { mutableStateOf(false) }
    var ilerleme by remember { mutableFloatStateOf(0f) }
    var durum by remember { mutableStateOf<String?>(null) }
    var hata by remember { mutableStateOf<String?>(null) }
    var islem by remember { mutableStateOf<Job?>(null) }
    var bekleyenUri by rememberSaveable { mutableStateOf<String?>(null) }
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    fun durdur() { islem?.cancel(); hazirlaniyor = false }
    BackHandler { if (hazirlaniyor) durdur() else geri() }
    DisposableEffect(Unit) { onDispose { islem?.cancel() } }
    LaunchedEffect(Unit) { withContext(Dispatchers.IO) { MedyaDeposu.eskiDosyalariTemizle(ctx) } }
    LaunchedEffect(ayar, soz, dil) {
        onizleme = null
        try {
            val bmp = withContext(Dispatchers.Default) { KartCizici.ciz(ctx, soz.metin(dil), soz.yazar, ayar, 480, (480 / ayar.format.oran).toInt()) }
            onizleme = bmp
            hata = null
        } catch (e: CancellationException) { throw e }
        catch (_: Exception) { hata = cevir(dil, "Bu fotoğraf açılamadı. Başka bir arka plan seç.", "Cannot open this photo. Choose another background.") }
    }
    fun dosyaSonucu(uri: Uri?) {
        val kaynak = bekleyenUri?.let(Uri::parse)
        if (uri != null && kaynak != null) kapsam.launch {
            try { MedyaDeposu.kopyala(ctx, kaynak, uri); durum = cevir(dil, "Dosya kaydedildi", "File saved") }
            catch (_: Exception) { hata = cevir(dil, "Dosya kaydedilemedi. Yeniden dene.", "Could not save the file. Try again.") }
        }
        bekleyenUri = null
    }
    val pngDosyasi = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png"), ::dosyaSonucu)
    val mp4Dosyasi = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("video/mp4"), ::dosyaSonucu)
    val fotoSecici = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            runCatching { ctx.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            ayar = ayar.copy(zemin = KartZemin.Foto(uri))
        }
    }
    fun uret(galeri: Boolean) {
        if (hazirlaniyor) return
        hazirlaniyor = true; ilerleme = 0f; hata = null; durum = null
        val secim = ayar; val videoMu = video; val sure = saniye
        islem = kapsam.launch {
            try {
                val uri = if (videoMu) {
                    val sonuc = VideoUretici.uret(ctx, soz.metin(dil), soz.yazar, secim, sure) { oran -> ilerleme = oran }
                    sonuc.uri ?: error(sonuc.hata ?: "Video export failed")
                } else MedyaDeposu.gorsel(ctx, soz.metin(dil), soz.yazar, secim)
                if (galeri) {
                    if (Build.VERSION.SDK_INT >= 29) {
                        MedyaDeposu.galeriyeKaydet(ctx, uri, videoMu)
                        durum = cevir(dil, "Galeriye kaydedildi ✓", "Saved to gallery ✓")
                    } else {
                        bekleyenUri = uri.toString()
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
                IconButton(onClick = { if (hazirlaniyor) durdur() else geri() }) { Icon(AzimIkon.Kapat, cevir(dil, "Kapat", "Close"), tint = Renk.metin) }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(Modifier.height(if (ayar.format == KartFormat.STORY) 278.dp else 225.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    onizleme?.let { bmp -> Image(bmp.asImageBitmap(), cevir(dil, "Paylaşılacak kart önizlemesi", "Export card preview"), Modifier.fillMaxHeight().aspectRatio(ayar.format.oran).clip(RoundedCornerShape(18.dp)).border(1.dp, Renk.kenarlik, RoundedCornerShape(18.dp)).testTag("share-preview"), contentScale = ContentScale.Fit) } ?: CircularProgressIndicator()
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(Atmosfer.entries) { a ->
                        val secili = ayar.zemin == KartZemin.Sahne(a.res)
                        Box(Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).border(if (secili) 2.dp else 0.dp, Renk.accent, RoundedCornerShape(14.dp)).clickable(enabled = !hazirlaniyor, role = Role.RadioButton) { ayar = ayar.copy(zemin = KartZemin.Sahne(a.res)) }.semantics { contentDescription = a.ad(dil); selected = secili }) { AtmosferResmi(a, Modifier.matchParentSize(), 0f) }
                    }
                    items(listOf(HazirZeminler.duzler.first(), HazirZeminler.duzler.last())) { z -> Box(Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(Color(z.renk)).border(if (ayar.zemin == z) 2.dp else 1.dp, if (ayar.zemin == z) Renk.accent else Renk.kenarlik, RoundedCornerShape(14.dp)).clickable(enabled = !hazirlaniyor) { ayar = ayar.copy(zemin = z) }.semantics { contentDescription = if (z == HazirZeminler.duzler.first()) cevir(dil, "Gece", "Night") else cevir(dil, "Kâğıt", "Paper"); selected = ayar.zemin == z }) }
                    item { OutlinedButton(onClick = { fotoSecici.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, enabled = !hazirlaniyor, contentPadding = PaddingValues(12.dp), modifier = Modifier.height(52.dp)) { Text(cevir(dil, "+ Fotoğraf", "+ Photo")) } }
                }
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    listOf(false, true).forEachIndexed { index, v -> SegmentedButton(selected = video == v, onClick = { video = v }, enabled = !hazirlaniyor, shape = SegmentedButtonDefaults.itemShape(index, 2)) { Text(if (v) "Video" else cevir(dil, "Görsel", "Image")) } }
                }
                if (video) {
                    Text(cevir(dil, "Süre · Yazı yavaşça belirir · Sessiz", "Duration · Text reveal · Silent"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5, 10, 30, 45).forEach { s -> FilterChip(selected = saniye == s, onClick = { saniye = s }, enabled = !hazirlaniyor, label = { Text("${s}s") }, modifier = Modifier.weight(1f)) }
                    }
                }
                TextButton(onClick = { arac = !arac }, enabled = !hazirlaniyor) { Text(cevir(dil, "Biçim ve yazı  ${if (arac) "−" else "+"}", "Format and type  ${if (arac) "−" else "+"}")) }
                if (arac) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { KartFormat.entries.forEach { f -> FilterChip(selected = ayar.format == f, onClick = { ayar = ayar.copy(format = f) }, enabled = !hazirlaniyor, label = { Text(f.etiket(dil)) }) } }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { KartYazi.entries.forEach { y -> FilterChip(selected = ayar.yazi == y, onClick = { ayar = ayar.copy(yazi = y) }, enabled = !hazirlaniyor, label = { Text(y.etiket(dil)) }) } }
                    Text(cevir(dil, "Yazı boyutu", "Text size"), color = Renk.metin)
                    Slider(ayar.yaziOlcegi, { ayar = ayar.copy(yaziOlcegi = it) }, valueRange = .8f..1.3f, enabled = !hazirlaniyor, modifier = Modifier.semantics { contentDescription = cevir(dil, "Yazı boyutu", "Text size") })
                    if (ayar.zemin is KartZemin.Sahne || ayar.zemin is KartZemin.Foto) {
                        Text(cevir(dil, "Arka plan karartması", "Background dimming"), color = Renk.metin)
                        Slider(ayar.karartma, { ayar = ayar.copy(karartma = it) }, valueRange = .25f.. .75f, enabled = !hazirlaniyor, modifier = Modifier.semantics { contentDescription = cevir(dil, "Arka plan karartması", "Background dimming") })
                    }
                }
                durum?.let { Text(it, color = Renk.accent, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) }
                hata?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }) }
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (hazirlaniyor) {
                    LinearProgressIndicator(progress = { ilerleme }, modifier = Modifier.fillMaxWidth())
                    Text(cevir(dil, "${if (video) "Video" else "Görsel"} hazırlanıyor · ${(ilerleme * 100).toInt()}%", "Preparing ${if (video) "video" else "image"} · ${(ilerleme * 100).toInt()}%"), Modifier.padding(8.dp), color = Renk.metin)
                    TextButton(onClick = ::durdur) { Text(cevir(dil, "İptal", "Cancel")) }
                } else {
                    Button(onClick = { uret(false) }, enabled = onizleme != null, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp), shape = RoundedCornerShape(18.dp)) { Icon(AzimIkon.Paylas, null, Modifier.size(19.dp)); Spacer(Modifier.width(8.dp)); Text(cevir(dil, "Paylaş", "Share")) }
                    TextButton(onClick = { uret(true) }, enabled = onizleme != null, modifier = Modifier.heightIn(min = 48.dp)) { Text(if (Build.VERSION.SDK_INT >= 29) cevir(dil, "Galeriye kaydet", "Save to gallery") else cevir(dil, "Dosyaya kaydet", "Save to file")) }
                }
            }
        }
    }
}
