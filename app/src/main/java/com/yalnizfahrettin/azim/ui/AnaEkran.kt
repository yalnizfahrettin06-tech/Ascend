package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AnaEkran(
    sozler: List<Soz>, aktifIndeks: Int, favoriler: Set<String>, seri: Int,
    haftalik: List<Boolean>, bugunGelenler: List<Soz>, gunlukGelenler: Map<String, List<Soz>>,
    bugunGorulen: Int, gunlukHedef: Int, sonrakiBildirim: String?, oneri: Oneri?,
    bugunPlanlanan: Int, dil: String, hatirlaticiAcik: Boolean, bildirimIzni: Boolean,
    indeksDegisti: (Int) -> Unit, favoriDegistir: (Soz) -> Unit, paylas: (Soz) -> Unit,
    sozSecildi: (Soz) -> Unit, kesfeGit: (KategoriGrubu) -> Unit,
    ipucunuKapat: () -> Unit, ayarlaraGit: () -> Unit,
) {
    var filtre by rememberSaveable { mutableStateOf<String?>(null) }
    val gruplar = remember(sozler) { Kategoriler.gruplar.filter { g -> sozler.any { Kategoriler.bul(it.kategori)?.grup == g.anahtar } } }
    val liste = remember(sozler, filtre) { sozler.filter { filtre == null || Kategoriler.bul(it.kategori)?.grup == filtre }.ifEmpty { sozler } }
    val pager = rememberPagerState { liste.size.coerceAtLeast(1) }
    val kapsam = rememberCoroutineScope()
    val mesaj = remember { SnackbarHostState() }
    val pano = LocalClipboardManager.current
    val ses = rememberSeslendirici(dil)
    var secilenAtmosfer by rememberSaveable { mutableStateOf<String?>(null) }
    var temaSecimi by remember { mutableStateOf(false) }
    LaunchedEffect(filtre) { pager.scrollToPage(0) }
    LaunchedEffect(aktifIndeks, sozler) {
        val hedef = sozler.getOrNull(aktifIndeks) ?: return@LaunchedEffect
        val yer = liste.indexOfFirst { it.kimlik == hedef.kimlik }
        if (yer >= 0 && yer != pager.currentPage) pager.scrollToPage(yer)
    }
    val aktif = liste.getOrNull(pager.settledPage)
    LaunchedEffect(aktif?.kimlik) {
        aktif?.let { soz -> sozler.indexOfFirst { it.kimlik == soz.kimlik }.takeIf { it >= 0 }?.let(indeksDegisti) }
        ses.durdur()
    }
    Box(Modifier.fillMaxSize().background(Renk.zemin)) {
        Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth().padding(start = 22.dp, end = 20.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("ASCEND", fontSize = 21.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Medium, color = Renk.metin)
                    Text(LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM, EEEE", Locale.forLanguageTag(dil))), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                }
                YuvarlakIkon(AzimIkon.Ayarlar, cevir(dil, "Ayarlar", "Settings"), ayarlaraGit)
            }
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(selected = filtre == null, onClick = { filtre = null }, label = { Text(cevir(dil, "Senin için", "For you")) }) }
                items(gruplar, key = { it.anahtar }) { g -> FilterChip(selected = filtre == g.anahtar, onClick = { filtre = g.anahtar }, label = { Text(kisaGrupAdi(g, dil)) }) }
            }
            if (liste.isEmpty()) {
                Text(cevir(dil, "Akışın için bir konu seç.", "Choose a topic for your feed."), Modifier.padding(24.dp), color = Renk.metin)
                Button(onClick = { kesfeGit(Kategoriler.gruplar.first()) }, modifier = Modifier.padding(horizontal = 20.dp)) { Text(cevir(dil, "Konuları keşfet", "Explore topics")) }
            } else {
                HorizontalPager(pager, contentPadding = PaddingValues(horizontal = 20.dp), pageSpacing = 12.dp, key = { liste[it].kimlik }) { sayfa ->
                    val soz = liste[sayfa]
                    val atmosfer = secilenAtmosfer?.let { Atmosfer.valueOf(it) } ?: Atmosfer.grup(Kategoriler.bul(soz.kategori)?.grup)
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))) {
                        AtmosferResmi(atmosfer, Modifier.matchParentSize(), karartma = .30f)
                        Column(Modifier.fillMaxWidth().heightIn(min = 424.dp).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(cevir(dil, "GÜNÜN İLHAMI", "DAILY INSPIRATION"), color = Color.White, fontSize = 10.sp, letterSpacing = 2.sp, modifier = Modifier.weight(1f))
                                IconButton(onClick = { temaSecimi = true }, modifier = Modifier.size(48.dp)) { Icon(AzimIkon.Izgara, cevir(dil, "Arka planı değiştir", "Change background"), tint = Color.White, modifier = Modifier.size(20.dp)) }
                            }
                            Spacer(Modifier.height(24.dp))
                            Text(soz.metin(dil), color = Color.White, fontFamily = LoraSerif, fontSize = if (soz.metin(dil).length > 160) 23.sp else 27.sp, lineHeight = 36.sp, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
                            Spacer(Modifier.height(18.dp))
                            Box(Modifier.size(28.dp, 1.dp).background(Color.White.copy(alpha = .65f)))
                            Spacer(Modifier.height(12.dp))
                            Text(if (soz.yazar == "Ascend") cevir(dil, "Günlük olumlama", "Daily affirmation") else soz.imza(dil), color = Color.White, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.weight(1f))
                            Spacer(Modifier.height(40.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                KartEylemi(if (soz.kimlik in favoriler) AzimIkon.KalpDolu else AzimIkon.Kalp, cevir(dil, if (soz.kimlik in favoriler) "Kaydedildi" else "Kaydet", if (soz.kimlik in favoriler) "Saved" else "Save")) { favoriDegistir(soz) }
                                KartEylemi(AzimIkon.Paylas, cevir(dil, "Paylaş", "Share")) { paylas(soz) }
                                KartEylemi(if (ses.konusuyor) AzimIkon.SesDur else AzimIkon.Ses, cevir(dil, if (ses.konusuyor) "Durdur" else "Dinle", if (ses.konusuyor) "Stop" else "Listen")) {
                                    if (ses.hazir) ses.degistir(soz.metin(dil)) else kapsam.launch { mesaj.showSnackbar(cevir(dil, "Bu dil için cihazın ses paketi hazır değil.", "Your device voice for this language is not ready.")) }
                                }
                                KartEylemi(AzimIkon.Kopyala, cevir(dil, "Kopyala", "Copy")) { pano.setText(AnnotatedString(soz.metin(dil))); kapsam.launch { mesaj.showSnackbar(cevir(dil, "Söz kopyalandı", "Quote copied")) } }
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { kapsam.launch { pager.animateScrollToPage(pager.currentPage - 1) } }, enabled = pager.currentPage > 0) { Text(cevir(dil, "Önceki", "Previous")) }
                    Text("${pager.currentPage + 1} / ${liste.size}  ·  ${cevir(dil, "Kaydır", "Swipe")}", style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil)
                    TextButton(onClick = { kapsam.launch { pager.animateScrollToPage(pager.currentPage + 1) } }, enabled = pager.currentPage < liste.lastIndex) { Text(cevir(dil, "Sonraki", "Next")) }
                }
            }
            Surface(onClick = ayarlaraGit, color = Renk.yuzey, shape = RoundedCornerShape(22.dp), modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(color = Renk.accentZemin, shape = CircleShape) { Icon(AzimIkon.Ses, null, Modifier.padding(12.dp).size(22.dp), tint = Renk.accent) }
                    Column(Modifier.weight(1f)) {
                        Text(cevir(dil, "Gününe yayılan ilham", "Inspiration throughout your day"), color = Renk.metin, style = MaterialTheme.typography.titleSmall)
                        Text(when { !hatirlaticiAcik -> cevir(dil, "Bildirimlerini kişiselleştir", "Personalize reminders"); !bildirimIzni -> cevir(dil, "Bildirim izni gerekli", "Notification permission needed"); sonrakiBildirim != null -> cevir(dil, "Sıradaki $sonrakiBildirim · Günde $bugunPlanlanan söz", "Next $sonrakiBildirim · $bugunPlanlanan quotes a day"); else -> cevir(dil, "Günde $bugunPlanlanan söz · Planın açık", "$bugunPlanlanan quotes a day · Plan is on") }, color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                    }
                    Text("↗", color = Renk.accent, fontSize = 24.sp)
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp), verticalAlignment = Alignment.CenterVertically) {
                KucukBaslik(cevir(dil, "Biraz da keşfet", "A little more inspiration"), Modifier.weight(1f))
                TextButton(onClick = { kesfeGit(Kategoriler.gruplar.first()) }) { Text(cevir(dil, "Tümü", "See all")) }
            }
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(Kategoriler.gruplar.filter { it.anahtar in setOf("azim", "filozoflar", "olumlamalar", "disiplin") }) { g ->
                    Box(Modifier.width(156.dp).height(150.dp).clip(RoundedCornerShape(22.dp)).clickable { kesfeGit(g) }) {
                        AtmosferResmi(Atmosfer.grup(g.anahtar), Modifier.matchParentSize())
                        Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                            Text(kisaGrupAdi(g, dil), color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(cevir(dil, "Koleksiyonu aç →", "Open collection →"), color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            if (bugunGelenler.isNotEmpty()) {
                KucukBaslik(cevir(dil, "Bugün gelen sözler", "Today's reminders"), Modifier.padding(horizontal = 22.dp))
                bugunGelenler.forEach { soz -> TextButton(onClick = { filtre = null; sozSecildi(soz) }, modifier = Modifier.padding(horizontal = 16.dp)) { Text(soz.metin(dil), textAlign = TextAlign.Start) } }
            }
            Spacer(Modifier.height(16.dp))
        }
        SnackbarHost(mesaj, Modifier.align(Alignment.BottomCenter))
    }
    if (temaSecimi) AlertDialog(onDismissRequest = { temaSecimi = false }, title = { Text(cevir(dil, "Manzaranı seç", "Choose your scenery")) }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Atmosfer.entries.forEach { a -> Surface(onClick = { secilenAtmosfer = a.name; temaSecimi = false }, shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.fillMaxWidth().height(70.dp)) { AtmosferResmi(a, Modifier.matchParentSize()); Text(a.ad(dil), Modifier.align(Alignment.CenterStart).padding(16.dp), color = Color.White) }
            } }
            TextButton(onClick = { secilenAtmosfer = null; temaSecimi = false }) { Text(cevir(dil, "Konuya göre otomatik", "Match the topic")) }
        }
    }, confirmButton = { TextButton(onClick = { temaSecimi = false }) { Text(cevir(dil, "Kapat", "Close")) } })
}

@Composable
private fun KartEylemi(ikon: ImageVector, ad: String, tikla: () -> Unit) {
    Column(Modifier.widthIn(min = 58.dp).clip(RoundedCornerShape(14.dp)).clickable(role = Role.Button, onClick = tikla).padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(ikon, null, Modifier.size(24.dp), tint = Color.White)
        Spacer(Modifier.height(6.dp))
        Text(ad, color = Color.White, fontSize = 11.sp)
    }
}

fun kisaGrupAdi(g: KategoriGrubu, dil: String) = when (g.anahtar) {
    "azim" -> cevir(dil, "Motivasyon", "Motivation")
    "olumlamalar" -> cevir(dil, "Olumlamalar", "Affirmations")
    "disiplin" -> cevir(dil, "Odak", "Focus")
    "filozoflar" -> cevir(dil, "Felsefe", "Philosophy")
    else -> g.ad(dil)
}
