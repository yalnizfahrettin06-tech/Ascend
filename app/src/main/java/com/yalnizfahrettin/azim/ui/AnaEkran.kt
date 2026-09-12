package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.yalnizfahrettin.azim.R
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import kotlinx.coroutines.launch

/** Primary actions remain visible. Only the quote scrolls when large text needs more room. */
@OptIn(ExperimentalMaterial3Api::class)
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
    gizle: (Soz) -> Unit = {},
    kullaniciAdi: String = "", planAc: () -> Unit = {}, ihtiyac: String? = null, ihtiyacSec: (String?) -> Unit = {},
) {
    // A feed replacement must replace its count, keys and page content together.
    // Updating the count of an existing pager while a lazy layout still holds the
    // preceding key provider can otherwise address a page outside that old list.
    val feed = remember(sozler) { sozler.toList() }
    val pager = key(feed) {
        rememberPagerState(initialPage = aktifIndeks.coerceIn(0, feed.lastIndex.coerceAtLeast(0))) { feed.size }
    }
    val kapsam = rememberCoroutineScope()
    val mesaj = remember { SnackbarHostState() }
    val pano = LocalClipboardManager.current
    val ses = rememberSeslendirici(dil)
    val haptik = LocalHapticFeedback.current
    var araclar by remember { mutableStateOf(false) }
    val buyukYazi = LocalDensity.current.fontScale > 1.35f
    val darEkran = LocalConfiguration.current.screenWidthDp < 360
    val darEylemler = buyukYazi || darEkran
    val shareInteraction = remember { MutableInteractionSource() }
    val sharePressed by shareInteraction.collectIsPressedAsState()
    val shareFocused by shareInteraction.collectIsFocusedAsState()
    val saveInteraction = remember { MutableInteractionSource() }
    val saveFocused by saveInteraction.collectIsFocusedAsState()
    val shareSurface by animateColorAsState(
        if (sharePressed) Renk.markaBasiliYuzeyi else Renk.markaYuzeyi,
        animationSpec = tween(140), label = "share-surface",
    )
    LaunchedEffect(pager, aktifIndeks) {
        if (feed.isNotEmpty()) {
            val hedef = aktifIndeks.coerceIn(feed.indices)
            if (pager.currentPage != hedef) pager.scrollToPage(hedef)
        }
    }
    val aktif = feed.getOrNull(pager.settledPage)
    LaunchedEffect(pager, aktif?.kimlik) { if (aktif != null) indeksDegisti(pager.settledPage); ses.durdur() }
    Box(Modifier.fillMaxSize().background(Renk.zemin)) {
        val homeTheme = AnaTemalar.find(secilenAtmosfer)
        TemaZemini(homeTheme, Modifier.matchParentSize(), if (buyukYazi) .94f else .25f)
        Column(Modifier.fillMaxSize().statusBarsPadding().testTag("home-content")) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(AzimIkon.YukselenMarka, null, Modifier.size(30.dp), tint = Renk.accent)
                Spacer(Modifier.width(10.dp))
                Text("ascend", fontSize = 32.sp, fontFamily = LoraSerif, color = Renk.metin,
                    letterSpacing = (-1).sp, modifier = Modifier.weight(1f))
                TextButton(onClick = planAc, modifier = Modifier.heightIn(min = 48.dp).testTag("home-plan")) {
                    Text(cevir(dil, "Planım", "My plan"), color = Renk.metin, fontSize = 14.sp)
                    Spacer(Modifier.width(5.dp)); Icon(AzimIkon.Disari, null, Modifier.size(20.dp), tint = Renk.accent)
                }
            }
            Column(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp)) {
            if (feed.isEmpty()) {
                Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(cevir(dil, "Biraz yer açalım.", "Make a little room."), color = Renk.metin, style = MaterialTheme.typography.headlineLarge)
                    Text(cevir(dil, "Bu tercihlere uygun açık bir konu yok. Planını değiştirebilir veya yeni bir konu açabilirsin.", "No unlocked topic matches these preferences. Adjust your plan or unlock another topic."), color = Renk.metinIkincil, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 16.dp))
                    Button(onClick = planAc) { Text(cevir(dil, "Planımı düzenle", "Edit my plan")) }
                    TextButton(onClick = konulariDuzenle) { Text(cevir(dil, "Konuları keşfet", "Explore topics")) }
                }
            } else {
                HorizontalPager(state = pager, modifier = Modifier.weight(1f).fillMaxWidth().testTag("quote-pager"),
                    key = { sayfa -> feed.getOrNull(sayfa)?.kimlik ?: sayfa }) { sayfa ->
                    // Discard an obsolete prefetch slot, not the active feed.
                    val soz = feed.getOrNull(sayfa) ?: return@HorizontalPager
                    val metin = soz.metin(dil)
                    val kisa = metin.length <= 80
                    val uzun = metin.length > 150
                    // Short thoughts can breathe at a larger size; longer passages
                    // use the full measure instead of forcing isolated trailing words.
                    val yaziBoyutu = when {
                        buyukYazi -> if (kisa) 30 else if (uzun) 26 else 28
                        darEkran -> if (kisa) 33 else if (uzun) 28 else 30
                        else -> if (kisa) 34 else if (uzun) 26 else 30
                    }
                    val satirYuksekligi = yaziBoyutu + 5
                    val metinGenisligi = if (buyukYazi || homeTheme.art == null) 1f else .76f
                    Column(Modifier.fillMaxSize().testTag(if (sayfa == pager.settledPage) "active-quote" else "other-quote")
                        .verticalScroll(rememberScrollState()).padding(top = 12.dp, bottom = 12.dp),
                        horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                        Row(Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(Modifier.padding(top = with(LocalDensity.current) { 9.sp.toDp() })
                                .width(28.dp).height(2.dp).background(Renk.accent))
                            Text(Kategoriler.bul(soz.kategori)?.ad(dil).orEmpty(), color = Renk.metinIkincil,
                                fontSize = 12.sp, lineHeight = 18.sp, letterSpacing = 0.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(metin, color = Renk.metin, fontFamily = LoraSerif,
                            fontSize = yaziBoyutu.sp, lineHeight = satirYuksekligi.sp,
                            letterSpacing = (if (kisa) -.6 else -.4).sp, textAlign = TextAlign.Start,
                            style = TextStyle(lineBreak = if (kisa) LineBreak.Heading else LineBreak.Paragraph),
                            modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(metinGenisligi).semantics { heading() })

                    }
                }
            }
            if (aktif != null) {
                Row(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    val kayitli = aktif.kimlik in favoriler
                    Text(aktif.sunumEtiketi(dil), Modifier.weight(1f), color = Renk.metinIkincil, fontSize = 10.sp, lineHeight = 15.sp)
                    IconButton(onClick = { if (haptikAcik) haptik.performHapticFeedback(HapticFeedbackType.TextHandleMove); favoriDegistir(aktif) },
                        modifier = Modifier.testTag("home-save").semantics { selected = kayitli }) {
                        Icon(if (kayitli) AzimIkon.KalpDolu else AzimIkon.Kalp,
                            cevir(dil, if (kayitli) "Kaydedilenlerden çıkar" else "Sözü kaydet", if (kayitli) "Remove from saved" else "Save quote"),
                            Modifier.size(22.dp), tint = if (kayitli) Renk.accent else Renk.metin)
                    }
                    IconButton(onClick = { paylas(aktif) }, modifier = Modifier.testTag("home-share")) {
                        Icon(AzimIkon.Paylas, cevir(dil, "Paylaş", "Share"), Modifier.size(22.dp), tint = Renk.metin)
                    }
                    Box {
                        IconButton(onClick = { araclar = true }, modifier = Modifier.testTag("home-more")) {
                            Icon(AzimIkon.Daha, cevir(dil, "Diğer araçlar", "More tools"), Modifier.size(22.dp), tint = Renk.metin)
                        }
                        DropdownMenu(expanded = araclar, onDismissRequest = { araclar = false }) {
                            DropdownMenuItem(text = { Text(cevir(dil, "Sonraki söz", "Next quote")) }, onClick = {
                                araclar = false; kapsam.launch { pager.animateScrollToPage((pager.currentPage + 1) % feed.size) }
                            }, leadingIcon = { Icon(AzimIkon.Sonraki, null) })
                            DropdownMenuItem(text = { Text(cevir(dil, "Önceki söz", "Previous quote")) }, enabled = pager.currentPage > 0, onClick = {
                                araclar = false; kapsam.launch { pager.animateScrollToPage((pager.currentPage - 1).coerceAtLeast(0)) }
                            }, leadingIcon = { Icon(AzimIkon.Geri, null) })
                            DropdownMenuItem(text = { Text(cevir(dil, if (ses.konusuyor) "Sesi durdur" else "Sesli dinle", if (ses.konusuyor) "Stop reading" else "Listen")) }, onClick = {
                                araclar = false
                                if (ses.hazir) ses.degistir(aktif.metin(dil))
                                else kapsam.launch { mesaj.showSnackbar(cevir(dil, "Cihazında bu dilin ses paketi hazır değil.", "Your device voice for this language is not ready.")) }
                            }, leadingIcon = { Icon(AzimIkon.Ses, null) })
                            DropdownMenuItem(text = { Text(cevir(dil, "Metni kopyala", "Copy text")) }, onClick = {
                                araclar = false; pano.setText(AnnotatedString(aktif.metin(dil)))
                                kapsam.launch { mesaj.showSnackbar(cevir(dil, "Söz kopyalandı", "Quote copied")) }
                            }, leadingIcon = { Icon(AzimIkon.Kopyala, null) })
                            DropdownMenuItem(text = { Text(cevir(dil, "Bu sözü gizle", "Hide this quote")) },
                                modifier = Modifier.testTag("home-hide"), onClick = { araclar = false; gizle(aktif) })
                            DropdownMenuItem(text = { Text(cevir(dil, "Bildirim ayarları", "Reminder settings")) }, onClick = { araclar = false; ayarlaraGit() })
                        }
                    }
                }
            }
            }
        }
        SnackbarHost(mesaj, Modifier.align(Alignment.BottomCenter))
    }
}

/** Keeps the transient chooser independent of pager feed replacements. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnlikIhtiyacSecimi(
    ihtiyac: String?, dil: String, ihtiyacSec: (String?) -> Unit, modifier: Modifier = Modifier,
) {
    var ihtiyaclar by rememberSaveable { mutableStateOf(false) }
    TextButton(
        onClick = { ihtiyaclar = true },
        modifier = modifier.heightIn(min = 48.dp).testTag("home-moment"),
    ) {
        Text(ihtiyacAdi(ihtiyac, dil) + cevir(dil, " · Şimdi", " · Now"), color = Renk.metin, fontSize = 14.sp)
        Spacer(Modifier.width(6.dp))
        Icon(AzimIkon.Asagi, null, Modifier.size(16.dp), tint = Renk.metinIkincil)
    }
    if (ihtiyaclar) ModalBottomSheet(onDismissRequest = { ihtiyaclar = false }, containerColor = Renk.zemin) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp)) {
            Text(cevir(dil, "Şu an sana ne iyi gelir?", "What would help right now?"), color = Renk.metin, style = MaterialTheme.typography.headlineLarge)
            Text(cevir(dil, "Yalnız bu anın akışını değiştirir. Bildirim planın aynı kalır.", "Only changes this moment's feed. Your reminder plan stays the same."), color = Renk.metinIkincil, modifier = Modifier.padding(top = 12.dp, bottom = 16.dp))
            listOf<String?>(null, "calm", "action", "focus", "perspective").forEach { key ->
                TextButton(onClick = { ihtiyacSec(key); ihtiyaclar = false }, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("moment-${key ?: "plan"}").semantics { selected = key == ihtiyac }) {
                    Text(ihtiyacAdi(key, dil), Modifier.weight(1f), textAlign = TextAlign.Start, color = Renk.metin)
                    if (key == ihtiyac) Icon(AzimIkon.Tik, null, Modifier.size(18.dp), tint = Renk.metin)
                }
                HorizontalDivider(color = Renk.kenarlik)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

fun ihtiyacAdi(key: String?, dil: String): String = when (key) {
    "calm" -> cevir(dil, "Biraz sakinlik", "A little calm")
    "action" -> cevir(dil, "Harekete geçmek", "Take a step")
    "focus" -> cevir(dil, "Yeniden odaklanmak", "Find my focus")
    "perspective" -> cevir(dil, "Yeni bir bakış", "A fresh perspective")
    else -> cevir(dil, "Sana göre", "For you")
}
fun kisaGrupAdi(g: KategoriGrubu, dil: String) = when (g.anahtar) {
    "azim" -> cevir(dil, "Motivasyon", "Motivation")
    "olumlamalar" -> cevir(dil, "Olumlamalar", "Affirmations")
    "disiplin" -> cevir(dil, "Odak", "Focus")
    "filozoflar" -> cevir(dil, "Felsefe", "Philosophy")
    else -> g.ad(dil)
}
