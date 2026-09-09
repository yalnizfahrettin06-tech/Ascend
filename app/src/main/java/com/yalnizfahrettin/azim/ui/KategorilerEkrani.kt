package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import java.util.Locale

@Composable
fun KilitDialog(kategori: Kategori, dil: String, kapat: () -> Unit, demoAc: () -> Unit, proAc: () -> Unit, hata: String? = null) {
    val sozSayisi = Sozler.kategoriden(kategori.anahtar).size
    AlertDialog(onDismissRequest = kapat, title = { Text(kategori.ad(dil)) }, text = {
        Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(cevir(dil, "$sozSayisi özgün söz · Yalnız bu kategori açılır", "$sozSayisi original quotes · Unlocks this topic only"), color = Renk.accent)
            Text(cevir(dil, "GEÇİCİ DEMO", "TEMPORARY DEMO"), style = MaterialTheme.typography.labelSmall)
            Text(cevir(dil, "Bu sürümde gerçek reklam yok. Google.com tarayıcıda açılır. Uygulamaya döndüğünde bu kategori açılır. Bildirimlerine eklemek senin seçimin.", "There is no real ad in this version. Google.com opens in your browser. Returning unlocks this topic. You choose whether to add it to your reminders."))
            TextButton(onClick = proAc) { Text(cevir(dil, "Tüm kategoriler için Pro’yu keşfet", "Explore Pro for all topics")) }
            hata?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }, confirmButton = { TextButton(onClick = demoAc) { Text(cevir(dil, "Demo bağlantısını aç", "Open demo link")) } },
    dismissButton = { TextButton(onClick = kapat) { Text(cevir(dil, "Vazgeç", "Cancel")) } })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategorilerEkrani(secili: Set<String>, acik: Set<String>, dil: String, sec: (String) -> Unit,
    kilidiAc: (Kategori) -> Unit, pro: Boolean, proAc: () -> Unit, acilacakGrup: String? = null,
) {
    var grupKey by rememberSaveable { mutableStateOf(acilacakGrup) }
    var sonAcilisGrubu by rememberSaveable { mutableStateOf(acilacakGrup) }
    var arama by rememberSaveable { mutableStateOf("") }
    var filtre by rememberSaveable { mutableStateOf("all") }
    var detayKey by rememberSaveable { mutableStateOf<String?>(null) }
    var alanlarAcik by rememberSaveable { mutableStateOf(false) }
    val odak = LocalFocusManager.current
    LaunchedEffect(acilacakGrup) {
        if (sonAcilisGrubu != acilacakGrup) { grupKey = acilacakGrup; sonAcilisGrubu = acilacakGrup }
    }
    val locale = Locale.forLanguageTag(dil)
    val query = arama.trim().lowercase(locale)
    val kategoriler = Kategoriler.tumAltlar.filter { k ->
        (grupKey == null || k.grup == grupKey) && (filtre != "selected" || k.anahtar in secili) &&
        (filtre != "open" || k.anahtar in acik) &&
        (query.isBlank() || k.ad(dil).lowercase(locale).contains(query) || k.adEn.lowercase(locale).contains(query))
    }
    LazyColumn(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().testTag("category-grid"),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)) {
        item {
            Column {
                Row(Modifier.fillMaxWidth().heightIn(min = 52.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(cevir(dil, "Keşfet", "Explore"), color = Renk.metin, fontFamily = LoraSerif,
                        fontSize = 30.sp, lineHeight = 38.sp, modifier = Modifier.weight(1f).semantics { heading() })
                    TextButton(onClick = proAc, modifier = Modifier.heightIn(min = 48.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)) {
                        Text(if (pro) "PRO DEMO" else "PRO", color = Renk.accent, fontSize = 11.sp, letterSpacing = 1.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                TextField(arama, { arama = it }, placeholder = { Text(cevir(dil, "Konu veya düşünür ara", "Search topics or thinkers"), fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("category-search").semantics {
                        contentDescription = cevir(dil, "Konu veya düşünür ara", "Search topics or thinkers")
                    }, singleLine = true, shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { odak.clearFocus() }),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Renk.yuzey, unfocusedContainerColor = Renk.yuzey,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                    leadingIcon = { Icon(AzimIkon.Ara, null, Modifier.size(20.dp)) },
                    trailingIcon = { if (arama.isNotEmpty()) IconButton(onClick = { arama = "" }) { Icon(AzimIkon.Kapat, cevir(dil, "Aramayı temizle", "Clear search")) } })
                Text(cevir(dil, "Bildirimlerin için ${secili.size} konu seçili.", "${secili.size} topics selected for your reminders."),
                    color = Renk.metinIkincil, fontSize = 12.sp, lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 2.dp).testTag("category-selection-summary"))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    items(listOf("all", "selected", "open")) { f ->
                        Column(Modifier.heightIn(min = 48.dp).testTag("category-filter-$f")
                            .selectable(selected = filtre == f, role = Role.Tab, onClick = { filtre = f })
                            .padding(horizontal = 2.dp), verticalArrangement = Arrangement.Center) {
                            Text(when (f) {
                                "selected" -> cevir(dil, "Seçtiklerim", "Selected")
                                "open" -> cevir(dil, "Açık", "Unlocked")
                                else -> cevir(dil, "Tümü", "All")
                            }, color = if (filtre == f) Renk.accent else Renk.metinIkincil, fontSize = 13.sp,
                                fontWeight = if (filtre == f) FontWeight.SemiBold else FontWeight.Normal)
                            Spacer(Modifier.height(6.dp))
                            Box(Modifier.width(18.dp).height(2.dp).background(if (filtre == f) Renk.accent else Color.Transparent))
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(cevir(dil, "${kategoriler.size} konu", "${kategoriler.size} topics"), fontSize = 11.sp,
                        color = Renk.metinIkincil, modifier = Modifier.weight(1f).testTag("category-count"))
                    TextButton(onClick = { alanlarAcik = true }, modifier = Modifier.weight(2.3f).testTag("category-group-filter"),
                        contentPadding = PaddingValues(horizontal = 2.dp)) {
                        Text(cevir(dil, "Grup: ", "Group: ") + (Kategoriler.grupBul(grupKey ?: "")?.ad(dil)
                            ?: cevir(dil, "Tümü", "All")) + "  ⌄", color = Renk.metinIkincil, fontSize = 11.sp,
                            lineHeight = 16.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                    }
                }
                HorizontalDivider(color = Renk.kenarlik)
            }
        }
        items(kategoriler, key = { it.anahtar }) { kat ->
            val secildi = kat.anahtar in secili
            val acildi = kat.anahtar in acik
            Column {
                Row(Modifier.fillMaxWidth().heightIn(min = 68.dp).clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClickLabel = cevir(dil, "Konuya göz at", "Browse topic")) {
                        odak.clearFocus(); detayKey = kat.anahtar
                    }
                    .testTag("category-${kat.anahtar}").semantics { stateDescription = kategoriDurumu(acildi, secildi, dil) }
                    .padding(vertical = 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text((Kategoriler.tumAltlar.indexOf(kat) + 1).toString().padStart(2, '0'), color = Renk.metinIkincil,
                        fontFamily = LoraSerif, fontSize = 11.sp, modifier = Modifier.clearAndSetSemantics {})
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(kat.ad(dil), color = Renk.metin, fontFamily = LoraSerif, fontSize = 18.sp, lineHeight = 24.sp)
                        Text(kategoriDurumu(acildi, secildi, dil), color = Renk.metinIkincil, fontSize = 11.sp, lineHeight = 16.sp)
                    }
                    Icon(AzimIkon.Ileri, null, Modifier.size(17.dp), tint = Renk.metinIkincil)
                }
                HorizontalDivider(color = Renk.kenarlik)
            }
        }
        if (kategoriler.isEmpty()) item {
            Column(Modifier.fillMaxWidth().padding(vertical = 36.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(cevir(dil, "Bu aramada bir konu yok.", "No topics match this search."), color = Renk.metin, fontFamily = LoraSerif, fontSize = 21.sp, lineHeight = 29.sp)
                TextButton(onClick = { arama = ""; filtre = "all"; grupKey = null }) { Text(cevir(dil, "Tüm konuları göster", "Show all topics")) }
            }
        }
    }
    if (alanlarAcik) ModalBottomSheet(onDismissRequest = { alanlarAcik = false }, containerColor = Renk.zemin) {
        LazyColumn(Modifier.fillMaxWidth(), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)) {
            item { Text(cevir(dil, "Konu grupları", "Topic groups"), color = Renk.metin, fontFamily = LoraSerif,
                fontSize = 26.sp, lineHeight = 34.sp, modifier = Modifier.padding(bottom = 12.dp).semantics { heading() }) }
            item {
                Row(Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    .selectable(selected = grupKey == null, role = Role.RadioButton, onClick = { grupKey = null; alanlarAcik = false })
                    .padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(cevir(dil, "Tüm gruplar", "All groups"), Modifier.weight(1f), color = Renk.metin)
                    if (grupKey == null) Icon(AzimIkon.Tik, null, Modifier.size(20.dp), tint = Renk.metin)
                }
                HorizontalDivider(color = Renk.kenarlik)
            }
            items(Kategoriler.gruplar, key = { it.anahtar }) { g ->
                Row(Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    .selectable(selected = grupKey == g.anahtar, role = Role.RadioButton, onClick = { grupKey = g.anahtar; alanlarAcik = false })
                    .padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(g.ad(dil), Modifier.weight(1f), color = Renk.metin)
                    if (grupKey == g.anahtar) Icon(AzimIkon.Tik, null, Modifier.size(20.dp), tint = Renk.metin)
                }
                HorizontalDivider(color = Renk.kenarlik)
            }
        }
    }
    val kat = Kategoriler.bul(detayKey ?: "")
    if (kat != null) ModalBottomSheet(onDismissRequest = { detayKey = null }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = Renk.zemin) {
        val sozler = Sozler.kategoriden(kat.anahtar)
        val acildi = kat.anahtar in acik
        val secildi = kat.anahtar in secili
        val gosterilenSozler = if (acildi) sozler else sozler.take(2)
        LazyColumn(Modifier.fillMaxWidth().testTag("category-detail"),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 32.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f).padding(top = 8.dp, end = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(Kategoriler.grupBul(kat.grup)?.ad(dil).orEmpty(), color = Renk.metinIkincil, fontSize = 11.sp, lineHeight = 16.sp)
                        Text(kat.ad(dil), color = Renk.metin, fontFamily = LoraSerif, fontSize = 27.sp, lineHeight = 35.sp,
                            modifier = Modifier.semantics { heading() })
                    }
                    IconButton(onClick = { detayKey = null }, modifier = Modifier.testTag("category-detail-close")) {
                        Icon(AzimIkon.Kapat, cevir(dil, "Konuya göz atmayı kapat", "Close topic"), Modifier.size(20.dp))
                    }
                }
                Text(if (acildi) cevir(dil, "${sozler.size} özgün söz · Erişime açık", "${sozler.size} original quotes · Unlocked")
                    else cevir(dil, "${sozler.size} özgün söz · ${gosterilenSozler.size} sözlük önizleme", "${sozler.size} original quotes · ${gosterilenSozler.size}-quote preview"),
                    color = Renk.metinIkincil, fontSize = 12.sp, lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 18.dp).testTag("category-detail-access"))
                HorizontalDivider(color = Renk.kenarlik)
            }
            item {
                if (acildi) {
                    val degisebilir = !secildi || secili.size > 1
                    Row(Modifier.fillMaxWidth().heightIn(min = 64.dp).testTag("category-reminder-${kat.anahtar}")
                        .toggleable(value = secildi, enabled = degisebilir, role = Role.Switch, onValueChange = { sec(kat.anahtar) })
                        .padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(cevir(dil, "Bildirim konularımda", "In my reminder topics"), modifier = Modifier.weight(1f),
                            color = Renk.metin, fontSize = 14.sp, lineHeight = 21.sp)
                        Switch(checked = secildi, onCheckedChange = null, enabled = degisebilir)
                    }
                    Text(if (!degisebilir) cevir(dil, "Son konunu kaldırmadan önce başka bir konu seç.", "Choose another topic before removing your last one.")
                        else cevir(dil, "Seçersen bildirimlerinde bu konuya öncelik veririz. Planındaki tür ve kaçınma tercihleri korunur.", "Selected topics have priority in your reminders. Your format and avoidance preferences still apply."),
                        color = Renk.metinIkincil, fontSize = 12.sp, lineHeight = 18.sp,
                        modifier = Modifier.padding(bottom = 18.dp))
                } else {
                    Text(cevir(dil, "Bu konu kilitli. Önce sözlere göz at; erişimi açtıktan sonra bildirimlerine eklemeyi seçebilirsin.",
                        "This topic is locked. Preview the quotes first; once unlocked, you can choose to add it to your reminders."),
                        color = Renk.metinIkincil, fontSize = 13.sp, lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp))
                    Button(onClick = { detayKey = null; kilidiAc(kat) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                        shape = RoundedCornerShape(50)) { Text(cevir(dil, "Bu kategoriyi aç · Demo", "Unlock this topic · Demo")) }
                    TextButton(onClick = { detayKey = null; proAc() }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(cevir(dil, "Pro ile tümüne eriş", "Get all topics with Pro"))
                    }
                }
                if (kat.grup in setOf("filozoflar", "tasavvuf", "inanc")) {
                    Text(cevir(dil, "Bu gelenekten ilham alan özgün Ascend düşünceleri; doğrudan alıntı veya kutsal metin değildir.",
                        "Original Ascend reflections inspired by this tradition, not direct quotations or sacred texts."),
                        color = Renk.metinIkincil, fontSize = 12.sp, lineHeight = 18.sp, fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(bottom = 16.dp))
                }
            }
            items(gosterilenSozler, key = { it.kimlik }) { soz ->
                HorizontalDivider(color = Renk.kenarlik)
                Text(soz.metin(dil), Modifier.fillMaxWidth().padding(vertical = 22.dp).testTag("category-quote-${soz.kimlik}"),
                    color = Renk.metin, fontFamily = LoraSerif, fontSize = 23.sp, lineHeight = 33.sp)
            }
        }
    }
}

private fun kategoriDurumu(acik: Boolean, secili: Boolean, dil: String): String =
    if (!acik) cevir(dil, "Kilitli", "Locked")
    else cevir(dil, "Açık", "Unlocked") + " · " +
        (if (secili) cevir(dil, "Bildirimde", "In reminders") else cevir(dil, "Bildirimde değil", "Not in reminders"))

@Composable
fun SeciliKonular(secili: Set<String>, dil: String, duzenle: (() -> Unit)?, modifier: Modifier = Modifier) {
    val konular = Kategoriler.tumAltlar.filter { it.anahtar in secili }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(cevir(dil, "Bildirim konuların", "Your reminder topics"), Modifier.weight(1f), color = Renk.metin, style = MaterialTheme.typography.titleSmall)
            duzenle?.let { TextButton(onClick = it) { Text(cevir(dil, "Düzenle", "Edit")) } }
        }
        Text(konular.take(3).joinToString(" · ") { it.ad(dil) } + if (konular.size > 3) " +${konular.size - 3}" else "", color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
    }
}
