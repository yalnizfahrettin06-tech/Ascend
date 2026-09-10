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
    kilidiAc: (Kategori) -> Unit, pro: Boolean, proAc: () -> Unit, acilacakGrup: String? = null, bildirimAcik: Boolean = true,
) {

    var grupKey by rememberSaveable { mutableStateOf(acilacakGrup) }
    var sonAcilisGrubu by rememberSaveable { mutableStateOf(acilacakGrup) }
    var gorunum by rememberSaveable { mutableStateOf(if (acilacakGrup == null) "collections" else "all") }
    var arama by rememberSaveable { mutableStateOf("") }
    var filtre by rememberSaveable { mutableStateOf("all") }
    var detayKey by rememberSaveable { mutableStateOf<String?>(null) }
    var alanlarAcik by rememberSaveable { mutableStateOf(false) }
    val odak = LocalFocusManager.current
    val searchInteraction = remember { MutableInteractionSource() }
    val searchFocused by searchInteraction.collectIsFocusedAsState()
    val collectionScroll = rememberLazyListState()
    val allScroll = rememberLazyListState()
    val selectedScroll = rememberLazyListState()
    val searchScroll = rememberLazyListState()
    val groupScroll = rememberLazyListState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(acilacakGrup) {
        if (sonAcilisGrubu != acilacakGrup) {
            grupKey = acilacakGrup; gorunum = if (acilacakGrup == null) "collections" else "all"
            sonAcilisGrubu = acilacakGrup
        }
    }
    BackHandler(grupKey != null && detayKey == null && !alanlarAcik && arama.isBlank()) {
        grupKey = null; gorunum = "collections"
    }
    val query = arama.trim()
    val collections = gorunum == "collections" && query.isBlank()
    val kategoriler = LibraryQuery.filter(query, dil,
        group = if (query.isBlank()) grupKey else null,
        selectedOnly = query.isBlank() && gorunum == "selected",
        unlockedOnly = filtre == "open", selected = secili, unlocked = acik)
    val scroll = when {
        query.isNotBlank() -> searchScroll
        collections -> collectionScroll
        gorunum == "selected" -> selectedScroll
        grupKey != null -> groupScroll
        else -> allScroll
    }
    val columns = if (LocalConfiguration.current.screenWidthDp < 360 || LocalDensity.current.fontScale > 1.3f) 1 else 2
    fun chooseView(value: String) { odak.clearFocus(); arama = ""; grupKey = null; gorunum = value }
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding()) {
        MarkaBasligi(sutun = true) {
            Column {
                Text(cevir(dil, "Keşfet", "Explore"), fontFamily = LoraSerif, fontSize = 29.sp, lineHeight = 37.sp,
                    modifier = Modifier.semantics { heading() })
                Text(cevir(dil, "Sözlere açılan bir kütüphane", "A library of perspectives"), fontSize = 11.sp, lineHeight = 17.sp)
            }
        }
        Column(Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    TextField(arama, { arama = it },
                        placeholder = { Text(cevir(dil, "Konu veya düşünür ara", "Search topics or thinkers"), fontSize = 14.sp) },
                        interactionSource = searchInteraction,
                        modifier = Modifier.fillMaxWidth().border(
                            if (searchFocused) 1.5.dp else 1.dp,
                            if (searchFocused) Renk.accent else Color.Transparent, RoundedCornerShape(14.dp))
                            .testTag("category-search").semantics {
                            contentDescription = cevir(dil, "Konu veya düşünür ara", "Search topics or thinkers")
                        }, singleLine = true, shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { odak.clearFocus() }),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Renk.yuzey, unfocusedContainerColor = Renk.yuzey,
                            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                        leadingIcon = { Icon(AzimIkon.Ara, null, Modifier.size(20.dp)) },
                        trailingIcon = { if (arama.isNotEmpty()) IconButton(onClick = { arama = "" }, modifier = Modifier.testTag("category-clear-search")) {
                            Icon(AzimIkon.Kapat, cevir(dil, "Aramayı temizle", "Clear search"))
                        } })
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth().testTag("category-grid"), state = scroll,
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 20.dp)) {
            item(key = "controls") {
                Column {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(top = 8.dp).selectableGroup()) {
                        items(listOf("collections", "all", "selected")) { view ->
                            Column(Modifier.heightIn(min = 52.dp).testTag("category-filter-" + view)
                                .selectable(selected = gorunum == view && query.isBlank(), role = Role.Tab, onClick = { chooseView(view) })
                                .padding(vertical = 10.dp), verticalArrangement = Arrangement.Center) {
                                Text(when (view) {
                                    "collections" -> cevir(dil, "Koleksiyonlar", "Collections")
                                    "selected" -> cevir(dil, "Seçtiklerim", "Selected")
                                    else -> cevir(dil, "Tüm konular", "All topics")
                                }, color = if (gorunum == view && query.isBlank()) Renk.accent else Renk.metinIkincil,
                                    fontSize = 13.sp, lineHeight = 20.sp,
                                    fontWeight = if (gorunum == view) FontWeight.SemiBold else FontWeight.Normal)
                                Spacer(Modifier.height(6.dp))
                                Box(Modifier.width(24.dp).height(2.dp).background(
                                    if (gorunum == view && query.isBlank()) Renk.accent else Color.Transparent))
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(12.dp)).background(Renk.markaSessizYuzeyi)
                        .clickable(role = Role.Button) { chooseView(if (secili.isEmpty()) "all" else "selected") }
                        .testTag("category-selection-summary").padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(AzimIkon.Bildirim, null, Modifier.size(20.dp), tint = Renk.accent)
                        Text(bildirimSecimOzeti(secili.size, bildirimAcik, dil),
                            color = Renk.metinIkincil, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.weight(1f))
                        Icon(AzimIkon.Ileri, null, Modifier.size(16.dp), tint = Renk.metinIkincil)
                    }
                    if (!bildirimAcik && gorunum == "selected") Text(
                        cevir(dil, "Konuların kayıtlı. Bildirim teslimatı şu anda kapalı; Planım’dan açabilirsin.",
                            "Your topics are saved. Reminder delivery is currently off; enable it in My plan."),
                        color = Renk.metinIkincil, fontSize = 12.sp, lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 10.dp).testTag("category-delivery-off"))
                    if (collections) {
                        Text(cevir(dil, Kategoriler.gruplar.size.toString() + " koleksiyon · " + Kategoriler.tumAltlar.size + " konu",
                            Kategoriler.gruplar.size.toString() + " collections · " + Kategoriler.tumAltlar.size + " topics"),
                            color = Renk.metinIkincil, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(vertical = 16.dp))
                    } else {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(cevir(dil, kategoriler.size.toString() + " konu", kategoriler.size.toString() + " topics"),
                                color = Renk.metinIkincil, fontSize = 12.sp, modifier = Modifier.weight(1f).testTag("category-count"))
                            if (gorunum == "all" || query.isNotBlank()) TextButton(onClick = { odak.clearFocus(); alanlarAcik = true },
                                modifier = Modifier.testTag("category-group-filter")) { Text(cevir(dil, "Filtrele", "Filter"), fontSize = 12.sp) }
                        }
                        if (query.isNotBlank()) Text(cevir(dil, "Tüm konularda arama", "Searching all topics"),
                            color = Renk.metinIkincil, fontSize = 11.sp, modifier = Modifier.padding(bottom = 10.dp))
                        if (grupKey != null && query.isBlank()) {
                            TextButton(onClick = { grupKey = null; gorunum = "collections" },
                                modifier = Modifier.testTag("category-back-collections")) {
                                Icon(AzimIkon.Geri, null, Modifier.size(16.dp)); Spacer(Modifier.width(8.dp))
                                Text(cevir(dil, "Koleksiyonlara dön", "Back to collections"), fontSize = 12.sp)
                            }
                            Text(Kategoriler.grupBul(grupKey.orEmpty())?.ad(dil).orEmpty(),
                                fontFamily = LoraSerif, fontSize = 24.sp, lineHeight = 32.sp, color = Renk.metin,
                                modifier = Modifier.padding(bottom = 12.dp).semantics { heading() })
                        }
                        if (filtre != "all" || (grupKey != null && query.isBlank())) {
                            TextButton(onClick = { filtre = "all"; grupKey = null; gorunum = "all" },
                                modifier = Modifier.testTag("category-clear-filters")) {
                                Text((if (filtre == "open") cevir(dil, "Açık konular · ", "Unlocked topics · ") else "") +
                                    cevir(dil, "Filtreleri temizle", "Clear filters"), fontSize = 12.sp)
                            }
                        }
                        HorizontalDivider(color = Renk.kenarlik)
                    }
                }
            }
            if (collections) {
                items(Kategoriler.gruplar.chunked(columns), key = { "collection-row-" + it.first().anahtar }) { groups ->
                    Row(Modifier.fillMaxWidth().padding(bottom = 12.dp).height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        groups.forEach { group ->
                            KoleksiyonKarti(group, dil, group.altlar.count { it.anahtar in secili },
                                Modifier.weight(1f).fillMaxHeight()) {
                                grupKey = group.anahtar; gorunum = "all"; scope.launch { groupScroll.scrollToItem(0) }
                            }
                        }
                        if (groups.size < columns) Spacer(Modifier.weight(1f))
                    }
                }
            } else {
                items(kategoriler, key = { it.anahtar }) { kat ->
                    val secildi = kat.anahtar in secili; val acildi = kat.anahtar in acik
                    Column {
                        Row(Modifier.fillMaxWidth().heightIn(min = 74.dp).clip(RoundedCornerShape(8.dp))
                            .clickable(role = Role.Button, onClickLabel = cevir(dil, "Konuya göz at", "Browse topic")) {
                                odak.clearFocus(); detayKey = kat.anahtar
                            }.testTag("category-" + kat.anahtar).semantics { stateDescription = kategoriDurumu(acildi, secildi, dil) }
                            .padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(kat.ad(dil), color = Renk.metin, fontFamily = LoraSerif, fontSize = 18.sp, lineHeight = 25.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (secildi && acildi) Icon(AzimIkon.Tik, null, Modifier.size(13.dp), tint = Renk.accent)
                                    Text(if (secildi && acildi) cevir(dil, "Bildirimlerinde", "In your reminders")
                                        else if (!acildi) cevir(dil, "Kilitli · Önizleme", "Locked · Preview")
                                        else cevir(dil, Sozler.kategoriden(kat.anahtar).size.toString() + " söz", Sozler.kategoriden(kat.anahtar).size.toString() + " quotes"),
                                        color = if (secildi && acildi) Renk.accent else Renk.metinIkincil, fontSize = 12.sp, lineHeight = 18.sp)
                                }
                            }
                            Icon(AzimIkon.Ileri, null, Modifier.size(17.dp), tint = Renk.metinIkincil)
                        }
                        HorizontalDivider(color = Renk.kenarlik)
                    }
                }
                if (kategoriler.isEmpty()) item {
                    Column(Modifier.fillMaxWidth().padding(vertical = 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(cevir(dil, "Bu seçimde bir konu yok.", "No topics match this selection."),
                            color = Renk.metin, fontFamily = LoraSerif, fontSize = 21.sp, lineHeight = 29.sp)
                        if (query.isNotBlank()) TextButton(onClick = { arama = "" }) { Text(cevir(dil, "Aramayı temizle", "Clear search")) }
                        if (filtre != "all" || grupKey != null) TextButton(onClick = { filtre = "all"; grupKey = null }) {
                            Text(cevir(dil, "Filtreleri temizle", "Clear filters"))
                        }
                    }
                }
            }
        }
    }
    if (alanlarAcik) ModalBottomSheet(onDismissRequest = { alanlarAcik = false },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = Renk.zemin) {
        LazyColumn(Modifier.fillMaxWidth().testTag("category-filters"),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
            item { Text(cevir(dil, "Filtrele", "Filter"), fontFamily = LoraSerif, fontSize = 26.sp,
                color = Renk.metin, modifier = Modifier.padding(bottom = 12.dp).semantics { heading() }) }
            items(listOf("all", "open")) { value ->
                Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("category-filter-" + value + "-access")
                    .selectable(selected = filtre == value, role = Role.RadioButton, onClick = { filtre = value })
                    .padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (value == "open") cevir(dil, "Erişime açık konular", "Unlocked topics") else cevir(dil, "Tüm erişimler", "Any access"),
                        Modifier.weight(1f), color = Renk.metin)
                    RadioButton(filtre == value, onClick = null)
                }
            }
            if (query.isBlank()) {
                item { HorizontalDivider(); Text(cevir(dil, "Koleksiyon", "Collection"),
                    modifier = Modifier.padding(vertical = 16.dp), color = Renk.accent) }
                items(listOf<String?>(null) + Kategoriler.gruplar.map { it.anahtar }) { value ->
                    Row(Modifier.fillMaxWidth().heightIn(min = 52.dp)
                        .selectable(selected = grupKey == value, role = Role.RadioButton, onClick = { grupKey = value })
                        .padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(Kategoriler.grupBul(value.orEmpty())?.ad(dil) ?: cevir(dil, "Tüm koleksiyonlar", "All collections"),
                            Modifier.weight(1f), color = Renk.metin)
                        if (grupKey == value) Icon(AzimIkon.Tik, null, Modifier.size(18.dp), tint = Renk.accent)
                    }
                }
            }
            item { Button(onClick = { alanlarAcik = false }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                .testTag("category-apply-filters")) { Text(cevir(dil, "Sonuçları göster", "Show results")) } }
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
    if (!acik) cevir(dil, "Kilitli · Önizleme", "Locked · Preview")
    else if (secili) cevir(dil, "Bildirimlerinde", "In your reminders")
    else cevir(dil, "Erişime açık", "Unlocked")

@Composable
private fun KoleksiyonKarti(group: KategoriGrubu, dil: String, seciliSayisi: Int,
    modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val konuSayisi = cevir(dil, "${group.altlar.size} konu", "${group.altlar.size} topics")
    val secim = cevir(dil, "${seciliSayisi} konu seçili", "${seciliSayisi} topics selected")
    Surface(onClick = onClick, interactionSource = interaction,
        modifier = modifier.testTag("collection-" + group.anahtar).semantics {
            // Opening a collection never toggles its reminder topics.
            role = Role.Button
        },
        color = Renk.koleksiyonYuzeyi, shape = RoundedCornerShape(20.dp),
        border = BorderStroke(if (focused) 2.dp else 1.dp, if (focused) Renk.accent else Renk.kenarlik)) {
        Column(Modifier.padding(16.dp).heightIn(min = 128.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(koleksiyonIkonu(group.anahtar), null, Modifier.size(20.dp), tint = Renk.accent)
            Text(group.ad(dil), fontFamily = LoraSerif, fontSize = 20.sp, lineHeight = 26.sp,
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

private fun koleksiyonIkonu(key: String) = when (key) {
    "olumlamalar" -> AzimIkon.AcikKalp
    "azim" -> AzimIkon.Basamak
    "disiplin" -> AzimIkon.Odak
    "cesaret" -> AzimIkon.Esik
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
