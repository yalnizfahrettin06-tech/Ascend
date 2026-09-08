package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
    kullaniciAdi: String = "", planAc: () -> Unit = {}, ihtiyac: String? = null, ihtiyacSec: (String?) -> Unit = {},
) {
    val pager = rememberPagerState { sozler.size.coerceAtLeast(1) }
    val kapsam = rememberCoroutineScope()
    val mesaj = remember { SnackbarHostState() }
    val pano = LocalClipboardManager.current
    val ses = rememberSeslendirici(dil)
    val haptik = LocalHapticFeedback.current
    var araclar by remember { mutableStateOf(false) }
    var ihtiyaclar by rememberSaveable { mutableStateOf(false) }
    val buyukYazi = LocalDensity.current.fontScale > 1.35f
    val darEylemler = buyukYazi || LocalConfiguration.current.screenWidthDp < 360
    LaunchedEffect(aktifIndeks, sozler) {
        if (sozler.isNotEmpty() && aktifIndeks in sozler.indices && pager.currentPage != aktifIndeks) pager.scrollToPage(aktifIndeks)
    }
    val aktif = sozler.getOrNull(pager.settledPage)
    LaunchedEffect(aktif?.kimlik) { if (aktif != null) indeksDegisti(pager.settledPage); ses.durdur() }
    Box(Modifier.fillMaxSize().background(Renk.zemin)) {
        Column(Modifier.fillMaxSize().statusBarsPadding().testTag("home-content").padding(horizontal = 24.dp)) {
            Row(Modifier.fillMaxWidth().heightIn(min = 60.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(AzimIkon.Yukselis, if (buyukYazi) "Ascend" else null, Modifier.size(28.dp), tint = Renk.metin)
                Spacer(Modifier.width(10.dp))
                if (buyukYazi) Spacer(Modifier.weight(1f))
                else Text("ascend", color = Renk.metin, fontSize = 20.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = planAc, modifier = Modifier.testTag("home-plan")) {
                    Text(cevir(dil, "Planım", "My plan"), color = Renk.metin, fontSize = 13.sp)
                    Text("  ↗", color = Renk.metin)
                }
            }
            TextButton(onClick = { ihtiyaclar = true }, modifier = Modifier.align(Alignment.CenterHorizontally).testTag("home-moment")) {
                Text(ihtiyacAdi(ihtiyac, dil), color = Renk.metinIkincil, fontSize = 13.sp)
                Text("  ⌄", color = Renk.metinIkincil)
            }
            if (sozler.isEmpty()) {
                Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(cevir(dil, "Biraz yer açalım.", "Make a little room."), color = Renk.metin, style = MaterialTheme.typography.headlineLarge)
                    Text(cevir(dil, "Bu tercihlere uygun açık bir konu yok. Planını değiştirebilir veya yeni bir konu açabilirsin.", "No unlocked topic matches these preferences. Adjust your plan or unlock another topic."), color = Renk.metinIkincil, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 16.dp))
                    Button(onClick = planAc) { Text(cevir(dil, "Planımı düzenle", "Edit my plan")) }
                    TextButton(onClick = konulariDuzenle) { Text(cevir(dil, "Konuları keşfet", "Explore topics")) }
                }
            } else {
                HorizontalPager(state = pager, modifier = Modifier.weight(1f).fillMaxWidth().testTag("quote-pager"), key = { sozler[it].kimlik }) { sayfa ->
                    val soz = sozler[sayfa]
                    Column(Modifier.fillMaxSize().testTag(if (sayfa == pager.settledPage) "active-quote" else "other-quote")
                        .verticalScroll(rememberScrollState()).padding(horizontal = 6.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(Kategoriler.bul(soz.kategori)?.ad(dil).orEmpty(), color = Renk.metinIkincil, fontSize = 12.sp, letterSpacing = .5.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(if (buyukYazi) 16.dp else 28.dp))
                        Text(soz.metin(dil), color = Renk.metin, fontFamily = LoraSerif, fontSize = 29.sp, lineHeight = 40.sp, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
                        Spacer(Modifier.height(24.dp))
                        Box(Modifier.width(24.dp).height(1.dp).background(Renk.kenarlikGuclu))
                        Spacer(Modifier.height(14.dp))
                        Text(soz.sunumEtiketi(dil), color = Renk.metinIkincil, fontSize = 11.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)
                    }
                }
            }
            if (aktif != null) {
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { kapsam.launch { pager.animateScrollToPage(pager.currentPage - 1) } }, enabled = pager.currentPage > 0, modifier = Modifier.testTag("quote-previous")) {
                        Icon(AzimIkon.Geri, cevir(dil, "Önceki söz", "Previous quote"), modifier = Modifier.size(20.dp), tint = if (pager.currentPage > 0) Renk.metin else Renk.kenarlikGuclu)
                    }
                    Text(cevir(dil, "Bir söz. Bir adım.", "One thought. One step."), color = Renk.metinIkincil, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    IconButton(onClick = { kapsam.launch { pager.animateScrollToPage((pager.currentPage + 1).coerceAtMost(sozler.lastIndex)) } }, enabled = pager.currentPage < sozler.lastIndex, modifier = Modifier.testTag("quote-next")) {
                        Text("→", color = Renk.metin, fontSize = 24.sp, modifier = Modifier.semantics { contentDescription = cevir(dil, "Sonraki söz", "Next quote") })
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    val kayitli = aktif.kimlik in favoriler
                    OutlinedButton(onClick = { if (haptikAcik) haptik.performHapticFeedback(HapticFeedbackType.TextHandleMove); favoriDegistir(aktif) },
                        modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("home-save").semantics {
                            selected = kayitli
                            contentDescription = cevir(dil, if (kayitli) "Kaydedilenlerden çıkar" else "Sözü kaydet", if (kayitli) "Remove from saved" else "Save quote")
                        },
                        shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Renk.kenarlik), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)) {
                        if (darEylemler) Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Icon(if (kayitli) AzimIkon.KalpDolu else AzimIkon.Kalp, null, Modifier.size(20.dp))
                            Text(cevir(dil, if (kayitli) "Kayıtlı" else "Kaydet", if (kayitli) "Saved" else "Save"), fontSize = 12.sp, lineHeight = 15.sp)
                        } else {
                            Icon(if (kayitli) AzimIkon.KalpDolu else AzimIkon.Kalp, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(7.dp))
                            Text(cevir(dil, if (kayitli) "Kayıtlı" else "Kaydet", if (kayitli) "Saved" else "Save"), fontSize = 13.sp)
                        }
                    }
                    Button(onClick = { paylas(aktif) }, modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("home-share"), shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)) {
                        if (darEylemler) Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Icon(AzimIkon.Paylas, null, Modifier.size(19.dp))
                            Text(cevir(dil, "Paylaş", "Share"), fontSize = 12.sp, lineHeight = 15.sp)
                        } else {
                            Icon(AzimIkon.Paylas, null, Modifier.size(19.dp)); Spacer(Modifier.width(7.dp))
                            Text(cevir(dil, "Paylaş", "Share"), fontSize = 13.sp)
                        }
                    }
                    Box {
                        IconButton(onClick = { araclar = true }, modifier = Modifier.testTag("home-more")) {
                            Text("···", color = Renk.metin, fontSize = 26.sp, modifier = Modifier.semantics { contentDescription = cevir(dil, "Diğer araçlar", "More tools") })
                        }
                        DropdownMenu(expanded = araclar, onDismissRequest = { araclar = false }) {
                            DropdownMenuItem(text = { Text(cevir(dil, if (ses.konusuyor) "Sesi durdur" else "Sesli dinle", if (ses.konusuyor) "Stop reading" else "Listen")) }, onClick = {
                                araclar = false
                                if (ses.hazir) ses.degistir(aktif.metin(dil))
                                else kapsam.launch { mesaj.showSnackbar(cevir(dil, "Cihazında bu dilin ses paketi hazır değil.", "Your device voice for this language is not ready.")) }
                            }, leadingIcon = { Icon(AzimIkon.Ses, null) })
                            DropdownMenuItem(text = { Text(cevir(dil, "Metni kopyala", "Copy text")) }, onClick = {
                                araclar = false; pano.setText(AnnotatedString(aktif.metin(dil)))
                                kapsam.launch { mesaj.showSnackbar(cevir(dil, "Söz kopyalandı", "Quote copied")) }
                            }, leadingIcon = { Icon(AzimIkon.Kopyala, null) })
                            DropdownMenuItem(text = { Text(cevir(dil, "Bildirim ayarları", "Reminder settings")) }, onClick = { araclar = false; ayarlaraGit() })
                        }
                    }
                }
            }
            Text(when {
                kullaniciAdi.isNotBlank() -> cevir(dil, "$kullaniciAdi, kendi hızında.", "$kullaniciAdi, at your own pace.")
                hatirlaticiAcik && bildirimIzni && sonrakiBildirim != null -> cevir(dil, "Sıradaki küçük hatırlatma · $sonrakiBildirim", "Your next reminder · $sonrakiBildirim")
                else -> cevir(dil, "Kendi hızında.", "At your own pace.")
            }, color = Renk.metinIkincil, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp))
        }
        SnackbarHost(mesaj, Modifier.align(Alignment.BottomCenter))
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
    else -> cevir(dil, "Sana göre · Şimdi", "For you · Right now")
}
fun kisaGrupAdi(g: KategoriGrubu, dil: String) = when (g.anahtar) {
    "azim" -> cevir(dil, "Motivasyon", "Motivation")
    "olumlamalar" -> cevir(dil, "Olumlamalar", "Affirmations")
    "disiplin" -> cevir(dil, "Odak", "Focus")
    "filozoflar" -> cevir(dil, "Felsefe", "Philosophy")
    else -> g.ad(dil)
}
