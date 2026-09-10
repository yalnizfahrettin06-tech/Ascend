package com.yalnizfahrettin.azim.ui

import android.graphics.Bitmap
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.draw.shadow
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
    val focusManager = LocalFocusManager.current
    val buyukYazi = LocalDensity.current.fontScale > 1.35f
    val kapsam = rememberCoroutineScope()
    var ayar by rememberSaveable(stateSaver = ayarSaver) { mutableStateOf(PaylasimAyari()) }
    var video by rememberSaveable { mutableStateOf(false) }
    var saniye by rememberSaveable { mutableIntStateOf(10) }
    var arac by rememberSaveable { mutableStateOf(false) }
    var kaynakSekmesi by rememberSaveable { mutableStateOf("background") }
    var kutuphane by rememberSaveable { mutableStateOf(false) }
    var aktifArac by rememberSaveable { mutableStateOf("type") }
    val tercih = remember { ctx.getSharedPreferences("share-theme-favorites", android.content.Context.MODE_PRIVATE) }
    var temaFavorileri by remember { mutableStateOf(tercih.getStringSet("themes", emptySet()).orEmpty().toSet()) }
    var zeminFiltresi by rememberSaveable { mutableStateOf(PaylasimZeminFiltresi.UCRETSIZ.name) }
    var gorselArama by rememberSaveable { mutableStateOf("") }
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
    val gorunenZeminler = remember(zeminler, etkinZeminFiltresi, gorselArama) {
        zeminler.filter { z -> etkinZeminFiltresi.kapsar(z) &&
            (etkinZeminFiltresi != PaylasimZeminFiltresi.KOLEKSIYON || gorselArama.isBlank() ||
                z.ad.contains(gorselArama, ignoreCase = true) || z.anahtar.contains(gorselArama, ignoreCase = true)) }
    }
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
            kaynakSekmesi = "background"
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
        hata = null
        try {
            val bmp = withContext(Dispatchers.Default) { KartCizici.ciz(ctx, soz.metin(dil), soz.sunumEtiketi(dil), gorunenAyar, 480, (480 / gorunenAyar.format.oran).toInt()) }
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
                    val sonuc = VideoUretici.uret(ctx, soz.metin(dil), soz.sunumEtiketi(dil), secim, sure) { oran -> ilerleme = oran }
                    sonuc.uri ?: error(sonuc.hata ?: "Video export failed")
                } else MedyaDeposu.gorsel(ctx, soz.metin(dil), soz.sunumEtiketi(dil), secim)
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
    Dialog(onDismissRequest = { if (hazirlaniyor) durdur() else geri() },
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Surface(Modifier.fillMaxSize(), color = Renk.zemin) {
            Column(Modifier.fillMaxSize().systemBarsPadding()) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (hazirlaniyor) durdur() else geri() }, modifier = Modifier.testTag("share-close")) {
                        Icon(AzimIkon.Geri, cevir(dil, "Geri", "Back"), tint = Renk.metin)
                    }
                    Text(cevir(dil, "Paylaşım", "Share"), Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 20.sp, color = Renk.metin)
                    TextButton(onClick = { if (hazirlaniyor) durdur() else geri() }) { Text(cevir(dil, "Bitti", "Done"), color = Renk.accent) }
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                    val previewHeight = (LocalConfiguration.current.screenHeightDp * .43f).coerceIn(200f, 390f).dp
                    Box(Modifier.padding(top = 8.dp, bottom = 14.dp).height(previewHeight)
                        .aspectRatio(gorunenAyar.format.oran).shadow(12.dp, RoundedCornerShape(6.dp))
                        .clip(RoundedCornerShape(6.dp)).background(Renk.yuzey).border(.7.dp, Renk.kenarlikGuclu, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                        val bitmap = onizleme
                        if (bitmap != null) Image(bitmap.asImageBitmap(), cevir(dil, "Paylaşılacak kart önizlemesi", "Export card preview"),
                            Modifier.fillMaxSize().testTag("share-preview"), contentScale = ContentScale.Fit)
                        else if (hata == null) CircularProgressIndicator(Modifier.size(24.dp), color = Renk.metin, strokeWidth = 2.dp)
                        else Text(cevir(dil, "Başka bir arka plan seç", "Choose another background"), Modifier.padding(16.dp), color = Renk.metin)
                        val themeKey = zeminler.firstOrNull { it.zemin == gorunenAyar.zemin }?.anahtar
                        if (themeKey != null) IconButton(onClick = {
                            temaFavorileri = if (themeKey in temaFavorileri) temaFavorileri - themeKey else temaFavorileri + themeKey
                            tercih.edit().putStringSet("themes", temaFavorileri).apply()
                        }, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Renk.zemin.copy(alpha = .82f), CircleShape)
                            .semantics { selected = themeKey in temaFavorileri }) {
                            Icon(if (themeKey in temaFavorileri) AzimIkon.KalpDolu else AzimIkon.Kalp,
                                cevir(dil, "Arka planı favorilere ekle veya çıkar", "Toggle favorite background"), Modifier.size(20.dp), tint = if (themeKey in temaFavorileri) Renk.accent else Renk.metin)
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).selectableGroup()) {
                        listOf("background", "photo", "video").forEach { source ->
                            Column(Modifier.weight(1f).selectable(kaynakSekmesi == source, role = Role.Tab, enabled = !hazirlaniyor, onClick = {
                                if (source != "background" && !pro) proAc() else {
                                    kaynakSekmesi = source; video = source == "video"
                                    if (source == "photo") fotoSecici.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                            }).heightIn(min = 56.dp).testTag(when(source) { "photo" -> "share-background-photo"; "video" -> "share-video"; else -> "share-image" }), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(if (source == "video") AzimIkon.Video else AzimIkon.Fotograf, null, Modifier.size(22.dp), tint = Renk.metin)
                                Text(when(source) { "photo" -> cevir(dil, "Fotoğraf", "Photo"); "video" -> "Video"; else -> cevir(dil, "Arka plan", "Background") },
                                    fontSize = 12.sp, color = Renk.metin, modifier = Modifier.padding(top = 4.dp, bottom = 7.dp))
                                Box(Modifier.width(60.dp).height(2.dp).background(if (kaynakSekmesi == source) Renk.accent else Color.Transparent))
                            }
                        }
                    }
                    val curated = remember(zeminler, temaFavorileri) {
                        (zeminler.take(3) + zeminler.filter { it.anahtar in temaFavorileri } + zeminler.filter { it.grup == AtmosferGrubu.EFSANE }.take(5)).distinctBy { it.anahtar }
                    }
                    LazyRow(Modifier.fillMaxWidth().padding(top = 12.dp).testTag("share-background-options"), contentPadding = PaddingValues(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(curated, key = { it.anahtar }) { z ->
                            PaylasimZeminSecenegi(z, gorunenAyar.zemin == z.zemin, dil, hazirlaniyor) {
                                if (PaylasimErisimi.zeminProMu(z.zemin) && !pro) proAc() else ayar = gorunenAyar.copy(zemin = z.zemin)
                            }
                        }
                        item { TextButton(onClick = { kutuphane = true }, modifier = Modifier.height(96.dp)) { Text(cevir(dil, "Tümünü\ngör", "See all")) } }
                    }
                    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(cevir(dil, "3 temel arka plan ücretsiz", "3 core backgrounds free"), fontSize = 10.sp, color = Renk.metinIkincil, modifier = Modifier.weight(1f))
                        TextButton(onClick = proAc, enabled = !hazirlaniyor) { Text(if (pro) "Pro demo ✓" else "Pro demo", fontSize = 10.sp, color = Renk.accent) }
                    }
                    HorizontalDivider(Modifier.padding(horizontal = 18.dp), color = Renk.kenarlik)
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
                        listOf("type", "layout", "ratio", "motion").forEach { tool ->
                            Column(Modifier.weight(1f).clickable(enabled = !hazirlaniyor, role = Role.Button) {
                                if (!pro) proAc() else { aktifArac = tool; arac = true }
                            }.heightIn(min = 62.dp).padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                if (tool == "type") Text("Aa", fontSize = 22.sp, color = Renk.metin)
                                else Icon(when(tool) { "layout" -> AzimIkon.Izgara; "ratio" -> AzimIkon.Kirp; else -> AzimIkon.Video }, null, Modifier.size(22.dp), tint = Renk.metin)
                                Text(when(tool) { "type" -> cevir(dil, "Yazı", "Type"); "layout" -> cevir(dil, "Yerleşim", "Layout"); "ratio" -> cevir(dil, "Oran", "Ratio"); else -> cevir(dil, "Hareket", "Motion") }, fontSize = 11.sp, color = Renk.metin)
                            }
                        }
                    }
                }
                HorizontalDivider(color = Renk.kenarlik)
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    durum?.let { Text(it, color = Renk.accent, fontSize = 12.sp, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) }
                    hata?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.heightIn(max = 80.dp).verticalScroll(rememberScrollState()).semantics { liveRegion = LiveRegionMode.Assertive }) }
                    if (hazirlaniyor) {
                        if (gorunenVideo) LinearProgressIndicator(progress = { ilerleme }, modifier = Modifier.fillMaxWidth()) else LinearProgressIndicator(Modifier.fillMaxWidth())
                        TextButton(onClick = ::durdur, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text(cevir(dil, "Hazırlanıyor · İptal", "Preparing · Cancel")) }
                    } else Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { uret(true) }, enabled = onizleme != null, modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("share-save-device"), shape = CircleShape,
                            border = BorderStroke(1.dp, Renk.metin), contentPadding = PaddingValues(8.dp)) {
                            Icon(AzimIkon.Indir, null, Modifier.size(20.dp)); Spacer(Modifier.width(6.dp)); Text(cevir(dil, "Cihaza kaydet", "Save to device"), fontSize = 12.sp)
                        }
                        Button(onClick = { uret(false) }, enabled = onizleme != null, modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("share-export"), shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Renk.metin, contentColor = Renk.zemin), contentPadding = PaddingValues(8.dp)) {
                            Icon(AzimIkon.Paylas, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(cevir(dil, "Paylaş", "Share"), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        if (kutuphane) ModalBottomSheet(onDismissRequest = { kutuphane = false }, containerColor = Renk.zemin) {
            Column(Modifier.fillMaxWidth().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(cevir(dil, "Arka planlar", "Backgrounds"), fontFamily = LoraSerif, fontSize = 26.sp, color = Renk.metin)
                LazyRow(Modifier.fillMaxWidth().testTag("share-background-filters"), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PaylasimZeminFiltresi.entries, key = { it.name }) { f ->
                        FilterChip(
                            selected = etkinZeminFiltresi == f, onClick = { zeminFiltresi = f.name }, enabled = !hazirlaniyor,
                            label = { Text(f.ad(dil)) }, shape = RoundedCornerShape(50),
                            modifier = Modifier.heightIn(min = 48.dp).testTag("share-filter-${f.name.lowercase()}"),
                        )
                    }
                }
                if (etkinZeminFiltresi == PaylasimZeminFiltresi.KOLEKSIYON) {
                    OutlinedTextField(value = gorselArama, onValueChange = { gorselArama = it }, singleLine = true,
                        enabled = !hazirlaniyor,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        placeholder = { Text(cevir(dil, "Görsel ara", "Search artwork")) },
                        leadingIcon = { Icon(AzimIkon.Ara, null, Modifier.size(20.dp)) },
                        trailingIcon = { if (gorselArama.isNotEmpty()) IconButton(onClick = { gorselArama = "" }, enabled = !hazirlaniyor) {
                            Icon(AzimIkon.Kapat, cevir(dil, "Aramayı temizle", "Clear search"), Modifier.size(20.dp))
                        } },
                        modifier = Modifier.fillMaxWidth().testTag("share-library-search"), shape = RoundedCornerShape(50))
                    Text(cevir(dil, "${gorunenZeminler.size} görsel", "${gorunenZeminler.size} artworks"),
                        color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().testTag("share-library-count"))
                    if (gorunenZeminler.isEmpty()) Text(cevir(dil, "Bu aramada görsel yok.", "No artwork matches your search."), color = Renk.metinIkincil)
                    else LazyVerticalGrid(columns = GridCells.Adaptive(if (buyukYazi) 122.dp else 84.dp), modifier = Modifier.fillMaxWidth().height(310.dp).testTag("share-artwork-grid"),
                        verticalArrangement = Arrangement.spacedBy(14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(gorunenZeminler, key = { it.anahtar }) { z ->
                            Box(contentAlignment = Alignment.TopCenter) {
                                PaylasimZeminSecenegi(z, gorunenAyar.zemin == z.zemin, dil, hazirlaniyor) {
                                    if (!pro) proAc() else ayar = gorunenAyar.copy(zemin = z.zemin)
                                }
                            }
                        }
                    }
                } else key(zeminFiltresi) {
                    LazyRow(Modifier.fillMaxWidth().testTag("share-background-options"), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(gorunenZeminler, key = { it.anahtar }) { z ->
                            PaylasimZeminSecenegi(z, gorunenAyar.zemin == z.zemin, dil, hazirlaniyor) {
                                if (PaylasimErisimi.zeminProMu(z.zemin) && !pro) proAc()
                                else ayar = gorunenAyar.copy(zemin = z.zemin)
                            }
                        }
                    }
                }

            }
        }
        if (arac && pro) ModalBottomSheet(onDismissRequest = { arac = false }, containerColor = Renk.zemin) {
            Column(Modifier.fillMaxWidth().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(cevir(dil, "İnce ayarlar", "Fine adjustments"), fontFamily = LoraSerif, fontSize = 26.sp, color = Renk.metin)
                if (aktifArac == "motion") {
                    Text(cevir(dil, "Sessiz video · Yazı yavaşça belirir", "Silent video · Gentle text reveal"), color = Renk.metinIkincil)
                    Switch(checked = gorunenVideo, onCheckedChange = { video = it; kaynakSekmesi = if (it) "video" else "background" })
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5, 10, 30, 45).forEach { sec -> FilterChip(selected = saniye == sec, onClick = { saniye = sec }, label = { Text("${sec}s") }, modifier = Modifier.testTag("share-duration-$sec")) }
                    }
                } else {
                    if (aktifArac == "ratio") {
                    Text(cevir(dil, "Kart boyutu", "Card size"), color = Renk.metin, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())
                    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KartFormat.entries.forEach { f -> FilterChip(selected = ayar.format == f, onClick = { ayar = ayar.copy(format = f) }, enabled = !hazirlaniyor,
                            label = { Text(f.etiket(dil)) }, shape = RoundedCornerShape(50), modifier = Modifier.heightIn(min = 48.dp)) }
                    }
                    }
                    if (aktifArac == "type") {
                    Text(cevir(dil, "Yazı stili", "Type style"), color = Renk.metin, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())
                    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KartYazi.entries.forEach { y -> FilterChip(selected = ayar.yazi == y, onClick = { ayar = ayar.copy(yazi = y) }, enabled = !hazirlaniyor,
                            label = { Text(y.etiket(dil)) }, shape = RoundedCornerShape(50), modifier = Modifier.heightIn(min = 48.dp)) }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(cevir(dil, "Yazı boyutu", "Text size"), color = Renk.metin, modifier = Modifier.weight(1f))
                        Text("${(ayar.yaziOlcegi * 100).toInt()}%", color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                    }
                    Slider(ayar.yaziOlcegi, { ayar = ayar.copy(yaziOlcegi = it) }, valueRange = .8f..1.3f, enabled = !hazirlaniyor, modifier = Modifier.semantics { contentDescription = cevir(dil, "Yazı boyutu", "Text size") })
                    }
                    if (aktifArac == "layout") {
                    if (ayar.zemin is KartZemin.Sahne || ayar.zemin is KartZemin.Foto) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(cevir(dil, "Arka plan karartması", "Background dimming"), color = Renk.metin, modifier = Modifier.weight(1f))
                            Text("${(ayar.karartma * 100).toInt()}%", color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                        }
                        Slider(ayar.karartma, { ayar = ayar.copy(karartma = it) }, valueRange = .25f.. .75f, enabled = !hazirlaniyor, modifier = Modifier.semantics { contentDescription = cevir(dil, "Arka plan karartması", "Background dimming") })
                    }
                    Text(cevir(dil, "Hizalama", "Alignment"), color = Renk.metin, modifier = Modifier.fillMaxWidth())
                    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KartHizalama.entries.forEach { h ->
                            FilterChip(selected = ayar.hizalama == h, onClick = { ayar = ayar.copy(hizalama = h) }, enabled = !hazirlaniyor,
                                label = { Text(if (ayar.zemin == KartZemin.Sahne(com.yalnizfahrettin.azim.R.drawable.art_roman_home_v9)) { if (h == KartHizalama.ORTA) cevir(dil, "Editoryal", "Editorial") else cevir(dil, "Geniş sol", "Wide left") } else if (h == KartHizalama.ORTA) cevir(dil, "Orta", "Center") else cevir(dil, "Sol", "Left")) },
                                shape = RoundedCornerShape(50), modifier = Modifier.heightIn(min = 48.dp))
                        }
                    }                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(cevir(dil, "Ascend imzası", "Ascend signature"), Modifier.weight(1f), color = Renk.metin)
                        Switch(checked = ayar.imzaGoster, onCheckedChange = { ayar = ayar.copy(imzaGoster = it) })
                    }
                    }

                }
                TextButton(onClick = { arac = false }) { Text(cevir(dil, "Bitti", "Done")) }
            }
        }
    }
}

@Composable
private fun PaylasimBolumBasligi(sira: String, baslik: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(sira, color = Renk.metinIkincil, fontSize = 10.sp, lineHeight = 14.sp, modifier = Modifier.clearAndSetSemantics {})
        Text(baslik, color = Renk.metin, fontFamily = LoraSerif, fontSize = 22.sp, lineHeight = 29.sp, modifier = Modifier.weight(1f))
    }
}

private data class PaylasimZemini(val anahtar: String, val zemin: KartZemin, val ad: String, val grup: AtmosferGrubu? = null)

private enum class PaylasimZeminFiltresi(val tr: String, val en: String) {
    UCRETSIZ("Ücretsiz", "Free"),
    MANZARA("Manzaralar", "Scenery"),
    EFSANE("Efsaneler", "Legends"),
    DOKU("Dokular", "Textures"),
    KOLEKSIYON("Koleksiyon", "Collection"),
    RENKLER("Renkler", "Colors");

    fun ad(dil: String) = if (dil == "en") en else tr
    fun kapsar(zemin: PaylasimZemini): Boolean = when (this) {
        UCRETSIZ -> !PaylasimErisimi.zeminProMu(zemin.zemin)
        MANZARA -> zemin.grup == AtmosferGrubu.MANZARA
        EFSANE -> zemin.grup == AtmosferGrubu.EFSANE
        DOKU -> zemin.grup == AtmosferGrubu.DOKU
        KOLEKSIYON -> zemin.anahtar.startsWith("topic-")
        RENKLER -> zemin.zemin is KartZemin.Duz || zemin.zemin is KartZemin.Gradyan
    }
}

// Every former category cover is now available for any quote in the share studio.
private fun paylasimZeminleri(dil: String, kategori: String): List<PaylasimZemini> = buildList {
    add(PaylasimZemini("marble", PaylasimErisimi.ucretsizZeminler[0], cevir(dil, "Mermer", "Marble"), AtmosferGrubu.MANZARA))
    add(PaylasimZemini("night", PaylasimErisimi.ucretsizZeminler[1], cevir(dil, "Gece", "Night")))
    add(PaylasimZemini("paper", PaylasimErisimi.ucretsizZeminler[2], cevir(dil, "Kâğıt", "Paper")))
    Kategoriler.tumAltlar.forEach { konu ->
        add(PaylasimZemini("topic-${konu.anahtar}", KartZemin.Sahne(KategoriResimleri.kaynak(konu.anahtar)), konu.ad(dil)))
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
    val buyukYazi = LocalDensity.current.fontScale > 1.35f
    Column(
        Modifier.width(if (buyukYazi) 100.dp else 62.dp).clip(RoundedCornerShape(12.dp))
            .selectable(selected = secili, enabled = !mesgul, role = Role.RadioButton, onClick = sec)
            .semantics {
                contentDescription = z.ad
                stateDescription = if (proSecenek) cevir(dil, "Pro arka planı", "Pro background") else cevir(dil, "Ücretsiz arka plan", "Free background")
            }
            .testTag("share-background-${z.anahtar}").padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(.66f).clip(RoundedCornerShape(10.dp))
            .border(if (secili) 2.dp else 1.dp, if (secili) Renk.accent else Renk.kenarlik, RoundedCornerShape(10.dp))) {
            when (val zemin = z.zemin) {
                is KartZemin.Sahne -> Image(painterResource(zemin.kaynak), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
                is KartZemin.Duz -> Box(Modifier.matchParentSize().background(Color(zemin.renk)))
                is KartZemin.Gradyan -> Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color(zemin.ust), Color(zemin.alt)))))
                is KartZemin.Foto -> Unit
            }
            if (proSecenek) Surface(color = Renk.zemin, shape = RoundedCornerShape(5.dp), modifier = Modifier.align(Alignment.BottomStart).padding(5.dp)) { ProRozeti() }
            if (secili) Surface(color = Renk.accent, shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).padding(5.dp).size(23.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(AzimIkon.Tik, null, Modifier.size(14.dp), tint = Renk.zemin) }
            }
        }
        Text(z.ad, color = if (secili) Renk.metin else Renk.metinIkincil, fontSize = 11.sp, lineHeight = 15.sp,
            fontWeight = if (secili) FontWeight.Medium else FontWeight.Normal, textAlign = TextAlign.Center)
    }
}
