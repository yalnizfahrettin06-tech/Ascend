package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnaEkran(
    sozler: List<Soz>, aktifIndeks: Int, favoriler: Set<String>, seri: Int,
    haftalik: List<Boolean>, bugunGelenler: List<Soz>, gunlukGelenler: Map<String, List<Soz>>,
    bugunGorulen: Int, gunlukHedef: Int, sonrakiBildirim: String?, oneri: Oneri?,
    bugunPlanlanan: Int, dil: String, hatirlaticiAcik: Boolean, bildirimIzni: Boolean,
    indeksDegisti: (Int) -> Unit, favoriDegistir: (Soz) -> Unit, paylas: (Soz) -> Unit,
    sozSecildi: (Soz) -> Unit, kesfeGit: (KategoriGrubu) -> Unit,
    ipucunuKapat: () -> Unit, ayarlaraGit: () -> Unit,
    secilenAtmosfer: String? = null, atmosferSec: (String?) -> Unit = {},
    seciliKonular: Set<String> = emptySet(), konulariDuzenle: () -> Unit = {}, haptikAcik: Boolean = true,
) {
    var filtre by rememberSaveable { mutableStateOf<String?>(null) }
    val konular = remember(sozler) { Kategoriler.tumAltlar.filter { k -> sozler.any { it.kategori == k.anahtar } } }
    val haptik = LocalHapticFeedback.current
    val ikiSutunEylem = LocalDensity.current.fontScale > 1.3f || LocalConfiguration.current.screenWidthDp < 360
    LaunchedEffect(konular) { if (filtre != null && konular.none { it.anahtar == filtre }) filtre = null }
    val liste = remember(sozler, filtre) { sozler.filter { filtre == null || it.kategori == filtre }.ifEmpty { sozler } }
    val pager = rememberPagerState { liste.size.coerceAtLeast(1) }
    val kapsam = rememberCoroutineScope()
    val mesaj = remember { SnackbarHostState() }
    val pano = LocalClipboardManager.current
    val ses = rememberSeslendirici(dil)
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
        Column(Modifier.fillMaxSize().statusBarsPadding().testTag("home-content").verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth().padding(start = 22.dp, end = 20.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("ASCEND", fontSize = 21.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Medium, color = Renk.metin)
                    Text(LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM, EEEE", Locale.forLanguageTag(dil))), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                }
                YuvarlakIkon(AzimIkon.Ayarlar, cevir(dil, "Ayarlar", "Settings"), ayarlaraGit)
            }
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(selected = filtre == null, onClick = { filtre = null }, label = { Text(cevir(dil, "Senin için", "For you")) }, shape = RoundedCornerShape(50)) }
                items(konular, key = { it.anahtar }) { k -> FilterChip(selected = filtre == k.anahtar, onClick = { filtre = k.anahtar }, label = { Text(k.ad(dil)) }, shape = RoundedCornerShape(50)) }
            }
            if (liste.isEmpty()) {
                Text(cevir(dil, "Akışın için bir konu seç.", "Choose a topic for your feed."), Modifier.padding(24.dp), color = Renk.metin)
                Button(onClick = konulariDuzenle, modifier = Modifier.padding(horizontal = 20.dp)) { Text(cevir(dil, "Konuları keşfet", "Explore topics")) }
            } else {
                HorizontalPager(pager, contentPadding = PaddingValues(horizontal = 20.dp), pageSpacing = 12.dp, key = { liste[it].kimlik }) { sayfa ->
                    val soz = liste[sayfa]
                    val atmosfer = secilenAtmosfer?.let { runCatching { Atmosfer.valueOf(it) }.getOrNull() } ?: Atmosfer.grup(Kategoriler.bul(soz.kategori)?.grup)
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).testTag(if (sayfa == pager.settledPage) "active-quote" else "other-quote")) {
                        AtmosferResmi(atmosfer, Modifier.matchParentSize(), karartma = .30f)
                        Column(Modifier.fillMaxWidth().heightIn(min = 424.dp).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(Kategoriler.bul(soz.kategori)?.ad(dil)?.uppercase(Locale.forLanguageTag(dil)) ?: cevir(dil, "GÜNÜN İLHAMI", "DAILY INSPIRATION"), color = Color.White, fontSize = 10.sp, letterSpacing = 2.sp, modifier = Modifier.weight(1f))
                                IconButton(onClick = { temaSecimi = true }, modifier = Modifier.size(48.dp)) { Icon(AzimIkon.Izgara, cevir(dil, "Arka planı değiştir", "Change background"), tint = Color.White, modifier = Modifier.size(20.dp)) }
                            }
                            Spacer(Modifier.height(24.dp))
                            Text(soz.metin(dil), color = Color.White, fontFamily = LoraSerif, fontSize = if (soz.metin(dil).length > 160) 23.sp else 27.sp, lineHeight = 36.sp, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
                            Spacer(Modifier.height(18.dp))
                            Box(Modifier.size(28.dp, 1.dp).background(Color.White.copy(alpha = .65f)))
                            Spacer(Modifier.height(12.dp))
                            Text(soz.sunumEtiketi(dil), color = Color.White, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.weight(1f))
                            Spacer(Modifier.height(40.dp))
                            FlowRow(Modifier.fillMaxWidth(), maxItemsInEachRow = if (ikiSutunEylem) 2 else 4,
                                horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                KartEylemi(if (soz.kimlik in favoriler) AzimIkon.KalpDolu else AzimIkon.Kalp, cevir(dil, if (soz.kimlik in favoriler) "Kaydedildi" else "Kaydet", if (soz.kimlik in favoriler) "Saved" else "Save"), Modifier.weight(1f)) { if (haptikAcik) haptik.performHapticFeedback(HapticFeedbackType.TextHandleMove); favoriDegistir(soz) }
                                KartEylemi(AzimIkon.Paylas, cevir(dil, "Paylaş", "Share"), Modifier.weight(1f)) { paylas(soz) }
                                KartEylemi(if (ses.konusuyor) AzimIkon.SesDur else AzimIkon.Ses, cevir(dil, if (ses.konusuyor) "Durdur" else "Dinle", if (ses.konusuyor) "Stop" else "Listen"), Modifier.weight(1f)) {
                                    if (ses.hazir) ses.degistir(soz.metin(dil)) else kapsam.launch { mesaj.showSnackbar(cevir(dil, "Bu dil için cihazın ses paketi hazır değil.", "Your device voice for this language is not ready.")) }
                                }
                                KartEylemi(AzimIkon.Kopyala, cevir(dil, "Kopyala", "Copy"), Modifier.weight(1f)) { pano.setText(AnnotatedString(soz.metin(dil))); kapsam.launch { mesaj.showSnackbar(cevir(dil, "Söz kopyalandı", "Quote copied")) } }
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { kapsam.launch { pager.animateScrollToPage(pager.currentPage - 1) } }, enabled = pager.currentPage > 0) { Text(cevir(dil, "Önceki", "Previous")) }
                    Text("${pager.currentPage + 1} / ${liste.size}  ·  ${cevir(dil, "Kaydır", "Swipe")}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil)
                    TextButton(onClick = { kapsam.launch { pager.animateScrollToPage(pager.currentPage + 1) } }, enabled = pager.currentPage < liste.lastIndex) { Text(cevir(dil, "Sonraki", "Next")) }
                }
            }
            SeciliKonular(seciliKonular, dil, konulariDuzenle, Modifier.padding(horizontal = 20.dp))
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
                TextButton(onClick = konulariDuzenle) { Text(cevir(dil, "Tümü", "See all")) }
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
    if (temaSecimi) AtmosferSecici(
        dil = dil,
        secili = secilenAtmosfer?.let { runCatching { Atmosfer.valueOf(it) }.getOrNull() },
        otomatik = Atmosfer.grup(Kategoriler.bul(aktif?.kategori.orEmpty())?.grup),
        sec = { atmosferSec(it?.name); temaSecimi = false },
        kapat = { temaSecimi = false },
    )
}

@Composable
private fun KartEylemi(ikon: ImageVector, ad: String, modifier: Modifier = Modifier, tikla: () -> Unit) {
    Column(modifier.heightIn(min = 64.dp).animateContentSize().clip(RoundedCornerShape(14.dp)).clickable(role = Role.Button, onClick = tikla).padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(ikon, null, Modifier.size(24.dp), tint = Color.White)
        Spacer(Modifier.height(6.dp))
        Text(ad, color = Color.White, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

fun kisaGrupAdi(g: KategoriGrubu, dil: String) = when (g.anahtar) {
    "azim" -> cevir(dil, "Motivasyon", "Motivation")
    "olumlamalar" -> cevir(dil, "Olumlamalar", "Affirmations")
    "disiplin" -> cevir(dil, "Odak", "Focus")
    "filozoflar" -> cevir(dil, "Felsefe", "Philosophy")
    else -> g.ad(dil)
}
