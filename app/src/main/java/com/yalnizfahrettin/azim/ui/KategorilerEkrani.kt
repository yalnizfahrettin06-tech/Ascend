package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch
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
    AlertDialog(onDismissRequest = kapat, title = { Text(kategori.ad(dil)) },
        text = { Text(cevir(dil, "Bu konu Ascend Pro ile kullanılabilir. Demo ödeme gerektirmez.", "This topic is available with Ascend Pro. The demo does not require payment.")) },
        confirmButton = { TextButton(onClick = proAc) { Text(cevir(dil, "Pro’yu incele", "Explore Pro")) } },
        dismissButton = { TextButton(onClick = kapat) { Text(cevir(dil, "Kapat", "Close")) } })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategorilerEkrani(secili: Set<String>, acik: Set<String>, dil: String, sec: (String) -> Unit,
    kilidiAc: (Kategori) -> Unit, pro: Boolean, proAc: () -> Unit, acilacakGrup: String? = null, bildirimAcik: Boolean = true,
    oku: (Soz) -> Unit = {}, selectedRequest: Int = 0, insets: Boolean = true,
) {

    var emptyTopic by rememberSaveable { mutableStateOf<String?>(null) }
    var group by rememberSaveable { mutableStateOf(acilacakGrup) }
    var reminders by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var detayKey by rememberSaveable { mutableStateOf<String?>(null) }
    val focus = LocalFocusManager.current
    LaunchedEffect(acilacakGrup) { group = acilacakGrup }
    LaunchedEffect(selectedRequest) { if(selectedRequest != 0) { reminders = selectedRequest > 0; group = null; query = "" } }
    BackHandler((group != null || reminders) && detayKey == null) { group = null; reminders = false }
    val showGroups = group == null && !reminders && query.isBlank()
    val results = LibraryQuery.filter(query.trim(), dil, group = if(query.isBlank()) group else null,
        selectedOnly = reminders, unlockedOnly = false, selected = secili, unlocked = acik).filter { !reminders || it.anahtar in secili }
    Column(Modifier.fillMaxSize().background(Renk.zemin).then(if(insets) Modifier.statusBarsPadding() else Modifier)) {
        TextField(query, { query = it }, singleLine = true,
            placeholder = { Text(cevir(dil, "Konu veya düşünür ara", "Search topics or thinkers"), fontSize = 14.sp) },
            leadingIcon = { Icon(AzimIkon.Ara, null, tint = Renk.metinIkincil) },
            trailingIcon = { if(query.isNotBlank()) IconButton(onClick = { query = "" }) { Icon(AzimIkon.Kapat, cevir(dil,"Temizle","Clear")) } },
            shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).testTag("category-search"),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() }),
            colors = TextFieldDefaults.colors(focusedContainerColor = Renk.yuzey, unfocusedContainerColor = Renk.yuzey,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            if(group != null || reminders) IconButton(onClick = { group = null; reminders = false; query = "" }) { Icon(AzimIkon.Geri, cevir(dil,"Koleksiyonlar","Collections"), tint = Renk.metin) }
            Text(if(reminders) cevir(dil,"Bildirimlerim","My reminders") else Kategoriler.grupBul(group.orEmpty())?.ad(dil) ?: cevir(dil,"Konular","Topics"),
                Modifier.weight(1f), color = Renk.metinIkincil, fontSize = 13.sp)
            if(!reminders) TextButton(onClick = { reminders = true; group = null; query = "" }, modifier = Modifier.testTag("category-selection-summary")) {
                Icon(AzimIkon.Bildirim, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp))
                Text(cevir(dil,"Bildirimlerim","My reminders"), fontSize = 12.sp)
            }
        }
        LazyColumn(Modifier.weight(1f).testTag("category-grid"), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if(showGroups) { items(Kategoriler.gruplar, key = { it.anahtar }) { g ->
                Surface(onClick = { group = g.anahtar }, color = Renk.yuzey, shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag(if(g == Kategoriler.gruplar.first()) "collection-feature" else "collection-${g.anahtar}")) {
                    Row(Modifier.padding(16.dp).heightIn(min = 44.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Icon(koleksiyonIkonu(g.anahtar), null, Modifier.size(24.dp), tint = Renk.metinIkincil)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(g.ad(dil), color = Renk.metin, fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.Medium)
                            Text(cevir(dil,"${g.altlar.size} konu","${g.altlar.size} topics"), color = Renk.metinIkincil, fontSize = 12.sp)
                        }
                        Icon(AzimIkon.Ileri, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
                    }
                }
            }
                item { Text(cevir(dil,"Yakında","Coming soon"), Modifier.padding(top = 16.dp, bottom = 4.dp), color = Renk.metinIkincil, fontSize = 12.sp) }
                items(listOf("Sabah niyeti" to "Morning intention", "Dijital mola" to "Digital break", "Yaratıcı cesaret" to "Creative courage")) { names ->
                    val name = cevir(dil,names.first,names.second)
                    Surface(onClick = { emptyTopic = name }, color = Renk.yuzey, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(name, Modifier.weight(1f), color = Renk.metinIkincil, fontSize = 15.sp)
                            Text(cevir(dil,"0 söz · Yakında","0 quotes · Soon"), color = Renk.metinIkincil, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                if(results.isEmpty()) item { Text(cevir(dil,"Burada henüz bir konu yok. Aramanı değiştir veya başka bir konu seç.","No topics here yet. Try another search or choose a topic."), color = Renk.metinIkincil, modifier = Modifier.padding(vertical = 24.dp)) }
                items(results, key = { it.anahtar }) { topic ->
                    Surface(onClick = { focus.clearFocus(); if(topic.anahtar !in acik) proAc() else detayKey = topic.anahtar }, color = Renk.yuzey, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().testTag("category-${topic.anahtar}")) {
                        Row(Modifier.padding(16.dp).heightIn(min = 42.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(topic.ad(dil), color = if(topic.anahtar !in acik) Renk.metinIkincil else Renk.metin, fontSize = 15.sp, lineHeight = 21.sp, textDecoration = if(topic.anahtar !in acik) androidx.compose.ui.text.style.TextDecoration.LineThrough else null)
                                if(topic.anahtar in secili) Text(cevir(dil,"✓ Bildirimlerinde","✓ In your reminders"), color = Renk.metinIkincil, fontSize = 11.sp)
                            }
                            if(topic.anahtar !in acik) ProRozeti()
                            Spacer(Modifier.width(8.dp)); Icon(AzimIkon.Ileri, null, Modifier.size(16.dp), tint = Renk.metinIkincil)
                        }
                    }
                }
            }
        }
    }

    emptyTopic?.let { name -> AlertDialog(onDismissRequest = { emptyTopic = null }, title = { Text(name) }, text = { Text(cevir(dil,"Bu kategori hazırlanıyor. Henüz söz eklenmedi.","This category is being prepared. No quotes have been added yet.")) }, confirmButton = { TextButton(onClick = { emptyTopic = null }) { Text(cevir(dil,"Tamam","OK")) } }) }

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
                        Text(kat.ad(dil), color = Renk.metin, fontFamily = ArayuzFont, fontSize = 27.sp, lineHeight = 35.sp,
                            modifier = Modifier.semantics { heading() })
                        Text(koleksiyonOzeti(kat.grup, dil), color = Renk.metinIkincil, fontSize = 13.sp, lineHeight = 20.sp)
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
                        Text(cevir(dil, "Bildirimlerime ekle", "Add to my reminders"), modifier = Modifier.weight(1f),
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
                    Button(onClick = { detayKey = null; proAc() }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                        shape = RoundedCornerShape(50)) { Text(cevir(dil, "Pro ile eriş · Demo", "Access with Pro · Demo")) }

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
                Text(soz.metin(dil), Modifier.fillMaxWidth().clickable(enabled = acildi, role = Role.Button,
                    onClickLabel = cevir(dil, "Sözü aç", "Open quote")) { oku(soz) }.padding(vertical = 22.dp).testTag("category-quote-${soz.kimlik}"),
                    color = Renk.metin, fontFamily = LoraSerif, fontSize = 23.sp, lineHeight = 33.sp)
            }
        }
    }
}

private fun kategoriDurumu(acik: Boolean, secili: Boolean, dil: String): String =
    if (!acik) cevir(dil, "Pro · Önizleme", "Pro · Preview")
    else if (secili) cevir(dil, "Bildirimlerinde", "In your reminders")
    else cevir(dil, "Erişime açık", "Unlocked")

@Composable
private fun KoleksiyonKarti(group: KategoriGrubu, dil: String, seciliSayisi: Int,
    modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val konuSayisi = cevir(dil, "${group.altlar.size} konu", "${group.altlar.size} topics")
    val secim = cevir(dil, "${seciliSayisi} seçili", "${seciliSayisi} topics selected")
    Surface(onClick = onClick, interactionSource = interaction,
        modifier = modifier.testTag("collection-" + group.anahtar).semantics {
            // Opening a collection never toggles its reminder topics.
            role = Role.Button
        },
        color = Renk.koleksiyonYuzeyi, shape = RoundedCornerShape(18.dp),
        border = BorderStroke(if (focused) 2.dp else 1.dp, if (focused) Renk.accent else Renk.kenarlik)) {
        Box {
        Column(Modifier.padding(16.dp).heightIn(min = 180.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(koleksiyonIkonu(group.anahtar), null, Modifier.size(20.dp), tint = Renk.accent)
            Text(group.ad(dil), fontFamily = ArayuzFont, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp,
                color = Renk.metin)
            Text(koleksiyonOzeti(group.anahtar, dil), color = Renk.metinIkincil, fontSize = 13.sp, lineHeight = 19.sp)
            Spacer(Modifier.weight(1f).heightIn(min = 4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(konuSayisi, color = Renk.metinIkincil, fontSize = 12.sp,
                    lineHeight = 18.sp, modifier = Modifier.weight(1f))
                Icon(AzimIkon.Ileri, null, Modifier.size(16.dp), tint = Renk.metinIkincil)
            }
            if (seciliSayisi > 0) Row(
                Modifier.clip(RoundedCornerShape(8.dp)).background(Renk.markaYuzeyi)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("collection-selection-" + group.anahtar),
                horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(AzimIkon.Tik, null, Modifier.size(14.dp), tint = Renk.accent)
                Text(secim, color = Renk.accent, fontSize = 11.sp, lineHeight = 16.sp,
                    modifier = Modifier.weight(1f, fill = false))
            }
        }
    }
}
}

private fun koleksiyonIkonu(key: String) = when (key) {
    "olumlamalar" -> AzimIkon.Yaprak
    "azim" -> AzimIkon.Basamak
    "disiplin" -> AzimIkon.Hedef
    "cesaret" -> AzimIkon.Kalkan
    "filozoflar" -> AzimIkon.Sutun
    "tasavvuf" -> AzimIkon.IcYol
    "inanc" -> AzimIkon.Eller
    "spor" -> AzimIkon.Hareket
    "is" -> AzimIkon.Canta
    "iliskiler" -> AzimIkon.Bag
    "zihin" -> AzimIkon.Dalga
    else -> AzimIkon.Kitap
}

internal fun bildirimSecimOzeti(sayi: Int, etkin: Boolean, dil: String): String = when {
    sayi == 0 -> cevir(dil, "Bildirim konularını seç", "Choose reminder topics")
    !etkin -> cevir(dil, "${sayi} konu seçili · Bildirimler kapalı", "${sayi} topics selected · Reminders off")
    else -> cevir(dil, "${sayi} konu bildirim planında", "${sayi} topics in your reminder plan")
}

private fun koleksiyonOzeti(key: String, dil: String): String {
    val pair = when (key) {
        "olumlamalar" -> "Kendine daha nazik bir dil" to "A kinder inner voice"
        "azim" -> "Devam et, yeniden başla" to "Keep going, begin again"
        "disiplin" -> "Dikkatine alan aç" to "Make room for focus"
        "cesaret" -> "Korkuya rağmen bir adım" to "A step beyond fear"
        "filozoflar" -> "Düşünceye yeni bir açı" to "A fresh perspective"
        "tasavvuf" -> "İç dünyana bir bakış" to "A look within"
        "inanc" -> "İnanç üzerine düşünceler" to "Reflections on faith"
        "spor" -> "Harekete eşlik eden sözler" to "Words for your movement"
        "is" -> "Emek, amaç ve gelişim" to "Effort, purpose and growth"
        "iliskiler" -> "Bağ kurmak ve anlamak" to "Connection and understanding"
        "zihin" -> "Günün içinde sakin bir durak" to "A quiet pause in your day"
        else -> "Merakına yer aç" to "Room for curiosity"
    }
    return cevir(dil, pair.first, pair.second)
}

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
